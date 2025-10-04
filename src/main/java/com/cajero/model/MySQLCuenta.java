package com.cajero.model;

import java.util.Map.Entry;
import java.math.BigDecimal;
import java.util.TreeMap;

import com.cajero.dao.MySQLDAOImp;

public class MySQLCuenta {
    private final String CAJERO_TB = "cajero_tb";
    private String accountName;
    private double totalAmmount;
    private TreeMap<Double, Integer> monedero = new TreeMap<>();
    private TreeMap<Double, Integer> montoVolatil = new TreeMap<>(); // variable aux para almacenar transacciones
    private final MySQLDAOImp HOST;

    public MySQLCuenta(String accountName, MySQLDAOImp dao) {
        // validar si la cuenta existe
        this.accountName = accountName;
        this.totalAmmount = 0;
        HOST = dao;
        HOST.createAccount(this);
        HOST.createTB(this);
        monedero = getMonedero();
    }

    public String getAccountName() {
        return accountName;
    }
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getTotalAmmount() {
        totalAmmount = 0;
        for (Entry<Double, Integer> kv : getMonedero().entrySet()) {
            totalAmmount += kv.getKey() * kv.getValue();
        }
        return totalAmmount;
    }
    public void setTotalAmmount(double totalAmmount) {
        this.totalAmmount = totalAmmount;
    }

    public void setMontoVolatil(TreeMap<Double, Integer> montoVolatil) {
        this.montoVolatil = montoVolatil;
    }
    public TreeMap<Double, Integer> getMontoVolatil() {
        return montoVolatil;
    }

    public String getCAJERO_TB() {
        return CAJERO_TB;
    }

    private void setMonedero(TreeMap<Double, Integer> monedero) {
        this.monedero = monedero;
    }
    public TreeMap<Double, Integer> getMonedero() {
        setMonedero(HOST.actualizarMonedero(this)); // siempre obtengo una respuesta actualizada
        return monedero;
    }
    // MÉTODOS LISTAR
    public String listarMonedero() {
        int i = 1;
        StringBuilder tabla = new StringBuilder();
        for (Entry<Double, Integer> kv : this.getMonedero().entrySet()) {
            totalAmmount += kv.getKey() * kv.getValue();
            tabla.append(String.format("| € %-6.2f - %-5d ", kv.getKey(), kv.getValue()));
            if (i % 3 == 0 && i != 0) {
                tabla.append("|");
                tabla.append("\n");
                i = 1;
                continue;
            }
            i++;
        }
        return tabla.toString();
    }

    // MÉTODO REALIZAR COMPRA (String moneda-cantidad#, int montoAPagar)
    public void realizarPago(String pago, double montoAPagar) {
        double recibido = 0, vuelto = 0;
        TreeMap<Double, Integer> detallePago = descomponerMonto(pago); // funcion que descomponga el monto y lo devuelva como treemap - al mismo tiempo que verifique la existencia de las monedas
        for (Entry<Double, Integer> kv : detallePago.entrySet()) { // itero para calcular cual es el total del detalle
            recibido += kv.getKey() * kv.getValue();
        }
        vuelto = recibido - montoAPagar; // la diferencia entre el total del detalle y lo que tengo que pagar
        if (vuelto < 0) {
            System.out.println("No has brindado lo suficiente");
        } else if (vuelto > 0) {
            if (getTotalAmmount() >= vuelto) { // calculo si tengo suficiente dinero para pagar
                if (!revisarCantidadMontos(monedero, detallePago)) { // pregunta si tengo la cantidad suficiente de lo que pagaré es decir las cantidades de cada moneda
                    System.out.println("No has brindado lo suficiente");
                    return;
                }
                TreeMap<Double, Integer> vueltoDetalle = componerMontoFull(vuelto); // esta funcion devuelve un TreeMap con un vuelto optimizado, asume que la tienda siempre tiene vuelto de todo tipo
                HOST.realizarPago(detallePago, vueltoDetalle, this);
            }
        } else if (vuelto == 0) {
            if (HOST.restMoney(this, detallePago)) { // primero le quito el dinero que entregara
                System.out.println("Pago justo, sin vuelto.");
            }
        }
    }
    // MÉTODO COMPRAR TREEMAPS
    /**
     * Compara montoA >= montoB -> true
     * @param montoA
     * @param montoB
     * @return boolean
     */
    private boolean revisarCantidadMontos(TreeMap<Double, Integer> montoA, TreeMap<Double, Integer> montoB) {
        // montoA >= montoB = true
        int cantidadA, cantidadB;
        for(Entry<Double, Integer> kv : montoA.entrySet()) {
            cantidadA = kv.getValue();
            cantidadB = montoB.get(kv.getKey());
            if (!(cantidadA >= cantidadB)){
                System.out.printf("No tienes suficientes €%.2f quieres dar %d pero tienes %d\n", kv.getKey(), cantidadB, cantidadA);
                return false; // monto insuficiente
            }
        }
        return true;
    }
    // MÉTODO INTRODUCIR DINERO (String moneda-cantidad#)
    public void introducirDinero(String pago) {
        this.montoVolatil = descomponerMonto(pago); // funcion que descomponga el monto y lo devuelva como treemap - al mismo tiempo que verific la existencia
        // mandar los datos al dao y los agrega
        if (HOST.addMoney(this)) {
        }
    }

    private boolean verificarMoneda(double moneda) {
        if (monedero.containsKey(moneda)) {
            return true;
        }
        return false;
    }

    public TreeMap<Double, Integer> descomponerMonto(String pago) {
        TreeMap<Double, Integer> detalle = new TreeMap<>();
        for (Entry<Double, Integer> kv : monedero.entrySet()) {
            detalle.put(kv.getKey(), 0);
        }
        int indice, cantidad;
        double moneda;
        String recorte;
        while (pago.length() > 0) {
            indice = pago.indexOf("#");
            if (indice == -1) {
                pago = "";
                break;
            }
            recorte = pago.substring(0, indice);
            pago = pago.substring(indice + 1);
            indice = recorte.indexOf("-");
            if (indice == -1) {
                break;
            }
            cantidad = Integer.parseInt(recorte.substring(0, indice));
            moneda = Double.parseDouble(recorte.substring(indice + 1));
            if (verificarMoneda(moneda)) {
                detalle.put(moneda, cantidad);
            }
        }
        return detalle;
    }

    public TreeMap<Double, Integer> componerMontoFull(double m) { // Este metodo se usa para la compra ya que se asume que la tienda tiene de todos los vueltos
        BigDecimal monto = BigDecimal.valueOf(m);
        TreeMap<Double, Integer> detalle = new TreeMap<>();
        monedero.forEach((k,v) -> {
            detalle.put(k, 0);
        });
        int entero;
        BigDecimal denominacion; // BG mantiene la precision
        for (Entry<Double, Integer> kv : detalle.descendingMap().entrySet()) {
            denominacion = BigDecimal.valueOf(kv.getKey());
            if (monto.compareTo(denominacion) >= 0) {
                entero = monto.divide(denominacion).intValue();
                entero = (int) (monto.doubleValue() / kv.getKey());
                kv.setValue(entero);
                monto = monto.subtract(denominacion.multiply(BigDecimal.valueOf(entero)));
            } else {
                continue;
            }
        }
        return detalle;
    }

    public TreeMap<Double, Integer> componerTransferencia(double m) throws Exception { // detallar una transferencia optimizada a lo que tengo
        BigDecimal monto = BigDecimal.valueOf(m); // monto a convertir
        TreeMap<Double, Integer> detalle = new TreeMap<>();
        monedero.forEach((k,v) -> { // iniciar detalle
            detalle.put(k, 0);
        });
        BigDecimal denominacion; // BG mantiene la precision
        int cantidad, entero, diff;
        for (Entry<Double, Integer> kv : getMonedero().descendingMap().entrySet()) { // itero sobre lo que tengo
            denominacion = BigDecimal.valueOf(kv.getKey()); // moneda
            cantidad = kv.getValue();
            entero = monto.divide(denominacion).intValue();
            diff = entero - cantidad;
            if (monto.compareTo(BigDecimal.valueOf(0)) == 0) {
                return detalle;
            }
            if (cantidad > 0) { // significa que puedo darle algunos de esos billetes
                if (diff >= 0) { // diferencia positiva es porque no hay cantidad que cubra el total
                    detalle.put(kv.getKey(), cantidad);
                    monto = monto.subtract(denominacion.multiply(BigDecimal.valueOf(cantidad)));
                } else { // diferencia negativa, es por que hay más de lo que necesito
                    detalle.put(kv.getKey(), entero);
                    monto = monto.subtract(denominacion.multiply(BigDecimal.valueOf(entero)));
                }
            } else {
                continue;
            }
        }
        if (monto.compareTo(BigDecimal.valueOf(0)) == 1) {
            throw new Exception("No tienes dinero suficiente para realizar la transferencia");
        }
        return detalle;
    }

    public void transferir(double monto, String cuenta) {
        try {
            TreeMap<Double, Integer> detalleTransferencia = componerTransferencia(monto); // llega el detalle a partir de lo que poseemos, si salta exception es porque no hay suficiente dinero
            MySQLCuenta destinatario = new MySQLCuenta(cuenta, HOST);
            MySQLCuenta quien = this;//
            HOST.transferMoney(detalleTransferencia, quien, destinatario);
            System.out.printf("Se han transferido €%.2f en el siguiente detalle\n", monto);
            String cadena = "";
            double moneda;
            int cantidad;
            for (Entry<Double, Integer> kv : detalleTransferencia.descendingMap().entrySet()) {
                moneda = kv.getKey();
                cantidad = kv.getValue();
                if (cantidad == 0) continue;
                if (kv.getKey() >= 1) {
                    cadena += String.format("%d-%d#", cantidad, (int)Math.floor(moneda));
                } else if (kv.getKey() < 1) {
                    cadena += String.format("%d-%.2f#", cantidad, moneda);
                }
            }
            System.out.println(cadena);
        } catch (Exception e) {
            System.err.println("Error transferir: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Monedero ").append(this.accountName).append('\n');
        sb.append("Dinero En la cuenta: ").append(String.format("€ %.2f", getTotalAmmount())).append('\n');
        return sb.toString();
    }

    // public static void main(String[] args) {
    //     MySQLDAOImp dao = new MySQLDAOImp();
    //     MySQLCuenta jim = new MySQLCuenta("bc_jimmy", dao);
    //     // MySQLCuenta iki = new MySQLCuenta("bc_iki", dao);

    //     // System.out.println(iki.listarMonedero());
    //     System.out.println(jim.listarMonedero());

    //     jim.realizarPago("2-500#", 1000);
    //     // System.out.println(iki.listarMonedero());
    //     System.out.println(jim.listarMonedero());

    // }
}

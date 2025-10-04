import java.util.TreeMap;

public class MainTests {
    // pago = 1-500#2-100#1-0.01#2-1#
    // public static TreeMap<Double, Integer> descomponerMonto(String pago) {
    //     TreeMap<Double, Integer> detalle = new TreeMap<>();
    //     int indice, cantidad;
    //     double moneda;
    //     String recorte;
    //     while (pago.length() > 0) {
    //         indice = pago.indexOf("#");
    //         if (indice == -1) {
    //             pago = "";
    //             break;
    //         }
    //         recorte = pago.substring(0, indice);
    //         pago = pago.substring(indice + 1);
    //         indice = recorte.indexOf("-");
    //         if (indice == -1) {
    //             break;
    //         }
    //         cantidad = Integer.parseInt(recorte.substring(0, indice));
    //         moneda = Double.parseDouble(recorte.substring(indice + 1));
    //         detalle.put(moneda, (detalle.get(moneda) != null ? detalle.get(moneda) + cantidad : cantidad ));
    //         // System.out.println(recorte);
    //         System.out.println(cantidad);
    //         System.out.println(moneda);
    //     }
    //     System.out.println(detalle);
    //     return detalle;
    // }
    
    public static void componerMonto(int monto) {
        
    }
    public static void main(String[] args) {
        // recordando el relleno del cajero
        // int[] money = {1, 2, 5};
        // for (int i = -2; i < 3; i++) {
        //     for (int m : money) {
        //         System.out.println(m*Math.pow(10, i));
        //     }
        // }

        // descomponerMonto("1-0.01#2-1#3-10#4-100#5-500#6-0.01#7-1#8-10#1-0.01#2-1#3-10#4-100#");
    }
}

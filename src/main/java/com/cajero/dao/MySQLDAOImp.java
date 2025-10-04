package com.cajero.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.cajero.model.MySQLCuenta;
import com.cajero.util.DBUtilMySQL;

public class MySQLDAOImp implements MySQLDAO {
    private final Connection conx;

    public MySQLDAOImp() {
        this.conx = DBUtilMySQL.openConnection();
    }

    @Override
    public void showAllDB() {
        try (
            Statement stmt = this.conx.createStatement();
        ) {
            String query = "SHOW DATABASES";
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("--Inicio operación--");
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
            System.out.println("--Operación concluida--");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void showAllTB(MySQLCuenta account) {
        try (
            Statement stmt = this.conx.createStatement();
        ) {
            String query = "SHOW TABLES FROM " + account.getAccountName();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("--Inicio operación--");
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
            System.out.println("--Operación concluida--");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public boolean accountExist(MySQLCuenta account) {
        try {
            String query = "SHOW DATABASES";
            Statement stmt = this.conx.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                if (rs.getString(1).equals(account.getAccountName())) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }
    public boolean accountExist(String accountName) {
        try {
            String query = "SHOW DATABASES";
            Statement stmt = this.conx.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                if (rs.getString(1).equals(accountName)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean tableExist(MySQLCuenta account) {
        try (
            Statement stmt = this.conx.createStatement();
        ) {
            String query = "SHOW TABLES FROM " + account.getAccountName();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                if (rs.getString(1).equals(account.getCAJERO_TB())) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error tableExist: " + e.getMessage());
        }
        return false;
    }
    public boolean tableExist(String account) {
        try (
            Statement stmt = this.conx.createStatement();
        ) {
            String query = "SHOW TABLES FROM " + account;
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                if (rs.getString(1).equals("cajero_tb")) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error tableExist: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void createAccount(MySQLCuenta account) {
        if (accountExist(account)) {
            return;
        }
        try (
            Statement stmt = this.conx.createStatement();
        ) {
            String query = "CREATE SCHEMA " + account.getAccountName();

            stmt.execute(query);
            System.out.printf("Cuenta %s creada con exito\n", account.getAccountName());
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    public void createAccount(String account) {
        if (accountExist(account)) {
            return;
        }
        try (
            Statement stmt = this.conx.createStatement();
        ) {
            String query = "CREATE SCHEMA " + account;

            stmt.execute(query);
            System.out.printf("Cuenta %s creada con exito\n", account);
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAccount(MySQLCuenta account) {
        try (
            Statement stmt = this.conx.createStatement();
        ) {
            String query = "DROP SCHEMA " + account.getAccountName();

            stmt.execute(query);
            System.out.printf("Cuenta %s eliminada con exito\n", account.getAccountName());
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void createTB(MySQLCuenta account) {
        if (tableExist(account)) {
            return;
        }
        String accountName = account.getAccountName();
        String tbName = account.getCAJERO_TB();
        String query = String.format("CREATE TABLE %s.%s (unidad DOUBLE(5, 2) PRIMARY KEY, cantidad INT(50))",
                                    accountName,
                                    tbName); // double(5, 2) -> 5 digitos en total, de los cuales 2 son decimales
        try (
            Statement stmt = this.conx.createStatement();
            ) {
            stmt.execute(query);
            // llamar a funcion que rellene la tabla con las chauchas
            insertIntoTB(account);
        } catch (SQLException e) {
            System.err.println("Error createTB: " + e.getMessage());
        }
    }
    public void createTB(String accountName) {
        if (tableExist(accountName)) {
            return;
        }
        String tbName = "cajero_tb";
        String query = String.format("CREATE TABLE %s.%s (unidad DOUBLE(5, 2) PRIMARY KEY, cantidad INT(50))",
                                    accountName,
                                    tbName); // double(5, 2) -> 5 digitos en total, de los cuales 2 son decimales
        try (
            Statement stmt = this.conx.createStatement();
            ) {
            stmt.execute(query);
            // llamar a funcion que rellene la tabla con las chauchas
            insertIntoTB(accountName);
        } catch (SQLException e) {
            System.err.println("Error createTB: " + e.getMessage());
        }
    }

    private void insertIntoTB(MySQLCuenta account) {
        String accountName = account.getAccountName();
        String tbName = account.getCAJERO_TB();
        String query = String.format("INSERT INTO %s.%s VALUES(?, ?)", accountName, tbName);
        try (
            PreparedStatement pstmt = this.conx.prepareStatement(query);
            ) {
            int[] money = {1, 2, 5};
            conx.setAutoCommit(false);
            for (int i = -2; i < 3; i++) {
                for (int m : money) {
                    // System.out.println(m*Math.pow(10, i));
                    pstmt.setDouble(1, (m*Math.pow(10, i)));
                    pstmt.setInt(2, 0);
                    pstmt.addBatch(); // Batch sirve para almacenar
                }
            }
            pstmt.executeBatch();
            conx.commit();
            conx.setAutoCommit(true);
            System.out.printf("Monedero activado\n");
        } catch (SQLException e) {
            System.err.println("Error insertIntoTB: " + e.getMessage());
        }
    }
    private void insertIntoTB(String account) {
        String accountName = account;
        String tbName = "cajero_tb";
        String query = String.format("INSERT INTO %s.%s VALUES(?, ?)", accountName, tbName);
        try (
            PreparedStatement pstmt = this.conx.prepareStatement(query);
            ) {
            int[] money = {1, 2, 5};
            conx.setAutoCommit(false);
            for (int i = -2; i < 3; i++) {
                for (int m : money) {
                    // System.out.println(m*Math.pow(10, i));
                    pstmt.setDouble(1, (m*Math.pow(10, i)));
                    pstmt.setInt(2, 0);
                    pstmt.addBatch(); // Batch sirve para almacenar
                }
            }
            pstmt.executeBatch();
            conx.commit();
            conx.setAutoCommit(true);
            System.out.printf("Monedero activado\n");
        } catch (SQLException e) {
            System.err.println("Error insertIntoTB: " + e.getMessage());
        }
    }
    @Override
    public void resetCajero(MySQLCuenta account) {
        String accountName = account.getAccountName();
        String tbName = account.getCAJERO_TB();
        String query = String.format("UPDATE %s.%s SET cantidad = 0 WHERE unidad = ?", accountName, tbName);
        try (
            PreparedStatement pstmt = this.conx.prepareStatement(query);
            ) {
            conx.setAutoCommit(false);
            for (Entry<Double, Integer> kv : account.getMontoVolatil().entrySet()) {
                pstmt.setDouble(2, kv.getKey()); // moneda = unidad
                pstmt.addBatch(); // Batch sirve para almacenar
            }
            pstmt.executeBatch();
            conx.commit();
            System.out.printf("cuenta limpia de dinero!\n", accountName, tbName);
        } catch (SQLException e) {
            try {
                conx.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
                System.err.println("Error rollback: " + e.getMessage());
            }
            System.err.println("Error insertIntoTB: " + e.getMessage());
        } finally {
            try {
                conx.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public TreeMap<Double, Integer> actualizarMonedero(MySQLCuenta account) {
        TreeMap<Double, Integer> monedero = new TreeMap<>();
        String accountName = account.getAccountName();
        String tbName = account.getCAJERO_TB();
        try (
            Statement stmt = this.conx.createStatement();
        ) {
            String query = String.format("SELECT * FROM %s.%s", accountName, tbName);
            double moneda;
            int cantidad;
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                moneda = rs.getDouble(1);
                cantidad = rs.getInt(2);
                monedero.put(moneda, cantidad);
            }
            // System.out.printf("Monedero de la cuenta %s actualizado.\n", accountName);
        } catch (SQLException e) {
            System.err.println("Error actualizarMonedero: " + e.getMessage());
        }
        return monedero;
    }

    // MËTODO QUE AGREGA DINERO
    @Override
    public boolean addMoney(MySQLCuenta account) {
        String accountName = account.getAccountName();
        String tbName = account.getCAJERO_TB();
        String query = String.format("UPDATE %s.%s SET cantidad = cantidad + ? WHERE unidad = ?", accountName, tbName);
        try (
            PreparedStatement pstmt = this.conx.prepareStatement(query);
            ) {
            conx.setAutoCommit(false);

            for (Entry<Double, Integer> kv : account.getMontoVolatil().entrySet()) {
                pstmt.setDouble(2, kv.getKey()); // moneda = unidad
                pstmt.setInt(1, kv.getValue());
                pstmt.addBatch(); // Batch sirve para almacenar
            }

            pstmt.executeBatch();
            conx.commit();
            System.out.printf("Ingreso de dinero exitoso en %s\n", accountName);
            return true;
        } catch (SQLException e) {
            try {
                conx.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
                System.err.println("Error rollback: " + e.getMessage());
            }
            System.err.println("Error insertIntoTB: " + e.getMessage());
        } finally {
            try {
                conx.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public boolean addMoney(MySQLCuenta account, TreeMap<Double, Integer> detalle) {
        String accountName = account.getAccountName();
        String tbName = account.getCAJERO_TB();
        String query = String.format("UPDATE %s.%s SET cantidad = cantidad + ? WHERE unidad = ?", accountName, tbName);
        try (
            PreparedStatement pstmt = this.conx.prepareStatement(query);
            ) {
            conx.setAutoCommit(false);

            for (Entry<Double, Integer> kv : detalle.entrySet()) {
                pstmt.setDouble(2, kv.getKey()); // moneda = unidad
                pstmt.setInt(1, kv.getValue());
                pstmt.addBatch(); // Batch sirve para almacenar
            }

            pstmt.executeBatch();
            conx.commit();
            System.out.printf("Ingreso de dinero exitoso en %s\n", accountName);
            return true;
        } catch (SQLException e) {
            try {
                conx.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
                System.err.println("Error rollback: " + e.getMessage());
            }
            System.err.println("Error insertIntoTB: " + e.getMessage());
        } finally {
            try {
                conx.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // MÉTODO QUE QUITA DINERO
    @Override
    public boolean restMoney(MySQLCuenta account, TreeMap<Double, Integer> montoDeResta) {
        String accountName = account.getAccountName();
        String tbName = account.getCAJERO_TB();
        String query = String.format("UPDATE %s.%s SET cantidad = cantidad - ? WHERE unidad = ?", accountName, tbName);
        try (
            PreparedStatement pstmt = this.conx.prepareStatement(query);
            ) {
            conx.setAutoCommit(false);

            TreeMap<Double, Integer> monederoCuenta = account.getMonedero();
            int cantidadDisponible; // cantidad disponible de una moneda para dar vuelto
            for (Entry<Double, Integer> kv : montoDeResta.entrySet()) {
                cantidadDisponible = monederoCuenta.get(kv.getKey());
                if (cantidadDisponible >= kv.getValue()) {
                    pstmt.setDouble(2, kv.getKey()); // moneda = unidad
                    pstmt.setInt(1, kv.getValue());
                    pstmt.addBatch(); // Batch sirve para almacenar
                } else {
                    pstmt.clearBatch();
                    throw new Exception("No tienes suficiente dinero de € " + kv.getKey());
                }
            }
            pstmt.executeBatch();
            conx.commit();
            System.out.printf("Extración de dinero exitosa en %s\n", accountName);
            return true;
        } catch (SQLException e) {
            try {
                conx.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
                System.err.println("Error rollback: " + e.getMessage());
            }
            System.err.println("Error restMoney: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error restMoney: " + e.getMessage());
        } finally {
            try {
                conx.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void transferMoney(TreeMap<Double, Integer> detalle, MySQLCuenta accountQuien, MySQLCuenta accountDonde) {
        String queryQuitar, queryAgregar;
        queryQuitar = String.format("UPDATE %s.%s SET cantidad = cantidad - ? WHERE unidad = ?", accountQuien.getAccountName(), "cajero_tb");
        queryAgregar = String.format("UPDATE %s.%s SET cantidad = cantidad + ? WHERE unidad = ?", accountDonde.getAccountName(), "cajero_tb");
        try (
            PreparedStatement pstmsOUT = conx.prepareStatement(queryQuitar);
            PreparedStatement pstmsIN = conx.prepareStatement(queryAgregar);
        ){
            conx.setAutoCommit(false);
            double moneda;
            int cantidad;
            for(Entry<Double, Integer> kv: detalle.entrySet()) { // Quitar monedas
                moneda = kv.getKey();
                cantidad = kv.getValue();
                pstmsOUT.setInt(1, cantidad);
                pstmsOUT.setDouble(2, moneda); // moneda = unidad
                pstmsOUT.addBatch();
            }
            for(Entry<Double, Integer> kv: detalle.entrySet()) { // Agregar monedas
                moneda = kv.getKey();
                cantidad = kv.getValue();
                pstmsIN.setInt(1, cantidad);
                pstmsIN.setDouble(2, moneda); // moneda = unidad
                pstmsIN.addBatch();
            }
            pstmsOUT.executeBatch();
            pstmsIN.executeBatch();
            conx.commit();
            conx.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conx.rollback();
            } catch (SQLException e1) {
                System.err.println("Error: " + e1.getMessage());
            }
        }
    }

    

    @Override
    public void realizarPago(TreeMap<Double, Integer> detallePago, TreeMap<Double, Integer> detalleVuelto, MySQLCuenta account) {
                String queryPago, queryVuelto;
                queryPago = String.format("UPDATE %s.%s SET cantidad = cantidad - ? WHERE unidad = ?", account.getAccountName(), account.getCAJERO_TB());
                queryVuelto = String.format("UPDATE %s.%s SET cantidad = cantidad + ? WHERE unidad = ?", account.getAccountName(), account.getCAJERO_TB());
                try (
                    PreparedStatement pstmsPago = conx.prepareStatement(queryPago);
                    PreparedStatement pstmsVuelto = conx.prepareStatement(queryVuelto);
                ){
                    conx.setAutoCommit(false);
                    double moneda;
                    int cantidad;
                    for(Entry<Double, Integer> kv: detallePago.entrySet()) { // Quitar monedas
                        moneda = kv.getKey();
                        cantidad = kv.getValue();
                        pstmsPago.setInt(1, cantidad);
                        pstmsPago.setDouble(2, moneda); // moneda = unidad
                        pstmsPago.addBatch();
                    }
                    for(Entry<Double, Integer> kv: detalleVuelto.entrySet()) { // Agregar monedas
                        moneda = kv.getKey();
                        cantidad = kv.getValue();
                        pstmsVuelto.setInt(1, cantidad);
                        pstmsVuelto.setDouble(2, moneda); // moneda = unidad
                        pstmsVuelto.addBatch();
                    }
                    pstmsPago.executeBatch();
                    pstmsVuelto.executeBatch();
                    conx.commit();
                    conx.setAutoCommit(true);
                    System.out.println("Pago exitoso");
                } catch (SQLException e) {
                    try {
                        conx.rollback();
                    } catch (SQLException e1) {
                        System.err.println("Error: " + e1.getMessage());
                    }
                }
    }

    @Override
    public void deleteAll(String comun) {
        String query = "SHOW DATABASES LIKE '"+ comun + "%'";
        String elimina = "DROP SCHEMA ";
        ArrayList<String> nombres = new ArrayList<>();
        try (
            Statement stmt = conx.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ) {
            conx.setAutoCommit(false);
            while (rs.next()) {
                nombres.add(rs.getString(1));
            }
            for (String nombre : nombres) {
                stmt.addBatch(elimina + nombre);
            }
            stmt.executeBatch();
            conx.commit();
        } catch (Exception e) {
            try {
                conx.rollback();
            } catch (SQLException e1) {
                System.err.println("Error: " + e.getMessage());
            }
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                conx.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        MySQLDAOImp mi = new MySQLDAOImp();

        mi.deleteAll("bc_");
    }







}

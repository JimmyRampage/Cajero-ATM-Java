package com.cajero.dao;

import java.util.TreeMap;

import com.cajero.model.MySQLCuenta;

public interface MySQLDAO {
    // Acá creare todos los metodos para interactuar en la bd para que cualquier model MySQLCuenta pueda interactuar

    public void showAllDB();
    public void showAllTB(MySQLCuenta account);
    public boolean accountExist(MySQLCuenta account); // True existe
    public boolean tableExist(MySQLCuenta account); // Consulta si la tabla existe
    public void createAccount(MySQLCuenta account); // quizas booleana para confirmar si se crea o no
    public void deleteAccount(MySQLCuenta account); // Para eliminar un eschema
    public void createTB(MySQLCuenta account); // Crea una tabla -> en particular cajero_tb
    public void resetCajero(MySQLCuenta account); // setea los valores en 0
    public TreeMap<Double, Integer> actualizarMonedero(MySQLCuenta account);
    public boolean addMoney(MySQLCuenta account); // Agrega dinero sobre la tabla
    public boolean restMoney(MySQLCuenta account, TreeMap<Double, Integer> montoDeResta); // quita dinero a una cuenta
    public void transferMoney(TreeMap<Double, Integer> detalleTransferencia, MySQLCuenta accountQuien, MySQLCuenta accountDonde);
    public void realizarPago(TreeMap<Double, Integer> detallePago, TreeMap<Double, Integer> detalleVuelto, MySQLCuenta account);



    public void deleteAll(String comun); // Borra todas las bases de datos que tengan un inicio comun
}

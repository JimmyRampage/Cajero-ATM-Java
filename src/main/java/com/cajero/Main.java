package com.cajero;

import java.util.Scanner;

import com.cajero.dao.MySQLDAOImp;
import com.cajero.model.MySQLCuenta;
import com.cajero.model.MySQLMenu;

public class Main {
    public final static Scanner INPUT = new Scanner(System.in);

    public static MySQLCuenta login(String accountName, MySQLDAOImp conx) {
        if (conx.accountExist(accountName)) {
            MySQLCuenta cuentaActual = new MySQLCuenta(accountName, conx); // cc = nombre de la cuenta -> bc_+nombre
            System.out.printf("Bienvenido %s\n", cuentaActual.getAccountName());
            return cuentaActual;
        } else {
            System.out.printf("Creando cuenta %s\n", accountName);
            conx.createAccount(accountName); // crea la bbdd
            System.out.printf("Creando monedero %s\n", accountName);
            conx.createTB(accountName); // crea la tb
            MySQLCuenta cuentaActual = new MySQLCuenta(accountName, conx); // cc = nombre de la cuenta -> bc_+nombre
            System.out.printf("Bienvenido %s\n", cuentaActual.getAccountName());
            return cuentaActual;
        }
    }

    public static void main() {
        MySQLDAOImp conx = new MySQLDAOImp(); // me traigo el dao implementado para las cuentas
        System.out.println("Ingresa el nombre de tu monedero: ");
        String nombreCuenta = INPUT.nextLine();
        MySQLCuenta cuentaActual = login(nombreCuenta, conx);
        String cadenaIngreso; // variable de uso auxiliar
        Double montoAuxiliar; // variable de uso auxiliar
        boolean menuActivo = true;
        while (menuActivo) {
            int choice = MySQLMenu.menu(MySQLMenu.MENU_MAIN);
            switch (choice) {
                case 1:
                    System.out.println("Desgloce del monedero\n" + cuentaActual.listarMonedero());
                    break;
                case 2:
                    // gastar money, solicitar cadena de pago y monto a pagar
                    System.out.println("Ingresa el monto con el que pagaras, en el siguiente formato:");
                    System.out.println("\tcantidad-unidad#cantidad-unidad#");
                    cadenaIngreso = INPUT.nextLine();
                    System.out.println("Ingresa el monto total a pagar con hasta 2 decimales:");
                    montoAuxiliar = INPUT.nextDouble();
                    INPUT.nextLine();
                    cuentaActual.realizarPago(cadenaIngreso, montoAuxiliar);
                    break;
                case 3:
                    // Depositar
                    System.out.println("Ingresa el monto en el siguiente formato:");
                    System.out.println("\tcantidad-unidad#cantidad-unidad#");
                    cadenaIngreso = INPUT.nextLine();
                    cuentaActual.introducirDinero(cadenaIngreso);
                    break;
                case 4:
                    // transferir fondos
                    System.out.println("Ingresa el monto total a transferir con hasta 2 decimales:");
                    montoAuxiliar = INPUT.nextDouble();
                    INPUT.nextLine(); // al parecer double no hace salto de linea
                    System.out.println("Ingresa el nombre del destinatario:");
                    nombreCuenta = INPUT.nextLine();
                    if (conx.accountExist(nombreCuenta)) {
                        cuentaActual.transferir(montoAuxiliar, nombreCuenta);
                    } else {
                        System.out.println("Cuenta no existe");
                    }
                    break;
                case 5:
                    System.out.println("Ingresa el nombre de tu monedero: ");
                    nombreCuenta = INPUT.nextLine();
                    cuentaActual = login(nombreCuenta, conx); // cc = nombre de la cuenta -> bc_+nombre
                    break;
                case 6:
                    System.out.println(cuentaActual);
                    break;
                case 7:
                    menuActivo = false;
                    System.out.println("Fin del programa");
                    break;
                default:
                    System.out.println("Opción invalida");
                    break;
                }
            }
    }
    public static void main(String[] args) {
        main();
    }
}
package com.cajero.model;

import java.util.Scanner;

public class MySQLMenu {
    final public static Scanner INPUT = new Scanner(System.in);
    final public static String[] MENU_MAIN = {"Listar", "Realizar Compra", "Introducir dinero", "Transferir fondos", "Cambiar BBDD", "Vista rapida","Salir"};
    final public static String[] MENU_PLANTILLA = {};


    // MÉTODO DESPLIEGUE DE MENUS
    public static int menu(String[] opciones) {
        int contador = 1;
        for(String opcion : opciones) {
            System.out.printf("%d.- %s\n", contador, opcion);
            contador ++;
        }
        System.out.printf("  --> ");
        // tratar valores erroneos de insercion
        int out = INPUT.nextInt();
        return out;
    }
    protected static void main(String[] args) {
        menu(MENU_MAIN);
    }
}

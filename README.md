# Cajero-ATM-Java (Simulador de Monedero Virtual)

Este proyecto es una aplicación de consola en Java que simula un monedero o cajero automático personal. Permite a los usuarios gestionar su dinero, realizar pagos, depósitos y transferencias utilizando denominaciones específicas de monedas y billetes. Cada "cuenta" de usuario se gestiona como un esquema independiente en una base de datos MySQL, proporcionando un aislamiento completo entre usuarios.

## ✨ Características Principales

- **Gestión de Cuentas**: Creación y acceso a cuentas de monedero personalizadas. Si una cuenta no existe al intentar acceder, se crea automáticamente.
- **Depósito de Dinero**: Permite introducir dinero en el monedero especificando la cantidad de cada billete o moneda (ej: `2-50#1-20#` para depositar dos billetes de 50 y uno de 20).
- **Realización de Pagos**: Simula un pago donde el usuario entrega una cantidad específica de dinero y el sistema calcula el vuelto, actualizando el inventario de billetes y monedas.
- **Transferencias**: Transfiere un monto total de una cuenta a otra. El sistema optimiza qué billetes y monedas usar basándose en el saldo disponible.
- **Consulta de Saldo**: Muestra el desglose completo del monedero, indicando la cantidad de cada denominación disponible y el saldo total.
- **Persistencia en Base de Datos**: Toda la información de las cuentas y sus saldos se almacena en una base de datos MySQL.

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Java 17
- **Gestor de Dependencias**: Apache Maven
- **Base de Datos**: MySQL
- **Conectividad DB**: JDBC con el driver `mysql-connector-java`.

## 📋 Prerrequisitos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

1. **JDK 17** o superior.
2. **Apache Maven**.
3. Un servidor de **MySQL** en ejecución.

## 🚀 Instalación y Ejecución

1. **Clonar el repositorio**:

    ```bash
    git clone <url-del-repositorio>
    cd cajero
    ```

2. **Configurar la Base de Datos**:
    Crea un archivo llamado `db-mysql.properties` dentro de la carpeta `src/main/resources/`. El proyecto está configurado para leer este archivo, pero no se incluye en el repositorio para proteger las credenciales.

    Contenido del archivo `src/main/resources/db-mysql.properties`:

    ```properties
    driver=com.mysql.cj.jdbc.Driver
    url=jdbc:mysql://localhost:3306/
    user=tu_usuario_mysql
    password=tu_contraseña_mysql
    ```

    > **Importante**: Reemplaza `tu_usuario_mysql` y `tu_contraseña_mysql` con tus credenciales. La URL asume que MySQL se ejecuta localmente en el puerto 3306.

3. **Compilar y Ejecutar**:
    Puedes ejecutar la aplicación directamente desde tu IDE (ej. IntelliJ, Eclipse, vsCode) o usando Maven en la terminal.

    Desde la terminal, en la raíz del proyecto:

    ```bash
    # Compilar el proyecto
    mvn compile

    # Ejecutar la aplicación
    mvn exec:java -Dexec.mainClass="com.cajero.Main"
    ```

## 📁 Estructura del Proyecto

```plaintext
cajero/
├── .gitignore         # Archivos ignorados por Git.
├── pom.xml            # Configuración del proyecto Maven y dependencias.
└── src/
    ├── main/
    │   ├── java/com/cajero/
    │   │   ├── Main.java          # Punto de entrada y bucle principal de la app.
    │   │   ├── dao/               # Capa de Acceso a Datos (DAO).
    │   │   │   ├── MySQLDAO.java      # Interfaz que define las operaciones de BD.
    │   │   │   └── MySQLDAOImp.java   # Implementación concreta para MySQL.
    │   │   ├── model/             # Clases del modelo de negocio.
    │   │   │   ├── MySQLCuenta.java   # Representa la cuenta/monedero del usuario.
    │   │   │   └── MySQLMenu.java     # Gestiona la lógica de los menús de consola.
    │   │   └── util/              # Clases de utilidad.
    │   │       └── DBUtilMySQL.java # Gestiona la conexión a la base de datos.
    │   └── resources/
    │       └── db-mysql.properties  # (Debes crearlo) Credenciales de la BD.
    └── test/
        └── ...
```

## 📄 Documentación del Código (Ligera)

### `Main.java`

Es el orquestador de la aplicación. Contiene el método `main` que inicia el programa, gestiona el "login" (o creación) de la cuenta y mantiene un bucle `while` para mostrar el menú principal y procesar las opciones seleccionadas por el usuario.

### `model/MySQLCuenta.java`

Clase central del modelo de negocio. Representa el monedero de un usuario.

- **Atributos**: `accountName`, `totalAmmount`, `monedero` (un `TreeMap` que almacena las denominaciones y sus cantidades).
- **Métodos clave**:
  - `realizarPago()`: Lógica para pagar una cantidad y calcular el vuelto.
  - `introducirDinero()`: Procesa una cadena de entrada para añadir fondos.
  - `transferir()`: Calcula el desglose óptimo de billetes/monedas para una transferencia y la ejecuta a través del DAO.
  - `descomponerMonto()` / `componerMontoFull()`: Métodos auxiliares para convertir entre cadenas de texto (ej: `1-50#`) y `TreeMap`.

### `dao/MySQLDAOImp.java`

Implementa la lógica de acceso a la base de datos. Traduce las operaciones de la aplicación (como "crear cuenta" o "agregar dinero") a sentencias SQL.

- **Funcionamiento**: Utiliza JDBC y `PreparedStatement` para interactuar de forma segura con la base de datos MySQL.
- **Transacciones**: Emplea `conx.setAutoCommit(false)` y `conx.commit()` o `conx.rollback()` para asegurar que operaciones complejas (como transferencias o pagos) sean atómicas: o se completan todas las sentencias SQL o no se completa ninguna.

### `util/DBUtilMySQL.java`

Clase de utilidad que gestiona la conexión a la base de datos.

- **Patrón Singleton (modificado)**: El método `openConnection()` crea una única instancia de `Connection` y la reutiliza en toda la aplicación para evitar abrir y cerrar conexiones innecesariamente.
- **Carga de Propiedades**: Lee las credenciales del archivo `db-mysql.properties` para no tenerlas escritas directamente en el código.

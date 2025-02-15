package com.example.pm1e1410306.Configuracion;

public class Transacciones {
    // Nombre de la base de datos
    public static final String NameDB = "PME1064103";
    // Tabla de contactos
    public static final String tabla_contactos = "Contactos";

    // Campos de la tabla contactos
    public static final String id = "id";
    public static final String nombres = "nombres";
    public static final String telefono = "telefono";
    public static final String pais = "pais";
    public static final String nota = "nota";
    public static final String foto = "foto";

    // DDL
    public static final String CreateTableContacts =
            "CREATE TABLE Contactos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombres TEXT, " +
                    "telefono TEXT, " +
                    "pais TEXT, " +
                    "nota TEXT, " +
                    "foto TEXT)";

    public static final String DropTableContacts = "DROP TABLE IF EXISTS Contactos";

    // DML
    public static final String SelectTableContacts = "SELECT * FROM " + tabla_contactos;
}
package com.example.pm1e1410306.Utils;

import android.text.InputFilter;
import android.text.Spanned;
import java.util.regex.Pattern;

public class ValidacionesRegex {

    public static InputFilter nombreFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            String regex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$";
            Pattern pattern = Pattern.compile(regex);
            if (!pattern.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    public static InputFilter telefonoFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            String regex = "^[0-9]*$";
            Pattern pattern = Pattern.compile(regex);
            if (!pattern.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    public static boolean validarTelefonoPorPais(String telefono, String pais) {
        if (telefono == null || telefono.isEmpty()) return false;

        telefono = telefono.replaceAll("[\\s-]", "");

        switch (pais) {
            case "Honduras (504)":
                return telefono.length() == 8;
            case "Costa Rica (506)":
                return telefono.length() == 8;
            case "Guatemala (502)":
                return telefono.length() == 8;
            case "El Salvador (503)":
                return telefono.length() == 8;
            case "Nicaragua (505)":
                return telefono.length() == 8;
            case "Panamá (507)":
                return telefono.length() == 8;
            case "México (52)":
                return telefono.length() == 10;
            case "Estados Unidos (1)":
            case "Canadá (1)":
                return telefono.length() == 10;
            default:
                return telefono.length() >= 8 && telefono.length() <= 15;
        }
    }

    public static String getMensajeErrorTelefono(String pais) {
        switch (pais) {
            case "Honduras (504)":
            case "Costa Rica (506)":
            case "Guatemala (502)":
            case "El Salvador (503)":
            case "Nicaragua (505)":
            case "Panamá (507)":
                return "El número debe tener 8 dígitos";
            case "México (52)":
                return "El número debe tener 10 dígitos";
            case "Estados Unidos (1)":
            case "Canadá (1)":
                return "El número debe tener 10 dígitos";
            default:
                return "El número debe tener entre 8 y 15 dígitos";
        }
    }
}
package com.example;

import java.util.Locale;
import java.util.ResourceBundle;

public class Idioma {
    static String[] idiomas = { "English", "Português", "Italiano", "Français", "Español" };
    static String[] idiomasCodigos = {"en", "pt", "it", "fr", "es"};
    static ResourceBundle[] idiomasBundles = {
            ResourceBundle.getBundle("arquivo", Locale.US),
            ResourceBundle.getBundle("arquivo", new Locale("pt", "BR")),
            ResourceBundle.getBundle("arquivo", Locale.ITALY),
            ResourceBundle.getBundle("arquivo", Locale.FRANCE),
            ResourceBundle.getBundle("arquivo", new Locale("es", "ES")),
    };
}

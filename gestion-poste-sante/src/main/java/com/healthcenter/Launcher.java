package com.healthcenter;

/**
 * Launcher séparé pour éviter conflit Spring Boot + JavaFX.
 * 
 * POURQUOI : Spring Boot module system incompatible avec JavaFX Application.
 * Ce launcher délègue à App.java sans être une Application lui-même.
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);  // Délègue à la vraie App JavaFX
    }
}

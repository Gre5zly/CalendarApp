package com.calendar;

/**
 * Вспомогательный класс для запуска Fat-JAR.
 * Необходим для обхода ограничений модульной системы JavaFX при запуске из консоли.
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}
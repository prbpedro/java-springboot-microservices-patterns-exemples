package com.github.prbpedro.javaspringbootreactiveapiexemple.config;

public enum EnvironmentVariables {

    MYSQL_HOST("JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_SERVICE_HOST"),
    MYSQL_PORT("JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_SERVICE_PORT"),
    MYSQL_READONLY_USER("JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_READONLY_SERVICE_USER"),
    MYSQL_READONLY_PASSWORD("JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_READONLY_SERVICE_PASSWORD"),
    MYSQL_DATABASE("JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_SERVICE_DATABASE"),
    MYSQL_WRITE_USER("JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_WRITE_SERVICE_USER"),
    MYSQL_WRITE_PASSWORD("JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_WRITE_SERVICE_PASSWORD");

    EnvironmentVariables(String s) {
        v = System.getenv(s);
    }

    private String v;

    public static String getReadOnlyR2dbcMysqlUrl() {
        return "r2dbc:mysql://" +
            MYSQL_READONLY_USER.v +
            ":" +
            MYSQL_READONLY_PASSWORD.v +
            "@" + MYSQL_HOST.v +
            ":" + MYSQL_PORT.v +
            "/" + MYSQL_DATABASE.v +
            "?sslMode=DISABLED";
    }

    public static String getWriteR2dbcMysqlUrl() {
        return "r2dbc:mysql://" +
            MYSQL_WRITE_USER.v +
            ":" +
            MYSQL_WRITE_PASSWORD.v +
            "@" + MYSQL_HOST.v +
            ":" + MYSQL_PORT.v +
            "/" + MYSQL_DATABASE.v +
            "?sslMode=DISABLED";
    }
}

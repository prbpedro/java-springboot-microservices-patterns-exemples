package com.github.prbpedro.javaspringbootreactiveapiexemple.config;

public enum EnvironmentVariables {

    MYSQL_HOST("JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_SERVICE_HOST"),
    MYSQL_PORT("JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_SERVICE_PORT"),
    MYSQL_READONLY_USER("JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_READONLY_SERVICE_USER"),
    MYSQL_READONLY_PASSWORD("JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_READONLY_SERVICE_PASSWORD"),
    MYSQL_DATABASE("JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_SERVICE_DATABASE"),
    MYSQL_WRITE_USER("JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_WRITE_SERVICE_USER"),
    MYSQL_WRITE_PASSWORD("JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_WRITE_SERVICE_PASSWORD"),
    AWS_SQS_ENDPOINT("AWS_SQS_ENDPOINT"),
    AWS_SNS_ENDPOINT("AWS_SNS_ENDPOINT"),
    AWS_REGION("AWS_REGION");

    EnvironmentVariables(String s) {
        v = s;
    }

    private String v;

    public String getValue() {
        return System.getenv(v);
    }

    public static String getReadOnlyR2dbcMysqlUrl() {
        return "r2dbc:mysql://" +
            MYSQL_READONLY_USER.getValue() +
            ":" +
            MYSQL_READONLY_PASSWORD.getValue() +
            "@" + MYSQL_HOST.getValue() +
            ":" + MYSQL_PORT.getValue() +
            "/" + MYSQL_DATABASE.getValue() +
            "?sslMode=DISABLED";
    }

    public static String getWriteR2dbcMysqlUrl() {
        return "r2dbc:mysql://" +
            MYSQL_WRITE_USER.getValue() +
            ":" +
            MYSQL_WRITE_PASSWORD.getValue() +
            "@" + MYSQL_HOST.getValue() +
            ":" + MYSQL_PORT.getValue() +
            "/" + MYSQL_DATABASE.getValue() +
            "?sslMode=DISABLED";
    }
}

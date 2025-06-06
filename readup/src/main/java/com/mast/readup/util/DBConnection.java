package com.mast.readup.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "mysql://avnadmin:AVNS_wUp0llg0g_asX1Hn0Z7@mysql-24417a7d-bookupdb.d.aivencloud.com:27233/defaultdb?ssl-mode=REQUIRED";
    private static final String USER = "avnadmin";
    private static final String PASSWORD = "AVNS_wUp0llg0g_asX1Hn0Z7";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

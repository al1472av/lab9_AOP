package com.company;

import java.sql.*;

public class DataBaseClient {

    private String dataBasePath;
    public Connection connection;

    public DataBaseClient(String dataBasePath) {
        this.dataBasePath = dataBasePath;

    }

    public ResultSet selectColumn(String columnName, String tabelName) {
        String command = "SELECT " + columnName + " FROM " + tabelName;

        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(command);

        } catch (
                SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void connect() {
        try {
            String url = "jdbc:sqlite:" + dataBasePath;
            connection = DriverManager.getConnection(url);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}

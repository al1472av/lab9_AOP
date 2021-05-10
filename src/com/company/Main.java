package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.sql.*;
import java.util.List;
import java.util.Vector;

public class Main {

    private static Vector<String> columnsHeader = new Vector<String>();
    private static Vector<Vector<Object>> tableData = new Vector<Vector<Object>>();
    private static DataBaseClient client;
    private static Connection connection;

    public static void main(String[] args) throws SQLException {

        connectAndLoadDataBase();

        JFrame mainFrame = new JFrame("Lab 8");
        mainFrame.setSize(1000, 600);
        mainFrame.setLayout(null);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JTable table = new JTable(tableData, columnsHeader);
        table.setBounds(50, 25, 900, 300);
        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        JButton addButton = new JButton("Добавить");
        addButton.setBounds(380, 500, 100, 80);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((DefaultTableModel) table.getModel()).addRow(new Object[3]);
            }
        });

        JButton removeButton = new JButton("Удалить");
        removeButton.setBounds(620, 500, 100, 80);
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((DefaultTableModel) table.getModel()).removeRow(table.getSelectedRow());
            }
        });

        JButton saveButton = new JButton("Сохрнаить");
        saveButton.setBounds(500, 500, 100, 80);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveTable(((DefaultTableModel) table.getModel()));
            }
        });


        JScrollPane tableScrollPanel = new JScrollPane(table);
        tableScrollPanel.setBounds(950, 25, 50, 300);
        tableScrollPanel.createVerticalScrollBar();
        mainFrame.add(saveButton);
        mainFrame.add(removeButton);
        mainFrame.add(tableScrollPanel);
        mainFrame.add(addButton);
        mainFrame.add(table);
        mainFrame.setVisible(true);

    }

    private static void saveTable(DefaultTableModel tableModel) {
        boolean isValid = false;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                Integer.parseInt((String) tableModel.getValueAt(i, 0));
                isValid = true;
            } catch (NumberFormatException ex) {
                System.out.println("Expected type: INT");
            }

            try {
                String.valueOf(tableModel.getValueAt(i, 1));
                isValid = true;

            } catch (NumberFormatException ex) {
                System.out.println("Expected type: String");
            }

            try {
                Integer.parseInt((String) tableModel.getValueAt(i, 2));
                isValid = true;

            } catch (NumberFormatException ex) {
                System.out.println("Expected type: INT");
            }
        }

        if (isValid) {
            client.connect();

            String sql = "DELETE FROM lab8";

            try (PreparedStatement delete = client.connection.prepareStatement(sql)) {
                delete.executeUpdate();

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String insertCommand = "INSERT INTO lab8(ID,Name,Price) VALUES(?,?,?)";

                try (PreparedStatement insert = client.connection.prepareStatement(insertCommand)) {
                    insert.setInt(1, Integer.parseInt((String) tableModel.getValueAt(i, 0)));
                    insert.setString(2, (String) tableModel.getValueAt(i, 1));
                    insert.setInt(3, Integer.parseInt((String) tableModel.getValueAt(i, 2)));
                    insert.executeUpdate();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }


    private static void connectAndLoadDataBase() throws SQLException {
        client = new DataBaseClient("data.db");
        client.connect();
        ResultSet rs = client.selectColumn("*", "lab8");
        initializeTableData(rs);
    }

    private static void initializeTableData(ResultSet rs) throws SQLException {


        columnsHeader.add("ID");
        columnsHeader.add("Наименование товара");
        columnsHeader.add("Цена");
        while (rs.next()) {
            tableData.add(new Vector<Object>());
            tableData.lastElement().add(rs.getString("ID"));
            tableData.lastElement().add(rs.getString("Name"));
            tableData.lastElement().add(rs.getString("Price"));
        }

    }
}

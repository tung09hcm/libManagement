package com.example.librarymanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class mysqlconnect {
    Connection conn = null;
    public static Connection ConnectDb()
    {
        try
        {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc","root", "401190");
            return conn;
        }catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    public static ObservableList<book> getDatabook()
    {
        Connection conn = ConnectDb();
        ObservableList<book> list = FXCollections.observableArrayList();
        try
        {
            PreparedStatement statement = conn.prepareStatement("select * from `jdbc`.`book`");
            ResultSet rs = statement.executeQuery();

            while(rs.next())
            {
                list.add(new book(Integer.parseInt(rs.getString("idbook")),
                        Integer.parseInt(rs.getString("amount")),
                        Integer.parseInt(rs.getString("request")),
                        Integer.parseInt(rs.getString("count")),
                        rs.getString("name")
                        ));

            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }
    public static ObservableList<rent> getRentlist()
    {
        Connection conn = ConnectDb();
        ObservableList<rent> list = FXCollections.observableArrayList();
        try
        {
            PreparedStatement statement = conn.prepareStatement("select * from `jdbc`.`rent`");
            ResultSet rs = statement.executeQuery();

            while(rs.next())
            {
                list.add(new rent(rs.getString("studentname"),
                        rs.getString("bookname"),
                        rs.getString("phonenumber"),
                        rs.getDate("returndate"),
                        Integer.parseInt(rs.getString("status")),
                        Integer.parseInt(rs.getString("id"))

                        ));

            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }
}

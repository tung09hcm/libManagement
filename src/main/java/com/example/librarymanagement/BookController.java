package com.example.librarymanagement;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class BookController implements Initializable {
    @FXML
    private TableColumn<book, Integer> amount;

    @FXML
    private TableColumn<book, Integer> count;

    @FXML
    private TableColumn<book, Integer> id;

    @FXML
    private TableColumn<book, String> name;

    @FXML
    private TableColumn<book, Integer> request;

    @FXML
    private TextField searchbar;

    @FXML
    private TableView<book> table;

    @FXML
    private Text totalamount;

    @FXML
    private TextField txt_amount;

    @FXML
    private TextField txt_name;
    @FXML
    private Button lendbook;
    @FXML
    private TextField txt_request;
    private Stage stage;
    private Scene scene;
    private Parent root;
    ObservableList<book> listM;
    ObservableList<book> datalist;
    int index = -1;

    Connection conn = null;
    ResultSet rs = null;
    public void getTotalBooks() {
        // Khởi tạo biến lưu tổng số lượng sách
        int totalBooks = 0;

        // Tạo kết nối đến cơ sở dữ liệu
        try {
            conn = mysqlconnect.ConnectDb();
            PreparedStatement stmt = conn.prepareStatement("SELECT SUM(amount) AS TongSoLuong FROM jdbc.book");
            ResultSet rs = stmt.executeQuery();
            // Nếu câu lệnh truy vấn trả về kết quả
            if (rs.next()) {
                // Lấy tổng số lượng sách từ kết quả truy vấn
                totalBooks = rs.getInt("TongSoLuong");
                totalamount.setText(Integer.toString(totalBooks));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Xử lý lỗi
        }
    }
    public void Add() throws SQLException {
        String name = txt_name.getText();
        String amount= txt_amount.getText();

        Connection conn = mysqlconnect.ConnectDb();
        Statement statement = conn.createStatement();
        statement.executeUpdate("insert into `jdbc`.`book` (name,amount,request,count) values ('"+name+"', '"+amount+"', 0, 0)");


        Update();
        Search_book();
    }
    public void Delete()
    {
        try
        {
            int idd = table.getSelectionModel().getSelectedIndex();
            String iddde = id.getCellData(index).toString();
            conn = mysqlconnect.ConnectDb();
            Statement statement = conn.createStatement();
            System.out.println("idd: " + idd);
            statement.executeUpdate("delete from `jdbc`.`book` where idbook = '"+iddde+"'");
            Update();
            Search_book();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void Search_book() {
        id.setCellValueFactory(new PropertyValueFactory<book,Integer>("id"));
        name.setCellValueFactory(new PropertyValueFactory<book,String>("name"));
        amount.setCellValueFactory(new PropertyValueFactory<book,Integer>("amount"));
        request.setCellValueFactory(new PropertyValueFactory<book,Integer>("request"));
        count.setCellValueFactory(new PropertyValueFactory<book,Integer>("count"));


        datalist= mysqlconnect.getDatabook();
        table.setItems(datalist);

        FilteredList<book> filteredData = new FilteredList<>(datalist, b->true);
        searchbar.textProperty().addListener((observable,oldValue,newValue) ->
        {
            filteredData.setPredicate(person ->
            {
                if(newValue == null || newValue.isEmpty())
                {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if(person.getName().toLowerCase().indexOf(lowerCaseFilter) != -1)
                {
                    return true;
                }
                else {
                    return false;
                }

            });

        });
        SortedList<book> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);
    }
    public void Update() {
        id.setCellValueFactory(new PropertyValueFactory<book,Integer>("id"));
        name.setCellValueFactory(new PropertyValueFactory<book,String>("name"));
        amount.setCellValueFactory(new PropertyValueFactory<book,Integer>("amount"));
        request.setCellValueFactory(new PropertyValueFactory<book,Integer>("request"));
        count.setCellValueFactory(new PropertyValueFactory<book,Integer>("count"));

        txt_amount.setText("");
        txt_name.setText("");
        txt_request.setText("");

        listM = mysqlconnect.getDatabook();
        table.setItems(listM);
        getTotalBooks();
    }
    public void getSelected(javafx.scene.input.MouseEvent mouseEvent) {
        index = table.getSelectionModel().getSelectedIndex();
        System.out.println("index: " + index);
        if(index <= -1)
        {
            txt_amount.setText("");
            txt_name.setText("");
            txt_request.setText("");
            return;
        }
        txt_name.setText(name.getCellData(index).toString());
        txt_amount.setText(amount.getCellData(index).toString());
        txt_request.setText(request.getCellData(index).toString());
    }
    public void edit(){
        try
        {
            int idd = index+1;
            conn = mysqlconnect.ConnectDb();
            String value1 = txt_name.getText();
            String value2 = txt_amount.getText();
            String value3 = txt_request.getText();
            Statement statement = conn.createStatement();
            statement.executeUpdate("update `jdbc`.`book` set name = '"+value1+"', " +
                    "amount = '"+value2+"', request = '"+value3+"' where idbook = '"+idd+"'");
            Update();
            Search_book();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Update();
        Search_book();
    }

    public void LendBook(javafx.event.ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("lendpage.fxml"));
        root = loader.load();

        // HomePageController homePageController =loader.getController();
        RentController rentController = loader.getController();
        // Lấy Stage hiện tại từ sự kiện ActionEvent
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        // Tạo Scene mới với root và đặt nó vào Stage
        scene = new Scene(root);
        stage.setScene(scene);
    }
}

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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RentController implements Initializable {

    @FXML
    private TableColumn<rent, String> bookname;

    @FXML
    private TableColumn<rent, String> phonenumber;

    @FXML
    private TableColumn<rent, java.sql.Date> returndate;

    @FXML
    private TextField searchbar;

    @FXML
    private TableColumn<rent, Integer> status;

    @FXML
    private TableColumn<rent, String> studentname;

    @FXML
    private TableView<rent> table;

    @FXML
    private Text totalamount;

    @FXML
    private TextField txt_book_name;

    @FXML
    private TextField txt_phone_number;

    @FXML
    private DatePicker txt_return_date;

    @FXML
    private TextField txt_student_name;

    private Stage stage;
    private Scene scene;
    private Parent root;
    ObservableList<rent> listM;
    ObservableList<rent> datalist;
    int index = -1;

    Connection conn = null;
    ResultSet rs = null;

    public void getTotalRent() {
        // Khởi tạo biến lưu tổng số lượng sách
        int totalRent = 0;

        // Tạo kết nối đến cơ sở dữ liệu
        try {
            conn = mysqlconnect.ConnectDb();
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS total_rows FROM jdbc.rent");
            ResultSet rs = stmt.executeQuery();
            // Nếu câu lệnh truy vấn trả về kết quả
            if (rs.next()) {
                // Lấy tổng số lượng sách từ kết quả truy vấn
                totalRent = rs.getInt("total_rows");
                totalamount.setText(Integer.toString(totalRent));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Xử lý lỗi
        }
    }
    public void Update() {
        studentname.setCellValueFactory(new PropertyValueFactory<rent,String>("studentname"));
        bookname.setCellValueFactory(new PropertyValueFactory<rent,String>("bookname"));
        phonenumber.setCellValueFactory(new PropertyValueFactory<rent,String>("phonenumber"));
        returndate.setCellValueFactory(new PropertyValueFactory<rent,java.sql.Date>("returndate"));
        status.setCellValueFactory(new PropertyValueFactory<rent,Integer>("status"));



        txt_book_name.setText("");
        txt_phone_number.setText("");
        txt_return_date.setValue(null);
        txt_student_name.setText("");


        table.setItems(listM);
        getTotalRent();
    }
    public void Search_rent() {
        studentname.setCellValueFactory(new PropertyValueFactory<rent,String>("studentname"));
        bookname.setCellValueFactory(new PropertyValueFactory<rent,String>("bookname"));
        phonenumber.setCellValueFactory(new PropertyValueFactory<rent,String>("phonenumber"));
        returndate.setCellValueFactory(new PropertyValueFactory<rent, java.sql.Date>("returndate"));
        status.setCellValueFactory(new PropertyValueFactory<rent,Integer>("status"));

        datalist= mysqlconnect.getRentlist();
        table.setItems(datalist);

        FilteredList<rent> filteredData = new FilteredList<>(datalist, b->true);
        searchbar.textProperty().addListener((observable,oldValue,newValue) ->
        {
            filteredData.setPredicate(person ->
            {
                if(newValue == null || newValue.isEmpty())
                {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if(person.getStudentname().toLowerCase().indexOf(lowerCaseFilter) != -1)
                {
                    return true;
                }
                else if(person.getBookname().toLowerCase().indexOf(lowerCaseFilter) != -1)
                {
                    return true;
                }
                else {
                    return false;
                }

            });

        });
        SortedList<rent> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);
    }
    public void getSelected(javafx.scene.input.MouseEvent mouseEvent) {
        index = table.getSelectionModel().getSelectedIndex();
        System.out.println("index: " + index);
        if(index <= -1)
        {
            txt_book_name.setText("");
            txt_student_name.setText("");
            txt_return_date.setValue(null);
            txt_phone_number.setText("");
            return;
        }
        txt_return_date.setValue(returndate.getCellData(index).toLocalDate());
        txt_phone_number.setText(phonenumber.getCellData(index).toString());
        txt_book_name.setText(bookname.getCellData(index).toString());
        txt_student_name.setText(studentname.getCellData(index).toString());
    }
    public void Add() throws SQLException {
        String studentname = txt_student_name.getText().trim();
        String bookname= txt_book_name.getText().trim();
        String phonenumber = txt_phone_number.getText().trim();
        LocalDate returndate = txt_return_date.getValue();
        if(returndate.isBefore(LocalDate.now()) || returndate.isEqual(LocalDate.now()))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Ngày trả không hợp lệ");
            alert.setContentText("Ngày trả không được phép ở quá khứ.");
            alert.showAndWait();
            return;
        }

        System.out.println("flag1");
        java.sql.Date sqlReturnDate = java.sql.Date.valueOf(returndate);

        Connection conn = mysqlconnect.ConnectDb();
        String checkBookQuery = "SELECT * FROM jdbc.book WHERE name = ?";
        PreparedStatement checkBookStatement = conn.prepareStatement(checkBookQuery);
        checkBookStatement.setString(1, bookname);
        ResultSet resultSet = checkBookStatement.executeQuery();
        System.out.println("flag2");
        PreparedStatement checkAmountStatement = conn.prepareStatement("SELECT amount FROM jdbc.book WHERE name = ?");
        checkAmountStatement.setString(1, bookname);
        ResultSet amountResultSet = checkAmountStatement.executeQuery();
        System.out.println("flag3");
        if (!resultSet.next()) {
            // Nếu không tìm thấy sách trong bảng book, hiển thị thông báo và không thực hiện thêm dữ liệu
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Sách không tồn tại");
            alert.setContentText("Sách bạn muốn mượn không có trong thư viện. Vui lòng kiểm tra lại.");
            alert.showAndWait();

            System.out.println("không tìm thấy sách trong book");
        }
        else if (amountResultSet.next()) {
            int availableAmount = amountResultSet.getInt("amount");
            if(availableAmount <= 0)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText("Sách không đủ số lượng");
                alert.setContentText("Sách bạn muốn mượn không có trong thư viện. Vui lòng kiểm tra lại.");
                alert.showAndWait();
            }
            else {
                // Nếu tìm thấy sách trong bảng book, tiếp tục thêm dữ liệu vào bảng rent
                System.out.println("Add entrance");
                Statement statement = conn.createStatement();
                statement.executeUpdate("INSERT INTO jdbc.rent " +
                        "(studentname, bookname, phonenumber, returndate, status) " +
                        "VALUES ('" + studentname + "', '" + bookname + "', '" + phonenumber + "', '" + sqlReturnDate + "', 0)");
                Statement statement1 = conn.createStatement();
                statement1.executeUpdate("UPDATE jdbc.book SET amount = amount - 1, request = request + 1, count = count + 1 WHERE name = '"+bookname+"'");


                Update();
                Search_rent();
            }

        }

    }
    public void Delete() {
        try
        {
            conn = mysqlconnect.ConnectDb();
            Statement statement = conn.createStatement();
            String val = txt_book_name.getText();
            System.out.println("bookname: " + bookname);
            statement.executeUpdate("delete from jdbc.rent where bookname = '"+val+"'");

            Statement statement1 = conn.createStatement();
            statement1.executeUpdate("UPDATE jdbc.book SET amount = amount + 1, request = request - 1 WHERE name = '"+val+"'");
            Update();
            Search_rent();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void AddBook(javafx.event.ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("homepage.fxml"));
        root = loader.load();

        // HomePageController homePageController =loader.getController();
        BookController bookController = loader.getController();
        // Lấy Stage hiện tại từ sự kiện ActionEvent
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        // Tạo Scene mới với root và đặt nó vào Stage
        scene = new Scene(root);
        stage.setScene(scene);
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LocalDate currentDate = LocalDate.now();
        java.sql.Date sqlDate = java.sql.Date.valueOf(currentDate);
        try
        {
            Connection conn = mysqlconnect.ConnectDb();
            Statement statement = conn.createStatement();

            statement.executeUpdate("UPDATE jdbc.rent " +
                    "SET status = 1 " +
                    "WHERE returndate < '" + sqlDate + "'");
            conn.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Update();
        Search_rent();
    }
}

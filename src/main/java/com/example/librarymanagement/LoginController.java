package com.example.librarymanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    TextField username;
    @FXML
    PasswordField password;
    @FXML
    Label notify;
    private Stage stage;
    private Scene scene;
    private Parent root;


    public void login(ActionEvent event) throws IOException {
        if (username.getText().trim().equals("admin") && password.getText().trim().equals("123456")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("homepage.fxml"));
            root = loader.load();

            // HomePageController homePageController =loader.getController();
            BookController bookController = loader.getController();
            // Lấy Stage hiện tại từ sự kiện ActionEvent
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Tạo Scene mới với root và đặt nó vào Stage
            scene = new Scene(root);
            stage.setScene(scene);
        } else {
            notify.setText("Login Failed");
        }
    }


}
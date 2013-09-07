package org.freecode.gmusic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import gmusic.api.impl.InvalidCredentialsException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class LoginBoxController implements Initializable {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    @FXML
    private CheckBox remember;

    @FXML
    void onButtonClick(ActionEvent event) {
        String username = this.username.getText();
        String password = this.password.getText();
        if (remember.isSelected()) {
            //save the username and password here
            try {
                FileWriter writer = new FileWriter(new File(GMusicDesktop.getAppDataDir(), "auth"));
                writer.write(username + "\n" + password);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            GMusicGui gui = new GMusicGui();
            gui.setPassword(password);
            gui.setUsername(username);
            gui.init();
            gui.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        this.username.getScene().getWindow().hide();

    }


    @FXML
    void onButtonClick2(ActionEvent event) {
        Platform.exit();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resources = resourceBundle;
        this.location = url;
    }
}

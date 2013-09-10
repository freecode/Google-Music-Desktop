package org.freecode.gmusic;

import java.io.*;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
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

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


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
            String userpass = username + "\n" + password;
            try {
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                KeySpec spec = new PBEKeySpec(System.getProperty("user.name").toCharArray(),
                        new byte[]{11, 12, 16, 55, 43, 33, 86, 89}, 65536, 128);
                SecretKey tmp = factory.generateSecret(spec);
                SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                byte[] in = userpass.getBytes("UTF8");
                cipher.init(Cipher.ENCRYPT_MODE, secret);
                byte[] out = cipher.doFinal(in);
                DataOutputStream dOut = new DataOutputStream(new FileOutputStream(new File(GMusicDesktop.getAppDataDir(), "auth2")));
                dOut.write(out, 0, out.length);
                dOut.close();
            } catch (Exception e) {
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

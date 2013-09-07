package org.freecode.gmusic;

import gmusic.api.comm.ApacheConnector;
import gmusic.api.comm.JSON;
import gmusic.api.impl.GoogleMusicAPI;
import gmusic.api.impl.GoogleSkyJamAPI;
import gmusic.api.impl.InvalidCredentialsException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created with IntelliJ IDEA.
 * User: shivam
 * Date: 9/7/13
 * Time: 12:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class GMusicGui extends Application {
    private GoogleMusicAPI musicApi;
    private GoogleSkyJamAPI allApi;
    private String username, password;

    @Override
    public void init() throws InvalidCredentialsException, IOException, URISyntaxException {
        if (username == null)
            username = getParameters().getUnnamed().get(0);
        if (password == null)
            password = getParameters().getUnnamed().get(1);
        System.out.println(username + " " + password);
        musicApi = new GoogleMusicAPI(new ApacheConnector(), new JSON(), new File("."));
        allApi = new GoogleSkyJamAPI(new ApacheConnector(), new JSON(), new File("."));
        musicApi.login(username, password);
        allApi.login(username, password);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void start(Stage stage) throws Exception {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/main-ui.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        stage.setTitle("GMusicDesktop");
        stage.setScene(scene);
        stage.show();
    }
}

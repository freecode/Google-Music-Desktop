package org.freecode.gmusic;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

/**
 * Created with IntelliJ IDEA.
 * User: shivam
 * Date: 9/7/13
 * Time: 1:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainUIController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label album;

    @FXML
    private ListView<?> albumList;

    @FXML
    private Label artist;

    @FXML
    private ListView<?> artistList;

    @FXML
    private ListView<?> songList;

    @FXML
    private Label title;


    @FXML
    void albumMouseClicked(MouseEvent event) {
    }

    @FXML
    void artistMouseClicked(MouseEvent event) {
    }

    @FXML
    void songMouseClicked(MouseEvent event) {
    }

    @FXML
    void initialize() {
        assert getAlbum() != null : "fx:id=\"album\" was not injected: check your FXML file 'main-ui.fxml'.";
        assert getAlbumList() != null : "fx:id=\"albumList\" was not injected: check your FXML file 'main-ui.fxml'.";
        assert getArtist() != null : "fx:id=\"artist\" was not injected: check your FXML file 'main-ui.fxml'.";
        assert getArtistList() != null : "fx:id=\"artistList\" was not injected: check your FXML file 'main-ui.fxml'.";
        assert getSongList() != null : "fx:id=\"songList\" was not injected: check your FXML file 'main-ui.fxml'.";
        assert getTitle() != null : "fx:id=\"title\" was not injected: check your FXML file 'main-ui.fxml'.";


    }


    public Label getAlbum() {
        return album;
    }

    public ListView<?> getAlbumList() {
        return albumList;
    }

    public Label getArtist() {
        return artist;
    }

    public ListView<?> getArtistList() {
        return artistList;
    }

    public ListView<?> getSongList() {
        return songList;
    }

    public Label getTitle() {
        return title;
    }
}


package org.freecode.gmusic;

import gmusic.api.comm.ApacheConnector;
import gmusic.api.comm.JSON;
import gmusic.api.impl.GoogleMusicAPI;
import gmusic.api.impl.GoogleSkyJamAPI;
import gmusic.api.impl.InvalidCredentialsException;
import gmusic.api.model.Song;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: shivam
 * Date: 9/7/13
 * Time: 12:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class GMusicGui extends Application implements ChangeListener {
	private GoogleMusicAPI musicApi;
	private GoogleSkyJamAPI allApi;
	private String username, password;
	private Parent root;
	private HashMap<String, Song> songMap;
	private SongPlayer songPlayer;
	private Executor executor;

	private static final ObservableList EMPTY_LIST = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList());

	@Override
	public void init() throws InvalidCredentialsException, IOException, URISyntaxException {
		if (getParameters() != null) {
			List<String> unnamed = getParameters().getUnnamed();
			if (username == null) {
				username = unnamed.get(0);
			}
			if (password == null) {
				password = unnamed.get(1);
			}
		}
		musicApi = new GoogleMusicAPI(new ApacheConnector(), new JSON(), new File("."));
		allApi = new GoogleSkyJamAPI(new ApacheConnector(), new JSON(), new File("."));
		musicApi.login(username, password);

		allApi.login(username, password);
		songMap = new HashMap<String, Song>();
		executor = Executors.newSingleThreadExecutor();
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-ui.fxml"));
		try {
			root = (Parent) loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(root);
		songPlayer = new SongPlayer(root);
		MainUIController controller = loader.getController();
		controller.setSongPlayer(songPlayer);
		stage.setTitle("GMusicDesktop");
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				System.exit(0);
			}
		});
		Collection<Song> songs = allApi.getAllSongs();
		final ListView artistView = (ListView) root.lookup("#artistList");
		ObservableList<String> artists = FXCollections.observableArrayList();
		for (Song s : songs) {
			String album = s.getAlbum();
			String title = s.getTitle();
			String artist = s.getAlbumArtist();
			songMap.put(artist + album + title, s);
			if (!artists.contains(artist))
				artists.add(artist);
		}
		Collections.sort(artists);
		artistView.setItems(artists);
		artistView.getSelectionModel().selectedItemProperty().addListener(this);
		final ListView albumView = (ListView) root.lookup("#albumList");
		final ListView songView = (ListView) root.lookup("#songList");
		albumView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
			public void changed(ObservableValue observableValue, Object o, Object o2) {
				if (o2 != null) {
					String album = (String) o2;
					String artist = (String) artistView.getSelectionModel().getSelectedItem();
					ObservableList<String> ss = FXCollections.observableArrayList();
					for (Song s : songMap.values()) {
						if (s.getAlbumArtist().equals(artist) && s.getAlbum().equals(album)) {
							ss.add(s.getTitle());
						}
					}
					songView.setItems(ss);
				}
			}
		});
		songView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
			public void changed(ObservableValue observableValue, Object o, Object o2) {
				if (o2 == null)
					return;
				final String title = (String) o2;
				final String album = albumView.getSelectionModel().getSelectedItem().toString();
				final String artist = artistView.getSelectionModel().getSelectedItem().toString();
				executor.execute(new Runnable() {
					@Override
					public void run() {
						Song s = songMap.get(artist + album + title);
						if (s != null) {
							try {
								songPlayer.init(s, allApi.getSongURL(s));
							} catch (URISyntaxException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							songPlayer.player().play();
							System.out.println(songPlayer.player().getStatus().toString());
						}
					}
				});

			}
		});

	}

	public void changed(ObservableValue observableValue, Object o, Object o2) {
		if (o2 == null)
			return;
		ListView songView = (ListView) root.lookup("#songList");
		ListView albumView = (ListView) root.lookup("#albumList");
		songView.getSelectionModel().clearSelection();
		albumView.getSelectionModel().clearSelection();
		songView.setItems(EMPTY_LIST);
		albumView.setItems(EMPTY_LIST);
		String artist = (String) o2;
		ObservableList<String> albums = FXCollections.observableArrayList();
		for (Song s : songMap.values()) {
			if (s.getAlbumArtist().equals(artist) && !albums.contains(s.getAlbum())) {
				albums.add(s.getAlbum());
			}
		}
		Collections.sort(albums);
		albumView.setItems(albums);
	}
}

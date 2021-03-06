package org.freecode.gmusic;

import gmusic.api.model.Song;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: shivam
 * Date: 9/9/13
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class SongPlayer {
	private Parent root;
	private MediaPlayer player;
	private Song song;
	private Media media;
	private volatile boolean update = true;

	public SongPlayer(Parent root) {
		this.root = root;
	}

	public void init(final Song song, URI songURL) {
		if (player() != null) {
			player().stop();
		}
		System.out.println(songURL.toString());
		media = new Media(songURL.toString());
		player = new MediaPlayer(media);
		final Slider slider = (Slider) root.lookup("#slider");
		slider.setMax(song.getDurationMillis());
		player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
			public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration nDur) {
				if (isUpdate())
					slider.setValue(nDur.toMillis());
			}
		});
		this.song = song;
		Platform.runLater(new Runnable() {
			public void run() {
				Label artist = (Label) root.lookup("#artist");
				Label album = (Label) root.lookup("#album");
				Label title = (Label) root.lookup("#title");
				artist.setText(song.getArtist());
				album.setText(song.getAlbum());
				title.setText(song.getTitle());
			}
		});

	}

	public MediaPlayer player() {
		return player;
	}


	public Song song() {
		return song;
	}


	public void setUpdate(boolean update) {
		this.update = update;
	}

	public boolean isUpdate() {
		return update;
	}
}

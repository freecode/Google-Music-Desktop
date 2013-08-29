package org.freecode.gmusic;

import gmusic.api.comm.ApacheConnector;
import gmusic.api.comm.JSON;
import gmusic.api.impl.GoogleMusicAPI;
import gmusic.api.impl.GoogleSkyJamAPI;
import gmusic.api.model.Song;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Collection;

/**
 * @author Shivam Mistry
 */
public class DesktopClient {

    private JFrame frame;
    private GoogleMusicAPI musicApi;
    private GoogleSkyJamAPI allApi;

    public DesktopClient() {
        frame = new JFrame();
        frame.setLayout(new GridLayout(0, 2));
        frame.setPreferredSize(new Dimension(600, 400));
        //TODO: build a proper login dialog, but for now we'll just use JOptionPane
        String username = JOptionPane.showInputDialog("Username:");
        String password = JOptionPane.showInputDialog("Password:");
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            System.out.println("Attempting to login");
            musicApi = new GoogleMusicAPI(new ApacheConnector(), new JSON(), new File("."));
            allApi = new GoogleSkyJamAPI(new ApacheConnector(), new JSON(), new File("."));
            try {
                //ideally we should do these separately, because some people may not be able to access both services
                musicApi.login(username, password);
                allApi.login(username, password);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Invalid credentials.");
                return;
            }
            System.out.println("Login successful");
            try {
                Collection<Song> songs = allApi.getAllSongs();
                for (Song song : songs) {
                    System.out.println(song.getArtistNorm() + " - " + song.getTitleNorm());

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            frame.setVisible(true);
            frame.pack();
        }

    }

    public static void main(String[] args) {
        //  WebLookAndFeel.install();
        new DesktopClient();

    }
}

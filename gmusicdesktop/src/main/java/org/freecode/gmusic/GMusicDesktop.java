package org.freecode.gmusic;

import javafx.application.Application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: shivam
 * Date: 9/7/13
 * Time: 12:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class GMusicDesktop {

    private static File appData;

    public static void main(String[] args) {
        createDirs();
        boolean login = true;
        String pass = null, user = null;
        try {
            FileReader read = new FileReader(new File(appData, "auth"));
            BufferedReader reader = new BufferedReader(read);
            user = reader.readLine();
            pass = reader.readLine();
            reader.close();
            login = user == null || pass == null;
        } catch (IOException ignored) {
        }
        if (login) {
            Application.launch(LoginClient.class);
        } else {
            Application.launch(GMusicGui.class, user, pass);
        }
    }

    public static File getAppDataDir() {
        return appData;
    }

    private static void createDirs() {
        String appDir, dirName;
        //lets do it properly on windows
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            appDir = System.getenv("APPDATA");
            dirName = "gmusicdesktop";
        } else {
            appDir = System.getProperty("user.home");
            dirName = ".gmusicdesktop";
        }
        File dir = new File(appDir, dirName);
        if (!dir.exists()) {
            dir.mkdir();
        }
        appData = dir;
    }
}

package org.freecode.gmusic;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.KeyFactory;
import java.security.spec.KeySpec;

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
        login(true, 0);
    }

    private static void login(boolean remember, int count) {
        String pass = null, user = null;
        File file = new File(appData, "auth2");

        if (file.exists() && remember) {
            try {
                byte[] bytes = readFile(file);
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                KeySpec spec = new PBEKeySpec(System.getProperty("user.name").toCharArray(),
                        new byte[]{11, 12, 16, 55, 43, 33, 86, 89}, 65536, 128);
                SecretKey tmp = factory.generateSecret(spec);
                SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, secret);
                byte[] text = cipher.doFinal(bytes);
                String userpass = new String(text, "UTF8");
                user = userpass.split("\n")[0];
                pass = userpass.split("\n")[1];
            } catch (Exception ignored) {
                ignored.printStackTrace();
                login(false, count);
                return;
            }
            try {
                if (count == 0) {
                    Application.launch(GMusicGui.class, user, pass);
                } else {
                    GMusicGui gui = new GMusicGui();
                    gui.setUsername(user);
                    gui.setPassword(pass);
                    gui.init();
                    gui.start(new Stage());
                }
            } catch (Exception e) {
                login(false, ++count);
            }
        } else {
            if (count == 0) {
                Application.launch(LoginClient.class);
            } else {
                Platform.runLater(new Runnable() {
                    public void run() {
                        try {
                            LoginClient client = new LoginClient();
                            client.init();
                            client.start(new Stage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }
    }

    private static byte[] readFile(File file) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int read;
        byte[] buffer = new byte[1024];
        while ((read = in.read(buffer)) != -1) {
            bos.write(buffer, 0, read);
        }
        in.close();
        return bos.toByteArray();
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

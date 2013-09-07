package org.freecode.gmusic;

import javafx.application.Application;

/**
 * Created with IntelliJ IDEA.
 * User: shivam
 * Date: 9/7/13
 * Time: 12:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class GMusicDesktop {

    public static void main(String[] args) {
        //TODO: check if login details are stored, and use them first
        //TODO: if login details failed, launch the login client
        if (true) {
            Application.launch(LoginClient.class);
        } else {
            Application.launch(GMusicGui.class);
        }
    }
}

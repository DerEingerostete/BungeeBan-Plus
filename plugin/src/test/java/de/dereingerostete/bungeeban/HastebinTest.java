package de.dereingerostete.bungeeban;

import de.dereingerostete.bungeeban.chatlog.HastebinAPI;

import java.io.IOException;

public class HastebinTest {

    public static void main(String[] args) {
        try {
            HastebinAPI api = new HastebinAPI("https://hastebin.com/");
            String url = api.post("Test Content with Umlaute: äüö");
            System.out.println("URL: " + url);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}

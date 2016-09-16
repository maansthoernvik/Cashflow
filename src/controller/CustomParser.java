package controller;

import model.input.Regex;
import model.time.TimeTracking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Måns on 9/16/2016.
 *
 *
 */

public class CustomParser {

    private static final String CONFIG_PATH = "config.txt";

    public static void parseConfig() {
        long lastSession = 0;

        // Try to open the config file to read settings.
        try (BufferedReader br = new BufferedReader(new FileReader(CONFIG_PATH))) {
            String readLine;    // String to read lines with.

            // While the lines read are not empty:
            while ((readLine = br.readLine()) != null) {
                // if the line read matches the LAST_SESSION regex, put it in lastSession.
                if (readLine.matches(Regex.LAST_SESSION.getRegex())) {
                    lastSession = Long.parseLong(readLine);
                    System.out.println("Last session: " + lastSession);
                }
            }

            // New last session of config is the current date.
            List<String> lines = Arrays.asList("LastSession", Long.toString(TimeTracking.getCurrentDate()));
            Path file = Paths.get("config.txt");
            // Write to txt.
            Files.write(file, lines, Charset.forName("UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();

            // If some shit went down, the last session txt is created again or for the first time.
            List<String> lines = Arrays.asList("LastSession", Long.toString(TimeTracking.getCurrentDate()));
            Path file = Paths.get("config.txt");

            try {
                Files.write(file, lines, Charset.forName("UTF-8"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        TimeTracking.setLastSession(lastSession);   // Set the lastSession value in TimeTracking to config value.
    }
}

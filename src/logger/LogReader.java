package logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javafx.application.Platform;

public class LogReader implements Runnable {

    private InputStream inputStream;
    private String buffer;

    public LogReader(InputStream inStream) {
        this.inputStream = inStream;
    }

    @Override
    public void run() {
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
        try {
            while ((buffer = stdInput.readLine()) != null) {
                Platform.runLater(() -> System.out.println(buffer));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

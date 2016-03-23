package logger;

import java.io.IOException;
import java.io.OutputStream;

import javafx.scene.control.TextArea;

public class LogWriter extends OutputStream {

    private TextArea output;

    public LogWriter(TextArea ta) {
        output = ta;
    }

    @Override
    public void write(int b) throws IOException {
        output.appendText(String.valueOf((char) b));
    }

}

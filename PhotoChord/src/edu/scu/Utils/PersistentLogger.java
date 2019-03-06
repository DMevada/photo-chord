package edu.scu.Utils;

import java.io.FileWriter;
import java.io.IOException;

public class PersistentLogger {
    PersistentLogger _logger = new PersistentLogger();
    FileWriter _log = new FileWriter("log.txt");

    private PersistentLogger() throws IOException {

    }

    PersistentLogger getInstance() {
        return _logger;
    }

    public void logE(String s) throws IOException {
        _log.write("Error: " + s);
    }

    public void logD(String s) throws IOException {
        _log.write("Debug: " + s);
    }

    public void finish() throws IOException {
        _log.close();
    }
}

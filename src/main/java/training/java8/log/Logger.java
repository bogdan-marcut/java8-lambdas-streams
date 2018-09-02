package training.java8.log;

import java.io.PrintStream;

public class Logger {

    public static final int DEBUG = 0;
    public static final int INFO = 1;
    public static final int WARN = 2;
    public static final int ERROR = 3;
    private int level;
    private PrintStream out;

    public Logger(int level, PrintStream out) {
        this.level = level;
        this.out = out;
    }

    public void debug(String log) {
        if (isDebugEnabled()) {
            out.println("DEBUG: " + log);
        }
    }

    public boolean isDebugEnabled() {
        return level <= DEBUG;
    }

    public void info(String log) {
        if (isInfoEnabled()) {
            out.println("INFO: " + log);
        }
    }

    public boolean isInfoEnabled() {
        return level <= INFO;
    }

    public void warn(String log) {
        if (isWarningEnabled()) {
            out.println("WARN: " + log);
        }
    }

    public boolean isWarningEnabled() {
        return level <= WARN;
    }

    public void error(String log) {
        if (isErrorEnabled()) {
            out.println("ERROR: " + log);
        }
    }

    public boolean isErrorEnabled() {
        return level <= ERROR;
    }
}

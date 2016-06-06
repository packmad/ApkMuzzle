package it.saonzo.apkmuzzle;

public class JarOutput implements it.saonzo.rmperm.IOutput {
    private final Level level;
    private final StringBuilder errors = new StringBuilder();
    private final StringBuilder messages = new StringBuilder();

    public String getErrors() {
        return errors.toString();
    }

    public String getMessages() {
        return messages.toString();
    }

    public JarOutput(Level level) {
        this.level = level;
    }

    @Override
    public void printf(Level msgLevel, String format, Object... args) {
        String strPrint = String.format(format, args);
        if (msgLevel == it.saonzo.rmperm.IOutput.Level.ERROR)
            errors.append(strPrint);
        else if (msgLevel.priority >= this.level.priority)
            messages.append(strPrint);
    }
    
}

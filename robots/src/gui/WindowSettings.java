package gui;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

class WindowSettings{
    private static final String CONFIG_FILE = ".robot_settings.ser";
    private static WindowSettings instance;
    private Map<String, WindowState> states;
    private WindowSettings(){
        states = new HashMap<String, WindowState>();
    }
    public static WindowSettings getInstance(){
        if (instance == null) {
            instance = loadFromFile();
        }
        return instance;
    }
    public void SaveWindowState(String windowID, Rectangle dimensions, Boolean isFolded){
        WindowState currentState = new WindowState(dimensions, isFolded);
        states.put(windowID, currentState);
    }
    public WindowState getStateByID(String windowID){
        return states.get(windowID);
    }
    public void saveToFile() {
        String userHome = System.getProperty("user.home");
        File settingsFile = new File(userHome, CONFIG_FILE);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(settingsFile))) {
            oos.writeObject(states);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static WindowSettings loadFromFile(){
        WindowSettings settings = new WindowSettings();
        String userHome = System.getProperty("user.home");
        File settingsFile = new File(userHome, CONFIG_FILE);
        if (settingsFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(settingsFile))) {
                settings.states = (Map<String, WindowState>) ois.readObject();
            } catch (java.io.IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return settings;
    }

    public static class WindowState implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Rectangle dimensions;
        private final Boolean isFolded;
        public WindowState(Rectangle rec, Boolean fold){
            this.dimensions = rec;
            this.isFolded = fold;
        }

        public Rectangle getDimensions() {
            return dimensions;
        }

        public Boolean getFolded() {
            return isFolded;
        }
    }
}



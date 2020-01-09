package com.kor.admiralty;

import java.io.*;
import java.util.Properties;

public class Configuration {
    private static final String PROPERTY_FILE = "aso.properties";
    private Properties properties = new Properties();
    private static final Configuration INSTANCE = new Configuration();

    private Configuration() {
        readProperties();
    }

    public static String get(Names name) {
        return INSTANCE.properties.getProperty(name.key, name.defaultValue);
    }

    public static void set(Names name, String value) {
        INSTANCE.properties.setProperty(name.key, value);
        INSTANCE.storeProperties();
    }

    public static boolean isDataUpdateEnabled() {
        return Boolean.parseBoolean(get(Names.DATA_UPDATE_ENABLED));
    }

    public static int getDataUpdateInterval() {
        return Integer.parseInt(get(Names.DATA_UPDATE_INTERVAL));
    }

    public static long getDataUpdateLastUpdated() {
        return Long.parseLong(get(Names.DATA_LAST_UPDATED));
    }

    public static void setDataUpdateLastUpdated(long value) {
        set(Names.DATA_LAST_UPDATED, String.valueOf(value));
    }

    public static String getDataUpdateUrl() {
        return get(Names.DATA_UPDATE_URL);
    }

    private void readProperties() {
        try (FileReader reader = new FileReader(PROPERTY_FILE)) {
            properties.load(reader);
        } catch (FileNotFoundException e) {
            createDefaultProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void storeProperties() {
        try (FileWriter writer = new FileWriter(PROPERTY_FILE)) {
            properties.store(writer, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDefaultProperties() {
        for (Names name : Names.values()) {
            properties.setProperty(name.key, name.defaultValue);
        }
        storeProperties();
    }

    public enum Names {
        DATA_UPDATE_URL("data.updateUrl","https://github.com/laurinius/sto-aso/raw/master/%s"),
        DATA_UPDATE_ENABLED("data.updateEnabled","true"),
        DATA_UPDATE_INTERVAL("data.updateIntervalDays","1"),
        DATA_LAST_UPDATED("data.lastUpdated","0");

        private String key;
        private String defaultValue;
        Names(String key, String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }
    }
}

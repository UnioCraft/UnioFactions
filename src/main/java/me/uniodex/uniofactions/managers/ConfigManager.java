package me.uniodex.uniofactions.managers;


import me.uniodex.uniofactions.UnioFactions;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private UnioFactions plugin;
    private Map<String, FileConfiguration> configurations = new HashMap<>();

    public ConfigManager(UnioFactions plugin) {
        this.plugin = plugin;
        registerConfig("data.yml");

        for (String fileName : configurations.keySet()) {
            reloadConfig(fileName);
            configurations.get(fileName).options().copyDefaults(true);
            saveConfig(fileName);
        }
    }

    public FileConfiguration getData() {
        return configurations.get("data.yml");
    }

    public void saveData() {
        saveConfig("data.yml");
    }

    private void registerConfig(String name) {
        configurations.put(name, YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), name)));
    }

    private void reloadConfig(String fileName) {
        InputStream inputStream = plugin.getResource(fileName);
        if (inputStream != null) {
            InputStreamReader reader = new InputStreamReader(inputStream);
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
            configurations.get(fileName).setDefaults(defConfig);
            try {
                reader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveConfig(String fileName) {
        try {
            configurations.get(fileName).save(new File(plugin.getDataFolder(), fileName));
        } catch (IOException ex) {
            Bukkit.getConsoleSender().sendMessage("Couldn't save " + fileName + "!");
        }
    }
}

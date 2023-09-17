package me.zowpy.meetup.utils;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

@Getter
public class ConfigFile {

    private final File file;
    private YamlConfiguration config;

    @SneakyThrows
    public ConfigFile(Plugin plugin, String name) {
        file = new File(plugin.getDataFolder(), name + ".yml");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        if (!file.exists()) {
            file.createNewFile();
            plugin.saveResource(name + ".yml", true);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void set(String path, Object value) { config.set(path, value); }

    public ConfigurationSection getConfigurationSection(String path) {
        return config.getConfigurationSection(path);
    }

    public ConfigurationSection createSection(String path) {
        return config.createSection(path);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    @SneakyThrows
    public void save() {
        config.save(file);
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }
}

package net.letcute.wanted.language;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Language {

    private File languageFile;
    private FileConfiguration data;
    private final JavaPlugin plugin;

    public Language(JavaPlugin plugin, String languageFileName) {
        this.plugin = plugin;
        loadLanguage(languageFileName);
    }

    private void loadLanguage(String languageFileName) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        languageFile = new File(plugin.getDataFolder(), languageFileName + ".yml");

        if (!languageFile.exists()) {
            plugin.saveResource(languageFileName + ".yml", false);
            plugin.getLogger().info("Language file " + languageFileName + ".yml not found. Saving default one.");
        }

        data = YamlConfiguration.loadConfiguration(languageFile);
    }

    public String getString(String key) {
        return data.getString(key, "Missing Key: " + key);
    }

    public void reloadLanguage(String languageFileName) {
        loadLanguage(languageFileName);
    }

    public void saveLanguage() {
        try {
            data.save(languageFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save language file " + languageFile.getName());
            e.printStackTrace();
        }
    }

    public String format(String text) {
        return text != null ? text.replace('&', '§') : text;
    }
}

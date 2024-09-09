package net.letcute.wanted;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;

import lombok.Getter;
import lombok.Setter;
import net.letcute.wanted.Config.Config;
import net.letcute.wanted.command.WantedCommand;
import net.letcute.wanted.hook.WantedPlaceholderAPI;
import net.letcute.wanted.language.Language;
import net.milkbowl.vault.economy.Economy;

public class Wanted extends JavaPlugin {
  private static final Logger LOGGER = Logger.getLogger("wanted");

  @Getter
  @Setter
  private Config data;

  @Getter
  @Setter
  private Language language;

  @Getter
  @Setter
  public static Wanted instance;

  @Getter
  @Setter
  private Database database;

  @Getter
  private Economy economy;

  @Getter
  private final Logger log = Logger.getLogger("Wanted");
  private SimpleCommandMap commandMap;

  @Getter
  private WantedPlaceholderAPI wantedPlaceholderAPI;

  public void onEnable() {
    setInstance(this);
    LOGGER.info("Wanted by LetCute enabled");
    createPluginFolder();
    registerEvents();
    initializeDatabase();
    loadConfig();
    setupEconomy();
    registerCommand(new WantedCommand(this));
    hook();
  }

  public void onDisable() {
    LOGGER.info("Wanted disabled");

  }

  public void hook() {
    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      new WantedPlaceholderAPI(this).register();
      log.info("Enable WantedPlaceholderAPI");
    }
  }

  private void registerEvents() {
    this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
  }

  private void loadConfig() {
    setData(new Config(this));
    getData().loadConfig();
    loadLanguage();
  }

  private void createPluginFolder() {
    File pluginFolder = getDataFolder();

    if (!pluginFolder.exists()) {
      boolean created = pluginFolder.mkdirs();

      if (created) {
        getLogger().info("Plugin folder created successfully.");
      } else {
        getLogger().warning("Failed to create plugin folder.");
      }
    } else {
      getLogger().info("Plugin folder already exists.");
    }
  }

  private void initializeDatabase() {
    setDatabase(new Database(getDataFolder(), "wanted", log));
    getDatabase().openConnection();
    getDatabase().executeUpdate(
        "CREATE TABLE IF NOT EXISTS players (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, money INT)");
  }

  private void loadLanguage() {
    setLanguage(new Language(this, data.getString("file-language")));
  }

  private void registerCommand(@NotNull Command command) {
    try {
      if (commandMap == null) {
        Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        commandMap = (SimpleCommandMap) commandMapField.get(getServer());
      }

      commandMap.register("", command);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    economy = rsp.getProvider();
    return economy != null;
  }

}

package me.rickperix.strictcraft;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main extends JavaPlugin {

    private static final String CURRENT_VERSION = "1.4";
    private static final String SPIGOT_ID = "127094";
    private static final int BSTATS_PLUGIN_ID = 26464;

    private static Main instance;
    private ConfigManager configManager;
    private CommandBlocker commandBlocker;
    private CommandBlockProtector commandBlockProtector;
    private GameModeMonitor gameModeMonitor;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        copyLicenseFile();
        copyReadmeFile();

        configManager = new ConfigManager(this);
        commandBlocker = new CommandBlocker(this);
        commandBlockProtector = new CommandBlockProtector(this);
        gameModeMonitor = new GameModeMonitor(this);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(commandBlocker, this);
        pm.registerEvents(commandBlockProtector, this);
        pm.registerEvents(new GameModeBlocker(this), this);
        pm.registerEvents(new JoinVersionListener(this), this);

        if (configManager.isEnabled() && configManager.isGamemodeEnforcementEnabled()) {
            gameModeMonitor.start();
        }

        PluginCommand reloadCommand = getCommand("strictcraftreload");
        if (reloadCommand != null) {
            reloadCommand.setExecutor(new StrictCraftReloadCommand(this));
        }

        logBanner();
        checkForUpdates();
        new Metrics(this, BSTATS_PLUGIN_ID);
    }

    @Override
    public void onDisable() {
        gameModeMonitor.stop();
    }

    public static Main getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CommandBlocker getCommandBlocker() {
        return commandBlocker;
    }

    public GameModeMonitor getGameModeMonitor() {
        return gameModeMonitor;
    }

    public CommandBlockProtector getCommandBlockProtector() {
        return commandBlockProtector;
    }

    public void rebuildConfigManager() {
        this.configManager = new ConfigManager(this);
    }

    private void copyLicenseFile() {
        File target = new File(getDataFolder(), "LICENSE.txt");
        if (!target.exists()) {
            InputStream resource = getResource("LICENSE.txt");
            if (resource != null) {
                saveResource("LICENSE.txt", false);
                getLogger().info("LICENSE.txt successfully copied to plugin folder.");
            } else {
                getLogger().warning("LICENSE.txt not found inside plugin jar.");
            }
        }
    }

    private void copyReadmeFile() {
        File target = new File(getDataFolder(), "README.txt");
        if (!target.exists()) {
            InputStream resource = getResource("README.txt");
            if (resource != null) {
                saveResource("README.txt", false);
                getLogger().info("README.txt successfully copied to plugin folder.");
            } else {
                getLogger().warning("README.txt not found inside plugin jar.");
            }
        }
    }

    private void logBanner() {
        getLogger().info(" ");
        getLogger().info("  _________ __         .__        __   _________                _____  __   ");
        getLogger().info(" /   _____//  |________|__| _____/  |_ \\_   ___ \\____________ _/ ____\\/  |_ ");
        getLogger().info(" \\_____  \\\\   __\\_  __ \\  |/ ___\\   __\\/    \\  \\/\\_  __ \\__  \\\\   __\\\\   __\\");
        getLogger().info(" /        \\|  |  |  | \\/  \\  \\___|  |  \\     \\____|  | \\/ __ \\|  |   |  |   ");
        getLogger().info("/_______  /|__|  |__|  |__|\\___  >__|   \\______  /|__|  (____  /__|   |__|   ");
        getLogger().info("        \\/                     \\/              \\/            \\/             ");
        getLogger().info("==============================================================================");
        getLogger().info("                                 By RickPerix                                 ");
        getLogger().info("                           Successfully Activated!!                           ");
        getLogger().info("==============================================================================");
        getLogger().info(" ");
    }

    private void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                URL url = new URL("https://api.spiget.org/v2/resources/" + SPIGOT_ID + "/versions/latest");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "StrictCraft/" + CURRENT_VERSION);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(response.toString());
                String latestVersion = (String) obj.get("name");

                if (latestVersion == null || latestVersion.isEmpty()) {
                    getLogger().warning("Could not retrieve latest version info.");
                } else if (!CURRENT_VERSION.equals(latestVersion)) {
                    getLogger().warning("====================================");
                    getLogger().warning(" NEW UPDATE AVAILABLE!");
                    getLogger().warning(" Current version: " + CURRENT_VERSION);
                    getLogger().warning(" Latest version: " + latestVersion);
                    getLogger().warning(" Download: https://spigotmc.org/resources/" + SPIGOT_ID);
                    getLogger().warning("====================================");
                } else {
                    getLogger().info("You're using the latest version!");
                }

            } catch (Exception e) {
                getLogger().warning("Update check failed: " + e.getMessage());
            }
        });
    }
}
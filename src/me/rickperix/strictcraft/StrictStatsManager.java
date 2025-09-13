package me.rickperix.strictcraft;

import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;
import java.util.Map;

public class StrictStatsManager {

    private final Main plugin;

    private final Map<String, Integer> blockedCommandMap = new HashMap<>();
    private int totalBlockedCommands = 0;
    private int blockedGamemodeChanges = 0;
    private int blockedCommandBlockInteractions = 0;

    public StrictStatsManager(Main plugin) {
        this.plugin = plugin;
    }

    public void incrementBlockedCommand(String command) {
        totalBlockedCommands++;
        blockedCommandMap.merge(command.toLowerCase(), 1, Integer::sum);

        FileConfiguration config = plugin.getStatsConfig();
        config.set("total-blocked-commands", totalBlockedCommands);
        config.set("most-blocked-command", getMostBlockedCommand());
        plugin.saveStats();
    }

    public void incrementBlockedGamemodeChange() {
        blockedGamemodeChanges++;

        FileConfiguration config = plugin.getStatsConfig();
        config.set("blocked-gamemode-changes", blockedGamemodeChanges);
        plugin.saveStats();
    }

    public void incrementCommandBlockInteraction() {
        blockedCommandBlockInteractions++;

        FileConfiguration config = plugin.getStatsConfig();
        config.set("blocked-command-block-interactions", blockedCommandBlockInteractions);
        plugin.saveStats();
    }

    public int getTotalBlockedCommands() {
        return totalBlockedCommands;
    }

    public int getBlockedGamemodeChanges() {
        return blockedGamemodeChanges;
    }

    public int getBlockedCommandBlockInteractions() {
        return blockedCommandBlockInteractions;
    }

    public String getMostBlockedCommand() {
        return blockedCommandMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> "/" + entry.getKey() + " (" + entry.getValue() + ")")
                .orElse("None");
    }
}
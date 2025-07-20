package me.rickperix.strictcraft;

import java.util.List;

public class ConfigManager {

    private final Main plugin;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("strictcraft.enabled", true);
    }

    public boolean isGamemodeEnforcementEnabled() {
        return plugin.getConfig().getBoolean("gamemode-enforcement.enabled", true);
    }

    public boolean isPlayerExcluded(String playerName) {
        List<String> excluded = plugin.getConfig().getStringList("strictcraft.excluded-players");
        return excluded.stream().anyMatch(name -> name.equalsIgnoreCase(playerName));
    }
}
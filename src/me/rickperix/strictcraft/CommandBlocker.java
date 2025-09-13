package me.rickperix.strictcraft;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Collections;
import java.util.List;

public class CommandBlocker implements Listener {

    private final Main plugin;
    private List<String> blockedCommands;
    private String blockedMessage;

    public CommandBlocker(Main plugin) {
        this.plugin = plugin;
        loadConfigValues();
    }

    public void reload() {
        loadConfigValues();
    }

    private void loadConfigValues() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("blocked-commands");

        if (section != null && section.isSet("list")) {
            List<String> list = section.getStringList("list");
            this.blockedCommands = (list != null && !list.isEmpty()) ? list : Collections.emptyList();
        } else {
            this.blockedCommands = Collections.emptyList();
        }

        this.blockedMessage = ChatColor.translateAlternateColorCodes('&',
                section != null ? section.getString("blocked-message", "&cThis command is blocked by StrictCraft.")
                        : "&cThis command is blocked by StrictCraft.");
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (blockedCommands == null || blockedCommands.isEmpty()) return;

        String message = event.getMessage().toLowerCase().trim();
        String playerName = event.getPlayer().getName();

        if (!plugin.getConfigManager().isEnabled()) return;
        if (plugin.getConfigManager().isPlayerExcluded(playerName)) return;
        if (event.getPlayer().hasPermission("strictcraft.bypass")) return;

        for (String cmd : blockedCommands) {
            String normalized = cmd.toLowerCase().trim();
            if (message.equals(normalized) || message.startsWith(normalized + " ")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(blockedMessage);
                plugin.getStatsManager().incrementBlockedCommand(normalized);
                return;
            }
        }
    }
}
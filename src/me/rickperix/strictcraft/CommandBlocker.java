package me.rickperix.strictcraft;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

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

        if (section != null) {
            this.blockedCommands = section.getStringList("list");
            this.blockedMessage = ChatColor.translateAlternateColorCodes('&',
                    section.getString("blocked-message", "&cThis command is blocked by StrictCraft."));
        } else {
            this.blockedCommands = List.of();
            this.blockedMessage = ChatColor.RED + "This command is blocked by StrictCraft.";
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase().trim();
        String playerName = event.getPlayer().getName();

        if (!plugin.getConfigManager().isEnabled()) return;
        if (plugin.getConfigManager().isPlayerExcluded(playerName)) return;
        if (event.getPlayer().hasPermission("strictcraft.bypass")) return;

        for (String cmd : blockedCommands) {
            if (message.equals(cmd) || message.startsWith(cmd + " ")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(blockedMessage);
                return;
            }
        }
    }
}
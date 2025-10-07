package me.rickperix.strictcraft;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class GameModeBlocker implements Listener {

    private final Main plugin;

    public GameModeBlocker(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGamemodeShortcut(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        GameMode newMode = event.getNewGameMode();

        if (isShortcutBlocked(newMode)) {
            event.setCancelled(true);
            String message = getBlockMessage(newMode);
            if (message != null && !message.isEmpty()) {
                player.sendMessage(message);
            }
        }
    }

    private boolean isShortcutBlocked(GameMode newMode) {
        String modeName = newMode.name().toLowerCase();

        for (String rawCommand : plugin.getConfig().getStringList("blocked-commands.list")) {
            String normalized = rawCommand.toLowerCase().replace("/", "").replace(":", "");

            if (normalized.contains("gamemode") && normalized.contains(modeName)) {
                return true;
            }
        }

        return false;
    }

    private String getBlockMessage(GameMode newMode) {
        String path = "messages." + newMode.name().toLowerCase() + "-blocked";
        String configured = plugin.getConfig().getString(path);

        if (configured != null) {
            String trimmed = configured.trim();
            if (!trimmed.isEmpty()) {
                return ChatColor.translateAlternateColorCodes('&', trimmed);
            }
        }

        String fallback = "&cSwitching to " + newMode.name() + " mode is blocked.";
        return ChatColor.translateAlternateColorCodes('&', fallback);
    }
}
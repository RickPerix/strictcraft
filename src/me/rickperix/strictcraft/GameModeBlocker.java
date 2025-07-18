package me.rickperix.strictcraft;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import java.util.List;

public class GameModeBlocker implements Listener {

    private final Main plugin;

    public GameModeBlocker(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();

        if (!plugin.getConfigManager().isEnabled()) return;
        if (plugin.getConfigManager().isPlayerExcluded(name)) return;
        if (player.hasPermission("strictcraft.bypass")) return;

        GameMode newMode = event.getNewGameMode();
        if (newMode != GameMode.CREATIVE) return;

        List<String> blockedCommands = plugin.getConfig().getStringList("blocked-commands.list");

        if (blockedCommands == null || blockedCommands.isEmpty()) return;

        for (String cmd : blockedCommands) {
            String normalized = cmd.toLowerCase().trim();
            if (normalized.contains("gamemode") && normalized.contains("creative")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Creative mode is blocked by StrictCraft.");
                return;
            }
        }
    }
}
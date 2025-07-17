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
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();

        if (!plugin.getConfigManager().isEnabled()) return;
        if (plugin.getConfigManager().isPlayerExcluded(name)) return;
        if (player.hasPermission("strictcraft.bypass")) return;

        GameMode newMode = event.getNewGameMode();

        if (newMode == GameMode.CREATIVE) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Creative mode is blocked by StrictCraft.");
        }
    }
}
package me.rickperix.strictcraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GameModeMonitor {

    private final Main plugin;
    private BukkitTask task;
    private final Set<UUID> recentlyWarned = new HashSet<>();

    public GameModeMonitor(Main plugin) {
        this.plugin = plugin;
    }

    public void start() {
        stop();

        if (!plugin.getConfigManager().isEnabled()) return;
        if (!plugin.getConfigManager().isGamemodeEnforcementEnabled()) return;

        int interval = plugin.getConfig().getInt("gamemode-enforcement.check-interval", 40);

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String name = player.getName();
                UUID id = player.getUniqueId();

                if (plugin.getConfigManager().isPlayerExcluded(name)) continue;
                if (player.hasPermission("strictcraft.bypass")) continue;

                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.setGameMode(GameMode.SURVIVAL);

                    if (recentlyWarned.add(id)) {
                        player.sendMessage(ChatColor.RED + "Creative mode is not allowed. You were switched to Survival.");
                        Bukkit.getScheduler().runTaskLater(plugin, () -> recentlyWarned.remove(id), 20L);
                    }
                }
            }
        }, 0L, interval);
    }

    public void stop() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
}
package me.rickperix.strictcraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;
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
        if (isSurvivalBlocked()) return;

        int interval = plugin.getConfig().getInt("gamemode-enforcement.check-interval", 40);

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String name = player.getName();
                UUID id = player.getUniqueId();

                if (plugin.getConfigManager().isPlayerExcluded(name)) continue;
                if (player.hasPermission("strictcraft.bypass")) continue;

                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.setGameMode(GameMode.SURVIVAL);
                    plugin.getStatsManager().incrementBlockedGamemodeChange();

                    if (recentlyWarned.add(id)) {
                        String message = plugin.getConfig().getString("messages.gamemode-enforced", "&cCreative mode is not allowed. You were switched to Survival.");
                        if (message != null && !message.isEmpty()) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
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

    private boolean isSurvivalBlocked() {
        List<String> blocked = plugin.getConfig().getStringList("blocked-commands.list");
        for (String rawCommand : blocked) {
            String normalized = rawCommand.toLowerCase().replace("/", "").replace(":", "");
            if (normalized.contains("gamemode") && normalized.contains("survival")) {
                return true;
            }
        }
        return false;
    }
}
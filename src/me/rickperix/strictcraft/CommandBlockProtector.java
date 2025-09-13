package me.rickperix.strictcraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CommandBlockProtector implements Listener {

    private final Main plugin;
    private final Set<UUID> recentlyWarned = new HashSet<>();
    private final Set<UUID> recentlyCounted = new HashSet<>();

    private boolean blockUsage;
    private String blockedMessage;

    public CommandBlockProtector(Main plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("command-blocks");

        if (section != null) {
            blockUsage = section.getBoolean("block-usage", true);
            blockedMessage = ChatColor.RED + "You cannot place or interact with command blocks.";
        } else {
            blockUsage = true;
            blockedMessage = ChatColor.RED + "Command block protection active (default).";
        }
    }

    @EventHandler
    public void onCommandBlockInteract(PlayerInteractEvent event) {
        if (!plugin.getConfigManager().isEnabled() || !blockUsage) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (player == null || plugin.getConfigManager().isPlayerExcluded(player.getName())) return;
        if (block == null || !isCommandBlock(block.getType())) return;
        if (player.hasPermission("strictcraft.bypass")) return;

        event.setCancelled(true);
        countOnce(player);
        sendOnce(player);
    }

    @EventHandler
    public void onCommandBlockPlace(BlockPlaceEvent event) {
        if (!plugin.getConfigManager().isEnabled() || !blockUsage) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (player == null || plugin.getConfigManager().isPlayerExcluded(player.getName())) return;
        if (block == null || !isCommandBlock(block.getType())) return;
        if (player.hasPermission("strictcraft.bypass")) return;

        event.setCancelled(true);
        countOnce(player);
        sendOnce(player);
    }

    private boolean isCommandBlock(Material type) {
        return type == Material.COMMAND_BLOCK
                || type == Material.CHAIN_COMMAND_BLOCK
                || type == Material.REPEATING_COMMAND_BLOCK;
    }

    private void sendOnce(Player player) {
        UUID id = player.getUniqueId();
        if (recentlyWarned.add(id)) {
            player.sendMessage(blockedMessage);
            plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                    recentlyWarned.remove(id), 20L);
        }
    }

    private void countOnce(Player player) {
        UUID id = player.getUniqueId();
        if (recentlyCounted.add(id)) {
            plugin.getStatsManager().incrementCommandBlockInteraction();
            plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                    recentlyCounted.remove(id), 1L);
        }
    }
}
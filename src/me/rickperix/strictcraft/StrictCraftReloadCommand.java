package me.rickperix.strictcraft;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StrictCraftReloadCommand implements CommandExecutor {

    private final Main plugin;

    public StrictCraftReloadCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("strictcraft.reload")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to reload StrictCraft!");
            return true;
        }

        plugin.reloadConfig();

        plugin.rebuildConfigManager();

        plugin.getCommandBlocker().reload();
        plugin.getCommandBlockProtector().reload();

        plugin.getGameModeMonitor().stop();

        if (plugin.getConfigManager().isEnabled() && plugin.getConfigManager().isGamemodeEnforcementEnabled()) {
            plugin.getGameModeMonitor().start();
        }

        sender.sendMessage(ChatColor.GREEN + "StrictCraft configuration reloaded successfully!");
        return true;
    }
}
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
            sender.sendMessage(ChatColor.RED + "You don't have permission to reload StrictCraft.");
            return true;
        }

        plugin.reloadConfig();
        plugin.getCommandBlocker().reload();
        plugin.getGameModeMonitor().start();
        plugin.getCommandBlockProtector().reload();

        plugin.getLogger().info("StrictCraft configuration reloaded by " + sender.getName());
        sender.sendMessage(ChatColor.GREEN + "Configuration reloaded successfully!");

        return true;
    }
}
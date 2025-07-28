package me.rickperix.strictcraft;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class JoinVersionListener implements Listener {
    private final JavaPlugin plugin;
    private final File versionFile;
    private final String currentVersion;

    public JoinVersionListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.versionFile = new File(plugin.getDataFolder(), "version.dat");
        this.currentVersion = plugin.getDescription().getVersion();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.isOp()) return;

        String savedVersion = null;

        if (versionFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(versionFile))) {
                savedVersion = reader.readLine();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not read version.dat: " + e.getMessage());
            }
        }

        if (savedVersion == null || !currentVersion.equals(savedVersion.trim())) {
            player.sendMessage("§a[StrictCraft] §fPlugin has been successfully installed! The version currently installed on this server is §e" + currentVersion);
            player.sendMessage("§7Please read the §6README.txt §7file in the plugin folder for more informations.");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(versionFile))) {
                writer.write(currentVersion);
            } catch (IOException e) {
                plugin.getLogger().warning("Could not write version.dat: " + e.getMessage());
            }
        }
    }
}
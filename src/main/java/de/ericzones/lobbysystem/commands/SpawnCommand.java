// Created by Eric B. 17.05.2021 12:59
package de.ericzones.lobbysystem.commands;

import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.Utils;
import de.ericzones.lobbysystem.manager.LocationManager;
import de.ericzones.lobbysystem.manager.MessageManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class SpawnCommand implements CommandExecutor {

    private LobbySystem instance;
    private LocationManager locationManager;
    private MessageManager messageManager;

    private HashMap<UUID, Long> spawnDelay;

    public SpawnCommand(LobbySystem instance) {
        this.instance = instance;
        locationManager = instance.getLocationManager();
        messageManager = instance.getMessageManager();
        spawnDelay = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command c, String s, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Utils.error_noconsole);
            return true;
        }
        Player player = (Player) sender;
        if(locationManager.getSpawn() != null) {
            if(spawnDelay.containsKey(player.getUniqueId()) && spawnDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                return true;
            }
            spawnDelay.put(player.getUniqueId(), System.currentTimeMillis() + 4*1000);

            player.teleport(locationManager.getSpawn());
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
            messageManager.sendTitle(player, 5, 20, 5, "", "§8• §bSpawn §8•");
        } else
            player.sendMessage(Utils.prefix_lobby+"§7Der §cSpawn §7wurde nicht gesetzt");
        return false;
    }

}

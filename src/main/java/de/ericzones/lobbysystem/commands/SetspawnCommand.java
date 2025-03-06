// Created by Eric B. 17.05.2021 12:59
package de.ericzones.lobbysystem.commands;

import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.Utils;
import de.ericzones.lobbysystem.manager.LocationManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetspawnCommand implements CommandExecutor {

    private LobbySystem instance;
    private LocationManager locationManager;

    public SetspawnCommand(LobbySystem instance) {
        this.instance = instance;
        locationManager = instance.getLocationManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command c, String s, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Utils.error_noconsole);
            return true;
        }
        Player player = (Player) sender;
        if(!player.hasPermission("spigot.command.setspawn")) {
            player.sendMessage(Utils.error_noperms);
            return true;
        }
        locationManager.setSpawn(player.getLocation());
        player.sendMessage(Utils.prefix_lobby+"§7Spawn wurde gesetzt");
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
        return false;
    }

}

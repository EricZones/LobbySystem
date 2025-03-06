// Created by Eric B. 17.05.2021 12:58
package de.ericzones.lobbysystem.commands;

import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.Utils;
import de.ericzones.lobbysystem.manager.LocationManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemovelocationCommand implements CommandExecutor {

    private LobbySystem instance;
    private LocationManager locationManager;

    public RemovelocationCommand(LobbySystem instance) {
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
        if(!player.hasPermission("spigot.command.removelocation")) {
            player.sendMessage(Utils.error_noperms);
            return true;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.prefix_lobby+"§7Diese §cSyntax §7ist falsch. §8(§7/removelocation Name§8)");
            return true;
        }
        if(!locationManager.getLocations().contains(args[0])) {
            player.sendMessage(Utils.prefix_lobby+"§7Diese §cLocation §7wurde nicht gefunden");
            return true;
        }
        locationManager.removeLocation(args[0]);
        player.sendMessage(Utils.prefix_lobby+"§7Location §b"+args[0]+" §7wurde gelöscht");
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
        return false;
    }

}

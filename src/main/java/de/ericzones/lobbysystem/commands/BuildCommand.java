// Created by Eric B. 17.05.2021 12:58
package de.ericzones.lobbysystem.commands;

import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.Utils;
import de.ericzones.lobbysystem.manager.InventoryManager;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildCommand implements CommandExecutor {

    private LobbySystem instance;
    private InventoryManager inventoryManager;

    public BuildCommand(LobbySystem instance) {
        this.instance = instance;
        inventoryManager = instance.getInventoryManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command c, String s, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Utils.error_noconsole);
            return true;
        }
        Player player = (Player) sender;
        if(!player.hasPermission("spigot.command.build")) {
            player.sendMessage(Utils.error_noperms);
            return true;
        }
        if(instance.getBuildPlayers().contains(player.getUniqueId())) {
            instance.getBuildPlayers().remove(player.getUniqueId());
            player.sendMessage(Utils.prefix_lobby+"§7Baumodus §cdeaktiviert");
            player.playSound(player.getLocation(), Sound.ANVIL_USE, 1, 1);
            player.setGameMode(GameMode.ADVENTURE);
            inventoryManager.setInventory(player);
        } else {
            instance.getBuildPlayers().add(player.getUniqueId());
            player.sendMessage(Utils.prefix_lobby+"§7Baumodus §aaktiviert");
            player.playSound(player.getLocation(), Sound.ANVIL_USE, 1, 1);
            player.setGameMode(GameMode.CREATIVE);
            inventoryManager.setInventory(player);
        }

        return false;
    }

}

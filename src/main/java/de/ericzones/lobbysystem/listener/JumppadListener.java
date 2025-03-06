// Created by Eric B. 22.06.2020 14:39
package de.ericzones.lobbysystem.listener;

import de.ericzones.lobbysystem.LobbySystem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class JumppadListener implements Listener {

    private LobbySystem instance;

    public JumppadListener(LobbySystem instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(player.getLocation().getBlock() == null) return;
        if(player.getLocation().getBlock().getType() != Material.IRON_PLATE) return;
        player.setVelocity(player.getLocation().getDirection().multiply(5).setY(2));
        player.playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 1, 1);
    }

}

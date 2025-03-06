// Created by Eric B. 22.06.2020 14:08
package de.ericzones.lobbysystem.listener;

import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.manager.ExtraManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class DoublejumpListener implements Listener {

    private LobbySystem instance;
    private ExtraManager extraManager;

    public DoublejumpListener(LobbySystem instance) {
        this.instance = instance;
        extraManager = instance.getExtraManager();
    }

    @Deprecated
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(!extraManager.hasDoublejump(player.getUniqueId())) return;
        if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        if(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) return;
        if(!player.isOnGround()) return;
        player.setAllowFlight(true);
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();
        if(!extraManager.hasDoublejump(player.getUniqueId())) return;
        if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        e.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setVelocity(player.getLocation().getDirection().multiply(1.55).setY(1.25));
        player.setFallDistance(0.0F);
        player.playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 1, 1);
    }
}

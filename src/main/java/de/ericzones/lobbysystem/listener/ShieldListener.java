// Created by Eric B. 28.12.2020 19:47
package de.ericzones.lobbysystem.listener;

import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.manager.ExtraManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public class ShieldListener implements Listener {

    private LobbySystem instance;
    private ExtraManager extraManager;

    public ShieldListener(LobbySystem instance) {
        this.instance = instance;
        extraManager = instance.getExtraManager();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(extraManager.hasShield(player.getUniqueId())) {
            for (Entity entity : player.getNearbyEntities(3.0D, 3.0D, 3.0D)) {
                if(!(entity instanceof Player)) return;
                Player target = (Player) entity;
                if(target.hasPermission("spigot.lobby.shield")) return;
                double Ax = player.getLocation().getX(); double Ay = player.getLocation().getY(); double Az = player.getLocation().getZ();
                double Bx = target.getLocation().getX(); double By = target.getLocation().getY(); double Bz = target.getLocation().getZ();
                double x = Bx - Ax; double y = By - Ay; double z = Bz - Az;
                Vector v = new Vector(x, y, z).normalize().multiply(1.0D).setY(0.3D);
                target.setVelocity(v);
            }
        } else {
            for(UUID shieldUserUUID : extraManager.getActiveShields()) {
                if(!(Bukkit.getPlayer(shieldUserUUID).isOnline())) return;
                Player shieldUser = Bukkit.getPlayer(shieldUserUUID);
                if(player == shieldUser || player.hasPermission("spigot.lobby.shield") || !(player.getLocation().distance(shieldUser.getLocation()) <= 3.0D)) return;
                double Ax = player.getLocation().getX(); double Ay = player.getLocation().getY(); double Az = player.getLocation().getZ();
                double Bx = shieldUser.getLocation().getX(); double By = shieldUser.getLocation().getY(); double Bz = shieldUser.getLocation().getZ();
                double x = Ax - Bx; double y = Ay - By; double z = Az - Bz;
                Vector v = new Vector(x, y, z).normalize().multiply(1.0D).setY(0.3D);
                player.setVelocity(v);
            }
        }
    }

}

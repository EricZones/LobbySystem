// Created by Eric B. 08.07.2020 12:16
package de.ericzones.lobbysystem.listener;

import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.ItemNames;
import de.ericzones.lobbysystem.extra.ParticleBuilder;
import de.ericzones.lobbysystem.manager.ExtraManager;
import de.ericzones.lobbysystem.manager.InventoryManager;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EnderpearlListener implements Listener {

    private LobbySystem instance;
    private ExtraManager extraManager;
    private InventoryManager inventoryManager;

    public EnderpearlListener(LobbySystem instance) {
        this.instance = instance;
        extraManager = instance.getExtraManager();
        inventoryManager = instance.getInventoryManager();
    }

    @Deprecated
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if(player.getItemInHand() == null) return;
        if(instance.getBuildPlayers().contains(player.getUniqueId())) return;
        if(!player.getItemInHand().hasItemMeta()) return;
        if(player.getItemInHand().getItemMeta().getDisplayName() == null) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;
        if(player.getItemInHand().getItemMeta().getDisplayName().equals(ItemNames.INV_ENDERPEARL)) {
            EnderPearl enderPearl = player.launchProjectile(EnderPearl.class);
            extraManager.getActivePearls().add(player.getUniqueId());
            inventoryManager.setInventory(player);
            enderPearl.setVelocity(player.getLocation().getDirection().multiply(2));
            player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1.5F, 1.1F);
            Bukkit.getScheduler().scheduleAsyncDelayedTask(instance, new Runnable() {
                @Override
                public void run() {
                    extraManager.getActivePearls().remove(player.getUniqueId());
                    inventoryManager.setInventory(player);
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                }
            }, 10 * 20);
        } else if(player.getItemInHand().getItemMeta().getDisplayName().equals(ItemNames.INV_DELUXEPEARL)) {
            EnderPearl enderPearl = player.launchProjectile(EnderPearl.class);

            extraManager.getActivePearls().add(player.getUniqueId());
            inventoryManager.setInventory(player);
            enderPearl.setVelocity(player.getLocation().getDirection().multiply(2));
            enderPearl.setPassenger(player);
            enderPearl.setShooter(null);
            player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1.5F, 1.1F);
            playDeluxepearlEffects(enderPearl);

            Bukkit.getScheduler().scheduleAsyncDelayedTask(instance, new Runnable() {
                @Override
                public void run() {
                    extraManager.getActivePearls().remove(player.getUniqueId());
                    inventoryManager.setInventory(player);
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                }
            }, 10 * 20);
        }
    }

    private void playDeluxepearlEffects(EnderPearl enderPearl) {
        if (enderPearl == null || enderPearl.isDead()) return;
        int scheduler = 0;
        scheduler = Bukkit.getScheduler().scheduleAsyncRepeatingTask(instance, new Runnable() {
            @Override
            public void run() {
                if(enderPearl.isDead())
                    return;
                for(Player current : Bukkit.getOnlinePlayers()) {
                    new ParticleBuilder(EnumParticle.FIREWORKS_SPARK, true, enderPearl.getLocation().add(0, -1, 0), 0.03f, 0, 0.03f, 0.1f, 10, 2003).showParticles(current);
                    new ParticleBuilder(EnumParticle.REDSTONE, true, enderPearl.getLocation().add(0, -1, 0), 0.5f, 0, 0.5f, 0.1f, 15, 2003).showParticles(current);
                }
            }
        }, 0, 3);
    }

}

// Created by Eric B. 21.06.2020 13:55
package de.ericzones.lobbysystem.listener;

import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.manager.LocationManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class ProtectionListener implements Listener {

    private LobbySystem instance;
    private LocationManager locationManager;

    public ProtectionListener(LobbySystem instance) {
        this.instance = instance;
        locationManager = instance.getLocationManager();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if(!instance.getBuildPlayers().contains(player.getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if(!instance.getBuildPlayers().contains(player.getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if(!instance.getBuildPlayers().contains(player.getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player) e.getWhoClicked();
        if(!instance.getBuildPlayers().contains(player.getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        if(instance.getBuildPlayers().contains(player.getUniqueId())) return;
        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 0.5F, 2.5F);
    }

    @EventHandler
    public void onPlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent e) {
        Player player = e.getPlayer();
        if(!instance.getBuildPlayers().contains(player.getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        if(!instance.getBuildPlayers().contains(player.getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent e) {
        Player player = e.getPlayer();
        if(!instance.getBuildPlayers().contains(player.getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent e) {
        if(e.getVehicle() instanceof Minecart)
            ((Minecart)e.getVehicle()).setMaxSpeed(0);
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent e) {
        if(!(e.getEntered() instanceof Player)) return;
        Player player = (Player) e.getEntered();
        if(!instance.getBuildPlayers().contains(player.getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent e) {
        if(!(e.getAttacker() instanceof Player)) return;
        Player player = (Player) e.getAttacker();
        if(!instance.getBuildPlayers().contains(player.getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if(e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)
            e.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent e) {
        if(!(e.getRemover() instanceof Player)) return;
        Player player = (Player) e.getRemover();
        if(!instance.getBuildPlayers().contains(player.getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent e) {
        e.setNewCurrent(15);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent e) {
        e.setCancelled(true);
    }

    @Deprecated
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e) {
        int id = e.getBlock().getTypeId();
        if(id == 8 || id == 9 || id == 10 || id == 11) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        e.blockList().clear();
    }

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent e) {
        if(locationManager.getSpawn() != null)
            e.setSpawnLocation(locationManager.getSpawn());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(player.getLocation().getBlockY() <= 0) {
            player.teleport(locationManager.getSpawn());
            return;
        }
        Location location = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY()+1, player.getLocation().getZ());
        if(location.getBlock().getType().isSolid() && !location.getBlock().isLiquid() && location.getBlock().getType().isBlock() && location.getBlock().getType() != Material.TRAP_DOOR && location.getBlock().getType() != Material.IRON_TRAPDOOR && location.getBlock().getType() != Material.STAINED_GLASS_PANE && location.getBlock().getType() != Material.STANDING_BANNER && location.getBlock().getType() != Material.WALL_BANNER && location.getBlock().getType() != Material.FENCE && location.getBlock().getType() != Material.FENCE_GATE && location.getBlock().getType() != Material.IRON_FENCE && location.getBlock().getType() != Material.DARK_OAK_FENCE && location.getBlock().getType() != Material.BIRCH_FENCE && location.getBlock().getType() != Material.SPRUCE_FENCE && location.getBlock().getType() != Material.WALL_SIGN && location.getBlock().getType() != Material.SIGN && location.getBlock().getType() != Material.IRON_DOOR_BLOCK && location.getBlock().getType() != Material.WOODEN_DOOR && location.getBlock().getType() != Material.THIN_GLASS && location.getBlock().getType() != Material.DARK_OAK_DOOR && location.getBlock().getType() != Material.SPRUCE_DOOR && location.getBlock().getType() != Material.ACACIA_DOOR && location.getBlock().getType() != Material.JUNGLE_DOOR && location.getBlock().getType() != Material.BIRCH_DOOR && location.getBlock().getType() != Material.BARRIER && location.getBlock().getType() != Material.COBBLE_WALL)
            player.teleport(new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY()+2, player.getLocation().getZ()));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player)) return;
        if(!instance.getBuildPlayers().contains(e.getDamager().getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);
        e.setKeepInventory(true);
    }

    @EventHandler
    public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();
        if(!instance.getBuildPlayers().contains(player.getUniqueId()))
            e.setCancelled(true);
        if(e.getItem().getItemStack().hasItemMeta() && e.getItem().getItemStack().getItemMeta().getDisplayName().startsWith("ID: "))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        if(!instance.getBuildPlayers().contains(player.getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        if(!instance.getBuildPlayers().contains(player.getUniqueId()))
            e.setCancelled(true);
    }
}

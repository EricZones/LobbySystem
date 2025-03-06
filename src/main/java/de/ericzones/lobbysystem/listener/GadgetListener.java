// Created by Eric B. 23.01.2021 18:19
package de.ericzones.lobbysystem.listener;

import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.ItemBuilder;
import de.ericzones.lobbysystem.extra.ItemNames;
import de.ericzones.lobbysystem.extra.ParticleBuilder;
import de.ericzones.lobbysystem.manager.GadgetManager;
import de.ericzones.lobbysystem.manager.VisibilityManager;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.*;

public class GadgetListener implements Listener {

    private LobbySystem instance;
    private GadgetManager gadgetManager;
    private VisibilityManager visibilityManager;

    private HashMap<UUID, Long> paintballDelay;
    private HashMap<String, UUID> snowballCache;

    public GadgetListener(LobbySystem instance) {
        this.instance = instance;
        gadgetManager = instance.getGadgetManager();
        visibilityManager = instance.getVisibilityManager();
        paintballDelay = new HashMap<>();
        snowballCache = new HashMap<>();
        startEffectScheduler();
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        Player player = e.getPlayer();
        if(gadgetManager.getToolGadget(player.getUniqueId()) != 1) return;
        if(player.getItemInHand() == null) return;
        if(instance.getBuildPlayers().contains(player.getUniqueId())) return;
        if(!player.getItemInHand().hasItemMeta()) return;
        if(player.getItemInHand().getItemMeta().getDisplayName() == null) return;
        if(!player.getItemInHand().getItemMeta().getDisplayName().equals(ItemNames.INV_GADGET_HOOK)) return;

        if(!e.getState().equals(PlayerFishEvent.State.IN_GROUND) && !e.getState().equals(PlayerFishEvent.State.CAUGHT_ENTITY) && !e.getState().equals(PlayerFishEvent.State.FAILED_ATTEMPT)) return;
        if(player.getWorld().getBlockAt(e.getHook().getLocation().getBlockX(), e.getHook().getLocation().getBlockY() - 1, e.getHook().getLocation().getBlockZ()).getType() == Material.AIR || player.getWorld().getBlockAt(e.getHook().getLocation().getBlockX(), e.getHook().getLocation().getBlockY() - 1, e.getHook().getLocation().getBlockZ()).getType() == Material.STATIONARY_WATER) return;

        Location playerLocation = player.getLocation();
        Location hookLocation = e.getHook().getLocation();
        playerLocation.setY(playerLocation.getY() + 0.8D);
        player.teleport(playerLocation);
        double a = -0.08D, b = hookLocation.distance(playerLocation);
        double vectorX = (1.0D + 0.07D * b) * (hookLocation.getX() - playerLocation.getX()) / b;
        double vectorY = (1.0D + 0.03D * b) * (hookLocation.getY() - playerLocation.getY()) / b - 0.5D * a * b;
        double vectorZ = (1.0D + 0.07D * b) * (hookLocation.getZ() - playerLocation.getZ()) / b;
        Vector vector = player.getVelocity(); vector.setX(vectorX); vector.setY(vectorY); vector.setZ(vectorZ);
        player.setVelocity(vector);
        player.playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 1.0F, 2.0F);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if(player.getItemInHand() == null) return;
        if(instance.getBuildPlayers().contains(player.getUniqueId())) return;
        if(!player.getItemInHand().hasItemMeta()) return;
        if(player.getItemInHand().getItemMeta().getDisplayName() == null) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;

        if(player.getItemInHand().getItemMeta().getDisplayName().equals(ItemNames.INV_GADGET_HOOK)) {
            e.setUseItemInHand(Event.Result.ALLOW);
        } else if(player.getItemInHand().getItemMeta().getDisplayName().equals(ItemNames.INV_GADGET_PAINTBALL)) {
            if(paintballDelay.containsKey(player.getUniqueId()) && paintballDelay.get(player.getUniqueId()) > System.currentTimeMillis())
                return;
            paintballDelay.put(player.getUniqueId(), System.currentTimeMillis() + 500);

            Snowball snowball = player.launchProjectile(Snowball.class);
            Random random = new Random(); String customName = "ID: "+random.nextInt();
            snowball.setCustomName(customName); snowballCache.put(customName, player.getUniqueId());
            snowball.setVelocity(player.getLocation().getDirection().multiply(3));
            player.playSound(player.getLocation(), Sound.HORSE_ARMOR, 2.0F, 1.75F);

        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if(e.getEntityType().getEntityClass() != Snowball.class) return;
        Snowball snowball = (Snowball) e.getEntity();
        if(snowball.getCustomName() == null || !snowballCache.containsKey(snowball.getCustomName())) return;

        ArrayList<Location> changeBlocks = new ArrayList<>();
        HashMap<Location, Block> blockCache = new HashMap<>();

        List<Location> list = new ArrayList<>();
        Location snowballLocation = snowball.getLocation(); list.add(snowballLocation);
        Location fakeLocation1 = snowball.getLocation().add(0, 0, 1); list.add(fakeLocation1);
        Location fakeLocation2 = snowball.getLocation().add(0, 0, -1); list.add(fakeLocation2);
        Location fakeLocation3 = snowball.getLocation().add(1, 0, 0); list.add(fakeLocation3);
        Location fakeLocation4 = snowball.getLocation().add(-1, 0, 0); list.add(fakeLocation4);
        for(Location current : list) {
            BlockIterator iterator = new BlockIterator(snowball.getWorld(), current.toVector(), snowball.getVelocity().normalize(), 0, 4);
            while (iterator.hasNext()) {
                Location currentLocation = iterator.next().getLocation();
                if(currentLocation.getBlock().getType() != Material.AIR && currentLocation.getBlock().getType() != Material.STATIONARY_WATER && currentLocation.getBlock().getType() != Material.STATIONARY_LAVA && currentLocation.getBlock().getType() != Material.WALL_SIGN && currentLocation.getBlock().getType() != Material.SIGN && currentLocation.getBlock().getType() != Material.SKULL && currentLocation.getBlock().getType() != Material.WALL_BANNER && currentLocation.getBlock().getType() != Material.STANDING_BANNER) {
                    if(!changeBlocks.contains(currentLocation))
                        changeBlocks.add(currentLocation);
                }
            }
        }

        int min = 0, max = 15;
        int blockId = (int) (Math.random()*(max-min+1)+min);

        for(Player current : Bukkit.getOnlinePlayers()) {
            if(!visibilityManager.canSeePlayer(current.getUniqueId(), snowballCache.get(snowball.getCustomName())) && current.getUniqueId() != snowballCache.get(snowball.getCustomName())) continue;
            current.playSound(snowballLocation, Sound.CHICKEN_EGG_POP, 1, 1.5F);
            for(Location location : changeBlocks) {
                blockCache.put(location, location.getBlock());
                current.sendBlockChange(location, Material.STAINED_CLAY, (byte) blockId);
            }
        }

        Bukkit.getScheduler().scheduleAsyncDelayedTask(instance, new Runnable() {
            @Override
            public void run() {
                for(Player current : Bukkit.getOnlinePlayers()) {
                    for(Location currentLocation : blockCache.keySet())
                        current.sendBlockChange(currentLocation, blockCache.get(currentLocation).getType(), blockCache.get(currentLocation).getData());
                }
            }
        },3 * 20);
    }

    private void startEffectScheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
            @Override
            public void run() {
                HashMap<UUID, Integer> bootsGadget = gadgetManager.getBootsGadgetMap();
                HashMap<UUID, Integer> rainGadget = gadgetManager.getRainGadgetMap();

                if (bootsGadget.size() > 0) {
                    for (UUID current : bootsGadget.keySet()) {
                        Player player = Bukkit.getPlayer(current);
                        if (player == null || !player.isOnline()) {
                            bootsGadget.remove(current);
                            continue;
                        }

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (visibilityManager.canSeePlayer(all.getUniqueId(), current) || all.getUniqueId() == current) {
                                if (bootsGadget.get(current) == 1)
                                    new ParticleBuilder(EnumParticle.HEART, true, player.getLocation().add(0, 0.1, 0), 0.5f, 0, 0.5f, 0.05F, 2, 2003).showParticles(all);
                                else if (bootsGadget.get(current) == 2)
                                    new ParticleBuilder(EnumParticle.FIREWORKS_SPARK, true, player.getLocation().add(0, 0.3, 0), 0.03f, 0, 0.03f, 0.1f, 5, 2003).showParticles(all);
                                else if (bootsGadget.get(current) == 3)
                                    new ParticleBuilder(EnumParticle.WATER_SPLASH, true, player.getLocation().add(0, 0.3, 0), 0.5f, 0, 0.5f, 0.25f, 20, 2003).showParticles(all);
                                else if (bootsGadget.get(current) == 4)
                                    new ParticleBuilder(EnumParticle.FLAME, true, player.getLocation().add(0, 0.3, 0), 0.02F, 0, 0.02F, 0.05F, 5, 2003).showParticles(all);
                                else if (bootsGadget.get(current) == 5)
                                    new ParticleBuilder(EnumParticle.SMOKE_LARGE, true, player.getLocation().add(0, 0.1, 0), 0.2F, 0, 0.2F, 0.05F, 4, 2003).showParticles(all);
                                else if (bootsGadget.get(current) == 6)
                                    new ParticleBuilder(EnumParticle.NOTE, true, player.getLocation().add(0, 0.1, 0), 0.5f, 0, 0.5f, 0.01F, 4, 2003).showParticles(all);
                            }
                        }
                    }
                }

                if (rainGadget.size() > 0) {
                    for (UUID current : rainGadget.keySet()) {
                        Player player = Bukkit.getPlayer(current);
                        if (player == null || !player.isOnline()) {
                            rainGadget.remove(current);
                            continue;
                        }

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (rainGadget.get(current) == 3) {
                                new ParticleBuilder(EnumParticle.SMOKE_LARGE, true, player.getLocation().add(0, 2.75, 0), 0.5f, 0, 0.5f, 0.01F, 8, 2003).showParticles(all);
                                new ParticleBuilder(EnumParticle.SMOKE_LARGE, true, player.getLocation().add(0, 3.0, 0), 0.5f, 0, 0.5f, 0.01F, 8, 2003).showParticles(all);
                            } else {
                                new ParticleBuilder(EnumParticle.CLOUD, true, player.getLocation().add(0, 2.75, 0), 0.5f, 0, 0.5f, 0.01F, 8, 2003).showParticles(all);
                                new ParticleBuilder(EnumParticle.CLOUD, true, player.getLocation().add(0, 3.0, 0), 0.5f, 0, 0.5f, 0.01F, 8, 2003).showParticles(all);
                            }
                        }

                        Random random = new Random();
                        if (rainGadget.get(current) == 1) {
                            Item item = player.getWorld().dropItem(player.getLocation().add(0, 2.5, 0), new ItemBuilder(Material.DIAMOND).setDisplayName("ID: " + random.nextInt()).build());
                            Item item2 = player.getWorld().dropItem(player.getLocation().add(0, 2.5, 0), new ItemBuilder(Material.DIAMOND).setDisplayName("ID: " + random.nextInt()).build());

                            setAutomaticDespawn(item, item2);

                        } else if (rainGadget.get(current) == 2) {
                            Item item = player.getWorld().dropItem(player.getLocation().add(0, 2.5, 0), new ItemBuilder(Material.EMERALD).setDisplayName("ID: " + random.nextInt()).build());
                            Item item2 = player.getWorld().dropItem(player.getLocation().add(0, 2.5, 0), new ItemBuilder(Material.EMERALD).setDisplayName("ID: " + random.nextInt()).build());

                            setAutomaticDespawn(item, item2);

                        } else if (rainGadget.get(current) == 3) {
                            Item item = player.getWorld().dropItem(player.getLocation().add(0, 2.5, 0), new ItemBuilder(Material.REDSTONE).setDisplayName("ID: " + random.nextInt()).build());
                            Item item2 = player.getWorld().dropItem(player.getLocation().add(0, 2.5, 0), new ItemBuilder(Material.REDSTONE).setDisplayName("ID: " + random.nextInt()).build());

                            setAutomaticDespawn(item, item2);

                        } else if (rainGadget.get(current) == 4) {
                            Item item = player.getWorld().dropItem(player.getLocation().add(0, 2.5, 0), new ItemBuilder(Material.GOLD_NUGGET).setDisplayName("ID: " + random.nextInt()).build());
                            Item item2 = player.getWorld().dropItem(player.getLocation().add(0, 2.5, 0), new ItemBuilder(Material.GOLD_NUGGET).setDisplayName("ID: " + random.nextInt()).build());

                            setAutomaticDespawn(item, item2);

                        } else if (rainGadget.get(current) == 5) {
                            Item item = player.getWorld().dropItem(player.getLocation().add(0, 2.5, 0), new ItemBuilder(Material.SNOW_BALL).setDisplayName("ID: " + random.nextInt()).build());
                            Item item2 = player.getWorld().dropItem(player.getLocation().add(0, 2.5, 0), new ItemBuilder(Material.SNOW_BALL).setDisplayName("ID: " + random.nextInt()).build());

                            setAutomaticDespawn(item, item2);

                        } else if (rainGadget.get(current) == 6) {
                            Item item = player.getWorld().dropItem(player.getLocation().add(0, 2.5, 0), new ItemBuilder(Material.NETHER_STAR).setDisplayName("ID: " + random.nextInt()).build());
                            Item item2 = player.getWorld().dropItem(player.getLocation().add(0, 2.5, 0), new ItemBuilder(Material.NETHER_STAR).setDisplayName("ID: " + random.nextInt()).build());

                            setAutomaticDespawn(item, item2);

                        }
                    }
                }
            }

        }, 0, 3);
    }

    private void setAutomaticDespawn(Item item1, Item item2) {
        try {
            Field itemField = item1.getClass().getDeclaredField("item");
            Field ageField;
            Object entityItem;

            itemField.setAccessible(true);
            entityItem = itemField.get(item1);

            ageField = entityItem.getClass().getDeclaredField("age");
            ageField.setAccessible(true);
            ageField.set(entityItem, 6000 - (1 * 20));

            Field itemField2 = item2.getClass().getDeclaredField("item");
            Field ageField2;
            Object entityItem2;

            itemField2.setAccessible(true);
            entityItem2 = itemField2.get(item2);

            ageField2 = entityItem2.getClass().getDeclaredField("age");
            ageField2.setAccessible(true);
            ageField2.set(entityItem2, 6000 - (1 * 20));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

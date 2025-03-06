// Created by Eric B. 24.06.2020 07:00
package de.ericzones.lobbysystem.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.InventoryBuilder;
import de.ericzones.lobbysystem.extra.ItemBuilder;
import de.ericzones.lobbysystem.extra.ItemNames;
import de.ericzones.lobbysystem.extra.Utils;
import de.ericzones.lobbysystem.friend.FriendManager;
import de.ericzones.lobbysystem.friend.FriendSetting;
import de.ericzones.lobbysystem.friend.FriendValue;
import de.ericzones.lobbysystem.inventories.FriendInventory;
import de.ericzones.lobbysystem.inventories.GadgetInventory;
import de.ericzones.lobbysystem.manager.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class InventoryClickListener implements Listener {

    private LobbySystem instance;
    private InventoryManager inventoryManager;
    private ExtraManager extraManager;
    private LocationManager locationManager;
    private MessageManager messageManager;
    private VisibilityManager visibilityManager;
    private ChatManager chatManager;
    private DelayManager delayManager;
    private FriendInventory friendInventory;
    private FriendManager friendManager;
    private GadgetInventory gadgetInventory;
    private GadgetManager gadgetManager;

    private HashMap<UUID, Long> chatsettingsDelay;
    private HashMap<UUID, Long> doublejumpDelay;
    private HashMap<UUID, Long> flyDelay;
    private HashMap<UUID, Long> deluxepearlDelay;
    private HashMap<UUID, Long> friendMenuDelay;
    private HashMap<UUID, Long> friendSettingsDelay;
    private HashMap<UUID, Long> friendHeadDelay;
    private HashMap<UUID, Long> requestHeadDelay;
    private HashMap<UUID, Long> gadgetSelectDelay;

    public InventoryClickListener(LobbySystem instance) {
        this.instance = instance;
        inventoryManager = instance.getInventoryManager();
        extraManager = instance.getExtraManager();
        locationManager = instance.getLocationManager();
        messageManager = instance.getMessageManager();
        visibilityManager = instance.getVisibilityManager();
        chatManager = instance.getChatManager();
        delayManager = instance.getDelayManager();
        friendInventory = instance.getFriendInventory();
        chatsettingsDelay = new HashMap<>();
        doublejumpDelay = new HashMap<>();
        flyDelay = new HashMap<>();
        friendMenuDelay = new HashMap<>();
        friendSettingsDelay = new HashMap<>();
        deluxepearlDelay = new HashMap<>();
        friendHeadDelay = new HashMap<>();
        requestHeadDelay = new HashMap<>();
        gadgetSelectDelay = new HashMap<>();
        friendManager = instance.getFriendManager();
        gadgetInventory = instance.getGadgetInventory();
        gadgetManager = instance.getGadgetManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player) e.getWhoClicked();
        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getItemMeta() == null) return;
        if(e.getCurrentItem().getItemMeta().getDisplayName() == null) return;
        if(e.getInventory().getHolder() instanceof Player) {

            if (instance.getBuildPlayers().contains(player.getUniqueId())) {
                if (e.getCurrentItem().getType() == Material.GRASS && e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.INV_BUILDMODE_ON)) {
                    e.setCancelled(true);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 1, 1);
                    instance.getBuildPlayers().remove(player.getUniqueId());
                    player.setGameMode(GameMode.ADVENTURE);
                    inventoryManager.setInventory(player);
                    player.closeInventory();
                }
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.INV_BUILDMODE_OFF) && player.hasPermission("spigot.command.build")) {
                e.setCancelled(true);
                player.playSound(player.getLocation(), Sound.ANVIL_USE, 1, 1);
                instance.getBuildPlayers().add(player.getUniqueId());
                player.setGameMode(GameMode.CREATIVE);
                inventoryManager.setInventory(player);
                player.closeInventory();
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.INV_FLYMODE_OFF) || e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.INV_FLYMODE_ON)) {
                if(player.hasPermission("spigot.lobby.fly")) {
                    if(extraManager.hasFlymode(player.getUniqueId())) {
                        if(flyDelay.containsKey(player.getUniqueId()) && flyDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                            player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                            return;
                        }
                        flyDelay.put(player.getUniqueId(), System.currentTimeMillis() + 5*1000);

                        extraManager.updateExtraSetting(player.getUniqueId(), ExtraManager.ExtraType.FLY, ExtraManager.ExtraSetting.OFF);
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                        player.closeInventory();
                    } else {
                        if(!extraManager.hasDoublejump(player.getUniqueId())) {
                            if(flyDelay.containsKey(player.getUniqueId()) && flyDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                                player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                                return;
                            }
                            flyDelay.put(player.getUniqueId(), System.currentTimeMillis() + 5*1000);

                            extraManager.updateExtraSetting(player.getUniqueId(), ExtraManager.ExtraType.FLY, ExtraManager.ExtraSetting.ON);
                            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                            player.closeInventory();
                        } else {
                            player.sendMessage(Utils.prefix_lobby + "§7Bitte deaktiviere §cDoppelsprung");
                            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                        }
                    }
                } else {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für dieses Extra");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                }
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.INV_DOUBLEJUMP_OFF) || e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.INV_DOUBLEJUMP_ON)) {
                if(player.hasPermission("spigot.lobby.doublejump")) {
                    if(extraManager.hasDoublejump(player.getUniqueId())) {
                        if(doublejumpDelay.containsKey(player.getUniqueId()) && doublejumpDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                            player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                            return;
                        }
                        doublejumpDelay.put(player.getUniqueId(), System.currentTimeMillis() + 5*1000);

                        extraManager.updateExtraSetting(player.getUniqueId(), ExtraManager.ExtraType.DOUBLEJUMP, ExtraManager.ExtraSetting.OFF);
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                        player.closeInventory();
                    } else {
                        if(!extraManager.hasFlymode(player.getUniqueId())) {
                            if(doublejumpDelay.containsKey(player.getUniqueId()) && doublejumpDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                                player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                                return;
                            }
                            doublejumpDelay.put(player.getUniqueId(), System.currentTimeMillis() + 5*1000);

                            extraManager.updateExtraSetting(player.getUniqueId(), ExtraManager.ExtraType.DOUBLEJUMP, ExtraManager.ExtraSetting.ON);
                            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                            player.closeInventory();
                        } else {
                            player.sendMessage(Utils.prefix_lobby + "§7Bitte deaktiviere §cFlugmodus");
                            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                        }
                    }
                } else {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für dieses Extra");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                }
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.INV_DELUXEPEARL_OFF) || e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.INV_DELUXEPEARL_ON)) {
                if(player.hasPermission("spigot.lobby.deluxepearl")) {
                    if(extraManager.hasDeluxepearl(player.getUniqueId())) {
                        if(deluxepearlDelay.containsKey(player.getUniqueId()) && deluxepearlDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                            player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                            return;
                        }
                        deluxepearlDelay.put(player.getUniqueId(), System.currentTimeMillis() + 5*1000);

                        extraManager.updateExtraSetting(player.getUniqueId(), ExtraManager.ExtraType.DELUXEPEARL, ExtraManager.ExtraSetting.OFF);
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                        player.closeInventory();
                    } else {
                        if(deluxepearlDelay.containsKey(player.getUniqueId()) && deluxepearlDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                            player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                            return;
                        }
                        deluxepearlDelay.put(player.getUniqueId(), System.currentTimeMillis() + 5*1000);

                        extraManager.updateExtraSetting(player.getUniqueId(), ExtraManager.ExtraType.DELUXEPEARL, ExtraManager.ExtraSetting.ON);
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                        player.closeInventory();
                    }
                } else {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für dieses Extra");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                }
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.INV_SHIELD_OFF) || e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.INV_SHIELD_ON)) {
                if(player.hasPermission("spigot.lobby.shield")) {
                    if(extraManager.hasShield(player.getUniqueId())) {
                        extraManager.updateExtraSetting(player.getUniqueId(), ExtraManager.ExtraType.SHIELD, ExtraManager.ExtraSetting.OFF);
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                        player.closeInventory();
                    } else {
                        extraManager.updateExtraSetting(player.getUniqueId(), ExtraManager.ExtraType.SHIELD, ExtraManager.ExtraSetting.ON);
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                        player.closeInventory();
                    }
                } else {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §cAdmin §7für dieses Extra");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                }
                return;
            }

        } else if(e.getInventory().getTitle().equals(ItemNames.TITLE_NAVIGATOR)) {
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.NAVIGATOR_SPAWN)) {
                if(locationManager.getSpawn() != null) {
                    player.closeInventory();
                    player.teleport(locationManager.getSpawn());
                    player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                    messageManager.sendTitle(player, 5, 20, 5, "", "§8• §bSpawn §8•");
                } else {
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    player.sendMessage(Utils.prefix_lobby+"§7Der §cSpawn §7wurde nicht gesetzt");
                }
                return;
            }
            if(e.getCurrentItem().getType() == Material.SULPHUR) {
                String servername = e.getCurrentItem().getItemMeta().getDisplayName().replace("§8➜ §b", "");
                ServiceInfoSnapshot server = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServiceByName(servername);
                ICloudPlayer cloudPlayer = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).getOnlinePlayer(player.getUniqueId());
                if(server != null && server.isConnected()) {
                    if(!e.getCurrentItem().getItemMeta().hasEnchants()) {
                        if(delayManager.checkDelay(player.getUniqueId(), DelayType.LOBBYSWITCHER) > System.currentTimeMillis()) {
                            player.closeInventory();
                            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                            player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                            return;
                        }
                        delayManager.setDelay(player.getUniqueId(), DelayType.LOBBYSWITCHER);
                        player.closeInventory();
                        player.sendMessage(Utils.prefix_lobby+"§7Verbinde zu §b"+servername+"§7...");
                        CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).getPlayerExecutor(cloudPlayer).connect(servername);
                    } else {
                        player.closeInventory();
                        player.sendMessage(Utils.prefix_lobby+"§7Du bist bereits in dieser Lobby");
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    }
                } else {
                    player.closeInventory();
                    player.sendMessage(Utils.prefix_lobby+"§7Diese §cLobby §7ist nicht erreichbar");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                }
                return;
            }
            if(e.getCurrentItem().getType() == Material.SUGAR) {
                player.sendMessage(Utils.prefix_lobby+"§7Diese §cLobby §7ist nicht erreichbar");
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                return;
            }
        } else if(e.getInventory().getTitle().equals(ItemNames.TITLE_PLAYERHIDER)) {
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.PLAYERHIDER_LIMEDYE)) {
                if(visibilityManager.getVisibilitySetting(player.getUniqueId()) == VisibilitySetting.ALL) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                visibilityManager.updatePlayerSetting(player.getUniqueId(), VisibilitySetting.ALL);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.PLAYERHIDER_PURPLEDYE)) {
                if(visibilityManager.getVisibilitySetting(player.getUniqueId()) == VisibilitySetting.TEAM) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                visibilityManager.updatePlayerSetting(player.getUniqueId(), VisibilitySetting.TEAM);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.PLAYERHIDER_BLUEDYE)) {
                if(visibilityManager.getVisibilitySetting(player.getUniqueId()) == VisibilitySetting.FRIENDS) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                visibilityManager.updatePlayerSetting(player.getUniqueId(), VisibilitySetting.FRIENDS);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.PLAYERHIDER_GRAYDYE)) {
                if(visibilityManager.getVisibilitySetting(player.getUniqueId()) == VisibilitySetting.NONE) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                visibilityManager.updatePlayerSetting(player.getUniqueId(), VisibilitySetting.NONE);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                return;
            }
        } else if(e.getInventory().getTitle().equals(ItemNames.TITLE_CHATSETTINGS)) {
            if((e.getCurrentItem().getType() == Material.STAINED_GLASS || e.getCurrentItem().getType() == Material.STAINED_CLAY) && chatsettingsDelay.containsKey(player.getUniqueId()) && chatsettingsDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
            }
            if(!e.getCurrentItem().getItemMeta().hasEnchants()) 
                chatsettingsDelay.put(player.getUniqueId(), System.currentTimeMillis() + 10*1000);

            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_BLACK)) {
                if(chatManager.getChatColor(player.getUniqueId()).equals("§0")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.color")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für diese Chatfarbe");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatColor(player.getUniqueId(), "0");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatfarbe geändert zu §0Schwarz");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_DARKBLUE)) {
                if(chatManager.getChatColor(player.getUniqueId()).equals("§1")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.color")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für diese Chatfarbe");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatColor(player.getUniqueId(), "1");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatfarbe geändert zu §1Dunkelblau");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_DARKGREEN)) {
                if(chatManager.getChatColor(player.getUniqueId()).equals("§2")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.color")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für diese Chatfarbe");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatColor(player.getUniqueId(), "2");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatfarbe geändert zu §2Dunkelgrün");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_DARKAQUA)) {
                if(chatManager.getChatColor(player.getUniqueId()).equals("§3")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.color")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für diese Chatfarbe");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatColor(player.getUniqueId(), "3");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatfarbe geändert zu §3Türkis");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_DARKRED)) {
                if(chatManager.getChatColor(player.getUniqueId()).equals("§4")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.color")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für diese Chatfarbe");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatColor(player.getUniqueId(), "4");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatfarbe geändert zu §4Dunkelrot");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_DARKPURPLE)) {
                if(chatManager.getChatColor(player.getUniqueId()).equals("§5")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.color")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für diese Chatfarbe");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatColor(player.getUniqueId(), "5");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatfarbe geändert zu §5Violett");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_GOLD)) {
                if(chatManager.getChatColor(player.getUniqueId()).equals("§6")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.color")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für diese Chatfarbe");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatColor(player.getUniqueId(), "6");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatfarbe geändert zu §6Orange");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_GRAY)) {
                if(chatManager.getChatColor(player.getUniqueId()).equals("§7")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatColor(player.getUniqueId(), "7");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatfarbe geändert zu §7Grau");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_DARKGRAY)) {
                if(chatManager.getChatColor(player.getUniqueId()).equals("§8")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.color")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für diese Chatfarbe");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatColor(player.getUniqueId(), "8");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatfarbe geändert zu §8Dunkelgrau");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_BLUE)) {
                if(chatManager.getChatColor(player.getUniqueId()).equals("§9")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.color")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für diese Chatfarbe");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatColor(player.getUniqueId(), "9");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatfarbe geändert zu §9Blau");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_GREEN)) {
                if(chatManager.getChatColor(player.getUniqueId()).equals("§a")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.color")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für diese Chatfarbe");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatColor(player.getUniqueId(), "a");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatfarbe geändert zu §aGrün");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_AQUA)) {
                if(chatManager.getChatColor(player.getUniqueId()).equals("§b")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.color")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für diese Chatfarbe");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatColor(player.getUniqueId(), "b");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatfarbe geändert zu §bAqua");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_RED)) {
                if(chatManager.getChatColor(player.getUniqueId()).equals("§c")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.color")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für diese Chatfarbe");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatColor(player.getUniqueId(), "c");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatfarbe geändert zu §cRot");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_PURPLE)) {
                if(chatManager.getChatColor(player.getUniqueId()).equals("§d")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.color")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für diese Chatfarbe");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatColor(player.getUniqueId(), "d");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatfarbe geändert zu §dPink");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_YELLOW)) {
                if(chatManager.getChatColor(player.getUniqueId()).equals("§e")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.color")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für diese Chatfarbe");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatColor(player.getUniqueId(), "e");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatfarbe geändert zu §eGelb");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_WHITE)) {
                if(chatManager.getChatColor(player.getUniqueId()).equals("§f")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.color")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §6Premium §7für diese Chatfarbe");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatColor(player.getUniqueId(), "f");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatfarbe geändert zu §fWeiß");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_VANISH)) {
                if(chatManager.getChatFormat(player.getUniqueId()) == ChatFormat.VANISH) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.admin")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §cAdmin §7für diesen Chatformat");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatFormat(player.getUniqueId(), ChatFormat.VANISH);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatformat geändert zu §b§kGGGGGGGG");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_CROSSED)) {
                if(chatManager.getChatFormat(player.getUniqueId()) == ChatFormat.CROSSED) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.admin")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §cAdmin §7für diesen Chatformat");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatFormat(player.getUniqueId(), ChatFormat.CROSSED);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatformat geändert zu §b§mDurchgestrichen");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_UNDERLINED)) {
                if(chatManager.getChatFormat(player.getUniqueId()) == ChatFormat.UNDERLINED) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.admin")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §cAdmin §7für diesen Chatformat");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatFormat(player.getUniqueId(), ChatFormat.UNDERLINED);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatformat geändert zu §b§nUnterstrichen");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_BOLD)) {
                if(chatManager.getChatFormat(player.getUniqueId()) == ChatFormat.BOLD) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.admin")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §cAdmin §7für diesen Chatformat");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatFormat(player.getUniqueId(), ChatFormat.BOLD);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatformat geändert zu §b§lFett");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_ITALIC)) {
                if(chatManager.getChatFormat(player.getUniqueId()) == ChatFormat.ITALIC) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                if(!player.hasPermission("proxy.chat.admin")) {
                    player.sendMessage(Utils.prefix_lobby+"§7Du benötigst §cAdmin §7für diesen Chatformat");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatFormat(player.getUniqueId(), ChatFormat.ITALIC);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatformat geändert zu §b§oKursiv");
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.CHATSETTINGS_NONE)) {
                if(chatManager.getChatFormat(player.getUniqueId()) == ChatFormat.NONE) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                chatManager.updatePlayerChatFormat(player.getUniqueId(), ChatFormat.NONE);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(Utils.prefix_lobby + "§7Chatformat geändert zu §bStandard");
                return;
            }
        } else if(e.getInventory().getTitle().equals(ItemNames.TITLE_PROFILE)) {
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.PROFILE_CHATSETTINGS)) {
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
                InventoryBuilder inventoryBuilder = new InventoryBuilder();
                inventoryBuilder.setSize(9*5);
                inventoryBuilder.setTitle(ItemNames.TITLE_CHATSETTINGS);
                ItemBuilder blackBuilder = new ItemBuilder(Material.STAINED_GLASS, 15).setDisplayName(ItemNames.CHATSETTINGS_BLACK).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
                ItemBuilder darkblueBuilder = new ItemBuilder(Material.STAINED_GLASS, 11).setDisplayName(ItemNames.CHATSETTINGS_DARKBLUE).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
                ItemBuilder darkgreenBuilder = new ItemBuilder(Material.STAINED_GLASS, 13).setDisplayName(ItemNames.CHATSETTINGS_DARKGREEN).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
                ItemBuilder darkaquaBuilder = new ItemBuilder(Material.STAINED_GLASS, 9).setDisplayName(ItemNames.CHATSETTINGS_DARKAQUA).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
                ItemBuilder darkredBuilder = new ItemBuilder(Material.STAINED_GLASS, 14).setDisplayName(ItemNames.CHATSETTINGS_DARKRED).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
                ItemBuilder darkpurpleBuilder = new ItemBuilder(Material.STAINED_GLASS, 10).setDisplayName(ItemNames.CHATSETTINGS_DARKPURPLE).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
                ItemBuilder goldBuilder = new ItemBuilder(Material.STAINED_GLASS, 1).setDisplayName(ItemNames.CHATSETTINGS_GOLD).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
                ItemBuilder grayBuilder = new ItemBuilder(Material.STAINED_GLASS, 8).setDisplayName(ItemNames.CHATSETTINGS_GRAY).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
                ItemBuilder darkgrayBuilder = new ItemBuilder(Material.STAINED_GLASS, 7).setDisplayName(ItemNames.CHATSETTINGS_DARKGRAY).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
                ItemBuilder blueBuilder = new ItemBuilder(Material.STAINED_CLAY, 3).setDisplayName(ItemNames.CHATSETTINGS_BLUE).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
                ItemBuilder greenBuilder = new ItemBuilder(Material.STAINED_GLASS, 5).setDisplayName(ItemNames.CHATSETTINGS_GREEN).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
                ItemBuilder aquaBuilder = new ItemBuilder(Material.STAINED_GLASS, 3).setDisplayName(ItemNames.CHATSETTINGS_AQUA).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
                ItemBuilder redBuilder = new ItemBuilder(Material.STAINED_CLAY, 6).setDisplayName(ItemNames.CHATSETTINGS_RED).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
                ItemBuilder purpleBuilder = new ItemBuilder(Material.STAINED_GLASS, 2).setDisplayName(ItemNames.CHATSETTINGS_PURPLE).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
                ItemBuilder yellowBuilder = new ItemBuilder(Material.STAINED_GLASS, 4).setDisplayName(ItemNames.CHATSETTINGS_YELLOW).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
                ItemBuilder whiteBuilder = new ItemBuilder(Material.STAINED_GLASS, 0).setDisplayName(ItemNames.CHATSETTINGS_WHITE).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
                switch (chatManager.getChatColor(player.getUniqueId())) {
                    case "§0":
                        blackBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "").setEnchanted(true);
                        break;
                    case "§1":
                        darkblueBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "").setEnchanted(true);
                        break;
                    case "§2":
                        darkgreenBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "").setEnchanted(true);
                        break;
                    case "§3":
                        darkaquaBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "").setEnchanted(true);
                        break;
                    case "§4":
                        darkredBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "").setEnchanted(true);
                        break;
                    case "§5":
                        darkpurpleBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "").setEnchanted(true);
                        break;
                    case "§6":
                        goldBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "").setEnchanted(true);
                        break;
                    case "§7":
                        grayBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "").setEnchanted(true);
                        break;
                    case "§8":
                        darkgrayBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "").setEnchanted(true);
                        break;
                    case "§9":
                        blueBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "").setEnchanted(true);
                        break;
                    case "§a":
                        greenBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "").setEnchanted(true);
                        break;
                    case "§b":
                        aquaBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "").setEnchanted(true);
                        break;
                    case "§c":
                        redBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "").setEnchanted(true);
                        break;
                    case "§d":
                        purpleBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "").setEnchanted(true);
                        break;
                    case "§e":
                        yellowBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "").setEnchanted(true);
                        break;
                    case "§f":
                        whiteBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "").setEnchanted(true);
                        break;
                    default:
                        break;
                }
                ItemStack black = blackBuilder.build(); ItemStack darkblue = darkblueBuilder.build(); ItemStack darkgreen = darkgreenBuilder.build();
                ItemStack darkaqua = darkaquaBuilder.build(); ItemStack darkred = darkredBuilder.build(); ItemStack darkpurple = darkpurpleBuilder.build();
                ItemStack gold = goldBuilder.build(); ItemStack gray = grayBuilder.build(); ItemStack darkgray = darkgrayBuilder.build();
                ItemStack blue = blueBuilder.build(); ItemStack green = greenBuilder.build(); ItemStack aqua = aquaBuilder.build();
                ItemStack red = redBuilder.build(); ItemStack purple = purpleBuilder.build(); ItemStack yellow = yellowBuilder.build();
                ItemStack white = whiteBuilder.build();

                inventoryBuilder.setItem(1, black); inventoryBuilder.setItem(3, darkblue); inventoryBuilder.setItem(5, darkgreen);
                inventoryBuilder.setItem(7, darkaqua); inventoryBuilder.setItem(11, darkred); inventoryBuilder.setItem(13, darkpurple);
                inventoryBuilder.setItem(15, gold); inventoryBuilder.setItem(19, gray); inventoryBuilder.setItem(21, darkgray);
                inventoryBuilder.setItem(23, blue); inventoryBuilder.setItem(25, green); inventoryBuilder.setItem(29, aqua);
                inventoryBuilder.setItem(31, red); inventoryBuilder.setItem(33, purple); inventoryBuilder.setItem(39, yellow);
                inventoryBuilder.setItem(41, white);
                inventoryBuilder.fillInventory(new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build());
                player.openInventory(inventoryBuilder.build());
                inventoryManager.openChatsettings(player);
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.PROFILE_GADGETS)) {
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
                gadgetInventory.openGadgetMenu(player);
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.PROFILE_FRIENDS)) {
//                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
//                friendInventory.openFriendMenu(player, 1);
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                player.sendMessage(Utils.prefix_lobby+"§7Verfügbar ab der §cBetaphase");
                return;
            }
        }
//        else if(e.getInventory().getTitle().startsWith(ItemNames.TITLE_FRIENDS) && !e.getInventory().getTitle().equals(ItemNames.TITLE_MANAGEFRIEND) && !e.getInventory().getTitle().equals(ItemNames.TITLE_REMOVEFRIEND)) {
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_REQUESTSMENU)) {
//                if(friendMenuDelay.containsKey(player.getUniqueId()) && friendMenuDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
//                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
//                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                    return;
//                }
//                friendMenuDelay.put(player.getUniqueId(), System.currentTimeMillis() + 3*1000);
//
//                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
//                friendInventory.openRequestsMenu(player, 1);
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_SETTINGSMENU)) {
//                if(friendMenuDelay.containsKey(player.getUniqueId()) && friendMenuDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
//                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
//                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                    return;
//                }
//                friendMenuDelay.put(player.getUniqueId(), System.currentTimeMillis() + 3*1000);
//
//                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
//                friendInventory.openSettingsMenu(player);
//                return;
//            }
//            if(e.getCurrentItem().getType() == Material.SKULL_ITEM && e.getCurrentItem().getItemMeta().hasLore()) {
//                if(friendHeadDelay.containsKey(player.getUniqueId()) && friendHeadDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
//                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
//                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                    return;
//                }
//                friendHeadDelay.put(player.getUniqueId(), System.currentTimeMillis() + 3*1000);
//
//                String playerName = e.getCurrentItem().getItemMeta().getDisplayName();
//                playerName = playerName.replace("§0", ""); playerName = playerName.replace("§7", "");
//                playerName = playerName.replace("§1", ""); playerName = playerName.replace("§8", "");
//                playerName = playerName.replace("§2", ""); playerName = playerName.replace("§9", "");
//                playerName = playerName.replace("§3", ""); playerName = playerName.replace("§a", "");
//                playerName = playerName.replace("§4", ""); playerName = playerName.replace("§b", "");
//                playerName = playerName.replace("§5", ""); playerName = playerName.replace("§c", "");
//                playerName = playerName.replace("§6", ""); playerName = playerName.replace("§d", "");
//                playerName = playerName.replace("§e", ""); playerName = playerName.replace("§f", "");
//                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
//                friendInventory.openManageFriendMenu(player, playerName);
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_PAGEBACK)) {
//                if(friendInventory.getPageIndexFriends(player.getUniqueId()) == 1)
//                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
//                else {
//                    friendInventory.openFriendMenu(player, friendInventory.getPageIndexFriends(player.getUniqueId()) - 1);
//                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
//                }
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_PAGENEXT)) {
//                if(e.getClickedInventory().getItem(35) == null)
//                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
//                else {
//                    friendInventory.openFriendMenu(player, friendInventory.getPageIndexFriends(player.getUniqueId()) + 1);
//                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
//                }
//                return;
//            }
//        } else if(e.getInventory().getTitle().startsWith(ItemNames.TITLE_REQUESTS) && !e.getInventory().getTitle().equals(ItemNames.TITLE_NEWFRIEND)) {
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_FRIENDMENU)) {
//                if(friendMenuDelay.containsKey(player.getUniqueId()) && friendMenuDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
//                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
//                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                    return;
//                }
//                friendMenuDelay.put(player.getUniqueId(), System.currentTimeMillis() + 3*1000);
//
//                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
//                friendInventory.openFriendMenu(player, 1);
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_SETTINGSMENU)) {
//                if(friendMenuDelay.containsKey(player.getUniqueId()) && friendMenuDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
//                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
//                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                    return;
//                }
//                friendMenuDelay.put(player.getUniqueId(), System.currentTimeMillis() + 3*1000);
//
//                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
//                friendInventory.openSettingsMenu(player);
//                return;
//            }
//            if(e.getCurrentItem().getType() == Material.SKULL_ITEM && e.getCurrentItem().getItemMeta().hasLore()) {
//                if(requestHeadDelay.containsKey(player.getUniqueId()) && requestHeadDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
//                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
//                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                    return;
//                }
//                requestHeadDelay.put(player.getUniqueId(), System.currentTimeMillis() + 3*1000);
//
//                String playerName = e.getCurrentItem().getItemMeta().getDisplayName();
//                playerName = playerName.replace("§0", ""); playerName = playerName.replace("§7", "");
//                playerName = playerName.replace("§1", ""); playerName = playerName.replace("§8", "");
//                playerName = playerName.replace("§2", ""); playerName = playerName.replace("§9", "");
//                playerName = playerName.replace("§3", ""); playerName = playerName.replace("§a", "");
//                playerName = playerName.replace("§4", ""); playerName = playerName.replace("§b", "");
//                playerName = playerName.replace("§5", ""); playerName = playerName.replace("§c", "");
//                playerName = playerName.replace("§6", ""); playerName = playerName.replace("§d", "");
//                playerName = playerName.replace("§e", ""); playerName = playerName.replace("§f", "");
//                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
//                friendInventory.openNewFriendMenu(player, playerName);
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_PAGEBACK)) {
//                if(friendInventory.getPageIndexRequests(player.getUniqueId()) == 1)
//                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
//                else {
//                    friendInventory.openRequestsMenu(player, friendInventory.getPageIndexRequests(player.getUniqueId()) - 1);
//                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
//                }
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_PAGENEXT)) {
//                if(e.getClickedInventory().getItem(35) == null)
//                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
//                else {
//                    friendInventory.openRequestsMenu(player, friendInventory.getPageIndexRequests(player.getUniqueId()) + 1);
//                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
//                }
//                return;
//            }
//        } else if(e.getInventory().getTitle().equals(ItemNames.TITLE_FRIENDSETTINGS)) {
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_FRIENDMENU)) {
//                if(friendMenuDelay.containsKey(player.getUniqueId()) && friendMenuDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
//                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
//                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                    return;
//                }
//                friendMenuDelay.put(player.getUniqueId(), System.currentTimeMillis() + 3*1000);
//
//                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
//                friendInventory.openFriendMenu(player, 1);
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_REQUESTSMENU)) {
//                if(friendMenuDelay.containsKey(player.getUniqueId()) && friendMenuDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
//                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
//                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                    return;
//                }
//                friendMenuDelay.put(player.getUniqueId(), System.currentTimeMillis() + 3*1000);
//
//                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
//                friendInventory.openRequestsMenu(player, 1);
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDSETTINGS_REQUESTS)) {
//                if(friendSettingsDelay.containsKey(player.getUniqueId()) && friendSettingsDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
//                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
//                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                    return;
//                }
//                friendSettingsDelay.put(player.getUniqueId(), System.currentTimeMillis() + 1*1000);
//
//                HashMap<FriendSetting, FriendValue> friendSettings = friendManager.getSettings(player.getUniqueId());
//                if(friendSettings.get(FriendSetting.FREUNDESANFRAGEN) == FriendValue.AN)
//                    friendManager.updateSettings(player.getUniqueId(), FriendSetting.FREUNDESANFRAGEN, FriendValue.AUS);
//                else
//                    friendManager.updateSettings(player.getUniqueId(), FriendSetting.FREUNDESANFRAGEN, FriendValue.AN);
//
//                friendInventory.openSettingsMenu(player);
//                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDSETTINGS_PARTYINVITES)) {
//                if(friendSettingsDelay.containsKey(player.getUniqueId()) && friendSettingsDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
//                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
//                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                    return;
//                }
//                friendSettingsDelay.put(player.getUniqueId(), System.currentTimeMillis() + 1*1000);
//
//                HashMap<FriendSetting, FriendValue> friendSettings = friendManager.getSettings(player.getUniqueId());
//                if(friendSettings.get(FriendSetting.PARTYANFRAGEN) == FriendValue.AN)
//                    friendManager.updateSettings(player.getUniqueId(), FriendSetting.PARTYANFRAGEN, FriendValue.FREUNDE);
//                else if(friendSettings.get(FriendSetting.PARTYANFRAGEN) == FriendValue.FREUNDE)
//                    friendManager.updateSettings(player.getUniqueId(), FriendSetting.PARTYANFRAGEN, FriendValue.AUS);
//                else if(friendSettings.get(FriendSetting.PARTYANFRAGEN) == FriendValue.AUS)
//                    friendManager.updateSettings(player.getUniqueId(), FriendSetting.PARTYANFRAGEN, FriendValue.AN);
//
//                friendInventory.openSettingsMenu(player);
//                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDSETTINGS_JUMPTO)) {
//                if(friendSettingsDelay.containsKey(player.getUniqueId()) && friendSettingsDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
//                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
//                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                    return;
//                }
//                friendSettingsDelay.put(player.getUniqueId(), System.currentTimeMillis() + 1*1000);
//
//                HashMap<FriendSetting, FriendValue> friendSettings = friendManager.getSettings(player.getUniqueId());
//                if(friendSettings.get(FriendSetting.NACHSPRINGEN) == FriendValue.FREUNDE)
//                    friendManager.updateSettings(player.getUniqueId(), FriendSetting.NACHSPRINGEN, FriendValue.AUS);
//                else
//                    friendManager.updateSettings(player.getUniqueId(), FriendSetting.NACHSPRINGEN, FriendValue.FREUNDE);
//
//                friendInventory.openSettingsMenu(player);
//                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDSETTINGS_MESSAGES)) {
//                if(friendSettingsDelay.containsKey(player.getUniqueId()) && friendSettingsDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
//                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
//                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                    return;
//                }
//                friendSettingsDelay.put(player.getUniqueId(), System.currentTimeMillis() + 1*1000);
//
//                HashMap<FriendSetting, FriendValue> friendSettings = friendManager.getSettings(player.getUniqueId());
//                if(friendSettings.get(FriendSetting.NACHRICHTEN) == FriendValue.AN)
//                    friendManager.updateSettings(player.getUniqueId(), FriendSetting.NACHRICHTEN, FriendValue.FREUNDE);
//                else if(friendSettings.get(FriendSetting.NACHRICHTEN) == FriendValue.FREUNDE)
//                    friendManager.updateSettings(player.getUniqueId(), FriendSetting.NACHRICHTEN, FriendValue.AUS);
//                else if(friendSettings.get(FriendSetting.NACHRICHTEN) == FriendValue.AUS)
//                    friendManager.updateSettings(player.getUniqueId(), FriendSetting.NACHRICHTEN, FriendValue.AN);
//
//                friendInventory.openSettingsMenu(player);
//                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
//                return;
//            }
//        } else if(e.getInventory().getTitle().equals(ItemNames.TITLE_NEWFRIEND)) {
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.REQUESTS_ACCEPTFRIEND)) {
//
//
//                friendManager.acceptFriendRequest(player, player.getUniqueId(), friendManager.getUUID(friendInventory.getNewFriendCache(player.getUniqueId())));
//                friendInventory.openRequestsMenu(player, 1);
//
//
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.REQUESTS_DENYFRIEND)) {
//                friendManager.denyFriendRequest(player, player.getUniqueId(), friendManager.getUUID(friendInventory.getNewFriendCache(player.getUniqueId())));
//                friendInventory.openRequestsMenu(player, 1);
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_BACK)) {
//                friendInventory.openRequestsMenu(player, friendInventory.getPageIndexRequests(player.getUniqueId()));
//                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
//                return;
//            }
//        } else if(e.getInventory().getTitle().equals(ItemNames.TITLE_MANAGEFRIEND)) {
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_PARTYINVITE)) {
//                ICloudPlayer cloudPlayerFriend = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).getOnlinePlayer(friendManager.getUUID(friendInventory.getManageFriendCache(player.getUniqueId())));
//                if(cloudPlayerFriend == null) {
//                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                    player.sendMessage(Utils.prefix_freunde+Utils.error_notonline);
//                    return;
//                }
//
//                HashMap<FriendSetting, FriendValue> friendSettings = friendManager.getSettings(friendManager.getUUID(friendInventory.getManageFriendCache(player.getUniqueId())));
//                if(friendSettings.get(FriendSetting.PARTYANFRAGEN) == FriendValue.FREUNDE) {
//                    if (!player.hasPermission("proxy.friends.admin")) {
//                        if (!friendManager.areFriends(player.getUniqueId(), friendManager.getUUID(friendInventory.getManageFriendCache(player.getUniqueId())))) {
//                            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                            player.sendMessage(Utils.prefix_party + "§7Du bist nicht mit diesem §cSpieler §7befreundet");
//                            instance.getFriendInventory().openFriendMenu(player, instance.getFriendInventory().getPageIndexFriends(player.getUniqueId()));
//                            return;
//                        }
//                    }
//                } else if(friendSettings.get(FriendSetting.PARTYANFRAGEN) == FriendValue.AUS) {
//                    if (!player.hasPermission("proxy.friends.admin")) {
//                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                        player.sendMessage(Utils.prefix_party+"§7Dieser §cSpieler §7hat Partyanfragen deaktiviert");
//                        return;
//                    }
//                }
//
//                ByteArrayDataOutput out = ByteStreams.newDataOutput();
//                out.writeUTF("partyInvite");
//                out.writeUTF(friendInventory.getManageFriendCache(player.getUniqueId()));
//                player.sendPluginMessage(instance, "BungeeCord", out.toByteArray());
//
//                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_JUMPTO)) {
//                friendManager.jumptoFriend(player, player.getUniqueId(), friendManager.getUUID(friendInventory.getManageFriendCache(player.getUniqueId())));
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_REMOVEFRIEND)) {
//                friendInventory.openFriendRemoveMenu(player, friendInventory.getManageFriendCache(player.getUniqueId()));
//                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_BACK)) {
//                friendInventory.openFriendMenu(player, friendInventory.getPageIndexFriends(player.getUniqueId()));
//                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
//                return;
//            }
//        } else if(e.getInventory().getTitle().equals(ItemNames.TITLE_REMOVEFRIEND)) {
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_REMOVEFRIENDACCEPT)) {
//                friendManager.removeExistingFriend(player, player.getUniqueId(), friendManager.getUUID(friendInventory.getManageFriendCache(player.getUniqueId())));
//                friendInventory.openFriendMenu(player, 1);
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_REMOVEFRIENDCANCEL)) {
//                friendInventory.openManageFriendMenu(player, friendInventory.getManageFriendCache(player.getUniqueId()));
//                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
//                return;
//            }
//            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.FRIENDS_BACK)) {
//                friendInventory.openManageFriendMenu(player, friendInventory.getManageFriendCache(player.getUniqueId()));
//                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
//                return;
//            }
//        }
        else if(e.getInventory().getTitle().equals(ItemNames.TITLE_GADGETS)) {
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.GADGETS_SKULLS)) {
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
                gadgetInventory.openSkullsMenu(player);
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.GADGETS_HATS)) {
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
                gadgetInventory.openHatsMenu(player);
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.GADGETS_BOOTS)) {
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
                gadgetInventory.openBootsMenu(player);
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.GADGETS_TOOLS)) {
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
                gadgetInventory.openToolsMenu(player);
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.GADGETS_RAIN)) {
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
                gadgetInventory.openRainMenu(player);
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.GADGETS_DEACTIVATEALL)) {
                if(gadgetSelectDelay.containsKey(player.getUniqueId()) && gadgetSelectDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                gadgetSelectDelay.put(player.getUniqueId(), System.currentTimeMillis() + 2*1000);

                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                gadgetManager.removeAllGadgets(player.getUniqueId());
                return;
            }
        } else if(e.getInventory().getTitle().equals(ItemNames.TITLE_SKULLS)) {
            if(e.getCurrentItem().getType() == Material.SKULL_ITEM && e.getCurrentItem().getItemMeta().hasLore()) {
                if(!gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.SKULL, gadgetInventory.getGadgetSetting(GadgetManager.GadgetType.SKULL, e.getCurrentItem().getItemMeta().getDisplayName()))) {
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
                    gadgetInventory.openBuyMenu(player, e.getInventory().getTitle(), e.getCurrentItem());
                    return;
                }
                if(gadgetManager.getSkullGadget(player.getUniqueId()).equals(gadgetInventory.getGadgetSetting(GadgetManager.GadgetType.SKULL, e.getCurrentItem().getItemMeta().getDisplayName()))) {
                    if(gadgetSelectDelay.containsKey(player.getUniqueId()) && gadgetSelectDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                        player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                        return;
                    }
                    gadgetSelectDelay.put(player.getUniqueId(), System.currentTimeMillis() + 2*1000);

                    gadgetManager.removeCurrentGadget(player.getUniqueId(), GadgetManager.GadgetType.SKULL);
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                    gadgetInventory.openSkullsMenu(player);
                    return;
                }
                if(gadgetSelectDelay.containsKey(player.getUniqueId()) && gadgetSelectDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                gadgetSelectDelay.put(player.getUniqueId(), System.currentTimeMillis() + 2*1000);

                gadgetManager.updateGadgetSetting(player.getUniqueId(), GadgetManager.GadgetType.SKULL, gadgetInventory.getGadgetSetting(GadgetManager.GadgetType.SKULL, e.getCurrentItem().getItemMeta().getDisplayName()));
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                gadgetInventory.openSkullsMenu(player);
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.GADGETS_BACK)) {
                gadgetInventory.openGadgetMenu(player);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                return;
            }
        } else if(e.getInventory().getTitle().equals(ItemNames.TITLE_HATS)) {
            if(e.getCurrentItem().getType() == Material.GLASS || e.getCurrentItem().getType() == Material.MOB_SPAWNER || e.getCurrentItem().getType() == Material.COMMAND || e.getCurrentItem().getType() == Material.LEAVES || e.getCurrentItem().getType() == Material.SLIME_BLOCK || e.getCurrentItem().getType() == Material.REDSTONE_LAMP_OFF) {

                if(!gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.HAT, gadgetInventory.getGadgetSetting(GadgetManager.GadgetType.HAT, e.getCurrentItem().getItemMeta().getDisplayName()))) {
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
                    gadgetInventory.openBuyMenu(player, e.getInventory().getTitle(), e.getCurrentItem());
                    return;
                }
                if(gadgetManager.getHatGadget(player.getUniqueId()).equals(gadgetInventory.getGadgetSetting(GadgetManager.GadgetType.HAT, e.getCurrentItem().getItemMeta().getDisplayName()))) {
                    if(gadgetSelectDelay.containsKey(player.getUniqueId()) && gadgetSelectDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                        player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                        return;
                    }
                    gadgetSelectDelay.put(player.getUniqueId(), System.currentTimeMillis() + 2*1000);

                    gadgetManager.removeCurrentGadget(player.getUniqueId(), GadgetManager.GadgetType.HAT);
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                    gadgetInventory.openHatsMenu(player);
                    return;
                }
                if(gadgetSelectDelay.containsKey(player.getUniqueId()) && gadgetSelectDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                gadgetSelectDelay.put(player.getUniqueId(), System.currentTimeMillis() + 2*1000);

                gadgetManager.updateGadgetSetting(player.getUniqueId(), GadgetManager.GadgetType.HAT, gadgetInventory.getGadgetSetting(GadgetManager.GadgetType.HAT, e.getCurrentItem().getItemMeta().getDisplayName()));
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                gadgetInventory.openHatsMenu(player);
                return;

            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.GADGETS_BACK)) {
                gadgetInventory.openGadgetMenu(player);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                return;
            }
        } else if(e.getInventory().getTitle().equals(ItemNames.TITLE_TOOLS)) {
            if(e.getCurrentItem().getType() == Material.FISHING_ROD || e.getCurrentItem().getType() == Material.DIAMOND_BARDING) {

                if(!gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.TOOL, gadgetInventory.getGadgetSetting(GadgetManager.GadgetType.TOOL, e.getCurrentItem().getItemMeta().getDisplayName()))) {
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
                    gadgetInventory.openBuyMenu(player, e.getInventory().getTitle(), e.getCurrentItem());
                    return;
                }
                if(gadgetManager.getToolGadget(player.getUniqueId()).equals(gadgetInventory.getGadgetSetting(GadgetManager.GadgetType.TOOL, e.getCurrentItem().getItemMeta().getDisplayName()))) {
                    if(gadgetSelectDelay.containsKey(player.getUniqueId()) && gadgetSelectDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                        player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                        return;
                    }
                    gadgetSelectDelay.put(player.getUniqueId(), System.currentTimeMillis() + 2*1000);

                    gadgetManager.removeCurrentGadget(player.getUniqueId(), GadgetManager.GadgetType.TOOL);
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                    gadgetInventory.openToolsMenu(player);
                    return;
                }
                if(gadgetSelectDelay.containsKey(player.getUniqueId()) && gadgetSelectDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                gadgetSelectDelay.put(player.getUniqueId(), System.currentTimeMillis() + 2*1000);

                gadgetManager.updateGadgetSetting(player.getUniqueId(), GadgetManager.GadgetType.TOOL, gadgetInventory.getGadgetSetting(GadgetManager.GadgetType.TOOL, e.getCurrentItem().getItemMeta().getDisplayName()));
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                gadgetInventory.openToolsMenu(player);
                return;

            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.GADGETS_BACK)) {
                gadgetInventory.openGadgetMenu(player);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                return;
            }
        } else if(e.getInventory().getTitle().equals(ItemNames.TITLE_BOOTS)) {
            if(e.getCurrentItem().getType() == Material.LEATHER_BOOTS) {

                if(!gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.BOOTS, gadgetInventory.getGadgetSetting(GadgetManager.GadgetType.BOOTS, e.getCurrentItem().getItemMeta().getDisplayName()))) {
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
                    gadgetInventory.openBuyMenu(player, e.getInventory().getTitle(), e.getCurrentItem());
                    return;
                }
                if(gadgetManager.getBootsGadget(player.getUniqueId()).equals(gadgetInventory.getGadgetSetting(GadgetManager.GadgetType.BOOTS, e.getCurrentItem().getItemMeta().getDisplayName()))) {
                    if(gadgetSelectDelay.containsKey(player.getUniqueId()) && gadgetSelectDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                        player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                        return;
                    }
                    gadgetSelectDelay.put(player.getUniqueId(), System.currentTimeMillis() + 2*1000);

                    gadgetManager.removeCurrentGadget(player.getUniqueId(), GadgetManager.GadgetType.BOOTS);
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                    gadgetInventory.openBootsMenu(player);
                    return;
                }
                if(gadgetSelectDelay.containsKey(player.getUniqueId()) && gadgetSelectDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                gadgetSelectDelay.put(player.getUniqueId(), System.currentTimeMillis() + 2*1000);

                gadgetManager.updateGadgetSetting(player.getUniqueId(), GadgetManager.GadgetType.BOOTS, gadgetInventory.getGadgetSetting(GadgetManager.GadgetType.BOOTS, e.getCurrentItem().getItemMeta().getDisplayName()));
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                gadgetInventory.openBootsMenu(player);
                return;

            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.GADGETS_BACK)) {
                gadgetInventory.openGadgetMenu(player);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                return;
            }
        } else if(e.getInventory().getTitle().equals(ItemNames.TITLE_RAIN)) {
            if(e.getCurrentItem().getType() == Material.DIAMOND || e.getCurrentItem().getType() == Material.EMERALD || e.getCurrentItem().getType() == Material.REDSTONE || e.getCurrentItem().getType() == Material.GOLD_NUGGET || e.getCurrentItem().getType() == Material.SNOW_BALL || e.getCurrentItem().getType() == Material.NETHER_STAR) {

                if(!gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.RAIN, gadgetInventory.getGadgetSetting(GadgetManager.GadgetType.RAIN, e.getCurrentItem().getItemMeta().getDisplayName()))) {
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
                    gadgetInventory.openBuyMenu(player, e.getInventory().getTitle(), e.getCurrentItem());
                    return;
                }
                if(gadgetManager.getRainGadget(player.getUniqueId()).equals(gadgetInventory.getGadgetSetting(GadgetManager.GadgetType.RAIN, e.getCurrentItem().getItemMeta().getDisplayName()))) {
                    if(gadgetSelectDelay.containsKey(player.getUniqueId()) && gadgetSelectDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                        player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                        return;
                    }
                    gadgetSelectDelay.put(player.getUniqueId(), System.currentTimeMillis() + 2*1000);

                    gadgetManager.removeCurrentGadget(player.getUniqueId(), GadgetManager.GadgetType.RAIN);
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                    gadgetInventory.openRainMenu(player);
                    return;
                }
                if(gadgetSelectDelay.containsKey(player.getUniqueId()) && gadgetSelectDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    return;
                }
                gadgetSelectDelay.put(player.getUniqueId(), System.currentTimeMillis() + 2*1000);

                gadgetManager.updateGadgetSetting(player.getUniqueId(), GadgetManager.GadgetType.RAIN, gadgetInventory.getGadgetSetting(GadgetManager.GadgetType.RAIN, e.getCurrentItem().getItemMeta().getDisplayName()));
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                gadgetInventory.openRainMenu(player);
                return;

            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.GADGETS_BACK)) {
                gadgetInventory.openGadgetMenu(player);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                return;
            }
        } else if(e.getInventory().getTitle().equals(ItemNames.TITLE_BUYGADGET)) {
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.GADGETS_BACK)) {
                gadgetInventory.openPreviousMenu(player);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.GADGETS_CONTINUEBUY)) {
                if (gadgetManager.buyNewGadget(player.getUniqueId(), gadgetInventory.getCurrentGadgetType(player.getUniqueId()), gadgetInventory.getCurrentGadgetSetting(player.getUniqueId()))) {
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                    gadgetInventory.openPreviousMenu(player);
                } else {
                    player.sendMessage(Utils.prefix_coinsystem+"§7Deine §cCoins §7reichen nicht aus");
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
                    gadgetInventory.openPreviousMenu(player);
                }
                return;
            }
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemNames.GADGETS_CANCELBUY)) {
                gadgetInventory.openPreviousMenu(player);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                return;
            }
        }
    }


}

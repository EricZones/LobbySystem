// Created by Eric B. 17.05.2021 12:48
package de.ericzones.lobbysystem.inventories;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.InventoryBuilder;
import de.ericzones.lobbysystem.extra.ItemBuilder;
import de.ericzones.lobbysystem.extra.ItemNames;
import de.ericzones.lobbysystem.friend.FriendManager;
import de.ericzones.lobbysystem.friend.FriendSetting;
import de.ericzones.lobbysystem.friend.FriendValue;
import de.ericzones.lobbysystem.manager.InventoryManager;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class FriendInventory {

    private LobbySystem instance;

    private FriendManager friendManager;
    private InventoryManager inventoryManager;

    private HashMap<UUID, Integer> pageIndexFriends;
    private HashMap<UUID, Integer> pageIndexRequests;
    private HashMap<String, String> playerCache;
    private HashMap<UUID, String> newFriendCache;
    private HashMap<UUID, String> manageFriendCache;

    public FriendInventory(LobbySystem instance) {
        this.instance = instance;
        pageIndexFriends = new HashMap<>();
        pageIndexRequests = new HashMap<>();
        playerCache = new HashMap<>();
        newFriendCache = new HashMap<>();
        manageFriendCache = new HashMap<>();
        friendManager = instance.getFriendManager();
        inventoryManager = instance.getInventoryManager();
    }
//
//    public void openFriendRemoveMenu(Player player, String name) {
//        InventoryBuilder inventoryBuilder = new InventoryBuilder();
//        inventoryBuilder.setSize(9*3);
//        inventoryBuilder.setTitle(ItemNames.TITLE_REMOVEFRIEND);
//
//        UUID friendUUID = friendManager.getUUID(name);
//        ICloudPlayer cloudPlayerFriend = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).getOnlinePlayer(friendUUID);
//        IPermissionUser permissionUserFriend = CloudNetDriver.getInstance().getPermissionManagement().getUser(friendUUID);
//        String friendColor = CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(permissionUserFriend).getColor();
//        String friendName = friendColor+name;
//
//        ItemStack background = new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build();
//        for(int i = 0; i < 9; i++)
//            inventoryBuilder.setItem(i, background);
//        for(int i = 19; i < 27; i++)
//            inventoryBuilder.setItem(i, background);
//
//        if(!instance.getCachedSkulls().containsKey(friendUUID)) {
//            instance.getCachedSkulls().put(friendUUID, getSkull(friendUUID, name));
//        }
//
//        if(cloudPlayerFriend != null) {
//            ItemBuilder friendHeadBuilder = new ItemBuilder(instance.getCachedSkulls().get(friendUUID)).setDisplayName(friendName);
//            if(cloudPlayerFriend.getConnectedService().getServerName().startsWith("Lobby") || cloudPlayerFriend.getConnectedService().getServerName().startsWith("Bauserver"))
//                friendHeadBuilder.setLore(" ", "§8•§7● Status", "  §8* §aOnline", "", "§8•§7● Server", "  §8* §b"+cloudPlayerFriend.getConnectedService().getServerName(), "");
//            else
//                friendHeadBuilder.setLore(" ", "§8•§7● Status", "  §8* §aIngame", "", "§8•§7● Server", "  §8* §b"+cloudPlayerFriend.getConnectedService().getServerName(), "");
//            inventoryBuilder.setItem(4, friendHeadBuilder.build());
//        } else {
//            ItemBuilder friendHeadBuilder = new ItemBuilder(Material.SKULL_ITEM).setDisplayName(friendName).setLore(" ", "§8•§7● Status", "  §8* §cOffline", "", "§8•§7● Dauer", "  §8* §c"+friendManager.getLogoutTime(friendUUID), "");
//            inventoryBuilder.setItem(4, friendHeadBuilder.build());
//        }
//        ItemStack back = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/bb0f6e8af46ac6faf88914191ab66f261d6726a7999c637cf2e4159fe1fc477")).setDisplayName(ItemNames.FRIENDS_BACK).build();
//        ItemStack removeFriendAccept = new ItemBuilder(Material.STAINED_GLASS, 5).setDisplayName(ItemNames.FRIENDS_REMOVEFRIENDACCEPT).setLore(" ", "§8•§7● Spieler", "  §8* "+friendName,  "").build();
//        ItemStack removeFriendCancel = new ItemBuilder(Material.STAINED_GLASS, 14).setDisplayName(ItemNames.FRIENDS_REMOVEFRIENDCANCEL).setLore(" ", "§8•§7● Spieler", "  §8* "+friendName,  "").build();
//
//        inventoryBuilder.setItem(11, removeFriendAccept); inventoryBuilder.setItem(15, removeFriendCancel); inventoryBuilder.setItem(18, back);
//        player.openInventory(inventoryBuilder.build());
//        inventoryManager.openFriendMenu(player);
//    }
//
//    public void openManageFriendMenu(Player player, String name) {
//        InventoryBuilder inventoryBuilder = new InventoryBuilder();
//        inventoryBuilder.setSize(9*3);
//        inventoryBuilder.setTitle(ItemNames.TITLE_MANAGEFRIEND);
//
//        UUID friendUUID = friendManager.getUUID(name);
//        ICloudPlayer cloudPlayerFriend = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).getOnlinePlayer(friendUUID);
//        IPermissionUser permissionUserFriend = CloudNetDriver.getInstance().getPermissionManagement().getUser(friendUUID);
//        String friendColor = CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(permissionUserFriend).getColor();
//        String friendName = friendColor+name;
//
//        manageFriendCache.put(player.getUniqueId(), name);
//
//        ItemStack background = new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build();
//        for(int i = 0; i < 9; i++)
//            inventoryBuilder.setItem(i, background);
//        for(int i = 19; i < 27; i++)
//            inventoryBuilder.setItem(i, background);
//
//        if(!instance.getCachedSkulls().containsKey(friendUUID)) {
//            instance.getCachedSkulls().put(friendUUID, getSkull(friendUUID, name));
//        }
//
//        if(cloudPlayerFriend != null) {
//            ItemBuilder friendHeadBuilder = new ItemBuilder(instance.getCachedSkulls().get(friendUUID)).setDisplayName(friendName);
//            if(cloudPlayerFriend.getConnectedService().getServerName().startsWith("Lobby") || cloudPlayerFriend.getConnectedService().getServerName().startsWith("Bauserver"))
//                friendHeadBuilder.setLore(" ", "§8•§7● Status", "  §8* §aOnline", "", "§8•§7● Server", "  §8* §b"+cloudPlayerFriend.getConnectedService().getServerName(), "");
//            else
//                friendHeadBuilder.setLore(" ", "§8•§7● Status", "  §8* §aIngame", "", "§8•§7● Server", "  §8* §b"+cloudPlayerFriend.getConnectedService().getServerName(), "");
//            inventoryBuilder.setItem(4, friendHeadBuilder.build());
//        } else {
//            ItemBuilder friendHeadBuilder = new ItemBuilder(Material.SKULL_ITEM).setDisplayName(friendName).setLore(" ", "§8•§7● Status", "  §8* §cOffline", "", "§8•§7● Dauer", "  §8* §c"+friendManager.getLogoutTime(friendUUID), "");
//            inventoryBuilder.setItem(4, friendHeadBuilder.build());
//        }
//        ItemStack back = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/bb0f6e8af46ac6faf88914191ab66f261d6726a7999c637cf2e4159fe1fc477")).setDisplayName(ItemNames.FRIENDS_BACK).build();
//        ItemStack partyInvite = new ItemBuilder(Material.FIREWORK).setDisplayName(ItemNames.FRIENDS_PARTYINVITE).setLore(" ", "§8•§7● Spieler", "  §8* "+friendName,  "").build();
//        ItemStack jumpto = new ItemBuilder(Material.ENDER_PEARL).setDisplayName(ItemNames.FRIENDS_JUMPTO).setLore(" ", "§8•§7● Spieler", "  §8* "+friendName,  "").build();
//        ItemStack removeFriend = new ItemBuilder(Material.BARRIER).setDisplayName(ItemNames.FRIENDS_REMOVEFRIEND).setLore(" ", "§8•§7● Spieler", "  §8* "+friendName,  "").build();
//
//        inventoryBuilder.setItem(11, partyInvite); inventoryBuilder.setItem(13, jumpto);
//        inventoryBuilder.setItem(15, removeFriend); inventoryBuilder.setItem(18, back);
//        player.openInventory(inventoryBuilder.build());
//        inventoryManager.openFriendMenu(player);
//    }
//
//    public void openNewFriendMenu(Player player, String name) {
//        InventoryBuilder inventoryBuilder = new InventoryBuilder();
//        inventoryBuilder.setSize(9*3);
//        inventoryBuilder.setTitle(ItemNames.TITLE_NEWFRIEND);
//
//        UUID friendUUID = friendManager.getUUID(name);
//        ICloudPlayer cloudPlayerFriend = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).getOnlinePlayer(friendUUID);
//        IPermissionUser permissionUserFriend = CloudNetDriver.getInstance().getPermissionManagement().getUser(friendUUID);
//        String friendColor = CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(permissionUserFriend).getColor();
//        String friendName = friendColor+name;
//
//        newFriendCache.put(player.getUniqueId(), name);
//
//        ItemStack background = new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build();
//        for(int i = 0; i < 9; i++)
//            inventoryBuilder.setItem(i, background);
//        for(int i = 19; i < 27; i++)
//            inventoryBuilder.setItem(i, background);
//
//        if(!instance.getCachedSkulls().containsKey(friendUUID)) {
//            instance.getCachedSkulls().put(friendUUID, getSkull(friendUUID, name));
//        }
//
//        if(cloudPlayerFriend != null) {
//            ItemBuilder friendHeadBuilder = new ItemBuilder(instance.getCachedSkulls().get(friendUUID)).setDisplayName(friendName).setLore(" ", "§8•§7● Status", "  §8* §aOnline", "");
//            inventoryBuilder.setItem(4, friendHeadBuilder.build());
//        } else {
//            ItemBuilder friendHeadBuilder = new ItemBuilder(Material.SKULL_ITEM).setDisplayName(friendName).setLore(" ", "§8•§7● Status", "  §8* §cOffline", "");
//            inventoryBuilder.setItem(4, friendHeadBuilder.build());
//        }
//        ItemStack back = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/bb0f6e8af46ac6faf88914191ab66f261d6726a7999c637cf2e4159fe1fc477")).setDisplayName(ItemNames.FRIENDS_BACK).build();
//        ItemStack acceptFriend = new ItemBuilder(Material.STAINED_GLASS, 5).setDisplayName(ItemNames.REQUESTS_ACCEPTFRIEND).setLore(" ", "§8•§7● Spieler", "  §8* "+friendName,  "").build();
//        ItemStack denyFriend = new ItemBuilder(Material.STAINED_GLASS, 14).setDisplayName(ItemNames.REQUESTS_DENYFRIEND).setLore(" ", "§8•§7● Spieler", "  §8* "+friendName,  "").build();
//
//        inventoryBuilder.setItem(11, acceptFriend); inventoryBuilder.setItem(15, denyFriend); inventoryBuilder.setItem(18, back);
//        player.openInventory(inventoryBuilder.build());
//        inventoryManager.openFriendMenu(player);
//    }
//
//    public void openSettingsMenu(Player player) {
//        InventoryBuilder inventoryBuilder = new InventoryBuilder();
//        inventoryBuilder.setSize(9*6);
//        inventoryBuilder.setTitle(ItemNames.TITLE_FRIENDSETTINGS);
//        HashMap<FriendSetting, FriendValue> friendSettings = friendManager.getSettings(player.getUniqueId());
//
//        ItemStack background = new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build();
//        ItemStack friendMenu = new ItemBuilder(Material.IRON_HELMET).setDisplayName(ItemNames.FRIENDS_FRIENDMENU).build();
//        ItemStack requestsMenu = new ItemBuilder(Material.BOOK_AND_QUILL).setDisplayName(ItemNames.FRIENDS_REQUESTSMENU).build();
//        ItemStack settingsMenu = new ItemBuilder(Material.REDSTONE_COMPARATOR).setDisplayName(ItemNames.FRIENDS_SETTINGSMENU).setEnchanted(true).build();
//
//        ItemBuilder friendRequestsBuilder = new ItemBuilder(Material.EMPTY_MAP).setDisplayName(ItemNames.FRIENDSETTINGS_REQUESTS).setLore(" ", "§8•§7● Info", "  §8* §7Verwalte wer dir Freundschafts-  ", "  §8* §7anfragen senden kann", "", "§8•§7● Status", "  §8* §cNiemand", "");
//        ItemBuilder partyInvitesBuilder = new ItemBuilder(Material.FIREWORK).setDisplayName(ItemNames.FRIENDSETTINGS_PARTYINVITES).setLore(" ", "§8•§7● Info", "  §8* §7Verwalte wer dir Partyeinladungen  ", "  §8* §7senden kann", "", "§8•§7● Status", "  §8* §cNiemand", "");
//        ItemBuilder jumptoBuilder = new ItemBuilder(Material.ENDER_PEARL).setDisplayName(ItemNames.FRIENDSETTINGS_JUMPTO).setLore(" ", "§8•§7● Info", "  §8* §7Verwalte wer dir nachspringen kann  ", "", "§8•§7● Status", "  §8* §cNiemand", "");
//        ItemBuilder messagesBuilder = new ItemBuilder(Material.BOOK).setDisplayName(ItemNames.FRIENDSETTINGS_MESSAGES).setLore(" ", "§8•§7● Info", "  §8* §7Verwalte wer dir Privatnachrichten  ", "  §8* §7senden kann", "", "§8•§7● Status", "  §8* §cNiemand", "");
//
//        if(friendSettings.get(FriendSetting.FREUNDESANFRAGEN) == FriendValue.AN)
//            friendRequestsBuilder.setLore(" ", "§8•§7● Info", "  §8* §7Verwalte wer dir Freundschafts-  ", "  §8* §7anfragen senden kann", "", "§8•§7● Status", "  §8* §aJeder", "");
//        if(friendSettings.get(FriendSetting.PARTYANFRAGEN) == FriendValue.AN)
//            partyInvitesBuilder.setLore(" ", "§8•§7● Info", "  §8* §7Verwalte wer dir Partyeinladungen  ", "  §8* §7senden kann", "", "§8•§7● Status", "  §8* §aJeder", "");
//        else if(friendSettings.get(FriendSetting.PARTYANFRAGEN) == FriendValue.FREUNDE)
//            partyInvitesBuilder.setLore(" ", "§8•§7● Info", "  §8* §7Verwalte wer dir Partyeinladungen  ", "  §8* §7senden kann", "", "§8•§7● Status", "  §8* §bFreunde", "");
//        if(friendSettings.get(FriendSetting.NACHSPRINGEN) == FriendValue.FREUNDE)
//            jumptoBuilder.setLore(" ", "§8•§7● Info", "  §8* §7Verwalte wer dir nachspringen kann  ", "", "§8•§7● Status", "  §8* §bFreunde", "");
//        if(friendSettings.get(FriendSetting.NACHRICHTEN) == FriendValue.AN)
//            messagesBuilder.setLore(" ", "§8•§7● Info", "  §8* §7Verwalte wer dir Privatnachrichten  ", "  §8* §7senden kann", "", "§8•§7● Status", "  §8* §aJeder", "");
//        else if(friendSettings.get(FriendSetting.NACHRICHTEN) == FriendValue.FREUNDE)
//            messagesBuilder.setLore(" ", "§8•§7● Info", "  §8* §7Verwalte wer dir Privatnachrichten  ", "  §8* §7senden kann", "", "§8•§7● Status", "  §8* §bFreunde", "");
//
//        ItemStack friendRequests = friendRequestsBuilder.build(); ItemStack partyInvites = partyInvitesBuilder.build(); ItemStack jumpto = jumptoBuilder.build();
//        ItemStack messages = messagesBuilder.build();
//
//        for(int i = 36; i < 45; i++)
//            inventoryBuilder.setItem(i, background);
//        for(int i = 0; i < 9; i++)
//            inventoryBuilder.setItem(i, background);
//
//        inventoryBuilder.setItem(19, friendRequests); inventoryBuilder.setItem(21, partyInvites); inventoryBuilder.setItem(23, jumpto);
//        inventoryBuilder.setItem(25, messages);
//        inventoryBuilder.setItem(48, friendMenu); inventoryBuilder.setItem(49, requestsMenu); inventoryBuilder.setItem(50, settingsMenu);
//        player.openInventory(inventoryBuilder.build());
//        inventoryManager.openFriendMenu(player);
//    }
//
//    public void openRequestsMenu(Player player, int page) {
//        InventoryBuilder inventoryBuilder = new InventoryBuilder();
//        inventoryBuilder.setSize(9*6);
//        inventoryBuilder.setTitle(ItemNames.TITLE_REQUESTS+" §8§l┃ §7Seite "+page);
//
//        List<String> requestsList;
//        if(page == 1)
//            requestsList = friendManager.getRequests(player.getUniqueId(), 0, 36);
//        else
//            requestsList = friendManager.getRequests(player.getUniqueId(), ((page - 1)*36) + 1, page*36);
//        pageIndexRequests.put(player.getUniqueId(), page);
//
//        for(int count = 0; count < 36; count++) {
//            if(count >= requestsList.size())
//                inventoryBuilder.setItem(count, new ItemStack(Material.AIR));
//            else {
//                UUID friendUUID = friendManager.getUUID(requestsList.get(count));
//                ICloudPlayer cloudPlayerFriend = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).getOnlinePlayer(friendUUID);
//                IPermissionUser permissionUserFriend = CloudNetDriver.getInstance().getPermissionManagement().getUser(friendUUID);
//                String friendColor = CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(permissionUserFriend).getColor();
//                String friendName = friendColor+requestsList.get(count);
//
//                if(!instance.getCachedSkulls().containsKey(friendUUID)) {
//                    instance.getCachedSkulls().put(friendUUID, getSkull(friendUUID, requestsList.get(count)));
//                }
//
//                if(cloudPlayerFriend != null) {
//                    ItemBuilder friendHeadBuilder = new ItemBuilder(instance.getCachedSkulls().get(friendUUID)).setDisplayName(friendName).setLore(" ", "§8•§7● Status", "  §8* §aOnline", "");
//                    inventoryBuilder.setItem(count, friendHeadBuilder.build());
//                } else {
//                    ItemBuilder friendHeadBuilder = new ItemBuilder(Material.SKULL_ITEM).setDisplayName(friendName).setLore(" ", "§8•§7● Status", "  §8* §cOffline", "");
//                    inventoryBuilder.setItem(count, friendHeadBuilder.build());
//                }
//            }
//        }
//        ItemStack background = new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build();
//        ItemStack pageBack = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/bb0f6e8af46ac6faf88914191ab66f261d6726a7999c637cf2e4159fe1fc477")).setDisplayName(ItemNames.FRIENDS_PAGEBACK).build();
//        ItemStack pageNext = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/f2f3a2dfce0c3dab7ee10db385e5229f1a39534a8ba2646178e37c4fa93b")).setDisplayName(ItemNames.FRIENDS_PAGENEXT).build();
//        ItemStack friendMenu = new ItemBuilder(Material.IRON_HELMET).setDisplayName(ItemNames.FRIENDS_FRIENDMENU).build();
//        ItemStack requestsMenu = new ItemBuilder(Material.BOOK_AND_QUILL).setDisplayName(ItemNames.FRIENDS_REQUESTSMENU).setEnchanted(true).build();
//        ItemStack settingsMenu = new ItemBuilder(Material.REDSTONE_COMPARATOR).setDisplayName(ItemNames.FRIENDS_SETTINGSMENU).build();
//
//        for(int i = 36; i < 45; i++)
//            inventoryBuilder.setItem(i, background);
//        inventoryBuilder.setItem(45, pageBack); inventoryBuilder.setItem(53, pageNext);
//        inventoryBuilder.setItem(48, friendMenu); inventoryBuilder.setItem(49, requestsMenu); inventoryBuilder.setItem(50, settingsMenu);
//        player.openInventory(inventoryBuilder.build());
//        inventoryManager.openFriendMenu(player);
//    }
//
//    public void openFriendMenu(Player player, int page) {
//        InventoryBuilder inventoryBuilder = new InventoryBuilder();
//        inventoryBuilder.setSize(9*6);
//        inventoryBuilder.setTitle(ItemNames.TITLE_FRIENDS+" §8§l┃ §7Seite "+page);
//
//        List<String> friendList;
//        if(page == 1)
//            friendList = friendManager.getFriends(player.getUniqueId(), 0, 36);
//        else
//            friendList = friendManager.getFriends(player.getUniqueId(), ((page - 1)*36) + 1, page*36);
//        pageIndexFriends.put(player.getUniqueId(), page);
//
//        for(int count = 0; count < 36; count++) {
//            if(count >= friendList.size())
//                inventoryBuilder.setItem(count, new ItemStack(Material.AIR));
//            else {
//                UUID friendUUID = friendManager.getUUID(friendList.get(count));
//                ICloudPlayer cloudPlayerFriend = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).getOnlinePlayer(friendUUID);
//                IPermissionUser permissionUserFriend = CloudNetDriver.getInstance().getPermissionManagement().getUser(friendUUID);
//                String friendColor = CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(permissionUserFriend).getColor();
//                String friendName = friendColor+friendList.get(count);
//
//                if(!instance.getCachedSkulls().containsKey(friendUUID)) {
//                    instance.getCachedSkulls().put(friendUUID, getSkull(friendUUID, friendList.get(count)));
//                }
//
//                if(cloudPlayerFriend != null) {
//                    ItemBuilder friendHeadBuilder = new ItemBuilder(instance.getCachedSkulls().get(friendUUID)).setDisplayName(friendName);
//                    if(cloudPlayerFriend.getConnectedService().getServerName().startsWith("Lobby") || cloudPlayerFriend.getConnectedService().getServerName().startsWith("Bauserver"))
//                        friendHeadBuilder.setLore(" ", "§8•§7● Status", "  §8* §aOnline", "", "§8•§7● Server", "  §8* §b"+cloudPlayerFriend.getConnectedService().getServerName(), "");
//                    else
//                        friendHeadBuilder.setLore(" ", "§8•§7● Status", "  §8* §aIngame", "", "§8•§7● Server", "  §8* §b"+cloudPlayerFriend.getConnectedService().getServerName(), "");
//                    inventoryBuilder.setItem(count, friendHeadBuilder.build());
//                } else {
//                    ItemBuilder friendHeadBuilder = new ItemBuilder(Material.SKULL_ITEM).setDisplayName(friendName).setLore(" ", "§8•§7● Status", "  §8* §cOffline", "", "§8•§7● Dauer", "  §8* §c"+friendManager.getLogoutTime(friendUUID), "");
//                    inventoryBuilder.setItem(count, friendHeadBuilder.build());
//                }
//            }
//        }
//        ItemStack background = new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build();
//        ItemStack pageBack = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/bb0f6e8af46ac6faf88914191ab66f261d6726a7999c637cf2e4159fe1fc477")).setDisplayName(ItemNames.FRIENDS_PAGEBACK).build();
//        ItemStack pageNext = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/f2f3a2dfce0c3dab7ee10db385e5229f1a39534a8ba2646178e37c4fa93b")).setDisplayName(ItemNames.FRIENDS_PAGENEXT).build();
//        ItemStack friendMenu = new ItemBuilder(Material.IRON_HELMET).setDisplayName(ItemNames.FRIENDS_FRIENDMENU).setEnchanted(true).build();
//        ItemStack requestsMenu = new ItemBuilder(Material.BOOK_AND_QUILL).setDisplayName(ItemNames.FRIENDS_REQUESTSMENU).build();
//        ItemStack settingsMenu = new ItemBuilder(Material.REDSTONE_COMPARATOR).setDisplayName(ItemNames.FRIENDS_SETTINGSMENU).build();
//
//        for(int i = 36; i < 45; i++)
//            inventoryBuilder.setItem(i, background);
//        inventoryBuilder.setItem(45, pageBack); inventoryBuilder.setItem(53, pageNext);
//        inventoryBuilder.setItem(48, friendMenu); inventoryBuilder.setItem(49, requestsMenu); inventoryBuilder.setItem(50, settingsMenu);
//        player.openInventory(inventoryBuilder.build());
//        inventoryManager.openFriendMenu(player);
//    }
//
//    public static ItemStack getSkull(String url) {
//        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
//        if(url.isEmpty())return head;
//
//
//        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
//        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
//        byte[] encodedData = org.apache.commons.codec.binary.Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
//        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
//        Field profileField = null;
//        try {
//            profileField = headMeta.getClass().getDeclaredField("profile");
//            profileField.setAccessible(true);
//            profileField.set(headMeta, profile);
//        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
//            e1.printStackTrace();
//        }
//        head.setItemMeta(headMeta);
//        return head;
//    }
//
//    private ItemStack getSkull(UUID uuid, String name) {
//        String skinURL = null;
//        loadSkin: if (playerCache.containsKey(name))
//            skinURL = playerCache.get(name);
//        else {
//            try {
//                GameProfile profile = new GameProfile(uuid, name);
//                Field field;
//                field = MinecraftServer.class.getDeclaredField("W"); //This will obviously break on next Minecraft update....
//                field.setAccessible(true);
//                Object value = field.get(MinecraftServer.getServer());
//                if (!(value instanceof MinecraftSessionService))
//                    break loadSkin;
//                MinecraftSessionService ss = (MinecraftSessionService) value;
//                ss.fillProfileProperties(profile, true);
//                Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = ss.getTextures(profile, true);
//                if (textures.containsKey(MinecraftProfileTexture.Type.SKIN)) {
//                    MinecraftProfileTexture tex = textures.get(MinecraftProfileTexture.Type.SKIN);
//                    skinURL = tex.getUrl();
//                }
//            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
//        if (skinURL == null)
//            return head;
//        playerCache.put(name, skinURL);
//        ItemMeta headMeta = head.getItemMeta();
//        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
//        byte[] encodedData = Base64.getEncoder()
//                .encode((String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", skinURL).getBytes()));
//        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
//        Field profileField = null;
//        try {
//            profileField = headMeta.getClass().getDeclaredField("profile");
//            profileField.setAccessible(true);
//            profileField.set(headMeta, profile);
//        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
//            e.printStackTrace();
//        }
//        head.setItemMeta(headMeta);
//        return head;
//    }
//
//    public int getPageIndexRequests(UUID uuid) {
//        return pageIndexRequests.get(uuid);
//    }
//
//    public int getPageIndexFriends(UUID uuid) {
//        return pageIndexFriends.get(uuid);
//    }
//
//    public String getNewFriendCache(UUID uuid) {
//        return newFriendCache.get(uuid);
//    }
//
//    public String getManageFriendCache(UUID uuid) {
//        return manageFriendCache.get(uuid);
//    }

}

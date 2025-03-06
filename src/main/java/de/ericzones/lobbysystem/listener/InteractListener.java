// Created by Eric B. 22.06.2020 23:41
package de.ericzones.lobbysystem.listener;

import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.InventoryBuilder;
import de.ericzones.lobbysystem.extra.ItemBuilder;
import de.ericzones.lobbysystem.extra.ItemNames;
import de.ericzones.lobbysystem.extra.Utils;
import de.ericzones.lobbysystem.manager.InventoryManager;
import de.ericzones.lobbysystem.manager.VisibilityManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class InteractListener implements Listener {

    private LobbySystem instance;
    private InventoryManager inventoryManager;
    private VisibilityManager visibilityManager;

    private HashMap<UUID, Long> navigatorDelay;
    private HashMap<UUID, Long> profileDelay;
    private HashMap<UUID, Long> playerhiderDelay;

    public InteractListener(LobbySystem instance) {
        this.instance = instance;
        inventoryManager = instance.getInventoryManager();
        visibilityManager = instance.getVisibilityManager();
        navigatorDelay = new HashMap<>();
        profileDelay = new HashMap<>();
        playerhiderDelay = new HashMap<>();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if(player.getItemInHand() == null) return;
        if(instance.getBuildPlayers().contains(player.getUniqueId())) return;
        if(!player.getItemInHand().hasItemMeta()) return;
        if(player.getItemInHand().getItemMeta().getDisplayName() == null) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;

        if(player.getItemInHand().getItemMeta().getDisplayName().equals(ItemNames.INV_NAVIGATOR)) {
            if(navigatorDelay.containsKey(player.getUniqueId()) && navigatorDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                return;
            }
            navigatorDelay.put(player.getUniqueId(), System.currentTimeMillis() + 2*1000);
            InventoryBuilder inventoryBuilder = new InventoryBuilder();
            inventoryBuilder.setSize(9*5);
            inventoryBuilder.setTitle(ItemNames.TITLE_NAVIGATOR);
            //0 - 3 - 5 - 8 - 11 - 15 - 22 - 29 - 33 - 36 - 39 - 41 - 44
            ItemStack unknown = new ItemBuilder(Material.BARRIER).setDisplayName(ItemNames.NAVIGATOR_UNKNOWN).build();
            inventoryBuilder.setItem(0, unknown); inventoryBuilder.setItem(3, unknown); inventoryBuilder.setItem(5, unknown);
            inventoryBuilder.setItem(8, unknown); inventoryBuilder.setItem(11, unknown); inventoryBuilder.setItem(15, unknown);
            inventoryBuilder.setItem(29, unknown); inventoryBuilder.setItem(33, unknown); inventoryBuilder.setItem(36, unknown);
            inventoryBuilder.setItem(39, unknown); inventoryBuilder.setItem(41, unknown); inventoryBuilder.setItem(44, unknown);
            inventoryBuilder.setItem(22, new ItemBuilder(Material.BEACON).setDisplayName(ItemNames.NAVIGATOR_SPAWN).build());
            inventoryBuilder.fillInventory(new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build());
            player.openInventory(inventoryBuilder.build());
            inventoryManager.openNavigator(player);
            player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 1.2F, 1.2F);
        } else if(player.getItemInHand().getItemMeta().getDisplayName().equals(ItemNames.INV_PLAYERHIDER)) {
            if(playerhiderDelay.containsKey(player.getUniqueId()) && playerhiderDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                return;
            }
            playerhiderDelay.put(player.getUniqueId(), System.currentTimeMillis() + 2*1000);

            InventoryBuilder inventoryBuilder = new InventoryBuilder();
            inventoryBuilder.setSize(9*3);
            inventoryBuilder.setTitle(ItemNames.TITLE_PLAYERHIDER);
            ItemBuilder limeDyeBuilder = new ItemBuilder(Material.INK_SACK, 10).setDisplayName(ItemNames.PLAYERHIDER_LIMEDYE).setLore(" ", "§8•§7● Info", "  §8* §7Zeige alle Spieler an", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            ItemBuilder purpleDyeBuilder = new ItemBuilder(Material.INK_SACK, 5).setDisplayName(ItemNames.PLAYERHIDER_PURPLEDYE).setLore(" ", "§8•§7● Info", "  §8* §7Zeige nur Teammitglieder an", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            ItemBuilder blueDyeBuilder = new ItemBuilder(Material.INK_SACK, 12).setDisplayName(ItemNames.PLAYERHIDER_BLUEDYE).setLore(" ", "§8•§7● Info", "  §8* §7Zeige nur Freunde an", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            ItemBuilder grayDyeBuilder = new ItemBuilder(Material.INK_SACK, 8).setDisplayName(ItemNames.PLAYERHIDER_GRAYDYE).setLore(" ", "§8•§7● Info", "  §8* §7Zeige keine Spieler an", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            switch (visibilityManager.getVisibilitySetting(player.getUniqueId())) {
                case ALL:
                    limeDyeBuilder.setEnchanted(true);
                    limeDyeBuilder.setLore(" ", "§8•§7● Info", "  §8* §7Zeige alle Spieler an", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
                    break;
                case TEAM:
                    purpleDyeBuilder.setEnchanted(true);
                    purpleDyeBuilder.setLore(" ", "§8•§7● Info", "  §8* §7Zeige nur Teammitglieder an", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
                    break;
                case FRIENDS:
                    blueDyeBuilder.setEnchanted(true);
                    blueDyeBuilder.setLore(" ", "§8•§7● Info", "  §8* §7Zeige nur Freunde an", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
                    break;
                case NONE:
                    grayDyeBuilder.setEnchanted(true);
                    grayDyeBuilder.setLore(" ", "§8•§7● Info", "  §8* §7Zeige keine Spieler an", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
                    break;
                default:
                    break;
            }
            ItemStack limeDye = limeDyeBuilder.build();
            ItemStack purpleDye = purpleDyeBuilder.build();
            ItemStack blueDye = blueDyeBuilder.build();
            ItemStack grayDye = grayDyeBuilder.build();
            //10 - 12 - 14 - 16
            inventoryBuilder.setItem(10, limeDye); inventoryBuilder.setItem(12, purpleDye); inventoryBuilder.setItem(14, blueDye); inventoryBuilder.setItem(16, grayDye);
            inventoryBuilder.fillInventory(new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build());
            player.openInventory(inventoryBuilder.build());
            inventoryManager.openPlayerhider(player);
            player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 1.2F, 1.2F);
        } else if(player.getItemInHand().getItemMeta().getDisplayName().equals(ItemNames.INV_PROFILE)) {
            if(profileDelay.containsKey(player.getUniqueId()) && profileDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
                player.sendMessage(Utils.prefix_lobby+"§7Bitte warte einen Moment");
                return;
            }
            profileDelay.put(player.getUniqueId(), System.currentTimeMillis() + 2*1000);

            InventoryBuilder inventoryBuilder = new InventoryBuilder();
            inventoryBuilder.setSize(9*3);
            inventoryBuilder.setTitle(ItemNames.TITLE_PROFILE);
            ItemStack gadgets = new ItemBuilder(Material.CHEST).setDisplayName(ItemNames.PROFILE_GADGETS).build();
            ItemStack chatsettings = new ItemBuilder(Material.BOOK_AND_QUILL).setDisplayName(ItemNames.PROFILE_CHATSETTINGS).build();
            ItemStack friends = new ItemBuilder(instance.getCachedSkulls().get(player.getUniqueId())).setDisplayName(ItemNames.PROFILE_FRIENDS).removeLore().build();
            inventoryBuilder.setItem(11, gadgets); inventoryBuilder.setItem(13, chatsettings); inventoryBuilder.setItem(15, friends);
            inventoryBuilder.fillInventory(new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build());
            player.openInventory(inventoryBuilder.build());
            inventoryManager.openProfile(player);
            player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 1.2F, 1.2F);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if(!(e.getPlayer() instanceof Player)) return;
        Player player = (Player) e.getPlayer();
        if(e.getInventory() == null) return;
        if(e.getInventory().getHolder() == player) return;
        inventoryManager.setInventory(player);
    }

}

// Created by Eric B. 17.05.2021 12:58
package de.ericzones.lobbysystem.commands;

import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.InventoryBuilder;
import de.ericzones.lobbysystem.extra.ItemBuilder;
import de.ericzones.lobbysystem.extra.ItemNames;
import de.ericzones.lobbysystem.extra.Utils;
import de.ericzones.lobbysystem.manager.ChatManager;
import de.ericzones.lobbysystem.manager.InventoryManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ChatCommand implements CommandExecutor {

    private LobbySystem instance;
    private ChatManager chatManager;
    private InventoryManager inventoryManager;

    public ChatCommand(LobbySystem instance) {
        this.instance = instance;
        chatManager = instance.getChatManager();
        inventoryManager = instance.getInventoryManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command c, String s, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Utils.error_noconsole);
            return true;
        }
        Player player = (Player) sender;
        if(!player.hasPermission("spigot.command.chat")) {
            player.sendMessage(Utils.error_noperms);
            return true;
        }
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
        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1.2F, 1.2F);
        return false;
    }

}

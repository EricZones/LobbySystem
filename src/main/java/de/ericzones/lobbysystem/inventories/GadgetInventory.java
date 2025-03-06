// Created by Eric B. 17.05.2021 12:50
package de.ericzones.lobbysystem.inventories;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.InventoryBuilder;
import de.ericzones.lobbysystem.extra.ItemBuilder;
import de.ericzones.lobbysystem.extra.ItemNames;
import de.ericzones.lobbysystem.manager.GadgetManager;
import de.ericzones.lobbysystem.manager.InventoryManager;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

public class GadgetInventory {

    private LobbySystem instance;
    private InventoryManager inventoryManager;
    private GadgetManager gadgetManager;

    private HashMap<UUID, GadgetManager.GadgetType> gadgetTypeCache;
    private HashMap<UUID, Integer> gadgetSettingCache;

    public GadgetInventory(LobbySystem instance) {
        this.instance = instance;
        inventoryManager = instance.getInventoryManager();
        gadgetManager = instance.getGadgetManager();
        gadgetTypeCache = new HashMap<>();
        gadgetSettingCache = new HashMap<>();
    }

    public void openGadgetMenu(Player player) {
        InventoryBuilder inventoryBuilder = new InventoryBuilder();
        inventoryBuilder.setSize(9*5);
        inventoryBuilder.setTitle(ItemNames.TITLE_GADGETS);

        ItemStack skull = new ItemBuilder(Material.SKULL_ITEM, 3).setDisplayName(ItemNames.GADGETS_SKULLS).build();
        ItemStack hat = new ItemBuilder(Material.COMMAND).setDisplayName(ItemNames.GADGETS_HATS).build();
        ItemStack boots = new ItemBuilder(Material.GOLD_BOOTS).setDisplayName(ItemNames.GADGETS_BOOTS).build();
        ItemStack rain = new ItemBuilder(Material.DIAMOND).setDisplayName(ItemNames.GADGETS_RAIN).build();
        ItemStack tool = new ItemBuilder(Material.FISHING_ROD).setDisplayName(ItemNames.GADGETS_TOOLS).build();

        inventoryBuilder.setItem(11, skull); inventoryBuilder.setItem(15, hat); inventoryBuilder.setItem(22, tool);
        inventoryBuilder.setItem(29, boots); inventoryBuilder.setItem(33, rain);

        inventoryBuilder.fillInventory(new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build());
        player.openInventory(inventoryBuilder.build());
        inventoryManager.openGadgetMainMenu(player);
    }

    public void openBuyMenu(Player player, String inventoryTitle, ItemStack item) {
        InventoryBuilder inventoryBuilder = new InventoryBuilder();
        inventoryBuilder.setSize(9*3);
        inventoryBuilder.setTitle(ItemNames.TITLE_BUYGADGET);

        registerNewGadget(player, inventoryTitle, item.getItemMeta().getDisplayName());

        ItemStack background = new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build();
        for(int i = 0; i < 9; i++)
            inventoryBuilder.setItem(i, background);
        for(int i = 19; i < 27; i++)
            inventoryBuilder.setItem(i, background);

        ItemBuilder gadgetBuilder = new ItemBuilder(item);
        ItemBuilder continueBuyBuilder = new ItemBuilder(Material.STAINED_GLASS, 5).setDisplayName(ItemNames.GADGETS_CONTINUEBUY);
        ItemBuilder cancelBuyBuilder = new ItemBuilder(Material.STAINED_GLASS, 14).setDisplayName(ItemNames.GADGETS_CANCELBUY);
        switch (gadgetTypeCache.get(player.getUniqueId())) {
            case SKULL:
                continueBuyBuilder.setLore(" ", "§8•§7● Preis", "  §8* §e5.000 Coins", "");
                gadgetBuilder.setLore(" ", "§8•§7● Preis", "  §8* §e5.000 Coins", "");
                break;
            case HAT:
                continueBuyBuilder.setLore(" ", "§8•§7● Preis", "  §8* §e2.500 Coins", "");
                gadgetBuilder.setLore(" ", "§8•§7● Preis", "  §8* §e2.500 Coins", "");
                break;
            case TOOL:
                if(item.getType() == Material.FISHING_ROD) {
                    continueBuyBuilder.setLore(" ", "§8•§7● Preis", "  §8* §e25.000 Coins", "");
                    gadgetBuilder.setLore(" ", "§8•§7● Preis", "  §8* §e25.000 Coins", "");
                } else if(item.getType() == Material.DIAMOND_BARDING) {
                    continueBuyBuilder.setLore(" ", "§8•§7● Preis", "  §8* §e35.000 Coins", "");
                    gadgetBuilder.setLore(" ", "§8•§7● Preis", "  §8* §e35.000 Coins", "");
                }
                break;
            case BOOTS:
                continueBuyBuilder.setLore(" ", "§8•§7● Preis", "  §8* §e15.000 Coins", "");
                gadgetBuilder.setLore(" ", "§8•§7● Preis", "  §8* §e15.000 Coins", "");
                break;
            case RAIN:
                continueBuyBuilder.setLore(" ", "§8•§7● Preis", "  §8* §e20.000 Coins", "");
                gadgetBuilder.setLore(" ", "§8•§7● Preis", "  §8* §e20.000 Coins", "");
                break;
            default:
                break;
        }
        ItemStack continueBuy = continueBuyBuilder.build(); ItemStack cancelBuy = cancelBuyBuilder.build(); ItemStack gadget = gadgetBuilder.build();
        ItemStack back = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/bb0f6e8af46ac6faf88914191ab66f261d6726a7999c637cf2e4159fe1fc477")).setDisplayName(ItemNames.GADGETS_BACK).build();

        inventoryBuilder.setItem(11, continueBuy); inventoryBuilder.setItem(15, cancelBuy); inventoryBuilder.setItem(18, back);
        inventoryBuilder.setItem(4, gadget);

        player.openInventory(inventoryBuilder.build());
        inventoryManager.openGadgetListMenu(player);
    }

    public void openPreviousMenu(Player player) {
        switch (gadgetTypeCache.get(player.getUniqueId())) {
            case SKULL:
                openSkullsMenu(player);
                break;
            case HAT:
                openHatsMenu(player);
                break;
            case TOOL:
                openToolsMenu(player);
                break;
            case BOOTS:
                openBootsMenu(player);
                break;
            case RAIN:
                openRainMenu(player);
                break;
            default:
                break;
        }
    }

    public void openToolsMenu(Player player) {
        InventoryBuilder inventoryBuilder = new InventoryBuilder();
        inventoryBuilder.setSize(9*5);
        inventoryBuilder.setTitle(ItemNames.TITLE_TOOLS);

        ItemBuilder hookBuilder = new ItemBuilder(Material.FISHING_ROD).setDisplayName(ItemNames.TOOLS_HOOK).setLore(" ", "§8•§7● Preis", "  §8* §e25.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder paintballBuilder = new ItemBuilder(Material.DIAMOND_BARDING).setDisplayName(ItemNames.TOOLS_PAINTBALL).setLore(" ", "§8•§7● Preis", "  §8* §e35.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.TOOL, 1)) {
            hookBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getToolGadget(player.getUniqueId()) == 1)
                hookBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.TOOL, 2)) {
            paintballBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getToolGadget(player.getUniqueId()) == 2)
                paintballBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        ItemStack hook = hookBuilder.build(); ItemStack paintball = paintballBuilder.build();
        ItemStack back = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/bb0f6e8af46ac6faf88914191ab66f261d6726a7999c637cf2e4159fe1fc477")).setDisplayName(ItemNames.GADGETS_BACK).build();

        inventoryBuilder.setItem(20, hook); inventoryBuilder.setItem(24, paintball);
        inventoryBuilder.setItem(36, back);

        inventoryBuilder.fillInventory(new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build());
        player.openInventory(inventoryBuilder.build());
        inventoryManager.openGadgetListMenu(player);
    }

    public void openRainMenu(Player player) {
        InventoryBuilder inventoryBuilder = new InventoryBuilder();
        inventoryBuilder.setSize(9*5);
        inventoryBuilder.setTitle(ItemNames.TITLE_RAIN);

        // 11 - 13 - 15 - 29 - 31 - 33
        ItemBuilder diamondRainBuilder = new ItemBuilder(Material.DIAMOND).setDisplayName(ItemNames.RAIN_DIAMOND).setLore(" ", "§8•§7● Preis", "  §8* §e20.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder emeraldRainBuilder = new ItemBuilder(Material.EMERALD).setDisplayName(ItemNames.RAIN_EMERALD).setLore(" ", "§8•§7● Preis", "  §8* §e20.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder redstoneRainBuilder = new ItemBuilder(Material.REDSTONE).setDisplayName(ItemNames.RAIN_REDSTONE).setLore(" ", "§8•§7● Preis", "  §8* §e20.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder goldnuggetRainBuilder = new ItemBuilder(Material.GOLD_NUGGET).setDisplayName(ItemNames.RAIN_GOLDNUGGET).setLore(" ", "§8•§7● Preis", "  §8* §e20.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder snowballRainBuilder = new ItemBuilder(Material.SNOW_BALL).setDisplayName(ItemNames.RAIN_SNOWBALL).setLore(" ", "§8•§7● Preis", "  §8* §e20.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder netherstarRainBuilder = new ItemBuilder(Material.NETHER_STAR).setDisplayName(ItemNames.RAIN_NETHERSTAR).setLore(" ", "§8•§7● Preis", "  §8* §e20.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.RAIN, 1)) {
            diamondRainBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getRainGadget(player.getUniqueId()) == 1)
                diamondRainBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.RAIN, 2)) {
            emeraldRainBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getRainGadget(player.getUniqueId()) == 2)
                emeraldRainBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.RAIN, 3)) {
            redstoneRainBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getRainGadget(player.getUniqueId()) == 3)
                redstoneRainBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.RAIN, 4)) {
            goldnuggetRainBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getRainGadget(player.getUniqueId()) == 4)
                goldnuggetRainBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.RAIN, 5)) {
            snowballRainBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getRainGadget(player.getUniqueId()) == 5)
                snowballRainBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.RAIN, 6)) {
            netherstarRainBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getRainGadget(player.getUniqueId()) == 6)
                netherstarRainBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        ItemStack diamondRain = diamondRainBuilder.build(); ItemStack emeraldRain = emeraldRainBuilder.build(); ItemStack redstoneRain = redstoneRainBuilder.build();
        ItemStack goldnuggetRain = goldnuggetRainBuilder.build(); ItemStack snowballRain = snowballRainBuilder.build(); ItemStack netherstarRain = netherstarRainBuilder.build();
        ItemStack back = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/bb0f6e8af46ac6faf88914191ab66f261d6726a7999c637cf2e4159fe1fc477")).setDisplayName(ItemNames.GADGETS_BACK).build();

        inventoryBuilder.setItem(11, diamondRain); inventoryBuilder.setItem(13, emeraldRain); inventoryBuilder.setItem(15, redstoneRain);
        inventoryBuilder.setItem(29, goldnuggetRain); inventoryBuilder.setItem(31, snowballRain); inventoryBuilder.setItem(33, netherstarRain);
        inventoryBuilder.setItem(36, back);

        inventoryBuilder.fillInventory(new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build());
        player.openInventory(inventoryBuilder.build());
        inventoryManager.openGadgetListMenu(player);
    }

    public void openBootsMenu(Player player) {
        InventoryBuilder inventoryBuilder = new InventoryBuilder();
        inventoryBuilder.setSize(9*5);
        inventoryBuilder.setTitle(ItemNames.TITLE_BOOTS);

        // 11 - 13 - 15 - 29 - 31 - 33
        ItemBuilder heartsBootsBuilder = new ItemBuilder(Material.LEATHER_BOOTS, Color.RED, true).setDisplayName(ItemNames.BOOTS_HEARTS).setLore(" ", "§8•§7● Preis", "  §8* §e15.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder snowBootsBuilder = new ItemBuilder(Material.LEATHER_BOOTS, Color.WHITE, true).setDisplayName(ItemNames.BOOTS_SNOW).setLore(" ", "§8•§7● Preis", "  §8* §e15.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder waterBootsBuilder = new ItemBuilder(Material.LEATHER_BOOTS, Color.AQUA, true).setDisplayName(ItemNames.BOOTS_WATER).setLore(" ", "§8•§7● Preis", "  §8* §e15.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder fireBootsBuilder = new ItemBuilder(Material.LEATHER_BOOTS, Color.ORANGE, true).setDisplayName(ItemNames.BOOTS_FIRE).setLore(" ", "§8•§7● Preis", "  §8* §e15.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder smokeBootsBuilder = new ItemBuilder(Material.LEATHER_BOOTS, Color.GRAY, true).setDisplayName(ItemNames.BOOTS_SMOKE).setLore(" ", "§8•§7● Preis", "  §8* §e15.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder notesBootsBuilder = new ItemBuilder(Material.LEATHER_BOOTS, Color.LIME, true).setDisplayName(ItemNames.BOOTS_NOTES).setLore(" ", "§8•§7● Preis", "  §8* §e15.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.BOOTS, 1)) {
            heartsBootsBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getBootsGadget(player.getUniqueId()) == 1)
                heartsBootsBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.BOOTS, 2)) {
            snowBootsBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getBootsGadget(player.getUniqueId()) == 2)
                snowBootsBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.BOOTS, 3)) {
            waterBootsBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getBootsGadget(player.getUniqueId()) == 3)
                waterBootsBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.BOOTS, 4)) {
            fireBootsBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getBootsGadget(player.getUniqueId()) == 4)
                fireBootsBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.BOOTS, 5)) {
            smokeBootsBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getBootsGadget(player.getUniqueId()) == 5)
                smokeBootsBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.BOOTS, 6)) {
            notesBootsBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getBootsGadget(player.getUniqueId()) == 6)
                notesBootsBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        ItemStack heartsBoots = heartsBootsBuilder.build(); ItemStack snowBoots = snowBootsBuilder.build(); ItemStack waterBoots = waterBootsBuilder.build();
        ItemStack fireBoots = fireBootsBuilder.build(); ItemStack smokeBoots = smokeBootsBuilder.build(); ItemStack notesBoots = notesBootsBuilder.build();
        ItemStack back = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/bb0f6e8af46ac6faf88914191ab66f261d6726a7999c637cf2e4159fe1fc477")).setDisplayName(ItemNames.GADGETS_BACK).build();

        inventoryBuilder.setItem(11, heartsBoots); inventoryBuilder.setItem(13, snowBoots); inventoryBuilder.setItem(15, waterBoots);
        inventoryBuilder.setItem(29, fireBoots); inventoryBuilder.setItem(31, smokeBoots); inventoryBuilder.setItem(33, notesBoots);
        inventoryBuilder.setItem(36, back);

        inventoryBuilder.fillInventory(new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build());
        player.openInventory(inventoryBuilder.build());
        inventoryManager.openGadgetListMenu(player);
    }

    public void openHatsMenu(Player player) {
        InventoryBuilder inventoryBuilder = new InventoryBuilder();
        inventoryBuilder.setSize(9*5);
        inventoryBuilder.setTitle(ItemNames.TITLE_HATS);

        // 11 - 13 - 15 - 29 - 31 - 33
        ItemBuilder glassBuilder = new ItemBuilder(Material.GLASS).setDisplayName(ItemNames.HATS_GLASS).setLore(" ", "§8•§7● Preis", "  §8* §e2.500 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder monsterspawnerBuilder = new ItemBuilder(Material.MOB_SPAWNER).setDisplayName(ItemNames.HATS_MONSTERSPAWNER).setLore(" ", "§8•§7● Preis", "  §8* §e2.500 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder commandblockBuilder = new ItemBuilder(Material.COMMAND).setDisplayName(ItemNames.HATS_COMMANDBLOCK).setLore(" ", "§8•§7● Preis", "  §8* §e2.500 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder leavesBuilder = new ItemBuilder(Material.LEAVES, 1).setDisplayName(ItemNames.HATS_LEAVES).setLore(" ", "§8•§7● Preis", "  §8* §e2.500 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder slimeblockBuilder = new ItemBuilder(Material.SLIME_BLOCK).setDisplayName(ItemNames.HATS_SLIMEBLOCK).setLore(" ", "§8•§7● Preis", "  §8* §e2.500 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder redstonelampBuilder = new ItemBuilder(Material.REDSTONE_LAMP_OFF).setDisplayName(ItemNames.HATS_REDSTONELAMP).setLore(" ", "§8•§7● Preis", "  §8* §e2.500 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.HAT, 1)) {
            glassBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getHatGadget(player.getUniqueId()) == 1)
                glassBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.HAT, 2)) {
            monsterspawnerBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getHatGadget(player.getUniqueId()) == 2)
                monsterspawnerBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.HAT, 3)) {
            commandblockBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getHatGadget(player.getUniqueId()) == 3)
                commandblockBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.HAT, 4)) {
            leavesBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getHatGadget(player.getUniqueId()) == 4)
                leavesBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.HAT, 5)) {
            slimeblockBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getHatGadget(player.getUniqueId()) == 5)
                slimeblockBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.HAT, 6)) {
            redstonelampBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getHatGadget(player.getUniqueId()) == 6)
                redstonelampBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }

        ItemStack glass = glassBuilder.build(); ItemStack monsterspawner = monsterspawnerBuilder.build(); ItemStack commandblock = commandblockBuilder.build();
        ItemStack leaves = leavesBuilder.build(); ItemStack slimeblock = slimeblockBuilder.build(); ItemStack redstonelamp = redstonelampBuilder.build();
        ItemStack back = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/bb0f6e8af46ac6faf88914191ab66f261d6726a7999c637cf2e4159fe1fc477")).setDisplayName(ItemNames.GADGETS_BACK).build();

        inventoryBuilder.setItem(11, glass); inventoryBuilder.setItem(13, monsterspawner); inventoryBuilder.setItem(15, commandblock);
        inventoryBuilder.setItem(29, leaves); inventoryBuilder.setItem(31, slimeblock); inventoryBuilder.setItem(33, redstonelamp);
        inventoryBuilder.setItem(36, back);

        inventoryBuilder.fillInventory(new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build());
        player.openInventory(inventoryBuilder.build());
        inventoryManager.openGadgetListMenu(player);
    }

    public void openSkullsMenu(Player player) {
        InventoryBuilder inventoryBuilder = new InventoryBuilder();
        inventoryBuilder.setSize(9*5);
        inventoryBuilder.setTitle(ItemNames.TITLE_SKULLS);

        // 10 - 12 - 14 - 16 - 20 - 22 - 24 - 28 - 30 - 32 - 34
        ItemBuilder presentBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/6cef9aa14e884773eac134a4ee8972063f466de678363cf7b1a21a85b7")).setDisplayName(ItemNames.SKULLS_PRESENT).setLore(" ", "§8•§7● Preis", "  §8* §e5.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder smileBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/52e98165deef4ed621953921c1ef817dc638af71c1934a4287b69d7a31f6b8")).setDisplayName(ItemNames.SKULLS_SMILE).setLore(" ", "§8•§7● Preis", "  §8* §e5.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder crewmateBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/f50b28033d31bafcba26eedef061b87eccc4969db1814e455d06222f80913a16")).setDisplayName(ItemNames.SKULLS_CREWMATE).setLore(" ", "§8•§7● Preis", "  §8* §e5.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder globeBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/2e2cc42015e6678f8fd49ccc01fbf787f1ba2c32bcf559a015332fc5db50")).setDisplayName(ItemNames.SKULLS_GLOBE).setLore(" ", "§8•§7● Preis", "  §8* §e5.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder chocolateBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/6acfc786469e636bf4eaac2ed49d9a6c5212af6d9071d961944be8a935f478e")).setDisplayName(ItemNames.SKULLS_CHOCOLATE).setLore(" ", "§8•§7● Preis", "  §8* §e5.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder pandaBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/df00858926cd8cdf3f1cf71e210cde5daf8708320547bd6df5795859c68d9b3f")).setDisplayName(ItemNames.SKULLS_PANDA).setLore(" ", "§8•§7● Preis", "  §8* §e5.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder strawberryBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/1c9a8315fdc307b8315918eeae7dd84269104b3d9bd779fc2ba779515b80214d")).setDisplayName(ItemNames.SKULLS_STRAWBERRY).setLore(" ", "§8•§7● Preis", "  §8* §e5.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder eyeBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/63bc0c5ea186c27a64f6c20da19855d2e0910d495f0985bfb01ca3c7d1514ca7")).setDisplayName(ItemNames.SKULLS_EYE).setLore(" ", "§8•§7● Preis", "  §8* §e5.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder elmoBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/1439189e6137532292c858f552122413108cdce729e26ee7b135ea7ca428ec5b")).setDisplayName(ItemNames.SKULLS_ELMO).setLore(" ", "§8•§7● Preis", "  §8* §e5.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder stormtrooperBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/52284e132bfd659bc6ada497c4fa3094cd93231a6b505a12ce7cd5135ba8ff93")).setDisplayName(ItemNames.SKULLS_STORMTROOPER).setLore(" ", "§8•§7● Preis", "  §8* §e5.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder cubeBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/6859e2f028b2956dae70ad0181568fb0ecfd74db2be0f9a5ef34d2867c37f769")).setDisplayName(ItemNames.SKULLS_CUBE).setLore(" ", "§8•§7● Preis", "  §8* §e5.000 Coins", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.SKULL, 1)) {
            presentBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getSkullGadget(player.getUniqueId()) == 1)
                presentBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.SKULL, 2)) {
            smileBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getSkullGadget(player.getUniqueId()) == 2)
                smileBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.SKULL, 3)) {
            crewmateBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getSkullGadget(player.getUniqueId()) == 3)
                crewmateBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.SKULL, 4)) {
            globeBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getSkullGadget(player.getUniqueId()) == 4)
                globeBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.SKULL, 5)) {
            chocolateBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getSkullGadget(player.getUniqueId()) == 5)
                chocolateBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.SKULL, 6)) {
            pandaBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getSkullGadget(player.getUniqueId()) == 6)
                pandaBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.SKULL, 7)) {
            strawberryBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getSkullGadget(player.getUniqueId()) == 7)
                strawberryBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.SKULL, 8)) {
            eyeBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getSkullGadget(player.getUniqueId()) == 8)
                eyeBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.SKULL, 9)) {
            elmoBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getSkullGadget(player.getUniqueId()) == 9)
                elmoBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.SKULL, 10)) {
            stormtrooperBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getSkullGadget(player.getUniqueId()) == 10)
                stormtrooperBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }
        if(gadgetManager.hasBoughtGadget(player.getUniqueId(), GadgetManager.GadgetType.SKULL, 11)) {
            cubeBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
            if(gadgetManager.getSkullGadget(player.getUniqueId()) == 11)
                cubeBuilder.setLore(" ", "§8•§7● Preis", "  §8* §a✔ Gekauft", "", "§8•§7● Status", "  §8* §aAusgewählt", "");
        }


        ItemStack present = presentBuilder.build(); ItemStack smile = smileBuilder.build(); ItemStack crewmate = crewmateBuilder.build();
        ItemStack globe = globeBuilder.build(); ItemStack chocolate = chocolateBuilder.build(); ItemStack panda = pandaBuilder.build();
        ItemStack strawberry = strawberryBuilder.build(); ItemStack eye = eyeBuilder.build(); ItemStack elmo = elmoBuilder.build();
        ItemStack stormtrooper = stormtrooperBuilder.build(); ItemStack cube = cubeBuilder.build();
        ItemStack back = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/bb0f6e8af46ac6faf88914191ab66f261d6726a7999c637cf2e4159fe1fc477")).setDisplayName(ItemNames.GADGETS_BACK).build();

        inventoryBuilder.setItem(10, present); inventoryBuilder.setItem(12, smile); inventoryBuilder.setItem(14, crewmate);
        inventoryBuilder.setItem(16, globe); inventoryBuilder.setItem(20, chocolate); inventoryBuilder.setItem(22, panda);
        inventoryBuilder.setItem(24, strawberry); inventoryBuilder.setItem(28, eye); inventoryBuilder.setItem(30, elmo);
        inventoryBuilder.setItem(32, stormtrooper); inventoryBuilder.setItem(34, cube);
        inventoryBuilder.setItem(36, back);

        inventoryBuilder.fillInventory(new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build());
        player.openInventory(inventoryBuilder.build());
        inventoryManager.openGadgetListMenu(player);
    }

    public static ItemStack getSkull(String url) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        if(url.isEmpty())return head;


        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = org.apache.commons.codec.binary.Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }

    public Integer getGadgetSetting(GadgetManager.GadgetType gadgetType, String itemName) {
        int gadgetSetting = 0;

        switch (gadgetType) {
            case SKULL:
                if(itemName.equals(ItemNames.SKULLS_PRESENT))
                    gadgetSetting = 1;
                else if(itemName.equals(ItemNames.SKULLS_SMILE))
                    gadgetSetting = 2;
                else if(itemName.equals(ItemNames.SKULLS_CREWMATE))
                    gadgetSetting = 3;
                else if(itemName.equals(ItemNames.SKULLS_GLOBE))
                    gadgetSetting = 4;
                else if(itemName.equals(ItemNames.SKULLS_CHOCOLATE))
                    gadgetSetting = 5;
                else if(itemName.equals(ItemNames.SKULLS_PANDA))
                    gadgetSetting = 6;
                else if(itemName.equals(ItemNames.SKULLS_STRAWBERRY))
                    gadgetSetting = 7;
                else if(itemName.equals(ItemNames.SKULLS_EYE))
                    gadgetSetting = 8;
                else if(itemName.equals(ItemNames.SKULLS_ELMO))
                    gadgetSetting = 9;
                else if(itemName.equals(ItemNames.SKULLS_STORMTROOPER))
                    gadgetSetting = 10;
                else if(itemName.equals(ItemNames.SKULLS_CUBE))
                    gadgetSetting = 11;
                break;
            case HAT:
                if(itemName.equals(ItemNames.HATS_GLASS))
                    gadgetSetting = 1;
                else if(itemName.equals(ItemNames.HATS_MONSTERSPAWNER))
                    gadgetSetting = 2;
                else if(itemName.equals(ItemNames.HATS_COMMANDBLOCK))
                    gadgetSetting = 3;
                else if(itemName.equals(ItemNames.HATS_LEAVES))
                    gadgetSetting = 4;
                else if(itemName.equals(ItemNames.HATS_SLIMEBLOCK))
                    gadgetSetting = 5;
                else if(itemName.equals(ItemNames.HATS_REDSTONELAMP))
                    gadgetSetting = 6;
                break;
            case TOOL:
                if(itemName.equals(ItemNames.TOOLS_HOOK))
                    gadgetSetting = 1;
                else if(itemName.equals(ItemNames.TOOLS_PAINTBALL))
                    gadgetSetting = 2;
                break;
            case BOOTS:
                if(itemName.equals(ItemNames.BOOTS_HEARTS))
                    gadgetSetting = 1;
                else if(itemName.equals(ItemNames.BOOTS_SNOW))
                    gadgetSetting = 2;
                else if(itemName.equals(ItemNames.BOOTS_WATER))
                    gadgetSetting = 3;
                else if(itemName.equals(ItemNames.BOOTS_FIRE))
                    gadgetSetting = 4;
                else if(itemName.equals(ItemNames.BOOTS_SMOKE))
                    gadgetSetting = 5;
                else if(itemName.equals(ItemNames.BOOTS_NOTES))
                    gadgetSetting = 6;
                break;
            case RAIN:
                if(itemName.equals(ItemNames.RAIN_DIAMOND))
                    gadgetSetting = 1;
                else if(itemName.equals(ItemNames.RAIN_EMERALD))
                    gadgetSetting = 2;
                else if(itemName.equals(ItemNames.RAIN_REDSTONE))
                    gadgetSetting = 3;
                else if(itemName.equals(ItemNames.RAIN_GOLDNUGGET))
                    gadgetSetting = 4;
                else if(itemName.equals(ItemNames.RAIN_SNOWBALL))
                    gadgetSetting = 5;
                else if(itemName.equals(ItemNames.RAIN_NETHERSTAR))
                    gadgetSetting = 6;
                break;
            default:
                break;
        }
        return gadgetSetting;
    }

    private void registerNewGadget(Player player, String inventoryTitle, String itemName) {
        GadgetManager.GadgetType gadgetType = null; int gadgetSetting = 0;

        switch (inventoryTitle) {
            case ItemNames.TITLE_SKULLS:
                if(itemName.equals(ItemNames.SKULLS_PRESENT))
                    gadgetSetting = 1;
                else if(itemName.equals(ItemNames.SKULLS_SMILE))
                    gadgetSetting = 2;
                else if(itemName.equals(ItemNames.SKULLS_CREWMATE))
                    gadgetSetting = 3;
                else if(itemName.equals(ItemNames.SKULLS_GLOBE))
                    gadgetSetting = 4;
                else if(itemName.equals(ItemNames.SKULLS_CHOCOLATE))
                    gadgetSetting = 5;
                else if(itemName.equals(ItemNames.SKULLS_PANDA))
                    gadgetSetting = 6;
                else if(itemName.equals(ItemNames.SKULLS_STRAWBERRY))
                    gadgetSetting = 7;
                else if(itemName.equals(ItemNames.SKULLS_EYE))
                    gadgetSetting = 8;
                else if(itemName.equals(ItemNames.SKULLS_ELMO))
                    gadgetSetting = 9;
                else if(itemName.equals(ItemNames.SKULLS_STORMTROOPER))
                    gadgetSetting = 10;
                else if(itemName.equals(ItemNames.SKULLS_CUBE))
                    gadgetSetting = 11;
                gadgetType = GadgetManager.GadgetType.SKULL;
                break;
            case ItemNames.TITLE_HATS:
                if(itemName.equals(ItemNames.HATS_GLASS))
                    gadgetSetting = 1;
                else if(itemName.equals(ItemNames.HATS_MONSTERSPAWNER))
                    gadgetSetting = 2;
                else if(itemName.equals(ItemNames.HATS_COMMANDBLOCK))
                    gadgetSetting = 3;
                else if(itemName.equals(ItemNames.HATS_LEAVES))
                    gadgetSetting = 4;
                else if(itemName.equals(ItemNames.HATS_SLIMEBLOCK))
                    gadgetSetting = 5;
                else if(itemName.equals(ItemNames.HATS_REDSTONELAMP))
                    gadgetSetting = 6;
                gadgetType = GadgetManager.GadgetType.HAT;
                break;
            case ItemNames.TITLE_TOOLS:
                if(itemName.equals(ItemNames.TOOLS_HOOK))
                    gadgetSetting = 1;
                else if(itemName.equals(ItemNames.TOOLS_PAINTBALL))
                    gadgetSetting = 2;
                gadgetType = GadgetManager.GadgetType.TOOL;
                break;
            case ItemNames.TITLE_BOOTS:
                if(itemName.equals(ItemNames.BOOTS_HEARTS))
                    gadgetSetting = 1;
                else if(itemName.equals(ItemNames.BOOTS_SNOW))
                    gadgetSetting = 2;
                else if(itemName.equals(ItemNames.BOOTS_WATER))
                    gadgetSetting = 3;
                else if(itemName.equals(ItemNames.BOOTS_FIRE))
                    gadgetSetting = 4;
                else if(itemName.equals(ItemNames.BOOTS_SMOKE))
                    gadgetSetting = 5;
                else if(itemName.equals(ItemNames.BOOTS_NOTES))
                    gadgetSetting = 6;
                gadgetType = GadgetManager.GadgetType.BOOTS;
                break;
            case ItemNames.TITLE_RAIN:
                if(itemName.equals(ItemNames.RAIN_DIAMOND))
                    gadgetSetting = 1;
                else if(itemName.equals(ItemNames.RAIN_EMERALD))
                    gadgetSetting = 2;
                else if(itemName.equals(ItemNames.RAIN_REDSTONE))
                    gadgetSetting = 3;
                else if(itemName.equals(ItemNames.RAIN_GOLDNUGGET))
                    gadgetSetting = 4;
                else if(itemName.equals(ItemNames.RAIN_SNOWBALL))
                    gadgetSetting = 5;
                else if(itemName.equals(ItemNames.RAIN_NETHERSTAR))
                    gadgetSetting = 6;
                gadgetType = GadgetManager.GadgetType.RAIN;
                break;
            default:
                break;
        }
        gadgetTypeCache.put(player.getUniqueId(), gadgetType);
        gadgetSettingCache.put(player.getUniqueId(), gadgetSetting);
    }

    public GadgetManager.GadgetType getCurrentGadgetType(UUID uuid) {
        return gadgetTypeCache.get(uuid);
    }

    public Integer getCurrentGadgetSetting(UUID uuid) {
        return gadgetSettingCache.get(uuid);
    }

}

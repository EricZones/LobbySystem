// Created by Eric B. 17.05.2021 12:06
package de.ericzones.lobbysystem.manager;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.ericzones.bungeedriver.BungeeDriver;
import de.ericzones.bungeedriver.collectives.plugindata.object.DataCorePlayer;
import org.apache.commons.codec.binary.Base64;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.ext.bridge.ServiceInfoSnapshotUtil;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.ItemBuilder;
import de.ericzones.lobbysystem.extra.ItemNames;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class InventoryManager {

    private LobbySystem instance;
    private ExtraManager extraManager;
    private ChatManager chatManager;
    private GadgetManager gadgetManager;

    public InventoryManager(LobbySystem instance) {
        this.instance = instance;
        extraManager = instance.getExtraManager();
        chatManager = instance.getChatManager();
        gadgetManager = instance.getGadgetManager();
    }

    public void setInventory(Player player) {
        player.getInventory().clear();
        player.getEquipment().setHelmet(null);
        player.getEquipment().setChestplate(null);
        player.getEquipment().setLeggings(null);
        player.getEquipment().setBoots(null);

        if (instance.getBuildPlayers().contains(player.getUniqueId())) {
            player.getInventory().setItem(22, new ItemBuilder(Material.GRASS).setDisplayName(ItemNames.INV_BUILDMODE_ON).build());
            return;
        }

        ItemStack invBackground = new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build();
        for(int i = 9; i < 36; i++) {
            player.getInventory().setItem(i, invBackground);
        }

        player.getInventory().setItem(4, new ItemBuilder(Material.COMPASS).setDisplayName(ItemNames.INV_NAVIGATOR).build());
        player.getInventory().setItem(7, new ItemBuilder(Material.BLAZE_ROD).setDisplayName(ItemNames.INV_PLAYERHIDER).build());
        player.getInventory().setItem(8, new ItemBuilder(instance.getCachedSkulls().get(player.getUniqueId())).setDisplayName(ItemNames.INV_PROFILE).removeLore().build());


        if(extraManager.getDeluxepearlExtra().get(player.getUniqueId()) == ExtraManager.ExtraSetting.ON) {
            if(!extraManager.getActivePearls().contains(player.getUniqueId()))
                player.getInventory().setItem(0, new ItemBuilder(Material.ENDER_PEARL).setDisplayName(ItemNames.INV_DELUXEPEARL).build());
            else
                player.getInventory().setItem(0, new ItemBuilder(Material.FIREWORK_CHARGE).setDisplayName(" ").setLore("§8➜ §7Warte 10 Sekunden").build());
            player.getInventory().setItem(13, new ItemBuilder(Material.ENDER_PEARL).setDisplayName(ItemNames.INV_DELUXEPEARL_ON).build());
        } else {
            if(!extraManager.getActivePearls().contains(player.getUniqueId()))
                player.getInventory().setItem(0, new ItemBuilder(Material.ENDER_PEARL).setDisplayName(ItemNames.INV_ENDERPEARL).build());
            else
                player.getInventory().setItem(0, new ItemBuilder(Material.FIREWORK_CHARGE).setDisplayName(" ").setLore("§8➜ §7Warte 10 Sekunden").build());
            player.getInventory().setItem(13, new ItemBuilder(Material.ENDER_PEARL).setDisplayName(ItemNames.INV_DELUXEPEARL_OFF).build());
        }

        if(extraManager.getDoublejumpExtra().get(player.getUniqueId()) == ExtraManager.ExtraSetting.ON)
            player.getInventory().setItem(21, new ItemBuilder(Material.SLIME_BALL).setDisplayName(ItemNames.INV_DOUBLEJUMP_ON).build());
        else
            player.getInventory().setItem(21, new ItemBuilder(Material.SLIME_BALL).setDisplayName(ItemNames.INV_DOUBLEJUMP_OFF).build());
        if(extraManager.getFlyExtra().get(player.getUniqueId()) == ExtraManager.ExtraSetting.ON)
            player.getInventory().setItem(23, new ItemBuilder(Material.FEATHER).setDisplayName(ItemNames.INV_FLYMODE_ON).build());
        else
            player.getInventory().setItem(23, new ItemBuilder(Material.FEATHER).setDisplayName(ItemNames.INV_FLYMODE_OFF).build());
        if(extraManager.getShieldExtra().get(player.getUniqueId()) == ExtraManager.ExtraSetting.ON)
            player.getInventory().setItem(31, new ItemBuilder(Material.EYE_OF_ENDER).setDisplayName(ItemNames.INV_SHIELD_ON).build());
        else
            player.getInventory().setItem(31, new ItemBuilder(Material.EYE_OF_ENDER).setDisplayName(ItemNames.INV_SHIELD_OFF).build());

        if(player.hasPermission("spigot.command.build"))
            player.getInventory().setItem(22, new ItemBuilder(Material.GRASS).setDisplayName(ItemNames.INV_BUILDMODE_OFF).build());

        if(gadgetManager.getSkullGadget(player.getUniqueId()) != 0)
            player.getInventory().setHelmet(getHeadGadget(GadgetManager.GadgetType.SKULL, gadgetManager.getSkullGadget(player.getUniqueId())));
        else if(gadgetManager.getHatGadget(player.getUniqueId()) != 0)
            player.getInventory().setHelmet(getHeadGadget(GadgetManager.GadgetType.HAT, gadgetManager.getHatGadget(player.getUniqueId())));

        if(gadgetManager.getToolGadget(player.getUniqueId()) != 0)
            player.getInventory().setItem(1, getToolGadget(gadgetManager.getToolGadget(player.getUniqueId())));

        if(gadgetManager.getBootsGadget(player.getUniqueId()) != 0)
            player.getInventory().setBoots(getBootsGadget(gadgetManager.getBootsGadget(player.getUniqueId())));

        player.updateInventory();
    }

    public void openNavigator(Player player) {
        player.getInventory().clear();
        player.updateInventory();
        ItemStack item = new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build();
        for(int i = 9; i < 36; i++)
            player.getInventory().setItem(i, item);

        Iterator<ItemStack> lobbies = getLobbies(player).iterator();
        for(int i = 21; i < 24; i++) {
            if(lobbies.hasNext())
                player.getInventory().setItem(i, lobbies.next());
        }
        player.updateInventory();
    }

    public void openPlayerhider(Player player) {
        player.getInventory().clear();
        player.updateInventory();
        ItemStack item = new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build();
        for(int i = 9; i < 36; i++)
            player.getInventory().setItem(i, item);
        player.updateInventory();
    }

    public void openGadgetMainMenu(Player player) {
        player.getInventory().clear();
        player.updateInventory();
        ItemStack item = new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build();
        for(int i = 9; i < 36; i++)
            player.getInventory().setItem(i, item);
        ItemStack deactivateAll = new ItemBuilder(Material.BARRIER).setDisplayName(ItemNames.GADGETS_DEACTIVATEALL).build();
        player.getInventory().setItem(22, deactivateAll);
        player.updateInventory();
    }

    public void openGadgetListMenu(Player player) {
        player.getInventory().clear();
        player.updateInventory();
        ItemStack item = new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build();
        for(int i = 9; i < 36; i++)
            player.getInventory().setItem(i, item);
        player.updateInventory();
    }

    public void openFriendMenu(Player player) {
        player.getInventory().clear();
        player.updateInventory();
        ItemStack item = new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build();
        for(int i = 9; i < 36; i++)
            player.getInventory().setItem(i, item);
        player.updateInventory();
    }

    public void openChatsettings(Player player) {
        player.getInventory().clear();
        player.updateInventory();
        ItemStack item = new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build();
        for(int i = 9; i < 36; i++)
            player.getInventory().setItem(i, item);
        ItemBuilder vanishBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/d2cd9a11ad2e4d955479fed92be78dfa5184a252ec1afcdb73d3a323939fc")).setDisplayName(ItemNames.CHATSETTINGS_VANISH).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder crossedBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/bd8a99db2c37ec71d7199cd52639981a7513ce9cca9626a3936f965b131193")).setDisplayName(ItemNames.CHATSETTINGS_CROSSED).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder underlinedBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/7966f891c1546aecbfcc3baedcfb67079d7f2a6a8b739ed5bac2bb3cf308d38")).setDisplayName(ItemNames.CHATSETTINGS_UNDERLINED).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder boldBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/2e3f50ba62cbda3ecf5479b62fedebd61d76589771cc19286bf2745cd71e47c6")).setDisplayName(ItemNames.CHATSETTINGS_BOLD).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder italicBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/7f95d7c1bbf3afa285d8d96757bb5572259a3ae854f5389dc53207699d94fd8")).setDisplayName(ItemNames.CHATSETTINGS_ITALIC).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        ItemBuilder noneBuilder = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/5a6787ba32564e7c2f3a0ce64498ecbb23b89845e5a66b5cec7736f729ed37")).setDisplayName(ItemNames.CHATSETTINGS_NONE).setLore(" ", "§8•§7● Status", "  §8* §cNicht ausgewählt", "");
        switch (chatManager.getChatFormat(player.getUniqueId())) {
            case VANISH:
                vanishBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "");
                break;
            case CROSSED:
                crossedBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "");
                break;
            case UNDERLINED:
                underlinedBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "");
                break;
            case BOLD:
                boldBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "");
                break;
            case ITALIC:
                italicBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "");
                break;
            case NONE:
                noneBuilder.setLore(" ", "§8•§7● Status", "  §8* §aAusgewählt", "");
                break;
            default:
                break;
        }
        ItemStack vanish = vanishBuilder.build(); ItemStack crossed = crossedBuilder.build(); ItemStack underlined = underlinedBuilder.build();
        ItemStack bold = boldBuilder.build(); ItemStack italic = italicBuilder.build(); ItemStack none = noneBuilder.build();
        player.getInventory().setItem(20, vanish); player.getInventory().setItem(21, crossed); player.getInventory().setItem(22, underlined);
        player.getInventory().setItem(23, bold); player.getInventory().setItem(24, italic); player.getInventory().setItem(13, none);
        player.updateInventory();
    }

    public void openProfile(Player player) {
        player.getInventory().clear();
        player.updateInventory();
        ItemStack item = new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").build();
        for(int i = 9; i < 36; i++)
            player.getInventory().setItem(i, item);
        player.getInventory().setItem(21, new ItemBuilder(getSkull("http://textures.minecraft.net/texture/b0a7b94c4e581b699159d48846ec091392506237c89a97c93248a0d8abc916d5")).setDisplayName(ItemNames.PROFILE_COINS).setLore(" ", "§8•§7● Betrag", "  §8* §e"+getCoins(player)+" Coins", "").build());
        player.getInventory().setItem(23, new ItemBuilder(getSkull("http://textures.minecraft.net/texture/b86b9d58bcd1a555f93e7d8659159cfd25b8dd6e9bce1e973822824291862")).setDisplayName(ItemNames.PROFILE_PLAYTIME).setLore(" ", "§8•§7● Betrag", "  §8* §a"+getPlaytime(player), "").build());
        player.updateInventory();
    }

    private ArrayList<ItemStack> getLobbies(Player player) {
//        ArrayList<ItemStack> list = new ArrayList<>();
//        Collection<ServiceInfoSnapshot> lobbies = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices("Lobby");
//
//        ICloudPlayer cloudPlayer = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).getOnlinePlayer(player.getUniqueId());
//        String server = cloudPlayer.getConnectedService().getServerName();
//
//        for(ServiceInfoSnapshot current : lobbies) {
//            if(current.isConnected()) {
//                Optional<Integer> players2 = current.getProperty(BridgeServiceProperty.ONLINE_COUNT);
//                int players = 0; if(players2.isPresent()) players = players2.get();
//
//                if(current.getName().equals(server))
//                     list.add(new ItemBuilder(Material.SULPHUR).setEnchanted(true).setDisplayName("§8➜ §b"+current.getServiceId().getName()).setLore(" ", "§8•§7● Status", "  §8* §bOnline", "", "§8•§7● Spieler", "  §8* §b"+players, " ").build());
//                else {
//                    Optional<Boolean> starting = current.getProperty(BridgeServiceProperty.IS_STARTING);
//                    if(starting.isPresent() && starting.get()) {
//                        list.add(new ItemBuilder(Material.SUGAR).setDisplayName("§8➜ §c"+current.getServiceId().getName()).setLore(" ", "§8•§7● Status", "  §8* §cOffline", " ").build());
//                    } else
//                        list.add(new ItemBuilder(Material.SULPHUR).setDisplayName("§8➜ §b" + current.getServiceId().getName()).setLore(" ", "§8•§7● Status", "  §8* §bOnline", "", "§8•§7● Spieler", "  §8* §b"+players, " ").build());
//                }
//            } else {
//                list.add(new ItemBuilder(Material.SUGAR).setDisplayName("§8➜ §c"+current.getServiceId().getName()).setLore(" ", "§8•§7● Status", "  §8* §cOffline", " ").build());
//            }
//        }
//        if(list.size() == 2) {
//            list.add(new ItemBuilder(Material.SUGAR).setDisplayName("§8➜ §cLobby-3").setLore(" ", "§8•§7● Status", "  §8* §cOffline", " ").build());
//        }
//        Collections.sort(list, new Comparator<ItemStack>() {
//            @Override
//            public int compare(ItemStack o1, ItemStack o2) {
//                return o1.getItemMeta().getDisplayName().replace("§8➜ ", "").replace("§c", "").replace("§b", "").compareTo(o2.getItemMeta().getDisplayName().replace("§8➜ ", "").replace("§c", "").replace("§b", ""));
//            }
//        });
//        return list;
        ArrayList<ItemStack> list = new ArrayList<>();
        Collection<ServiceInfoSnapshot> lobbies = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices("Lobby");
        for(ServiceInfoSnapshot current : lobbies) {
            if(ServiceInfoSnapshotUtil.isOnline(current)) {
                Optional<Integer> players2 = current.getProperty(BridgeServiceProperty.ONLINE_COUNT);
                int players = 0; if(players2.isPresent()) players = players2.get();
                if(Wrapper.getInstance().getServiceId().getTaskServiceId() == current.getServiceId().getTaskServiceId()) {
                    list.add(new ItemBuilder(Material.SULPHUR).setEnchanted(true).setDisplayName("§8➜ §b"+current.getServiceId().getName()).setLore(" ", "§8•§7● Status", "  §8* §bOnline", "", "§8•§7● Spieler", "  §8* §b"+players, " ").build());
                } else {
                    Optional<Boolean> starting = current.getProperty(BridgeServiceProperty.IS_STARTING);
                    if(starting.isPresent() && starting.get()) {
                        list.add(new ItemBuilder(Material.SUGAR).setDisplayName("§8➜ §c"+current.getServiceId().getName()).setLore(" ", "§8•§7● Status", "  §8* §cOffline", " ").build());
                    } else
                        list.add(new ItemBuilder(Material.SULPHUR).setDisplayName("§8➜ §b" + current.getServiceId().getName()).setLore(" ", "§8•§7● Status", "  §8* §bOnline", "", "§8•§7● Spieler", "  §8* §b"+players, " ").build());
                }
            } else {
                list.add(new ItemBuilder(Material.SUGAR).setDisplayName("§8➜ §c"+current.getServiceId().getName()).setLore(" ", "§8•§7● Status", "  §8* §cOffline", " ").build());
            }
        }
        if(list.size() == 2) {
            list.add(new ItemBuilder(Material.SUGAR).setDisplayName("§8➜ §cLobby-3").setLore(" ", "§8•§7● Status", "  §8* §cOffline", " ").build());
        }
        Collections.sort(list, new Comparator<ItemStack>() {
            @Override
            public int compare(ItemStack o1, ItemStack o2) {
                return o1.getItemMeta().getDisplayName().replace("§8➜ ", "").replace("§c", "").replace("§b", "").compareTo(o2.getItemMeta().getDisplayName().replace("§8➜ ", "").replace("§c", "").replace("§b", ""));
            }
        });
        return list;
    }

    private ItemStack getSkull(String url) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        if(url.isEmpty())return head;
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
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

    private String getCoins(Player player) {
        DataCorePlayer dataCorePlayer = BungeeDriver.getInstance().getCorePlayerManager().getCorePlayer(player.getUniqueId());
        if(dataCorePlayer == null)
            return "§7Lade...";

        int rawCoins = dataCorePlayer.getCoins();
        String coins = String.format("%,d", rawCoins);
        coins = coins.replace(",", ".");
        return coins;
    }

    private String getPlaytime(Player player) {
        DataCorePlayer dataCorePlayer = BungeeDriver.getInstance().getCorePlayerManager().getCorePlayer(player.getUniqueId());
        if(dataCorePlayer == null)
            return "§7Lade...";

        long time = dataCorePlayer.getPlaytime();
        long seconds = time / 1000;
        int hours = (int) (seconds / 3600);
        int minutes2 = (int) ((seconds % 3600) / 60);
        String minutes = String.valueOf(minutes2);
        if(minutes.length() == 1)
            minutes = "0"+minutes;
        String playtime = String.valueOf(hours)+" §7h §a"+minutes+" §7min";
        return playtime;
    }

    private ItemStack getBootsGadget(Integer setting) {
        ItemStack bootsGadget = null;
        if(setting == 1)
            bootsGadget = new ItemBuilder(Material.LEATHER_BOOTS, Color.RED, true).setDisplayName(ItemNames.BOOTS_HEARTS).build();
        else if(setting == 2)
            bootsGadget = new ItemBuilder(Material.LEATHER_BOOTS, Color.WHITE, true).setDisplayName(ItemNames.BOOTS_SNOW).build();
        else if(setting == 3)
            bootsGadget = new ItemBuilder(Material.LEATHER_BOOTS, Color.AQUA, true).setDisplayName(ItemNames.BOOTS_WATER).build();
        else if(setting == 4)
            bootsGadget = new ItemBuilder(Material.LEATHER_BOOTS, Color.ORANGE, true).setDisplayName(ItemNames.BOOTS_FIRE).build();
        else if(setting == 5)
            bootsGadget = new ItemBuilder(Material.LEATHER_BOOTS, Color.GRAY, true).setDisplayName(ItemNames.BOOTS_SMOKE).build();
        else if(setting == 6)
            bootsGadget = new ItemBuilder(Material.LEATHER_BOOTS, Color.LIME, true).setDisplayName(ItemNames.BOOTS_NOTES).build();
        return bootsGadget;
    }

    private ItemStack getToolGadget(Integer setting) {
        ItemStack toolGadget = null;
        if(setting == 1)
            toolGadget = new ItemBuilder(Material.FISHING_ROD).setDisplayName(ItemNames.INV_GADGET_HOOK).setUnbreakable(true).build();
        else if(setting == 2)
            toolGadget = new ItemBuilder(Material.DIAMOND_BARDING).setDisplayName(ItemNames.INV_GADGET_PAINTBALL).build();
        return toolGadget;
    }

    private ItemStack getHeadGadget(GadgetManager.GadgetType gadget, Integer setting) {
        ItemStack headGadget = null;
        switch (gadget) {
            case SKULL:
                if(setting == 1)
                    headGadget = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/6cef9aa14e884773eac134a4ee8972063f466de678363cf7b1a21a85b7")).setDisplayName(ItemNames.SKULLS_PRESENT).build();
                else if(setting == 2)
                    headGadget = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/52e98165deef4ed621953921c1ef817dc638af71c1934a4287b69d7a31f6b8")).setDisplayName(ItemNames.SKULLS_SMILE).build();
                else if(setting == 3)
                    headGadget = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/f50b28033d31bafcba26eedef061b87eccc4969db1814e455d06222f80913a16")).setDisplayName(ItemNames.SKULLS_CREWMATE).build();
                else if(setting == 4)
                    headGadget = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/2e2cc42015e6678f8fd49ccc01fbf787f1ba2c32bcf559a015332fc5db50")).setDisplayName(ItemNames.SKULLS_GLOBE).build();
                else if(setting == 5)
                    headGadget = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/6acfc786469e636bf4eaac2ed49d9a6c5212af6d9071d961944be8a935f478e")).setDisplayName(ItemNames.SKULLS_CHOCOLATE).build();
                else if(setting == 6)
                    headGadget = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/df00858926cd8cdf3f1cf71e210cde5daf8708320547bd6df5795859c68d9b3f")).setDisplayName(ItemNames.SKULLS_PANDA).build();
                else if(setting == 7)
                    headGadget = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/1c9a8315fdc307b8315918eeae7dd84269104b3d9bd779fc2ba779515b80214d")).setDisplayName(ItemNames.SKULLS_STRAWBERRY).build();
                else if(setting == 8)
                    headGadget = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/63bc0c5ea186c27a64f6c20da19855d2e0910d495f0985bfb01ca3c7d1514ca7")).setDisplayName(ItemNames.SKULLS_EYE).build();
                else if(setting == 9)
                    headGadget = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/1439189e6137532292c858f552122413108cdce729e26ee7b135ea7ca428ec5b")).setDisplayName(ItemNames.SKULLS_ELMO).build();
                else if(setting == 10)
                    headGadget = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/52284e132bfd659bc6ada497c4fa3094cd93231a6b505a12ce7cd5135ba8ff93")).setDisplayName(ItemNames.SKULLS_STORMTROOPER).build();
                else if(setting == 11)
                    headGadget = new ItemBuilder(getSkull("http://textures.minecraft.net/texture/6859e2f028b2956dae70ad0181568fb0ecfd74db2be0f9a5ef34d2867c37f769")).setDisplayName(ItemNames.SKULLS_CUBE).build();
                break;
            case HAT:
                if(setting == 1)
                    headGadget = new ItemBuilder(Material.GLASS).setDisplayName(ItemNames.HATS_GLASS).build();
                else if(setting == 2)
                    headGadget = new ItemBuilder(Material.MOB_SPAWNER).setDisplayName(ItemNames.HATS_MONSTERSPAWNER).build();
                else if(setting == 3)
                    headGadget = new ItemBuilder(Material.COMMAND).setDisplayName(ItemNames.HATS_COMMANDBLOCK).build();
                else if(setting == 4)
                    headGadget = new ItemBuilder(Material.LEAVES, 1).setDisplayName(ItemNames.HATS_LEAVES).build();
                else if(setting == 5)
                    headGadget = new ItemBuilder(Material.SLIME_BLOCK).setDisplayName(ItemNames.HATS_SLIMEBLOCK).build();
                else if(setting == 6)
                    headGadget = new ItemBuilder(Material.REDSTONE_LAMP_OFF).setDisplayName(ItemNames.HATS_REDSTONELAMP).build();
                break;
            default:
                break;
        }
        return headGadget;
    }

}

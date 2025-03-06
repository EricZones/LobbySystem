// Created by Eric B. 17.05.2021 12:08
package de.ericzones.lobbysystem.manager;

import de.ericzones.lobbysystem.LobbySystem;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class MessageManager {

    private LobbySystem instance;
    private ExtraManager extraManager;

    public MessageManager(LobbySystem instance) {
        this.instance = instance;
        extraManager = instance.getExtraManager();
        startActionbar();
    }

    public void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subTitle) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);
        connection.sendPacket(packet);
        if(subTitle != null) {
            IChatBaseComponent subTitleMessage = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subTitle + "\"}");
            PacketPlayOutTitle PacketPlayOutSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subTitleMessage);
            connection.sendPacket(PacketPlayOutSubTitle);
        }
        if(title != null) {
            IChatBaseComponent titleMessage = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
            PacketPlayOutTitle PacketPlayOutBigTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleMessage);
            connection.sendPacket(PacketPlayOutBigTitle);
        }
    }

    private void startActionbar() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
            @Override
            public void run() {
                for(Player current : Bukkit.getOnlinePlayers()) {
                    String message = null;
                    String extra = null;
                    String deluxepearl = null;
                    String shield = null;
                    if(instance.getBuildPlayers().contains(current.getUniqueId())) {
                        message = "§7Baumodus §8● §aAktiviert";
                    } else {
                        if(extraManager.hasDoublejump(current.getUniqueId()))
                            extra = "§7Extra §8● §bDoppelsprung";
                        if(extraManager.hasFlymode(current.getUniqueId()))
                            extra = "§7Extra §8● §bFlugmodus";
                        if(extra == null)
                            extra = "§7Extra §8● §cKeins";
                        if(extraManager.hasDeluxepearl(current.getUniqueId()))
                            deluxepearl = "§7Pearl §8● §bDeluxe §8§l┃ ";
                        else
                            deluxepearl = "§7Pearl §8● §bStandard §8§l┃ ";
                        if(extraManager.hasShield(current.getUniqueId()))
                            shield = "§7Schild §8● §aAktiviert §8§l┃ ";
                        if(shield != null)
                            message = shield+deluxepearl+extra;
                        else
                            message = deluxepearl+extra;
                    }
                    IChatBaseComponent text = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
                    PacketPlayOutChat packet = new PacketPlayOutChat(text, (byte)2);
                    ((CraftPlayer)current).getHandle().playerConnection.sendPacket(packet);
                }
            }
        }, 0, 20);
    }

}

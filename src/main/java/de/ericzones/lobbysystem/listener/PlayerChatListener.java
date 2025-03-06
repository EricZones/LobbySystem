// Created by Eric B. 29.12.2020 17:12
package de.ericzones.lobbysystem.listener;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.manager.ChatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    private LobbySystem instance;
    private ChatManager chatManager;

    public PlayerChatListener(LobbySystem instance) {
        this.instance = instance;
        chatManager = instance.getChatManager();
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage().trim();
        if(player.hasPermission("proxy.chat.color")) {
            message = message.replace("&0", "§0");
            message = message.replace("&1", "§1");
            message = message.replace("&2", "§2");
            message = message.replace("&3", "§3");
            message = message.replace("&4", "§4");
            message = message.replace("&5", "§5");
            message = message.replace("&6", "§6");
            message = message.replace("&7", "§7");
            message = message.replace("&8", "§8");
            message = message.replace("&9", "§9");
            message = message.replace("&a", "§a");
            message = message.replace("&b", "§b");
            message = message.replace("&c", "§c");
            message = message.replace("&d", "§d");
            message = message.replace("&e", "§e");
            message = message.replace("&f", "§f");
            message = message.replace("&r", "§r");
        }
        if(player.hasPermission("proxy.chat.admin")) {
            message = message.replace("&k", "§k");
            message = message.replace("&m", "§m");
            message = message.replace("&n", "§n");
            message = message.replace("&l", "§l");
            message = message.replace("&o", "§o");
        }
        IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(player.getUniqueId());
        String rank = "§7Spieler";
        if(permissionUser != null)
            rank = CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(permissionUser).getPrefix();
        String chatFormat = chatManager.getChatFormat(player.getUniqueId()).getSymbol();
        String chatColor = chatManager.getChatColor(player.getUniqueId());

        if(chatFormat == null) {
            e.setFormat(rank + " §8● §7" + player.getName() + " §8» " + chatColor + message);
            return;
        }
        e.setFormat(rank + " §8● §7" + player.getName() + " §8» " + chatColor + chatFormat + message);
    }

}

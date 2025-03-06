// Created by Eric B. 17.05.2021 12:41
package de.ericzones.lobbysystem.friend;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class FriendManager {

    private final LobbySystem instance;

    public FriendManager(LobbySystem instance) {
        this.instance = instance;
    }

//    public void acceptFriendRequest(Player player, UUID spieler, UUID freund) {
//        if (getFriendList(spieler).size() >= 36) {
//            if (!player.hasPermission("proxy.friends.unlimited")) {
//                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                player.sendMessage(Utils.prefix_freunde + "§7Du benötigst §6Premium §7um mehr Freunde hinzuzufügen");
//                return;
//            }
//        }
//        if(areFriends(spieler, freund)) {
//            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//            player.sendMessage(Utils.prefix_freunde + "§7Du bist bereits mit diesem §cSpieler §7befreundet");
//            return;
//        }
//        if (hasRequestedPlayer(freund, spieler)) {
//            acceptFriend(spieler, freund);
//            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
//            return;
//        } else {
//            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//            player.sendMessage(Utils.prefix_freunde+"§7Dieser §cSpieler §7hat dir keine Anfrage gesendet");
//            return;
//        }
//    }
//
//    public void denyFriendRequest(Player player, UUID spieler, UUID freund) {
//        if(areFriends(spieler, freund)) {
//            removeFriend(spieler, freund);
//            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
//            return;
//        }
//        if (hasRequestedPlayer(freund, spieler)) {
//            denyFriend(spieler, freund);
//            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
//            return;
//        } else {
//            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//            player.sendMessage(Utils.prefix_freunde + "§7Dieser §cSpieler §7hat dir keine Anfrage gesendet");
//            return;
//        }
//    }
//
//    public void removeExistingFriend(Player player, UUID spieler, UUID freund) {
//        if(hasRequestedPlayer(freund, spieler)) {
//            denyFriend(spieler, freund);
//            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
//            return;
//        }
//        if(areFriends(spieler, freund)) {
//            removeFriend(spieler, freund);
//            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
//            return;
//        } else {
//            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//            player.sendMessage(Utils.prefix_freunde+"§7Du bist nicht mit diesem §cSpieler §7befreundet");
//            return;
//        }
//    }
//
//    private boolean hasRequestedPlayer(UUID ersteller, UUID ziel) {
//        if(mysql.isConnected()) {
//            try {
//                ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Anfragenliste WHERE Ersteller='"+ersteller+"' AND Ziel='"+ziel+"'");
//                if(resultSet.next())
//                    return true;
//            } catch(SQLException e) {
//                e.printStackTrace();
//            }
//        } else {
//            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
//        }
//        return false;
//    }
//
//    public boolean areFriends(UUID spieler, UUID freund) {
//        if(mysql.isConnected()) {
//            try {
//                ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Freundesliste WHERE Spieler='"+spieler+"' AND Freund='"+freund+"'");
//                if(resultSet.next())
//                    return true;
//            } catch(SQLException e) {
//                e.printStackTrace();
//            }
//        } else {
//            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
//        }
//        return false;
//    }
//
//    private boolean isOnline(UUID uuid, String name) {
//        if(mysql.isConnected()) {
//            try {
//                if (uuid == null) {
//                    ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Onlineliste WHERE Name='"+name+"'");
//                    if(resultSet.next())
//                        return true;
//                } else {
//                    ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Onlineliste WHERE UUID='"+uuid+"'");
//                    if(resultSet.next())
//                        return true;
//                }
//            } catch(SQLException e) {
//                e.printStackTrace();
//            }
//        } else {
//            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
//        }
//        return false;
//    }
//
//
//
//    private String getName(UUID uuid) {
//        if(mysql.isConnected()) {
//            try {
//                ResultSet resultSet = connection.createStatement().executeQuery("SELECT Name FROM Spielersettings WHERE UUID='"+uuid+"'");
//                if(resultSet.next())
//                    return resultSet.getString("Name");
//            } catch(SQLException e) {
//                e.printStackTrace();
//            }
//        } else {
//            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
//        }
//        return null;
//    }
//
//    public UUID getUUID(String name) {
//        if(mysql.isConnected()) {
//            try {
//                ResultSet resultSet = connection.createStatement().executeQuery("SELECT UUID FROM Spielersettings WHERE Name='"+name+"'");
//                if(resultSet.next())
//                    return UUID.fromString(resultSet.getString("UUID"));
//            } catch(SQLException e) {
//                e.printStackTrace();
//            }
//        } else {
//            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
//        }
//        return null;
//    }
//
//    public List<String> getRequests(UUID uuid, int from, int to) {
//        List<String> requestsList = getRequests(uuid);
//        List<String> list = new ArrayList<>();
//        ArrayList<String> temp = new ArrayList<>();
//
//        for(int i = from; i < to; i++) {
//            if(requestsList.size() > i) {
//
//                if(isOnline(getUUID(requestsList.get(i)), requestsList.get(i))) {
//                    list.add(requestsList.get(i));
//                } else {
//                    temp.add(requestsList.get(i));
//                }
//            }
//        }
//        list.addAll(temp);
//        return list;
//    }
//
//    public List<String> getFriends(UUID uuid, int from, int to) {
//        List<String> friendList = getFriendList(uuid);
//        List<String> list = new ArrayList<>();
//        ArrayList<String> temp = new ArrayList<>();
//
//        for(int i = from; i < to; i++) {
//            if(friendList.size() > i) {
//
//                if(isOnline(getUUID(friendList.get(i)), friendList.get(i))) {
//                    list.add(friendList.get(i));
//                } else {
//                    temp.add(friendList.get(i));
//                }
//            }
//        }
//        list.addAll(temp);
//        return list;
//    }
//
//    public String getLogoutTime(UUID uuid) {
//        long logoutTime = 0;
//        if(mysql.isConnected()) {
//            try {
//                ResultSet resultSet = connection.createStatement().executeQuery("SELECT Logout FROM Offlineliste WHERE UUID='"+uuid+"'");
//                if(resultSet.next())
//                    logoutTime = resultSet.getLong("Logout");
//            } catch(SQLException e) {
//                e.printStackTrace();
//            }
//        } else {
//            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
//        }
//        long millis = System.currentTimeMillis() - logoutTime;
//        long seconds = 0;
//        long minutes = 0;
//        long hours = 0;
//        long days = 0;
//        long months = 0;
//        long years = 0;
//        while(millis > 1000) {
//            millis-=1000;
//            seconds++;
//        }
//        while(seconds > 60) {
//            seconds-=60;
//            minutes++;
//        }
//        while(minutes > 60) {
//            minutes-=60;
//            hours++;
//        }
//        while(hours > 24) {
//            hours-=24;
//            days++;
//        }
//        while(days > 30) {
//            days-=30;
//            months++;
//        }
//        while(months > 12) {
//            months-=12;
//            years++;
//        }
//
//        if(years != 0) {
//            return years + " Jahr(e)";
//
//        } else if(months != 0) {
//            return months + " Monat(e)";
//
//        } else if(days != 0) {
//            return days + " Tag(e)";
//
//        } else if(hours != 0) {
//            return hours + " Stunde(n)";
//
//        } else if(minutes != 0) {
//            return minutes + " Minute(n)";
//
//        } else {
//            return seconds + " Sekunde(n)";
//        }
//    }
//
//    public void jumptoFriend(Player player, UUID spieler, UUID freund) {
//        if(!areFriends(spieler, freund)) {
//            instance.getFriendInventory().openFriendMenu(player, instance.getFriendInventory().getPageIndexFriends(spieler));
//            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//            player.sendMessage(Utils.prefix_freunde + "§7Du bist nicht mit diesem §cSpieler §7befreundet");
//            return;
//        }
//        ICloudPlayer cloudPlayerFriend = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).getOnlinePlayer(freund);
//        ICloudPlayer cloudPlayer = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).getOnlinePlayer(spieler);
//        if(cloudPlayerFriend == null) {
//            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//            player.sendMessage(Utils.prefix_freunde+Utils.error_notonline);
//            return;
//        }
//        HashMap<FriendSetting, FriendValue> friendSettings = getSettings(freund);
//        if(friendSettings.get(FriendSetting.NACHSPRINGEN) == FriendValue.AUS) {
//            if(!player.hasPermission("proxy.friends.admin")) {
//                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//                player.sendMessage(Utils.prefix_freunde+"§7Dieser §cSpieler §7hat Nachspringen deaktiviert");
//                return;
//            }
//        }
//        String targetServer = cloudPlayerFriend.getConnectedService().getServerName();
//        String ownServer = cloudPlayer.getConnectedService().getServerName();
//        if(targetServer.equalsIgnoreCase(ownServer)) {
//            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.5F);
//            player.sendMessage(Utils.error_alreadyconnected);
//            return;
//        }
//        IPermissionUser permissionUserFriend = CloudNetDriver.getInstance().getPermissionManagement().getUser(freund);
//        String friendColor = CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(permissionUserFriend).getColor();
//        String friendName = friendColor+cloudPlayerFriend.getName();
//
//        player.sendMessage(Utils.prefix_freunde+"§7Springe zu "+friendName+"§7...");
//        CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).getPlayerExecutor(cloudPlayer).connect(cloudPlayerFriend.getConnectedService().getServerName());
//    }

}

// Created by Eric B. 17.05.2021 12:11
package de.ericzones.lobbysystem.manager;

import de.ericzones.bungeedriver.BungeeDriver;
import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.MySQL;
import de.ericzones.lobbysystem.extra.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class VisibilityManager {

    private LobbySystem instance;
    private MySQL mysql;
    private Connection connection;

    private HashMap<UUID, VisibilitySetting> settings;

    public VisibilityManager(LobbySystem instance) {
        this.instance = instance;
        mysql = instance.getMysql();
        mysql.createTable("Visibilitymanager", "UUID varchar(36) NOT NULL", "Setting varchar(10) NOT NULL", "PRIMARY KEY(UUID)");
        connection = mysql.getConnection();
        settings = new HashMap<>();
    }

    private void updateVisibility() {
        for(Player current : Bukkit.getOnlinePlayers()) {
            for(Player all : Bukkit.getOnlinePlayers())
                current.showPlayer(all);
            VisibilitySetting setting = settings.get(current.getUniqueId());
            if(setting == VisibilitySetting.TEAM) {
                for(Player all : Bukkit.getOnlinePlayers()) {
                    if(!all.hasPermission("proxy.group.team"))
                        current.hidePlayer(all);
                }
            } else if(setting == VisibilitySetting.FRIENDS) {
                for(Player all : Bukkit.getOnlinePlayers()) {
                    if(!BungeeDriver.getInstance().getFriendManager().getFriendPlayer(current.getUniqueId()).isFriend(all.getUniqueId()))
                        current.hidePlayer(all);
                }
            } else if(setting == VisibilitySetting.NONE) {
                for(Player all : Bukkit.getOnlinePlayers())
                    current.hidePlayer(all);
            }
        }
    }

    public void updatePlayerSetting(UUID uuid, VisibilitySetting setting) {
        if(mysql.isConnected()) {
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT Setting FROM Visibilitymanager WHERE UUID='"+uuid+"'");
                if(resultSet.next())
                    connection.createStatement().executeUpdate("UPDATE Visibilitymanager SET Setting='"+setting.toString()+"' WHERE UUID='"+uuid+"'");
                else
                    connection.createStatement().executeUpdate("INSERT INTO Visibilitymanager (UUID, Setting) VALUES ('"+uuid+"', '"+setting.toString()+"')");
                settings.put(uuid, setting);
                updateVisibility();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
    }

    public void registerPlayerSetting(UUID uuid) {
        if(mysql.isConnected()) {
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT Setting FROM Visibilitymanager WHERE UUID='"+uuid+"'");
                if(resultSet.next())
                    settings.put(uuid, VisibilitySetting.valueOf(resultSet.getString("Setting")));
                else {
                    connection.createStatement().executeUpdate("INSERT INTO Visibilitymanager (UUID, Setting) VALUES ('"+uuid+"', '"+VisibilitySetting.ALL.toString()+"')");
                    settings.put(uuid, VisibilitySetting.ALL);
                }
                updateVisibility();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
    }

    public VisibilitySetting getVisibilitySetting(UUID uuid) {
        return settings.get(uuid);
    }

    public boolean canSeePlayer(UUID uuid, UUID targetUuid) {
        if(getVisibilitySetting(uuid) == VisibilitySetting.ALL)
            return true;
        else if(getVisibilitySetting(uuid) == VisibilitySetting.TEAM) {
            if(Bukkit.getPlayer(targetUuid).hasPermission("proxy.group.team"))
                return true;
        } else if(getVisibilitySetting(uuid) == VisibilitySetting.FRIENDS) {
            if(BungeeDriver.getInstance().getFriendManager().getFriendPlayer(uuid).isFriend(targetUuid))
                return true;
        } else if(getVisibilitySetting(uuid) == VisibilitySetting.NONE)
            return false;
        return false;
    }

}

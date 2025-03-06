// Created by Eric B. 17.05.2021 12:00
package de.ericzones.lobbysystem.manager;

import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.MySQL;
import de.ericzones.lobbysystem.extra.Utils;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class ChatManager {

    private LobbySystem instance;
    private MySQL mysql;
    private Connection connection;

    private HashMap<UUID, String> chatColor;
    private HashMap<UUID, ChatFormat> chatFormat;

    public ChatManager(LobbySystem instance) {
        this.instance = instance;
        mysql = instance.getMysql();
        mysql.createTable("Chatmanager", "UUID varchar(36) NOT NULL", "Color varchar(5) NOT NULL", "Format varchar(12) NOT NULL", "PRIMARY KEY(UUID)");
        connection = mysql.getConnection();
        chatColor = new HashMap<>();
        chatFormat = new HashMap<>();
    }

    private void checkPlayerChatSettings(UUID uuid) {
        if(!getChatColor(uuid).equals("§7") && !Bukkit.getPlayer(uuid).hasPermission("proxy.chat.color"))
            updatePlayerChatColor(uuid, "7");
        if(getChatFormat(uuid) != ChatFormat.NONE && !Bukkit.getPlayer(uuid).hasPermission("proxy.chat.admin"))
            updatePlayerChatFormat(uuid, ChatFormat.NONE);
    }

    public void registerPlayerChatSettings(UUID uuid) {
        if(mysql.isConnected()) {
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT Color FROM Chatmanager WHERE UUID='"+uuid+"'");
                if(resultSet.next())
                    chatColor.put(uuid, "§"+resultSet.getString("Color"));
                else {
                    connection.createStatement().executeUpdate("INSERT INTO Chatmanager (UUID, Color, Format) VALUES ('"+uuid+"', '7', '"+ChatFormat.NONE.toString()+"')");
                    chatColor.put(uuid, "§7");
                }
                resultSet = connection.createStatement().executeQuery("SELECT Format FROM Chatmanager WHERE UUID='"+uuid+"'");
                if(resultSet.next())
                    chatFormat.put(uuid, ChatFormat.valueOf(resultSet.getString("Format")));
                checkPlayerChatSettings(uuid);
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
    }

    public void updatePlayerChatColor(UUID uuid, String color) {
        if(mysql.isConnected()) {
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT Color FROM Chatmanager WHERE UUID='"+uuid+"'");
                if(resultSet.next())
                    connection.createStatement().executeUpdate("UPDATE Chatmanager SET Color='"+color+"' WHERE UUID='"+uuid+"'");
                else
                    connection.createStatement().executeUpdate("INSERT INTO Chatmanager (UUID, Color, Format) VALUES ('"+uuid+"', '"+color+"', '"+ChatFormat.NONE.toString()+"')");
                chatColor.put(uuid, "§"+color);
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
    }

    public void updatePlayerChatFormat(UUID uuid, ChatFormat chatFormat) {
        if(mysql.isConnected()) {
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT Format FROM Chatmanager WHERE UUID='"+uuid+"'");
                if(resultSet.next())
                    connection.createStatement().executeUpdate("UPDATE Chatmanager SET Format='"+chatFormat.toString()+"' WHERE UUID='"+uuid+"'");
                else
                    connection.createStatement().executeUpdate("INSERT INTO Chatmanager (UUID, Color, Format) VALUES ('"+uuid+"', '7', '"+chatFormat.toString()+"')");
                this.chatFormat.put(uuid, chatFormat);
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
    }

    public ChatFormat getChatFormat(UUID uuid) {
        return chatFormat.get(uuid);
    }

    public String getChatColor(UUID uuid) {
        return chatColor.get(uuid);
    }

}

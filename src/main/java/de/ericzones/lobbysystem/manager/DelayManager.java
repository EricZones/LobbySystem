// Created by Eric B. 17.05.2021 12:01
package de.ericzones.lobbysystem.manager;

import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.MySQL;
import de.ericzones.lobbysystem.extra.Utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DelayManager {

    private LobbySystem instance;
    private MySQL mysql;
    private Connection connection;

    public DelayManager(LobbySystem instance) {
        this.instance = instance;
        mysql = instance.getMysql();
        mysql.createTable("Delaymanager", "UUID varchar(36) NOT NULL", "Type varchar(25) NOT NULL", "Delay varchar(50) NOT NULL", "PRIMARY KEY(UUID)");
        connection = mysql.getConnection();
    }

    public long checkDelay(UUID uuid, DelayType delayType) {
        if(mysql.isConnected()) {
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT Delay FROM Delaymanager WHERE UUID='"+uuid+"' AND Type='"+delayType.toString()+"'");
                if(resultSet.next())
                    return Long.parseLong(resultSet.getString("Delay"));
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
        return 0;
    }

    public void setDelay(UUID uuid, DelayType delayType) {
        if(mysql.isConnected()) {
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT Delay FROM Delaymanager WHERE UUID='"+uuid+"' AND Type='"+delayType.toString()+"'");
                if(resultSet.next())
                    connection.createStatement().executeUpdate("UPDATE Delaymanager SET Delay='"+(System.currentTimeMillis() + 10 * 1000)+"' WHERE UUID='"+uuid+"' AND Type='"+delayType.toString()+"'");
                else
                    connection.createStatement().executeUpdate("INSERT INTO Delaymanager (UUID, Type, Delay) VALUES ('"+uuid+"', '"+delayType.toString()+"', '"+(System.currentTimeMillis() + 10 * 1000)+"')");
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
    }

}

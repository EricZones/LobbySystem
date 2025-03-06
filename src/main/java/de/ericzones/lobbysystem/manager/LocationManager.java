// Created by Eric B. 17.05.2021 12:07
package de.ericzones.lobbysystem.manager;

import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.MySQL;
import de.ericzones.lobbysystem.extra.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LocationManager {

    private LobbySystem instance;
    private MySQL mysql;
    private Connection connection;

    private ArrayList<String> locations;

    public LocationManager(LobbySystem instance) {
        this.instance = instance;
        mysql = instance.getMysql();
        mysql.createTable("Locationmanager", "Name varchar(20) NOT NULL", "World varchar(16) NOT NULL", "X varchar(50) NOT NULL", "Y varchar(50) NOT NULL", "Z varchar(50) NOT NULL", "Yaw varchar(50) NOT NULL", "Pitch varchar(50) NOT NULL", "PRIMARY KEY(Name)");
        mysql.createTable("Locationlist", "Name varchar(20) NOT NULL", "PRIMARY KEY(Name)");
        connection = mysql.getConnection();
        locations = new ArrayList<>();
        loadLocations();
    }

    public Location getSpawn() {
        if(mysql.isConnected()) {
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Locationmanager WHERE Name='Spawn'");
                if(resultSet.next()) {
                    World world = Bukkit.getWorld(resultSet.getString("World"));
                    double x = resultSet.getDouble("X");
                    double y = resultSet.getDouble("Y");
                    double z = resultSet.getDouble("Z");
                    float yaw = resultSet.getFloat("Yaw");
                    float pitch = resultSet.getFloat("Pitch");
                    return new Location(world, x, y, z, yaw, pitch);
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
        return null;
    }

    public void setSpawn(Location location) {
        if(mysql.isConnected()) {
            try {
                String world = location.getWorld().getName();
                double x = location.getX();
                double y = location.getY();
                double z = location.getZ();
                float yaw = location.getYaw();
                float pitch = location.getPitch();
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Locationmanager WHERE Name='Spawn'");
                if(resultSet.next()) {
                    connection.createStatement().executeUpdate("UPDATE Locationmanager SET World='"+world+"', X="+x+", Y="+y+", Z="+z+", Yaw="+yaw+", Pitch="+pitch+" WHERE Name='Spawn'");
                } else {
                    connection.createStatement().executeUpdate("INSERT INTO Locationmanager (Name, World, X, Y, Z, Yaw, Pitch) VALUES ('Spawn', '"+world+"', "+x+", "+y+", "+z+", "+yaw+", "+pitch+")");
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
    }

    public Location getLocation(String name) {
        if(mysql.isConnected()) {
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Locationmanager WHERE Name='"+name+"'");
                if(resultSet.next()) {
                    World world = Bukkit.getWorld(resultSet.getString("World"));
                    double x = resultSet.getDouble("X");
                    double y = resultSet.getDouble("Y");
                    double z = resultSet.getDouble("Z");
                    float yaw = resultSet.getFloat("Yaw");
                    float pitch = resultSet.getFloat("Pitch");
                    return new Location(world, x, y, z, yaw, pitch);
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
        return null;
    }

    public void setLocation(String name, Location location) {
        if(mysql.isConnected()) {
            try {
                String world = location.getWorld().getName();
                double x = location.getX();
                double y = location.getY();
                double z = location.getZ();
                float yaw = location.getYaw();
                float pitch = location.getPitch();
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Locationmanager WHERE Name='"+name+"'");
                if(resultSet.next()) {
                    connection.createStatement().executeUpdate("UPDATE Locationmanager SET World='"+world+"', X="+x+", Y="+y+", Z="+z+", Yaw="+yaw+", Pitch="+pitch+" WHERE Name='"+name+"'");
                } else {
                    connection.createStatement().executeUpdate("INSERT INTO Locationmanager (Name, World, X, Y, Z, Yaw, Pitch) VALUES ('"+name+"', '"+world+"', "+x+", "+y+", "+z+", "+yaw+", "+pitch+")");
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
    }

    private void loadLocations() {
        if(mysql.isConnected()) {
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Locationlist");
                while (resultSet.next())
                    locations.add(resultSet.getString("Name"));
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
    }

    public boolean addLocation(String name) {
        if(mysql.isConnected()) {
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Locationlist WHERE Name='"+name+"'");
                if(!resultSet.next()) {
                    connection.createStatement().executeUpdate("INSERT INTO Locationlist (Name) VALUES ('" + name + "')");
                    locations.add(name);
                    return true;
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
        return false;
    }

    public boolean removeLocation(String name) {
        if(mysql.isConnected()) {
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Locationlist WHERE Name='"+name+"'");
                if(resultSet.next()) {
                    connection.createStatement().executeUpdate("DELETE FROM Locationlist WHERE Name='"+name+"'");
                    locations.remove(name);
                    return true;
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
        return false;
    }

    public ArrayList<String> getLocations() {
        return locations;
    }

}

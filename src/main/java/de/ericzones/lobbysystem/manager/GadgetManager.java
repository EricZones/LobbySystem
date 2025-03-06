// Created by Eric B. 17.05.2021 12:04
package de.ericzones.lobbysystem.manager;

import de.ericzones.bungeedriver.BungeeDriver;
import de.ericzones.bungeedriver.collectives.plugindata.object.DataCorePlayer;
import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.MySQL;
import de.ericzones.lobbysystem.extra.Utils;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class GadgetManager {

    private LobbySystem instance;
    private MySQL mysql;
    private Connection connection;

    private HashMap<UUID, Integer> skullGadget;
    private HashMap<UUID, Integer> hatGadget;
    private HashMap<UUID, Integer> bootsGadget;
    private HashMap<UUID, Integer> rainGadget;
    private HashMap<UUID, Integer> toolGadget;

    public GadgetManager(LobbySystem instance) {
        this.instance = instance;
        mysql = instance.getMysql();
        connection = mysql.getConnection();
        mysql.createTable("Gadgetmanager", "UUID varchar(36) NOT NULL", "Gadget varchar(20) NOT NULL", "Setting varchar(10) NOT NULL");
        mysql.createTable("Gadgetshop", "UUID varchar(36) NOT NULL", "Gadget varchar(20) NOT NULL", "Setting varchar(10) NOT NULL");
        skullGadget = new HashMap<>();
        hatGadget = new HashMap<>();
        bootsGadget = new HashMap<>();
        rainGadget = new HashMap<>();
        toolGadget = new HashMap<>();
    }

    public void loadGadgets(UUID uuid) {
        if (mysql.isConnected()) {
            try {
                ResultSet skullGadget = connection.createStatement().executeQuery("SELECT * FROM Gadgetmanager WHERE UUID='" + uuid + "' AND Gadget='" + GadgetType.SKULL.toString() + "'");
                if(skullGadget.next())
                    this.skullGadget.put(uuid, skullGadget.getInt("Setting"));
                ResultSet hatGadget = connection.createStatement().executeQuery("SELECT * FROM Gadgetmanager WHERE UUID='" + uuid + "' AND Gadget='" + GadgetType.HAT.toString() + "'");
                if(hatGadget.next())
                    this.hatGadget.put(uuid, hatGadget.getInt("Setting"));
                ResultSet bootsGadget = connection.createStatement().executeQuery("SELECT * FROM Gadgetmanager WHERE UUID='" + uuid + "' AND Gadget='" + GadgetType.BOOTS.toString() + "'");
                if(bootsGadget.next())
                    this.bootsGadget.put(uuid, bootsGadget.getInt("Setting"));
                ResultSet rainGadget = connection.createStatement().executeQuery("SELECT * FROM Gadgetmanager WHERE UUID='" + uuid + "' AND Gadget='" + GadgetType.RAIN.toString() + "'");
                if(rainGadget.next())
                    this.rainGadget.put(uuid, rainGadget.getInt("Setting"));
                ResultSet toolGadget = connection.createStatement().executeQuery("SELECT * FROM Gadgetmanager WHERE UUID='" + uuid + "' AND Gadget='" + GadgetType.TOOL.toString() + "'");
                if(toolGadget.next())
                    this.toolGadget.put(uuid, toolGadget.getInt("Setting"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
    }

    public boolean buyNewGadget(UUID uuid, GadgetType gadget, Integer setting) {
        if (mysql.isConnected()) {
            try {
                if(hasBoughtGadget(uuid, gadget, setting))
                    return false;

                DataCorePlayer dataCorePlayer = BungeeDriver.getInstance().getCorePlayerManager().getCorePlayer(uuid);
                long currentCoins = dataCorePlayer.getCoins();
                long gadgetPrice = getGadgetPrice(gadget, setting);
                if(gadgetPrice > currentCoins)
                    return false;
                BungeeDriver.getInstance().getCorePlayerManager().removeCoins(dataCorePlayer, (int) gadgetPrice);

                connection.createStatement().executeUpdate("INSERT INTO Gadgetshop (UUID, Gadget, Setting) VALUES ('"+uuid+"', '"+gadget.toString()+"', '"+setting.toString()+"')");
                updateGadgetSetting(uuid, gadget, setting);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
        return false;
    }

    public boolean hasBoughtGadget(UUID uuid, GadgetType gadget, Integer setting) {
        if (mysql.isConnected()) {
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Gadgetshop WHERE UUID='" + uuid + "' AND Gadget='" + gadget.toString() + "' AND Setting='" + setting.toString() + "'");
                if(resultSet.next())
                    return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
        return false;
    }

    public void updateGadgetSetting(UUID uuid, GadgetType gadget, Integer setting) {
        if (mysql.isConnected()) {
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Gadgetmanager WHERE UUID='" + uuid + "' AND Gadget='" + gadget.toString() + "'");
                if(resultSet.next())
                    connection.createStatement().executeUpdate("UPDATE Gadgetmanager SET Setting='" + setting.toString() + "' WHERE UUID='" + uuid + "' AND Gadget='" + gadget.toString() + "'");
                else
                    connection.createStatement().executeUpdate("INSERT INTO Gadgetmanager (UUID, Gadget, Setting) VALUES ('" + uuid + "', '" + gadget.toString() + "', '" + setting.toString() + "')");
                switch (gadget) {
                    case SKULL:
                        skullGadget.put(uuid, setting);
                        removeCurrentGadget(uuid, GadgetType.HAT);
                        break;
                    case HAT:
                        hatGadget.put(uuid, setting);
                        removeCurrentGadget(uuid, GadgetType.SKULL);
                        break;
                    case BOOTS:
                        bootsGadget.put(uuid, setting);
                        break;
                    case RAIN:
                        rainGadget.put(uuid, setting);
                        break;
                    case TOOL:
                        toolGadget.put(uuid, setting);
                        break;
                    default:
                        break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
    }

    public void removeAllGadgets(UUID uuid) {
        skullGadget.remove(uuid);
        hatGadget.remove(uuid);
        toolGadget.remove(uuid);
        bootsGadget.remove(uuid);
        rainGadget.remove(uuid);
        if (mysql.isConnected()) {
            try {
                connection.createStatement().executeUpdate("DELETE FROM Gadgetmanager WHERE UUID='"+uuid+"'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
    }

    public void removeCurrentGadget(UUID uuid, GadgetType gadget) {
        switch (gadget) {
            case SKULL:
                skullGadget.remove(uuid);
                break;
            case HAT:
                hatGadget.remove(uuid);
                break;
            case TOOL:
                toolGadget.remove(uuid);
                break;
            case BOOTS:
                bootsGadget.remove(uuid);
                break;
            case RAIN:
                rainGadget.remove(uuid);
                break;
            default:
                break;
        }
        if (mysql.isConnected()) {
            try {
                connection.createStatement().executeUpdate("DELETE FROM Gadgetmanager WHERE UUID='"+uuid+"' AND Gadget='"+gadget.toString()+"'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
    }

    private boolean hasGadget(UUID uuid, GadgetType gadget) {
        switch (gadget) {
            case SKULL:
                if(skullGadget.containsKey(uuid))
                    return true;
                break;
            case HAT:
                if(hatGadget.containsKey(uuid))
                    return true;
                break;
            case BOOTS:
                if(bootsGadget.containsKey(uuid))
                    return true;
                break;
            case RAIN:
                if(rainGadget.containsKey(uuid))
                    return true;
                break;
            case TOOL:
                if(toolGadget.containsKey(uuid))
                    return true;
                break;
            default:
                break;
        }
        return false;
    }

    public Integer getSkullGadget(UUID uuid) {
        if(hasGadget(uuid, GadgetType.SKULL))
            return skullGadget.get(uuid);
        else
            return 0;
    }

    public Integer getHatGadget(UUID uuid) {
        if(hasGadget(uuid, GadgetType.HAT))
            return hatGadget.get(uuid);
        else
            return 0;
    }

    public Integer getBootsGadget(UUID uuid) {
        if(hasGadget(uuid, GadgetType.BOOTS))
            return bootsGadget.get(uuid);
        else
            return 0;
    }

    public Integer getRainGadget(UUID uuid) {
        if(hasGadget(uuid, GadgetType.RAIN))
            return rainGadget.get(uuid);
        else
            return 0;
    }

    public Integer getToolGadget(UUID uuid) {
        if(hasGadget(uuid, GadgetType.TOOL))
            return toolGadget.get(uuid);
        else
            return 0;
    }

    public HashMap<UUID, Integer> getBootsGadgetMap() {
        return bootsGadget;
    }

    public HashMap<UUID, Integer> getRainGadgetMap() {
        return rainGadget;
    }

    private long getGadgetPrice(GadgetType gadgetType, Integer gadgetSetting) {
        long gadgetPrice = 8000000;

        switch (gadgetType) {
            case SKULL:
                gadgetPrice = 5000;
                break;
            case HAT:
                gadgetPrice = 2500;
                break;
            case TOOL:
                if(gadgetSetting == 1)
                    gadgetPrice = 25000;
                else if(gadgetSetting == 2)
                    gadgetPrice = 35000;
                break;
            case BOOTS:
                gadgetPrice = 15000;
                break;
            case RAIN:
                gadgetPrice = 20000;
                break;
            default:
                break;
        }
        return gadgetPrice;
    }

    public enum GadgetType {

        SKULL,
        HAT,
        BOOTS,
        RAIN,
        TOOL;

        private GadgetType(){}
    }

}

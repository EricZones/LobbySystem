// Created by Eric B. 17.05.2021 12:02
package de.ericzones.lobbysystem.manager;

import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.extra.MySQL;
import de.ericzones.lobbysystem.extra.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ExtraManager {

    private LobbySystem instance;
    private MySQL mysql;
    private Connection connection;

    private HashMap<UUID, ExtraSetting> deluxepearlExtra;
    private HashMap<UUID, ExtraSetting> doublejumpExtra;
    private HashMap<UUID, ExtraSetting> flyExtra;
    private HashMap<UUID, ExtraSetting> shieldExtra;

    private ArrayList<UUID> activePearls;

    private int shieldScheduler;

    public ExtraManager(LobbySystem instance) {
        this.instance = instance;
        mysql = instance.getMysql();
        connection = mysql.getConnection();
        mysql.createTable("Extramanager", "UUID varchar(36) NOT NULL", "Extra varchar(20) NOT NULL", "Setting varchar(10) NOT NULL");
        deluxepearlExtra = new HashMap<>();
        doublejumpExtra = new HashMap<>();
        flyExtra = new HashMap<>();
        shieldExtra = new HashMap<>();
        activePearls = new ArrayList<>();
    }

    public void loadExtras(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (mysql.isConnected()) {
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Extramanager WHERE UUID='" + uuid + "'");
                if (resultSet.next()) {
                    ResultSet deluxepearl = connection.createStatement().executeQuery("SELECT Setting FROM Extramanager WHERE UUID='" + uuid + "' AND Extra='" + ExtraType.DELUXEPEARL.toString() + "'");
                    if (deluxepearl.next()) {
                        if(ExtraSetting.valueOf(deluxepearl.getString("Setting")) == ExtraSetting.ON && player.hasPermission("spigot.lobby.deluxepearl"))
                            deluxepearlExtra.put(uuid, ExtraSetting.valueOf(deluxepearl.getString("Setting")));
                        else {
                            connection.createStatement().executeUpdate("UPDATE Extramanager SET Setting='"+ExtraSetting.OFF.toString()+"' WHERE UUID='" + uuid + "' AND Extra='"+ExtraType.DELUXEPEARL.toString()+"'");
                            deluxepearlExtra.put(uuid, ExtraSetting.OFF);
                        }
                    }
                    ResultSet doublejump = connection.createStatement().executeQuery("SELECT Setting FROM Extramanager WHERE UUID='" + uuid + "' AND Extra='" + ExtraType.DOUBLEJUMP.toString() + "'");
                    if (doublejump.next()) {
                        if(ExtraSetting.valueOf(doublejump.getString("Setting")) == ExtraSetting.ON && player.hasPermission("spigot.lobby.doublejump"))
                            doublejumpExtra.put(uuid, ExtraSetting.valueOf(doublejump.getString("Setting")));
                        else {
                            connection.createStatement().executeUpdate("UPDATE Extramanager SET Setting='"+ExtraSetting.OFF.toString()+"' WHERE UUID='" + uuid + "' AND Extra='"+ExtraType.DOUBLEJUMP.toString()+"'");
                            doublejumpExtra.put(uuid, ExtraSetting.OFF);
                        }
                    }
                    ResultSet fly = connection.createStatement().executeQuery("SELECT Setting FROM Extramanager WHERE UUID='" + uuid + "' AND Extra='" + ExtraType.FLY.toString() + "'");
                    if (fly.next()) {
                        if(ExtraSetting.valueOf(fly.getString("Setting")) == ExtraSetting.ON && player.hasPermission("spigot.lobby.fly"))
                            flyExtra.put(uuid, ExtraSetting.valueOf(fly.getString("Setting")));
                        else {
                            connection.createStatement().executeUpdate("UPDATE Extramanager SET Setting='"+ExtraSetting.OFF.toString()+"' WHERE UUID='" + uuid + "' AND Extra='"+ExtraType.FLY.toString()+"'");
                            flyExtra.put(uuid, ExtraSetting.OFF);
                        }
                    }
                    ResultSet shield = connection.createStatement().executeQuery("SELECT Setting FROM Extramanager WHERE UUID='" + uuid + "' AND Extra='" + ExtraType.SHIELD.toString() + "'");
                    if (shield.next()) {
                        if(ExtraSetting.valueOf(shield.getString("Setting")) == ExtraSetting.ON && player.hasPermission("spigot.lobby.shield")) {
                            shieldExtra.put(uuid, ExtraSetting.valueOf(shield.getString("Setting")));
                            if(!isShieldSchedulerRunning())
                                startShieldScheduler();
                        } else {
                            connection.createStatement().executeUpdate("UPDATE Extramanager SET Setting='"+ExtraSetting.OFF.toString()+"' WHERE UUID='" + uuid + "' AND Extra='"+ExtraType.SHIELD.toString()+"'");
                            shieldExtra.put(uuid, ExtraSetting.OFF);
                        }
                    }

                    if (flyExtra.get(uuid) == ExtraSetting.ON)
                        Bukkit.getPlayer(uuid).setAllowFlight(true);
                    if (doublejumpExtra.get(uuid) == ExtraSetting.ON)
                        Bukkit.getPlayer(uuid).setAllowFlight(true);
                } else {
                    connection.createStatement().executeUpdate("INSERT INTO Extramanager (UUID, Extra, Setting) VALUES ('" + uuid + "', '" + ExtraType.DELUXEPEARL.toString() + "', '" + ExtraSetting.OFF.toString() + "')");
                    connection.createStatement().executeUpdate("INSERT INTO Extramanager (UUID, Extra, Setting) VALUES ('" + uuid + "', '" + ExtraType.DOUBLEJUMP.toString() + "', '" + ExtraSetting.OFF.toString() + "')");
                    connection.createStatement().executeUpdate("INSERT INTO Extramanager (UUID, Extra, Setting) VALUES ('" + uuid + "', '" + ExtraType.FLY.toString() + "', '" + ExtraSetting.OFF.toString() + "')");
                    connection.createStatement().executeUpdate("INSERT INTO Extramanager (UUID, Extra, Setting) VALUES ('" + uuid + "', '" + ExtraType.SHIELD.toString() + "', '" + ExtraSetting.OFF.toString() + "')");
                    deluxepearlExtra.put(uuid, ExtraSetting.OFF);
                    doublejumpExtra.put(uuid, ExtraSetting.OFF);
                    flyExtra.put(uuid, ExtraSetting.OFF);
                    shieldExtra.put(uuid, ExtraSetting.OFF);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
    }

    public void updateExtraSetting(UUID uuid, ExtraType extra, ExtraSetting setting) {
        Player player = Bukkit.getPlayer(uuid);
        if (mysql.isConnected()) {
            try {
                connection.createStatement().executeUpdate("UPDATE Extramanager SET Setting='" + setting.toString() + "' WHERE UUID='" + uuid + "' AND Extra='" + extra.toString() + "'");
                if (extra == ExtraType.DELUXEPEARL)
                    deluxepearlExtra.put(uuid, setting);
                else if (extra == ExtraType.DOUBLEJUMP) {
                    doublejumpExtra.put(uuid, setting);
                    if (setting == ExtraSetting.OFF)
                        player.setAllowFlight(false);
                    else if (setting == ExtraSetting.ON)
                        player.setAllowFlight(true);
                } else if (extra == ExtraType.FLY) {
                    flyExtra.put(uuid, setting);
                    if (setting == ExtraSetting.OFF) {
                        player.setAllowFlight(false);
                        player.setFlying(false);
                    } else if (setting == ExtraSetting.ON)
                        player.setAllowFlight(true);
                } else if (extra == ExtraType.SHIELD) {
                    shieldExtra.put(uuid, setting);
                    if(setting == ExtraSetting.ON && !isShieldSchedulerRunning())
                        startShieldScheduler();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
        instance.getInventoryManager().setInventory(player);
    }

    private void startShieldScheduler() {
        shieldScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
            @Override
            public void run() {
                if(getActiveShields().size() == 0) {
                    Bukkit.getScheduler().cancelTask(shieldScheduler);
                    return;
                }
                for(UUID current : getActiveShields()) {
                    Player player = (Player) Bukkit.getPlayer(current);
                    if(player == null) return;
                    playEffects(player.getLocation());
                }
            }
        }, 0, 5);
    }

    private boolean isShieldSchedulerRunning() {
        return Bukkit.getScheduler().isCurrentlyRunning(shieldScheduler);
    }

    private void playEffects(Location location) {
        double y = location.getY() + 1;
        Location loc = new Location(location.getWorld(), location.getX(), y, location.getZ());

        Location loc1 = new Location(loc.getWorld(), loc.getX()+1, loc.getY(), loc.getZ());
        Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()+1);
        Location loc3 = new Location(loc.getWorld(), loc.getX()-1, loc.getY(), loc.getZ());
        Location loc4 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()-1);

        Location loc5 = new Location(loc.getWorld(), loc.getX()+1, loc.getY()-1, loc.getZ());
        Location loc6 = new Location(loc.getWorld(), loc.getX(), loc.getY()-1, loc.getZ()+1);
        Location loc7 = new Location(loc.getWorld(), loc.getX()-1, loc.getY()-1, loc.getZ());
        Location loc8 = new Location(loc.getWorld(), loc.getX(), loc.getY()-1, loc.getZ()-1);

        location.getWorld().playEffect(loc1, Effect.WITCH_MAGIC, 2, 20);
        location.getWorld().playEffect(loc2, Effect.WITCH_MAGIC, 2, 20);
        location.getWorld().playEffect(loc3, Effect.WITCH_MAGIC, 2, 20);
        location.getWorld().playEffect(loc4, Effect.WITCH_MAGIC, 2, 20);
        location.getWorld().playEffect(loc5, Effect.WITCH_MAGIC, 2, 20);
        location.getWorld().playEffect(loc6, Effect.WITCH_MAGIC, 2, 20);
        location.getWorld().playEffect(loc7, Effect.WITCH_MAGIC, 2, 20);
        location.getWorld().playEffect(loc8, Effect.WITCH_MAGIC, 2, 20);
        location.getWorld().playEffect(loc1, Effect.INSTANT_SPELL, 2);
        location.getWorld().playEffect(loc2, Effect.INSTANT_SPELL, 2);
        location.getWorld().playEffect(loc3, Effect.INSTANT_SPELL, 2);
        location.getWorld().playEffect(loc4, Effect.INSTANT_SPELL, 2);
        location.getWorld().playEffect(loc5, Effect.INSTANT_SPELL, 2);
        location.getWorld().playEffect(loc6, Effect.INSTANT_SPELL, 2);
        location.getWorld().playEffect(loc7, Effect.INSTANT_SPELL, 2);
        location.getWorld().playEffect(loc8, Effect.INSTANT_SPELL, 2);
    }

    public boolean hasDeluxepearl(UUID uuid) {
        if(deluxepearlExtra.get(uuid) == ExtraSetting.ON)
            return true;
        return false;
    }

    public boolean hasShield(UUID uuid) {
        if(shieldExtra.get(uuid) == ExtraSetting.ON)
            return true;
        return false;
    }

    public boolean hasFlymode(UUID uuid) {
        if (flyExtra.get(uuid) == ExtraSetting.ON)
            return true;
        return false;
    }

    public boolean hasDoublejump(UUID uuid) {
        if (doublejumpExtra.get(uuid) == ExtraSetting.ON)
            return true;
        return false;
    }

    public ArrayList<UUID> getActivePearls() {
        return activePearls;
    }

    public HashMap<UUID, ExtraSetting> getDeluxepearlExtra() {
        return deluxepearlExtra;
    }

    public HashMap<UUID, ExtraSetting> getDoublejumpExtra() {
        return doublejumpExtra;
    }

    public HashMap<UUID, ExtraSetting> getFlyExtra() {
        return flyExtra;
    }

    public HashMap<UUID, ExtraSetting> getShieldExtra() {
        return shieldExtra;
    }

    public ArrayList<UUID> getActiveShields() {
        ArrayList<UUID> activeShields = new ArrayList<>();
        for(UUID current : shieldExtra.keySet())
            if(shieldExtra.get(current) == ExtraSetting.ON) {
                activeShields.add(current);
            }
        return activeShields;
    }

    public enum ExtraType {

        DELUXEPEARL,
        DOUBLEJUMP,
        FLY,
        SHIELD;

        private ExtraType() {
        }
    }

    public enum ExtraSetting {

        ON,
        OFF;

        private ExtraSetting(){}
    }

}

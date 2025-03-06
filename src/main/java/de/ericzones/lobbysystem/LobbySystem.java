// Created by Eric B. 16.05.2021 19:54
package de.ericzones.lobbysystem;

import de.ericzones.lobbysystem.commands.*;
import de.ericzones.lobbysystem.inventories.FriendInventory;
import de.ericzones.lobbysystem.extra.MySQL;
import de.ericzones.lobbysystem.friend.FriendManager;
import de.ericzones.lobbysystem.inventories.GadgetInventory;
import de.ericzones.lobbysystem.listener.*;
import de.ericzones.lobbysystem.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class LobbySystem extends JavaPlugin {

    private ScoreboardManager scoreboardManager;
    private LocationManager locationManager;
    private VisibilityManager visibilityManager;
    private InventoryManager inventoryManager;
    private MessageManager messageManager;
    private ExtraManager extraManager;
    private ChatManager chatManager;
    private DelayManager delayManager;
    private FriendManager friendManager;
    private FriendInventory friendInventory;
    private GadgetInventory gadgetInventory;
    private GadgetManager gadgetManager;

    private ArrayList<UUID> buildPlayers;
    private HashMap<UUID, ItemStack> cachedSkulls;

    private MySQL mysql;

    public void onEnable() {
        registerPlugins();
        registerListener();
        registerCommands();
    }

    public void onDisable() {
        mysql.disconnect();
    }

    private void registerPlugins() {
        mysql = new MySQL(this, "username", "password", "database", "localhost", 3306);
        mysql.connect();
        buildPlayers = new ArrayList<>();
        cachedSkulls = new HashMap<>();
        extraManager = new ExtraManager(this);
        gadgetManager = new GadgetManager(this);
        scoreboardManager = new ScoreboardManager(this);
        locationManager = new LocationManager(this);
        visibilityManager = new VisibilityManager(this);
        chatManager = new ChatManager(this);
        inventoryManager = new InventoryManager(this);
        messageManager = new MessageManager(this);
        delayManager = new DelayManager(this);
        friendManager = new FriendManager(this);
        friendInventory = new FriendInventory(this);
        gadgetInventory = new GadgetInventory(this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    private void registerListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinListener(this), this);
        pluginManager.registerEvents(scoreboardManager, this);
        pluginManager.registerEvents(new ProtectionListener(this), this);
        pluginManager.registerEvents(new LoginListener(this), this);
        pluginManager.registerEvents(new QuitListener(this), this);
        pluginManager.registerEvents(new DoublejumpListener(this), this);
        pluginManager.registerEvents(new JumppadListener(this), this);
        pluginManager.registerEvents(new InteractListener(this), this);
        pluginManager.registerEvents(new InventoryClickListener(this), this);
        pluginManager.registerEvents(new EnderpearlListener(this), this);
        pluginManager.registerEvents(new ShieldListener(this), this);
        pluginManager.registerEvents(new PlayerChatListener(this), this);
        pluginManager.registerEvents(new GadgetListener(this), this);
    }

    private void registerCommands() {
        getCommand("build").setExecutor(new BuildCommand(this));
        getCommand("setspawn").setExecutor(new SetspawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("setlocation").setExecutor(new SetlocationCommand(this));
        getCommand("addlocation").setExecutor(new AddlocationCommand(this));
        getCommand("removelocation").setExecutor(new RemovelocationCommand(this));
        getCommand("chat").setExecutor(new ChatCommand(this));
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public ArrayList<UUID> getBuildPlayers() {
        return buildPlayers;
    }

    public MySQL getMysql() {
        return mysql;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public VisibilityManager getVisibilityManager() {
        return visibilityManager;
    }

    public HashMap<UUID, ItemStack> getCachedSkulls() {
        return cachedSkulls;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ExtraManager getExtraManager() {
        return extraManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public DelayManager getDelayManager() {
        return delayManager;
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }

    public FriendInventory getFriendInventory() {
        return friendInventory;
    }

    public GadgetInventory getGadgetInventory() {
        return gadgetInventory;
    }

    public GadgetManager getGadgetManager() {
        return gadgetManager;
    }

}

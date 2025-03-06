// Created by Eric B. 20.06.2020 16:01
package de.ericzones.lobbysystem.listener;

import de.ericzones.lobbysystem.LobbySystem;
import de.ericzones.lobbysystem.manager.*;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class JoinListener implements Listener {

    private LobbySystem instance;
    private LocationManager locationManager;
    private VisibilityManager visibilityManager;
    private InventoryManager inventoryManager;
    private MessageManager messageManager;
    private ExtraManager extraManager;
    private ChatManager chatManager;
    private GadgetManager gadgetManager;

    public JoinListener(LobbySystem instance) {
        this.instance = instance;
        locationManager = instance.getLocationManager();
        visibilityManager = instance.getVisibilityManager();
        inventoryManager = instance.getInventoryManager();
        messageManager = instance.getMessageManager();
        extraManager = instance.getExtraManager();
        chatManager = instance.getChatManager();
        gadgetManager = instance.getGadgetManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.setMaxHealth(6);
        player.setAllowFlight(false);
        e.setJoinMessage(null);
        player.teleport(locationManager.getSpawn());
        player.setGameMode(GameMode.ADVENTURE);
        visibilityManager.registerPlayerSetting(player.getUniqueId());
        chatManager.registerPlayerChatSettings(player.getUniqueId());
        extraManager.loadExtras(player.getUniqueId());
        gadgetManager.loadGadgets(player.getUniqueId());
        inventoryManager.setInventory(player);
        messageManager.sendTitle(player, 10, 40, 10, "§8• §bWillkommen §8•", "§8• §7"+player.getName()+" §8•");

        ArrayList<String> disableFeatures = new ArrayList<>();
        disableFeatures.add("CHAT");
        //SpigotAPI.getInstance().getLabyModManager().disableFeatures(player, disableFeatures);
        //SpigotAPI.getInstance().getLabyModManager().disableDamageIndicator(player);
        //SpigotAPI.getInstance().getLabyModManager().disableMinimap(player);

    }

}

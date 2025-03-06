// Created by Eric B. 21.06.2020 21:59
package de.ericzones.lobbysystem.listener;

import de.ericzones.lobbysystem.LobbySystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    private LobbySystem instance;

    public QuitListener(LobbySystem instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        e.setQuitMessage(null);
    }

}

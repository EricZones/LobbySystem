// Created by Eric B. 21.06.2020 21:31
package de.ericzones.lobbysystem.listener;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import de.ericzones.lobbysystem.LobbySystem;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginListener implements Listener {

    private LobbySystem instance;
    private HashMap<String, String> playerCache;

    public LoginListener(LobbySystem instance) {
        this.instance = instance;
        playerCache = new HashMap<>();
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        instance.getCachedSkulls().put(player.getUniqueId(), getSkull(player));
    }


    public ItemStack getSkull(OfflinePlayer player) {
        String skinURL = null;
        loadSkin: if (playerCache.containsKey(player.getName()))
            skinURL = playerCache.get(player.getName());
        else {
            try {
                GameProfile profile = new GameProfile(player.getUniqueId(), player.getName());
                Field field;
                field = MinecraftServer.class.getDeclaredField("W"); //This will obviously break on next Minecraft update....
                field.setAccessible(true);
                Object value = field.get(MinecraftServer.getServer());
                if (!(value instanceof MinecraftSessionService))
                    break loadSkin;
                MinecraftSessionService ss = (MinecraftSessionService) value;
                ss.fillProfileProperties(profile, true);
                Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = ss.getTextures(profile, true);
                if (textures.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                    MinecraftProfileTexture tex = textures.get(MinecraftProfileTexture.Type.SKIN);
                    skinURL = tex.getUrl();
                }
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (skinURL == null)
            return head;
        playerCache.put(player.getName(), skinURL);
        ItemMeta headMeta = head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder()
                .encode((String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", skinURL).getBytes()));
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }

}

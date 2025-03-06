// Created by Eric B. 17.05.2021 13:08
package de.ericzones.lobbysystem.extra;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ParticleBuilder {

    EnumParticle particleType;
    boolean longDistance;
    Location location;
    float offsetX;
    float offsetY;
    float offsetZ;
    float speed;
    int amount;
    int data;

    public ParticleBuilder(EnumParticle particleType, boolean longDistance, Location location, float offsetX, float offsetY, float offsetZ, float speed, int amount, int data) {
        this.particleType = particleType;
        this.longDistance = longDistance;
        this.location = location;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.amount = amount;
        this.data = data;
    }

    public void showParticles(Player player) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(this.particleType, this.longDistance, (float) this.location.getX(), (float) this.location.getY(), (float) this.location.getZ(), this.offsetX, this.offsetY, this.offsetZ, this.speed, this.amount, this.data);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

}

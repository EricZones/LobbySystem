// Created by Eric B. 17.05.2021 11:53
package de.ericzones.lobbysystem.extra;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class ItemBuilder {

    private ItemStack item;
    private ItemMeta itemMeta;
    private SkullMeta skullMeta;
    private LeatherArmorMeta leatherArmorMeta;

    public ItemBuilder(Material material) {
        item = new ItemStack(material);
        itemMeta = item.getItemMeta();
    }

    public ItemBuilder(Material material, int id) {
        item = new ItemStack(material, 1, (short) id);
        itemMeta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        item = itemStack;
        itemMeta = item.getItemMeta();
    }

    public ItemBuilder(Material material, Color color, boolean isLeatherArmor) {
        item = new ItemStack(material, 1);
        leatherArmorMeta = (LeatherArmorMeta) item.getItemMeta();
        leatherArmorMeta.setColor(color);
        leatherArmorMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(leatherArmorMeta);
    }

    public ItemBuilder setOwner(String owner) {
        skullMeta = (SkullMeta) item.getItemMeta();
        skullMeta.setOwner(owner);
        item.setItemMeta(skullMeta);
        itemMeta = item.getItemMeta();
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        if(leatherArmorMeta != null)
            leatherArmorMeta.setDisplayName(name);
        else
            itemMeta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        if(leatherArmorMeta != null)
            leatherArmorMeta.setLore(Arrays.asList(lore));
        else
            itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder removeLore() {
        itemMeta.setLore(null);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        itemMeta.spigot().setUnbreakable(unbreakable);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        return this;
    }

    public ItemBuilder setEnchanted(boolean enchanted) {
        if(enchanted) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemStack build() {
        if(leatherArmorMeta != null)
            item.setItemMeta(leatherArmorMeta);
        else
            item.setItemMeta(itemMeta);
        return item;
    }

}

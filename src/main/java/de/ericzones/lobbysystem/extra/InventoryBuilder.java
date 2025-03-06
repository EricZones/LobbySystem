// Created by Eric B. 17.05.2021 11:52
package de.ericzones.lobbysystem.extra;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InventoryBuilder {

    private int size;
    private InventoryHolder inventoryHolder;
    private String title;
    private Map<Integer, ItemStack> inventoryItems = new HashMap<>();

    public InventoryBuilder setSize(int size) {
        this.size = size;
        return this;
    }

    public InventoryBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public InventoryBuilder setHolder(InventoryHolder inventoryHolder) {
        this.inventoryHolder = inventoryHolder;
        return this;
    }

    public InventoryBuilder setItem(int slot, ItemStack itemStack) {
        inventoryItems.put(slot, itemStack);
        return this;
    }

    public InventoryBuilder fillInventory(ItemStack itemStack) {
        for(int i = 0; i < size; i++) {
            if(!inventoryItems.containsKey(i))
                inventoryItems.put(i, itemStack);
        }
        return this;
    }

    public InventoryBuilder fillInventoryExact(Map<Integer, ItemStack> map) {
        for(int slot : map.keySet()) {
            if(!inventoryItems.containsKey(slot))
                inventoryItems.put(slot, map.get(slot));
        }
        return this;
    }

    public Inventory build() {
        Inventory inventory = Bukkit.createInventory(inventoryHolder, size, title);
        for(int slot : inventoryItems.keySet())
            inventory.setItem(slot, inventoryItems.get(slot));
        return inventory;
    }

}

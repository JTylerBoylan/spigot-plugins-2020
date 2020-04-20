package com.jtylerboylan.marketplace.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.jtylerboylan.marketplace.Core;
import com.jtylerboylan.marketplace.Item;

public class ItemData {

	private static File file = new File(Core.getPlugin().getDataFolder() + "/items.yml");
	private static FileConfiguration config = YamlConfiguration.loadConfiguration(file);
	
	private static List<Item> holder = new ArrayList<Item>();
	
	
	public static void load() {
		if (config.getConfigurationSection("items") == null)
			config.createSection("items");
		for (String id : config.getConfigurationSection("items").getKeys(false)) {
			String path = "items." + id;
			ItemStack itemstack = config.getItemStack(path + ".itemstack");
			Item item = new Item(id, itemstack);
			holder.add(item);
		}
	}
	
	public static void save() {
		config.set("items", null);
		for (Item item : holder) {
			config.set("items." + item.getID() + ".itemstack", item.getItemStack());
		}
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void addItem(Item item) {
		holder.add(item);
	}
	public static void removeItem(Item item) {
		holder.remove(item);
	}
	
	public static List<Item> allItems(){
		return holder;
	}
	
	public static Item getItem(String item_id) {
		for (Item item : holder)
			if (item.getID().equals(item_id))
				return item;
		return null;
	}
	
}

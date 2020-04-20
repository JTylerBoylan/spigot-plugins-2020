package com.jtylerboylan.marketplace;

import org.bukkit.inventory.ItemStack;

import com.jtylerboylan.marketplace.data.ItemData;

public class Item {

	private String item_id;
	
	private ItemStack item;
	
	public Item (ItemStack item) {
		this.item = item;
		this.item_id = generateItemID();
	}
	
	public Item (String item_id, ItemStack item) {
		this.item_id = item_id;
		this.item = item;
	}
	
	public ItemStack getItemStack() {
		return item;
	}
	
	public String getID() {
		return item_id;
	}
	
	private String generateItemID() {
		int next = 0;
		String id = item.getType().toString() + "-";
		while (ItemData.getItem(id+next) != null) {
			next++;
		}
		return id+next;
		
	}
	
}

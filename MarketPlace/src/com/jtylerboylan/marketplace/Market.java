package com.jtylerboylan.marketplace;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.jtylerboylan.marketplace.data.ItemData;

public class Market {
	
	private Business owner;
	
	private String item_id;
	
	private double min_price, max_price, sell_multiplier;
	
	private int level, stock;
	
	private List<String> permit_list;
	
	public Market(Business owner) {
		this.item_id = "";
		this.owner = owner;
		this.min_price = 0;
		this.max_price = min_price;
		this.sell_multiplier = 0.50;
		this.level = 1;
		this.stock = 0;
		this.permit_list = new ArrayList<String>();
	}
	
	public Market(Business owner, String item_id, double min_price, double max_price, double sell_multiplier, int level, int stock, List<String> permit_list) {
		this.owner = owner;
		this.item_id = item_id;
		this.min_price = min_price;
		this.max_price = max_price;
		this.sell_multiplier = sell_multiplier;
		this.level = level;
		this.stock = stock;
		this.permit_list = permit_list;
	}
	
	public Business getOwner() {
		return owner;
	}
	
	public String getItemID() {
		return item_id;
	}
	
	public void setItemID(String item_id) {
		this.item_id = item_id;
	}
	
	public ItemStack getItemStack() {
		ItemStack itemstack = ItemData.getItem(item_id) == null ? null : ItemData.getItem(item_id).getItemStack();
		return itemstack;
	}
	
	public void setItemStack(ItemStack itemstack) {
		Item newItem = new Item(itemstack);
		item_id = newItem.getID();
		ItemData.addItem(newItem);
	}
	
	public double getMinPrice() {
		return min_price;
	}
	
	public void setMinPrice(double price) {
		this.min_price = price;
	}
	
	public double getMaxPrice() {
		return max_price;
	}
	
	public void setMaxPrice(double price) {
		this.max_price = price;
	}
	
	public double getPrice() {
		return (((min_price-max_price)/maxStock())*(stock-maxStock()))+min_price;
	}
	
	public double getSellMultiplier() {
		return sell_multiplier;
	}
	
	public void setSellMultiplier(double sell_multiplier) {
		this.sell_multiplier = sell_multiplier;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void upgradeLevel() {
		level++;
	}
	
	public int getStock() {
		return stock;
	}
	
	public void setStock(int i) {
		stock = i;
	}
	
	public void addStock(int i) {
		stock += i;
	}
	
	public void removeStock(int i) {
		stock -= i;
	}
	
	public List<String> getPermitted(){
		return permit_list;
	}
	
	public void addPermit(String permit) {
		permit_list.add(permit);
	}
	
	public boolean isPermitted(Entity entity) {
		return permit_list.contains(entity.getID());
	}
	
	public int maxStock() {
		int max_stock = 1728;
		switch (level) {
		case 2:
			max_stock = 3456;
			break;
		case 3:
			max_stock = 6912;
			break;
		case 4:
			max_stock = 13824;
			break;
		case 5:
			max_stock = 27648;
			break;
		}
		return max_stock;
	}
	
	
}

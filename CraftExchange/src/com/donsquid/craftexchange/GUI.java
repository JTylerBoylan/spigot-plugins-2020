package com.donsquid.craftexchange;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class GUI {

	public static HashMap<Player, GUI> player_gui = new HashMap<Player, GUI>();
	
	protected Player USER;
	
	protected int SIZE = 9;
	
	protected GUI PREVIOUS;
	
	public GUI(GUI PREVIOUS, Player USER) {
		this.USER = USER;
		this.PREVIOUS = PREVIOUS;
	}
	
	public Player getUser() {
		return USER;
	}
	
	public GUI getPrevious() {
		return PREVIOUS;
	}
	
	public abstract Inventory getInventory();
	
	protected abstract void clickSlot(int slot);
	
	public int getSize() {
		return SIZE;
	}
	
	public void open() {
		USER.openInventory(getInventory());
		player_gui.put(USER, this);
	}
	
	protected static ItemStack createTile(ItemStack item, int amount, String name, String...strings) {
		
		if (item == null)
			return null;
		
		item = item.clone();
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		
		ArrayList<String> lore = new ArrayList<String>();
		for (String s : strings)
			lore.add(ChatColor.translateAlternateColorCodes('&', s));
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		item.setAmount(amount);
		
		return item;
	}
	
	public static Listener getListener() {
		
		Listener listener = new Listener() {
			
			@EventHandler
			public void onInventoryClick(InventoryClickEvent e) {				
				if (!(e.getWhoClicked() instanceof Player))
					return;	
				
				Player player = (Player) e.getWhoClicked();	
				if (player_gui.get(player) != null) {
					e.setCancelled(true);
					player_gui.get(player).clickSlot(e.getSlot());
				}
					
			}
			
			@EventHandler
			public void onInventoryClose(InventoryCloseEvent e) {
				if (!(e.getPlayer() instanceof Player))
					return;
				
				Player player = (Player) e.getPlayer();
				
				if (player_gui.get(player) != null)
					player_gui.put(player, null);
				
			}
			
			@EventHandler
			public void onInventoryOpen(InventoryOpenEvent e) {
				if (!(e.getPlayer() instanceof Player))
					return;
				
				Player player = (Player) e.getPlayer();
				
				if (player_gui.get(player) != null)
					player_gui.put(player, null);
				
			}
			
		};
		
		return listener;
	}
	
}

package com.jtylerboylan.marketplace.guis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.jtylerboylan.marketplace.Core;

public abstract class UserGUI {

	protected Player user;
	
	protected static String prefix = Core.getServerConfig().getString("plugin-prefix");
	
	protected static ItemStack back_tile = UserGUI.tile(Material.COMPASS, 1, (short) 0, cc("&c&lBACK"), cc("&4Click to go back"));
	
	protected static String entity_prefix = Core.getServerConfig().getString("entity-prefix");
	protected static String business_prefix = Core.getServerConfig().getString("business-prefix");
	protected static String market_prefix = Core.getServerConfig().getString("market-prefix");
	
	private static HashMap<Player, UserGUI> viewing = new HashMap<Player, UserGUI>();
	
	public UserGUI(Player user) {
		this.user = user;
	}
	
	public Player getUser() {
		return user;
	}
	
	public abstract void load();
	
	public abstract void clickSlot(int slot);
	
	public abstract void stringInput(String string);
	
	public void punchSlot(int slot) {
		clickSlot(slot);
	}
	
	public abstract boolean hasAccess(Player player);
	
	public static UserGUI getUserViewing(Player player) {
		return viewing.get(player);
	}
	public static void setUserViewing(Player player, UserGUI gui) {
		viewing.put(player, gui);
	}
	
	public static void openGUI(Player player, UserGUI gui) {
		if (gui.hasAccess(player)) {
			gui.load();
			setUserViewing(player, gui);
		} else {
			player.sendMessage(cc(prefix + " &cDenied > You do not have access!"));
		}
	}
	
	public static void closeAll() {
		for (Player player : viewing.keySet()) {
			player.closeInventory();
		}
	}
	
	protected static String cc(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
	protected static ItemStack tile(Material material, int amount, short data, String name, String...lore) {
		
		ItemStack tile = new ItemStack(material, amount, data);
		ItemMeta tileMeta = tile.getItemMeta();
		tileMeta.setDisplayName(cc(name));
		List<String> tileLore = new ArrayList<String>();
		for (String lores : lore) {
			tileLore.add(cc(lores));
		}
		tileMeta.setLore(tileLore);
		tile.setItemMeta(tileMeta);
		return tile;
		
	}
	
}

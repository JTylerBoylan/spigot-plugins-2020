package com.jtylerboylan.marketplace.guis;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.jtylerboylan.marketplace.Business;
import com.jtylerboylan.marketplace.Market;
import com.jtylerboylan.marketplace.data.MarketData;

public class MarketShopGUI extends UserGUI {

	private Business business;
	
	private List<Market> markets_list;
	
	private int page;
	
	public MarketShopGUI(Player user, Business business, int page) {
		super(user);
		this.business = business;
		this.markets_list = MarketData.allMarkets(business);
		this.page = page;
	}

	public void load() {

		
		Inventory markets_page = Bukkit.createInventory(user, 54, cc(business_prefix + business.getName()));
		
		for (int slot = 0; slot < 45; slot++) {
			if (slot + (page-1)*45 >= markets_list.size()) continue;
			
			Market markets = markets_list.get(slot + (page-1)*45);
			
			ItemStack tile = markets.getItemStack().clone();
			ItemMeta itemmeta = tile.getItemMeta();
			List<String> lore = itemmeta.hasLore() ? itemmeta.getLore() : new ArrayList<String>();
			lore.add(cc("&7Stock: " + markets.getStock()));
			DecimalFormat format = new DecimalFormat("#.##");
			lore.add(cc("&7Current Buy Price: &a$") + format.format(markets.getPrice()));
			lore.add(cc("&7Current Sell Price: &e$") + format.format(markets.getPrice() * markets.getSellMultiplier()));
			lore.add(cc("&eClick to Buy/Sell"));
			itemmeta.setLore(lore);
			tile.setItemMeta(itemmeta);
			tile.setAmount(markets.getLevel());
			markets_page.setItem(slot, tile);
		}
		
		if (markets_list.size() > page*45) {
			ItemStack next_page = UserGUI.tile(Material.PAPER, page+1, (short) 0, cc("&aNext Page"), cc("&2Click to go to next page"));
			markets_page.setItem(52, next_page);
		}
		if (page > 1) {
			ItemStack previous_page = UserGUI.tile(Material.PAPER, page-1, (short) 0, cc("&ePrevious Page"), cc("&6Click to go to previous page"));
			markets_page.setItem(51, previous_page);
		}
		
		markets_page.setItem(53, back_tile);
		
		
		user.openInventory(markets_page);
		
	}

	public void clickSlot(int slot) {
		if (slot < 45) {
			if (slot + 1 + (page-1)*45 <= markets_list.size()) {
				UserGUI.openGUI(user, new MarketBuySellGUI(user, markets_list.get(slot+(page-1)*45)));
			}
		}
		if (slot == 51) {
			if (page > 1) {
				UserGUI.openGUI(user, new MarketShopGUI(user, business, page-1));
			}
		}
		if (slot == 52) {
			if (markets_list.size() > page*45) {
				UserGUI.openGUI(user, new MarketShopGUI(user,business,page+1));
			}
		}
		if (slot == 53) {
			user.closeInventory();
		}
	}

	public void stringInput(String string) {}

	public boolean hasAccess(Player player) { return player.hasPermission("market.open");}

}

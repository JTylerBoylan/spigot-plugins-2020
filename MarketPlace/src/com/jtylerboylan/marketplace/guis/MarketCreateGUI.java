package com.jtylerboylan.marketplace.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.jtylerboylan.marketplace.Business;
import com.jtylerboylan.marketplace.Core;
import com.jtylerboylan.marketplace.Entity;
import com.jtylerboylan.marketplace.Market;
import com.jtylerboylan.marketplace.data.MarketData;

public class MarketCreateGUI extends UserGUI {

	private Business business;
	
	private Market market;
	
	private int inventory_size;
	
	private List<Double> prices = Core.getServerConfig().getDoubleList("market-set-price-intervals");
	
	private List<Double> sell_prices = Core.getServerConfig().getDoubleList("market-sell-multipliers");
	
	private List<Double> upgrade_prices = Core.getServerConfig().getDoubleList("market-upgrade-prices");
	
	private int levels = Core.getServerConfig().getInt("market-max-level");
	
	public MarketCreateGUI(Player user, Business business, Market market) {
		super(user);
		this.business = business;
		this.market = market;
	}

	public void load() {
		
		inventory_size = market.getMinPrice() == market.getMaxPrice() ? 18 : 27;
		
		Inventory create_market = Bukkit.createInventory(user, inventory_size, cc(market_prefix + "New Market"));
		
		ItemStack delete = UserGUI.tile(Material.REDSTONE_BLOCK, 1, (short) 0, cc("&cCancel"), cc("&4Click to cancel!"));
		ItemStack item = UserGUI.tile(Material.STAINED_GLASS_PANE, 1, (short) 14, cc("&aITEM"), cc("&4Click to change to item in your hand"));
		if (market.getItemStack() != null) {
			item = market.getItemStack().clone();
			ItemMeta itemmeta = item.getItemMeta();
			List<String> lore = itemmeta.hasLore() ? itemmeta.getLore() : new ArrayList<String>();
			lore.add(cc("&2Click to change to item in your hand"));
			itemmeta.setLore(lore);
			item.setItemMeta(itemmeta);
		}
		ItemStack sell_multiplier = UserGUI.tile(Material.GOLD_INGOT, 1, (short) 0, cc("&eSell Multiplier"), cc("&6x" + market.getSellMultiplier()), cc("&7Click to change"));
		String current = market.getMinPrice() == market.getMaxPrice() ? "&3Add Max Price" : "&9Remove Max Price"; 
		ItemStack min_max = UserGUI.tile(Material.IRON_INGOT, 1, (short) 0, cc(current), cc("&5Click to change"));
		String upgrade = upgradePrice() == -1 ? "&6&lMAX" : "" + upgradePrice();
		ItemStack level = UserGUI.tile(Material.DIAMOND, market.getLevel(), (short) 0, cc("&aLevel " + market.getLevel()), cc("&7Max Stock Size: " + market.maxStock()), cc("&7Price: &2$" + upgrade), cc("&2Click to upgrade"));
		ItemStack confirm = UserGUI.tile(Material.NETHER_STAR, 1, (short) 0, cc("&a&lCONFIRM"), cc("&7Price: &2$" + market.getMinPrice()), cc("&2Click to confirm!"));
		
		create_market.setItem(0, delete);
		create_market.setItem(2, item);
		create_market.setItem(4, sell_multiplier);
		create_market.setItem(5, min_max);
		create_market.setItem(6, level);
		create_market.setItem(8, confirm);
		
		for (int i = 0; i < 8; i++) {
			ItemStack min_price = UserGUI.tile(Material.PAPER, 1, (short) 0, cc("&a" + prices.get(i)), cc("&7Price: &a$" + market.getMinPrice()));
			create_market.setItem(i+9, min_price);
		}
		
		ItemStack total_min = UserGUI.tile(Material.EMERALD, 1, (short) 0, cc("&7Price: &a$" + market.getMinPrice()), cc("&cClick to reset to $0.00"));
		create_market.setItem(17, total_min);
		
		if (inventory_size == 27) {
			for (int i = 0; i < 8; i++) {
				ItemStack max_price = UserGUI.tile(Material.PAPER, 1, (short) 0, cc("&a" + prices.get(i)), cc("&7Price: &a$" + market.getMaxPrice()));
				create_market.setItem(i+18, max_price);
			}
			ItemStack total_max = UserGUI.tile(Material.EMERALD, 1, (short) 0, cc("&7Maximum Price: &a$" + market.getMaxPrice()), cc("&cClick to reset to $" + market.getMinPrice()));
			create_market.setItem(26, total_max);
		}
		
		user.openInventory(create_market);
	}

	public void clickSlot(int slot) {
		switch (slot) {
		case 0:
			UserGUI.openGUI(user, new BusinessPageGUI(user, business));
			break;
		case 2:
			if (user.getInventory().getItemInMainHand().getType() == Material.AIR) {
				user.sendMessage(cc(prefix + " &cError > There is no item in your hand"));
				return;
			}
			ItemStack item = user.getInventory().getItemInMainHand().clone();
			item.setAmount(1);
			market.setItemStack(item);
			UserGUI.openGUI(user, new MarketCreateGUI(user, business, market));
			break;
		case 4:
			int current = 3;
			for (int i = 0; i < sell_prices.size(); i++)
				if (sell_prices.get(i) == market.getSellMultiplier())
					current = i;
			if (current+1 >= sell_prices.size()) current = -1;
			market.setSellMultiplier(sell_prices.get(current+1));
			UserGUI.openGUI(user, new MarketCreateGUI(user, business, market));
			break;
		case 5:
			if (market.getMinPrice() == market.getMaxPrice())
				market.setMaxPrice(market.getMinPrice() + 1.00);
			else
				market.setMaxPrice(market.getMinPrice());
			UserGUI.openGUI(user, new MarketCreateGUI(user, business, market));
			break;
		case 6:
			if (market.getLevel() < levels) {
				if (Core.getEconomy().getBalance(user) < upgradePrice() && !business.getOwner().override()) {
					user.sendMessage(cc(prefix + " &cDenied > Insufficient funds!"));
					return;
				}
				Core.getEconomy().withdrawPlayer(user, upgradePrice());
				market.upgradeLevel();
			} else
				user.sendMessage(cc(prefix + " &cUpgrade > You are at max level!"));
			UserGUI.openGUI(user,  new MarketCreateGUI(user, business, market));
			break;
		case 8:
			if (market.getItemStack() == null) {
				user.sendMessage(cc(prefix + " &cError > No Item is set!"));
				return;
			}
			if (MarketData.allMarkets(business).size() >= Core.getServerConfig().getInt("max-markets-per-business") && !business.getOwner().override()) {
				user.sendMessage(cc(prefix + " &cDenied > This business already has the maximum number of markets!"));
				return;
			}
			market.addStock(user.getInventory().getItemInMainHand().getAmount());
			user.getInventory().setItemInMainHand(null);
			MarketData.addMarket(market);
			UserGUI.openGUI(user,  new BusinessMarketGUI(user, business, 1));
			user.sendMessage(cc(prefix + " &aMarket > Market was succesfully created!"));
			break;
		case 17:
			if (market.getMinPrice() == market.getMaxPrice())
				market.setMaxPrice(0.00);
			else
				market.setMaxPrice(1.00);
			market.setMinPrice(0.00);
			UserGUI.openGUI(user, new MarketCreateGUI(user, business, market));
			break;
		case 26:
			market.setMaxPrice(market.getMinPrice() + 1.00);
			UserGUI.openGUI(user, new MarketCreateGUI(user, business, market));
			break;
		}
	
		if (8 < slot && slot < 17) {
			market.setMinPrice(market.getMinPrice() + prices.get(slot-9));
			market.setMaxPrice(market.getMaxPrice() + prices.get(slot-9));
			UserGUI.openGUI(user, new MarketCreateGUI(user, business, market));
		}
		if (17 < slot && slot < 26) {
			market.setMaxPrice(market.getMaxPrice() + prices.get(slot-18));
			UserGUI.openGUI(user, new MarketCreateGUI(user, business, market));
		}
		
	}

	public void stringInput(String string) {}

	public boolean hasAccess(Player player) {
		Entity entity = new Entity(player);
		if (entity.hasAccess(business))
			return true;
		return false;
	}
	
	public double upgradePrice() {
		
		if (market.getLevel() > levels)
			return -1;
		
		return upgrade_prices.get(market.getLevel()-1);
	}

}

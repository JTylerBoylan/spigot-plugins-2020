package com.jtylerboylan.marketplace.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.jtylerboylan.marketplace.Business;
import com.jtylerboylan.marketplace.Core;
import com.jtylerboylan.marketplace.Market;

public class MarketData {

	private static File file = new File(Core.getPlugin().getDataFolder() + "/markets.yml");
	private static FileConfiguration config = YamlConfiguration.loadConfiguration(file);
	
	private static List<Market> holder = new ArrayList<Market>();

	public static void load() {
		if (config.getConfigurationSection("markets") == null)
			config.createSection("markets");
		for (String owner_id : config.getConfigurationSection("markets").getKeys(false)) {
			Business owner = BusinessData.getBusiness(owner_id);
			for (String item_id : config.getConfigurationSection("markets." + owner_id).getKeys(false)) {
				String path = "markets." + owner_id + "." + item_id;
				int stock = config.getInt(path + ".stock");
				double min_price = config.getDouble(path + ".price");
				double max_price = config.getDouble(path + ".max-price");
				double sell_multiplier = config.getDouble(path + ".sell-multiplier");
				int level = config.getInt(path + ".level");
				List<String> permit_list = config.getStringList(path + ".permit-list");
				Market market = new Market(owner, item_id, min_price, max_price, sell_multiplier, level, stock, permit_list);
				holder.add(market);
			}
		}	
	}
	
	public static void save() {
		config.set("markets", null);
		for (Market market: holder) {
			config.set("markets." + market.getOwner().getID() + "." + market.getItemID() + ".stock", market.getStock());
			config.set("markets." + market.getOwner().getID() + "." + market.getItemID() + ".price", market.getMinPrice());
			config.set("markets." + market.getOwner().getID() + "." + market.getItemID() + ".max-price", market.getMaxPrice());
			config.set("markets." + market.getOwner().getID() + "." + market.getItemID() + ".sell-multiplier", market.getSellMultiplier());
			config.set("markets." + market.getOwner().getID() + "." + market.getItemID() + ".level", market.getLevel());
			config.set("markets." + market.getOwner().getID() + "." + market.getItemID() + ".permit-list", market.getPermitted());
		}
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void addMarket(Market m) {
		holder.add(m);
	}
	
	public static void removeMarket(Market m) {
		holder.remove(m);
	}
	
	public static List<Market> allMarkets(){
		return holder;
	}
	
	public static List<Market> allMarkets(Business business){
		List<Market> business_markets = new ArrayList<Market>();
		for (Market market : holder)
			if (market.getOwner().getID().equals(business.getID()))
				business_markets.add(market);
		return business_markets;
	}
	
}

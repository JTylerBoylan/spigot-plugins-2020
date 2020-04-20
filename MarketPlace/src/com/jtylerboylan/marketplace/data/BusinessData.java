package com.jtylerboylan.marketplace.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.jtylerboylan.marketplace.Business;
import com.jtylerboylan.marketplace.Core;
import com.jtylerboylan.marketplace.Entity;

public class BusinessData {

	private static File file = new File(Core.getPlugin().getDataFolder() + "/business.yml");
	private static FileConfiguration config = YamlConfiguration.loadConfiguration(file);
	
	private static List<Business> holder = new ArrayList<Business>();

	public static void load() {
		if (config.getConfigurationSection("business") == null)
			config.createSection("business");
		for (String id : config.getConfigurationSection("business").getKeys(false)) {
			String path = "business." + id;
			Entity owner = EntityData.getEntity(config.getString(path + ".owner"));
			String display_name = config.getString(path + ".display-name");
			double balance = config.getDouble(path + ".balance");
			List<String> permit_list = config.getStringList(path  + ".permit-list");
			Business business = new Business(owner, id, display_name, permit_list, balance);
			holder.add(business);
		}
	}
	
	public static void save() {
		config.set("business", null);
		for (Business business : holder) {
			config.set("business." + business.getID() + ".display-name", business.getName());
			config.set("business." + business.getID() + ".owner", business.getOwner().getID());
			config.set("business." + business.getID() + ".balance", business.getBalance());
			config.set("business." + business.getID() + ".permit-list", business.getPermitted());
		}
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void addBusiness(Business business) {
		holder.add(business);
	}
	public static void removeBusiness(Business business) {
		holder.remove(business);
	}
	
	public static List<Business> allBusiness(){
		return holder;
	}
	
	public static List<Business> allBusiness(Entity entity){
		List<Business> entity_business = new ArrayList<Business>();
		for (Business business : holder) {
			if (business.getOwner().getID().equals(entity.getID())) {
				entity_business.add(business);
			}
		}
		return entity_business;
	}
	
	public static Business getBusiness(String business_id) {
		for (Business business : holder)
			if (business.getID().equals(business_id))
				return business;
		return null;
	}
	
	
	
}

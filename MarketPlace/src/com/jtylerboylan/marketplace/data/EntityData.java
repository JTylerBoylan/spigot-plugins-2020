package com.jtylerboylan.marketplace.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.jtylerboylan.marketplace.Core;
import com.jtylerboylan.marketplace.Entity;

public class EntityData {

	private static File file = new File(Core.getPlugin().getDataFolder() + "/entities.yml");
	private static FileConfiguration config = YamlConfiguration.loadConfiguration(file);
	
	private static List<Entity> holder = new ArrayList<Entity>();

	public static void load() {
		if (config.getConfigurationSection("entities") == null)
			config.createSection("entities");
		for (String id : config.getConfigurationSection("entities").getKeys(false)) {
			String path = "entities." + id;
			String display_name = config.getString(path + ".display-name");
			List<String> permit_list = config.getStringList(path + ".permit-list");
			boolean override = config.getBoolean(path + ".override");
			Entity entity = new Entity(id, display_name, permit_list, override);
			holder.add(entity);
		}
	}
	
	public static void save() {
		config.set("entities", null);
		for (Entity entity: holder) {
			config.set("entities." + entity.getID() + ".display-name", entity.getName());
			config.set("entities." + entity.getID() + ".permit-list", entity.getPermitted());
			config.set("entities." + entity.getID() + ".override", entity.override());
		} try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void addEntity(Entity entity) {
		holder.add(entity);
	}
	public static void removeEntity(Entity entity) {
		holder.remove(entity);
	}
	
	public static Entity getEntity(String entity_id) {
		try {
			Player test = Bukkit.getPlayer(UUID.fromString(entity_id));
			return new Entity(test);
		} catch (Exception e) {
			for (Entity entity : holder)
				if (entity.getID().equalsIgnoreCase(entity_id))
					return entity;
			return null;
		}
	}
	
	public static List<Entity> allEntities(){
		return holder;
	}
	
}

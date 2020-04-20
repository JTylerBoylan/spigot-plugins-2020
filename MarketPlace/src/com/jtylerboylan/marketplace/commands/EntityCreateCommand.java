package com.jtylerboylan.marketplace.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jtylerboylan.marketplace.Core;
import com.jtylerboylan.marketplace.Entity;
import com.jtylerboylan.marketplace.data.EntityData;

public class EntityCreateCommand implements CommandExecutor {

	private static String prefix = Core.getServerConfig().getString("plugin-prefix");
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("createentity")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(cc(prefix + " &cDenied > You must be a player to run this command!"));
				return true;
			}
			if (!sender.hasPermission("market.createentity")) {
				sender.sendMessage(cc(prefix + " &cDenied > You do not have permission!"));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(cc(prefix + " &cUsage > /createentity <entity-id>"));
				return true;
			}
			String entity_id = args[0];
			if (EntityData.getEntity(entity_id) != null) {
				sender.sendMessage(cc(prefix + " &cError > An entity with that id already exists!"));
				return true;
			}
			Entity custom_entity = new Entity(entity_id);
			custom_entity.permit(new Entity((Player) sender).getID());
			EntityData.addEntity(custom_entity);
			sender.sendMessage(cc(prefix + " &aEntity > Entity " + entity_id + " was successfully created!"));
			return true;
		}
		return false;
	}
	
	private String cc(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}

}

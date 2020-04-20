package com.jtylerboylan.marketplace.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.jtylerboylan.marketplace.Core;
import com.jtylerboylan.marketplace.Entity;
import com.jtylerboylan.marketplace.data.EntityData;

public class EntityDeleteCommand implements CommandExecutor {

	private static String prefix = Core.getServerConfig().getString("plugin-prefix");
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (label.equalsIgnoreCase("deleteentity")) {
			if (!sender.hasPermission("market.deleteentity")) {
				sender.sendMessage(cc(prefix + " &cDenied > You do not have permission!"));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(cc(prefix + " &cUsage > /deleteentity <entity-id>"));
				return true;
			}
			String entity_id = args[0];
			if (EntityData.getEntity(entity_id) == null) {
				sender.sendMessage(cc(prefix + " &cError > An entity with that id doesn't exists!"));
				return true;
			}
			Entity entity = EntityData.getEntity(entity_id);
			EntityData.removeEntity(entity);
			sender.sendMessage(cc(prefix + " &aEntity > Entity " + entity_id + " was successfully deleted!"));
			return true;
		}
		
		return false;
	}
	
	private String cc(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
}

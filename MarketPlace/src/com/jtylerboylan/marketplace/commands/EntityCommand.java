package com.jtylerboylan.marketplace.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jtylerboylan.marketplace.Core;
import com.jtylerboylan.marketplace.Entity;
import com.jtylerboylan.marketplace.data.EntityData;
import com.jtylerboylan.marketplace.guis.EntityPageGUI;
import com.jtylerboylan.marketplace.guis.UserGUI;

public class EntityCommand implements CommandExecutor {

	private static String prefix = Core.getServerConfig().getString("plugin-prefix");
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (label.equalsIgnoreCase("entity")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(cc(prefix + " &cDenied > You must be a player to run this command!"));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(cc(prefix + " &cUsage > /entity <entity-id>"));
				return true;
			}
			String entity_id = args[0];
			Entity entity = EntityData.getEntity(entity_id);
			if (entity == null) {
				sender.sendMessage(cc(prefix + " &cError > An entity with the id: &e" + entity_id + " &cdoes not exist!"));
				return true;
			}
			UserGUI.openGUI((Player) sender, new EntityPageGUI((Player) sender, entity));
			return true;
		}
		
		return false;
	}
	
	private String cc(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
}

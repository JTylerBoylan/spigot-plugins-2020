package com.jtylerboylan.marketplace.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jtylerboylan.marketplace.Business;
import com.jtylerboylan.marketplace.Core;
import com.jtylerboylan.marketplace.data.BusinessData;
import com.jtylerboylan.marketplace.guis.BusinessPageGUI;
import com.jtylerboylan.marketplace.guis.UserGUI;

public class BusinessCommand implements CommandExecutor {

	private static String prefix = Core.getServerConfig().getString("plugin-prefix");
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (label.equalsIgnoreCase("business")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(cc(prefix + " &cDenied > You must be a player to run this command!"));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(cc(prefix + " &cUsage > /business <business-id>"));
				return true;
			}
			String business_id = args[0];
			Business business = BusinessData.getBusiness(business_id);
			if (business == null) {
				sender.sendMessage(cc(prefix + " &cError > An business with the id: &e" + business_id + " &cdoes not exist!"));
				return true;
			}
			UserGUI.openGUI((Player) sender, new BusinessPageGUI((Player) sender, business));
			return true;
		}
		
		return false;
	}
	
	private String cc(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
}

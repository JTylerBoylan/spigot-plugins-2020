package com.jtylerboylan.marketplace.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jtylerboylan.marketplace.Business;
import com.jtylerboylan.marketplace.Core;
import com.jtylerboylan.marketplace.Entity;
import com.jtylerboylan.marketplace.data.BusinessData;

public class BusinessDeleteCommand implements CommandExecutor {

	private static String prefix = Core.getServerConfig().getString("plugin-prefix");

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (label.equalsIgnoreCase("deletebusiness")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(cc(prefix + " &cDenied > You must be a player to run this command!"));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(cc(prefix + " &cUsage > /deletebusiness <business-id>"));
				return true;
			}		
			String business_id = args[0];
			if (BusinessData.getBusiness(business_id) == null) {
				sender.sendMessage(cc(prefix + " &cError > A business with that id doesn't exists!"));
				return true;
			}
			Business business = BusinessData.getBusiness(business_id);
			Entity entity = new Entity((Player) sender);
			if (!sender.hasPermission("market.deletebusiness") && entity.hasAccess(business)) {
				sender.sendMessage(cc(prefix + " &cDenied > You do not have permission!"));
				return true;
			}
			BusinessData.removeBusiness(business);
			sender.sendMessage(cc(prefix + " &aBusiness > Business " + business_id + " was successfully deleted!"));
			return true;
		}
		
		return false;
	}
	
	private String cc(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
}

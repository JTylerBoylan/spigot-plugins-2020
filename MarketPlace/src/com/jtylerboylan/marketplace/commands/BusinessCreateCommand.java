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
import com.jtylerboylan.marketplace.data.EntityData;

public class BusinessCreateCommand implements CommandExecutor {
	
	private static String prefix = Core.getServerConfig().getString("plugin-prefix");
	private static int max_business = Core.getServerConfig().getInt("max-business-per-entity");

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (label.equalsIgnoreCase("createbusiness")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(cc(prefix + " &cDenied > You must be a player to run this command!"));
				return true;
			}
			if (!sender.hasPermission("market.createbusiness")) {
				sender.sendMessage(cc(prefix + " &cDenied > You do not have permission!"));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(cc(prefix + " &cUsage > /createbusiness <business-id>"));
				return true;
			}
			
			String business_id = args[0];
			if (BusinessData.getBusiness(business_id) != null) {
				sender.sendMessage(cc(prefix + " &cError > A business with that id already exists!"));
				return true;
			}
			
			Entity owner = new Entity((Player) sender);
			if (args.length > 1) {
				if (EntityData.getEntity(args[1]) == null) {
					sender.sendMessage(cc(prefix + " &cError > Unknown entity!"));
					return true;
				}
				if (!owner.hasAccess(EntityData.getEntity(args[1]))) {
					sender.sendMessage(cc(prefix + " &cDenied > You do not have access to this entity!"));
					return true;
				}
				owner = EntityData.getEntity(args[1]);
			}
			
			if (Core.getEconomy().getBalance((Player) sender) < Core.getServerConfig().getDouble("create-business-price") && !owner.override()) {
				sender.sendMessage(cc(prefix + " &cDenied > Insufficient funds! It costs &2$" + Core.getServerConfig().getDouble("create-business-price") + " &cto create a business"));
				return true;
			}
			
			if (BusinessData.allBusiness(owner).size() >= max_business && !owner.override()) {
				sender.sendMessage(cc(prefix + " &cDenied > You already have the maximum number of business's!"));
				return true;
			}
			
			Core.getEconomy().withdrawPlayer((Player) sender, Core.getServerConfig().getDouble("create-business-price"));
			Business business = new Business(owner, business_id);
			BusinessData.addBusiness(business);
			sender.sendMessage(cc(prefix + " &aBusiness > Business " + business_id + " was successfully created!"));
			return true;
		}
		
		return false;
	}
	
	private String cc(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
}

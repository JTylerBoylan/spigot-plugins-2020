package com.jtylerboylan.marketplace.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import com.jtylerboylan.marketplace.Business;
import com.jtylerboylan.marketplace.Core;
import com.jtylerboylan.marketplace.Entity;
import com.jtylerboylan.marketplace.data.BusinessData;

public class MarketCreateEvent implements Listener {
	
	private static String prefix = Core.getServerConfig().getString("plugin-prefix");

	@EventHandler
	public void onMarketPlace(SignChangeEvent e) {
		if (ChatColor.stripColor(e.getLine(0)).equals("[Market]") && e.getLine(3) != null) {
			if (BusinessData.getBusiness(e.getLine(3)) == null) {
				e.getPlayer().sendMessage(cc(prefix + " &cError > A business with this id does not exist"));
				e.getBlock().breakNaturally();
				return;
			}
			Business business = BusinessData.getBusiness(e.getLine(3));
			Entity entity = new Entity(e.getPlayer());
			if (!entity.hasAccess(business)) {
				e.getPlayer().sendMessage(cc(prefix + " &cDenied > You do not have permission to this business"));
				e.getBlock().breakNaturally();
				return;
			}
			Sign sign = (Sign) e.getBlock().getState();
			sign.setLine(0, cc("&a[Market]"));
			sign.setLine(1, cc("&8Click to view"));
			sign.setLine(2, cc(business.getName()));
			sign.setLine(3, cc("&b"+business.getID()));
			Bukkit.getScheduler().runTask(Core.getPlugin(), sign::update);
			e.getPlayer().sendMessage(cc(prefix + " &aMarket > Market Sign successfully created!"));
		}
	}
	
	private String cc(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
}

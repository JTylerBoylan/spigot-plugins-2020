package com.jtylerboylan.marketplace.guis;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.jtylerboylan.marketplace.Business;
import com.jtylerboylan.marketplace.Entity;
import com.jtylerboylan.marketplace.Market;

public class BusinessPageGUI extends UserGUI {

	private Business business;
	
	public BusinessPageGUI(Player user, Business business) {
		super(user);
		this.business = business;
	}
	
	public void load() {
		
		Inventory business_page = Bukkit.createInventory(user, 9, cc(business_prefix + business.getName()));
		
		ItemStack info_tile = UserGUI.tile(Material.APPLE, 1, (short) 0, cc(business_prefix + business.getName()), cc("&9ID: &7" + business.getID()), cc("&7Owned by: " + business.getOwner().getName()),
				cc("&7Balance: &a$" + business.getBalance()), cc("&2Click to deposit/withdraw"));
		ItemStack create_market = UserGUI.tile(Material.SEEDS, 1, (short) 0, cc("&aCreate Market"), cc("&2Click to create market"));
		ItemStack markets_tile = UserGUI.tile(Material.WHEAT, 1, (short) 0, cc("&dMarkets"), cc("&5Click to view")); 
		ItemStack edit_tile = UserGUI.tile(Material.COAL, 1, (short) 0, cc("&eEdit Business"), cc("&6Click to edit"));
		
		ItemStack entity_tile = UserGUI.tile(Material.SKULL_ITEM, 1, (short) 3, cc("&6&l" + business.getOwner().getName()), cc("&8&oID: " + business.getOwner().getID())); 
		SkullMeta head_meta = (SkullMeta) entity_tile.getItemMeta();
		
		Player player;
		if (business.getOwner().isCustom())
			player = Bukkit.getPlayer(business.getOwner().getID());
		else
			player = Bukkit.getPlayer(UUID.fromString(business.getOwner().getID()));
		
		head_meta.setOwningPlayer(player);
		entity_tile.setItemMeta(head_meta);
		
		business_page.setItem(0, info_tile);
		business_page.setItem(2, markets_tile);
		business_page.setItem(4, create_market);
		business_page.setItem(6, edit_tile);
		business_page.setItem(8, entity_tile);
		
		user.openInventory(business_page);
		
	}

	public void clickSlot(int slot) {
		switch(slot) {
		case 0:
			UserGUI.openGUI(user, new BusinessBalanceGUI(user, business, false ,false));
			break;
		case 2:
			UserGUI.openGUI(user, new BusinessMarketGUI(user, business, 1));
			break;
		case 4:
			UserGUI.openGUI(user, new MarketCreateGUI(user, business, new Market(business)));
			break;
		case 6:
			UserGUI.openGUI(user, new BusinessEditGUI(user, business));
			break;
		case 8:
			if (business.getOwner().isCustom())
				UserGUI.openGUI(user, new EntityPageGUI(user, business.getOwner()));
			break;
		}
		
	}
	
	public void stringInput(String string) {}

	public boolean hasAccess(Player player) {
		Entity entity = new Entity(player);
		if (entity.hasAccess(business))
			return true;
		return false;
	}
	
	public void itemClicked(ItemStack clicked) {}

}

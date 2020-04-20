package com.jtylerboylan.marketplace.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.jtylerboylan.marketplace.Business;
import com.jtylerboylan.marketplace.Entity;
import com.jtylerboylan.marketplace.data.EntityData;

public class BusinessAddAccessGUI extends UserGUI {

	private Business business;
	
	private List<Player> online_players;
	
	private boolean custom_input = false;
	
	private int page;
	
	
	public BusinessAddAccessGUI(Player user, Business business, int page, boolean custom_input) {
		super(user);
		this.business = business;
		this.page = page;
		this.custom_input = custom_input;
	}

	public void load() {
		if (custom_input) {
			user.closeInventory();
			user.sendMessage(cc(prefix + " &e&lENTER ENTITY ID " + prefix));
			return;
		}
		
		Inventory add_access_page = Bukkit.createInventory(user, 54, cc(business_prefix + business.getName()));
		
		online_players = new ArrayList<Player>();
		for (Player all : Bukkit.getOnlinePlayers()) {
			online_players.add(all);
		}
		for (int slot = 0; slot < 45; slot++) {
			if (slot + (page-1)*45 >= online_players.size()) continue;
			
			Player all = online_players.get(slot + ((page-1)*45));
			
			ItemStack player_head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			
			SkullMeta head_meta = (SkullMeta) player_head.getItemMeta();
			head_meta.setOwningPlayer(all);
			head_meta.setDisplayName(cc("&b&l" + all.getName()));
			
			List<String> lore = new ArrayList<String>();
			if (new Entity(all).hasAccess(business))
				lore.add(cc("&e&lADDED"));
			else
				lore.add(cc("&a&lCLICK TO ADD"));
			
			head_meta.setLore(lore);
			
			player_head.setItemMeta(head_meta);
			
			add_access_page.setItem(slot, player_head);
		}
		
		ItemStack custom_id = UserGUI.tile(Material.SKULL_ITEM, 1, (short) 2, cc("&e&lCUSTOM"), cc("&6&lClick to add a custom entity"));
		
		if (online_players.size() > page*45) {
			ItemStack next_page = UserGUI.tile(Material.PAPER, page+1, (short) 0, cc("&aNext Page"), cc("&2Click to go to next page"));
			add_access_page.setItem(52, next_page);
		}
		if (page > 1) {
			ItemStack previous_page = UserGUI.tile(Material.PAPER, page-1, (short) 0, cc("&ePrevious Page"), cc("&6Click to go to previous page"));
			add_access_page.setItem(51, previous_page);
		}
		
		add_access_page.setItem(45, custom_id);
		add_access_page.setItem(53, back_tile);
		
		user.openInventory(add_access_page);
		
	}

	public void clickSlot(int slot) {
		if (slot < 45) {
			if (slot + 1 + (page-1)*45 <= online_players.size()) {
				Entity permit = new Entity(online_players.get(slot + ((page-1)*45)));
				if (permit.hasAccess(business)) {
					user.sendMessage(cc(prefix + " &cError > User already has access!"));
					return;
				}
				business.permit(permit.getID());
				UserGUI.openGUI(user, new BusinessAddAccessGUI(user, business, page, false));
			}
		}
		if (slot == 45) {
			UserGUI.openGUI(user, new BusinessAddAccessGUI(user,business,page,true));
		}
		if (slot == 51) {
			if (page > 1) {
				UserGUI.openGUI(user, new BusinessAddAccessGUI(user, business, page-1, false));
			}
		}
		if (slot == 52) {
			if (online_players.size() > page*45) {
				UserGUI.openGUI(user, new BusinessAddAccessGUI(user,business,page+1, false));
			}
		}
		if (slot == 53) {
			UserGUI.openGUI(user, new BusinessEditGUI(user,business));
		}
		
	}

	public void stringInput(String string) {
		if (EntityData.getEntity(string) != null) 
			business.permit(EntityData.getEntity(string).getID());
		 else 
			business.permit(new Entity(string).getID());
		user.sendMessage(cc(prefix + " &aBusiness> Entity ID: &e" + string + " &awas given access to business ID: &e" + business.getID()));
		UserGUI.openGUI(user, new BusinessAddAccessGUI(user, business, 1, false));
	}

	public boolean hasAccess(Player player) {
		Entity userEntity = new Entity(player);
		if (userEntity.hasAccess(business))
			return true;
		return false;
	}

}

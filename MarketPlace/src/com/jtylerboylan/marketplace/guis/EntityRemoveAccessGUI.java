package com.jtylerboylan.marketplace.guis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.jtylerboylan.marketplace.Entity;
import com.jtylerboylan.marketplace.data.EntityData;

public class EntityRemoveAccessGUI extends UserGUI {

	private Entity entity;
	
	private List<String> added;
	
	private int page;
	
	public EntityRemoveAccessGUI(Player user, Entity entity, int page) {
		super(user);
		this.entity = entity;
		this.page = page;
	}

	public void load() {
		Inventory remove_access_page = Bukkit.createInventory(user, 54, cc(entity_prefix + entity.getName()));
		
		added = entity.getPermitted();
		
		for (int slot = 0; slot < 45; slot++) {
			if (slot + (page-1)*45 >= added.size()) continue;
			Player player;
			
			Entity permit = EntityData.getEntity(added.get(slot + (page-1)*45));
			
			if (permit != null)
				player = Bukkit.getPlayer(added.get(slot + (page-1)*45));
			else {
				try {
					player = Bukkit.getPlayer(UUID.fromString(added.get(slot + (page-1)*45)));
				} catch (Exception e) {
					entity.unpermit(added.get(slot + (page-1)*45));
					added.remove(slot + (page-1)*45);
					continue;
				}
			}
			
			ItemStack player_head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta head_meta = (SkullMeta) player_head.getItemMeta();
			head_meta.setDisplayName(cc("&e&l[E] &7" + added.get(slot+(page-1)*45)));
			if (player != null) {
				head_meta.setOwningPlayer(player);
				head_meta.setDisplayName(cc("&b&l[P] &7" + player.getName()));
			}
			List<String> lore = new ArrayList<String>();
			lore.add(cc("&eClick to remove"));
			head_meta.setLore(lore);
			player_head.setItemMeta(head_meta);
			remove_access_page.setItem(slot, player_head);
		}
		
		if (added.size() > page*45) {
			ItemStack next_page = UserGUI.tile(Material.PAPER, page+1, (short) 0, cc("&aNext Page"), cc("&2Click to go to next page"));
			remove_access_page.setItem(52, next_page);
		}
		if (page > 1) {
			ItemStack previous_page = UserGUI.tile(Material.PAPER, page-1, (short) 0, cc("&ePrevious Page"), cc("&6Click to go to previous page"));
			remove_access_page.setItem(51, previous_page);
		}
		
		remove_access_page.setItem(53, back_tile);
		
		user.openInventory(remove_access_page);

		
	}

	public void clickSlot(int slot) {
		if (slot < 45) {
			if (slot + 1 + (page-1)*45 <= added.size()) {
				Entity unpermit = new Entity(added.get(slot + ((page-1)*45)));
				if (unpermit.getID().equals(user.getUniqueId().toString())) {
					user.sendMessage(cc(prefix + " &cError > You can not remove access from yourself!"));
					return;
				}
				entity.unpermit(unpermit.getID());
				UserGUI.openGUI(user, new EntityRemoveAccessGUI(user, entity, page));
			}
		}
		if (slot == 51) {
			if (page > 1) {
				UserGUI.openGUI(user, new EntityRemoveAccessGUI(user, entity, page-1));
			}
		}
		if (slot == 52) {
			if (added.size() > page*45) {
				UserGUI.openGUI(user, new EntityRemoveAccessGUI(user,entity,page+1));
			}
		}
		if (slot == 53) {
			UserGUI.openGUI(user, new EntityEditGUI(user,entity));
		}
		
	}

	public void stringInput(String string) {}

	public boolean hasAccess(Player player) {
		Entity userEntity = new Entity(player);
		if (userEntity.hasAccess(entity))
			return true;
		return false;
	}
	
	public void itemClicked(ItemStack clicked) {}

	
	
}

package com.jtylerboylan.marketplace.guis;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.jtylerboylan.marketplace.Core;
import com.jtylerboylan.marketplace.Entity;
import com.jtylerboylan.marketplace.Market;

public class MarketBuySellGUI extends UserGUI {

	private Market market;
	
	private DecimalFormat dec = new DecimalFormat("#.##");
	
	public MarketBuySellGUI(Player user, Market market) {
		super(user);
		this.market = market;
	}

	public void load() {
		
		Inventory buy_sell = Bukkit.createInventory(user, 18, cc(market_prefix + market.getItemID()));
		
		int slot = 0;
		for (int i : Core.getServerConfig().getIntegerList("market-buy-sell-intervals")){
			ItemStack item_buy = market.getItemStack().clone();
			ItemStack item_sell = market.getItemStack().clone();
			
			item_buy.setAmount(i);
			item_sell.setAmount(i);
			
			ItemMeta buy_meta = item_buy.getItemMeta();
			List<String> buy_lore = new ArrayList<String>();
			if (buy_meta.hasLore())
				buy_lore = buy_meta.getLore();
			String buy_price = dec.format(market.getPrice()*i);
			buy_lore.add(cc("&7Buy Price: &2$" + buy_price));
			buy_lore.add(cc("&aClick to buy!"));
			buy_meta.setLore(buy_lore);
			item_buy.setItemMeta(buy_meta);
			
			ItemMeta sell_meta = item_sell.getItemMeta();
			List<String> sell_lore = new ArrayList<String>();
			if (sell_meta.hasLore())
				sell_lore = sell_meta.getLore();
			String sell_price = dec.format(market.getPrice()*i*market.getSellMultiplier());
			sell_lore.add(cc("&7Sell Price: &6$" + sell_price));
			sell_lore.add(cc("&eClick to sell!"));
			sell_meta.setLore(sell_lore);
			item_sell.setItemMeta(sell_meta);
			
			buy_sell.setItem(slot, item_buy);
			buy_sell.setItem(slot+9, item_sell);
			
			slot+=2;
		}
		
		buy_sell.setItem(8, back_tile);
		
		user.openInventory(buy_sell);
	}

	public void clickSlot(int slot) {
		List<Integer> quantities = Core.getServerConfig().getIntegerList("market-buy-sell-intervals");
		if (slot < 8) {
			int quant = quantities.get(slot/2);
			if (market.getStock() == 0) {
				user.sendMessage(cc(prefix + " &cMarket > Market is out of stock!"));
				return;
			}
			if (market.getStock() < quant)
				quant = market.getStock();
			
			Entity owner = new Entity(user);
			
			if (owner.hasAccess(market)) {
				ItemStack give = market.getItemStack().clone();
				give.setAmount(quant);
				user.getInventory().addItem(give);
				user.sendMessage(cc(prefix + " &aMarket > Withdrew " + quant + " of " + market.getItemID()));
				market.removeStock(quant);
				return;
			}
			
			if (Core.getEconomy().getBalance(user) < market.getPrice()*quant) {
				user.sendMessage(cc(prefix + " &cDenied > Insufficient funds!"));
				return;
			}
			Core.getEconomy().withdrawPlayer(user, market.getPrice()*quant);
			market.getOwner().addBalance(market.getPrice()*quant);
			ItemStack give = market.getItemStack().clone();
			give.setAmount(quant);
			user.getInventory().addItem(give);
			market.removeStock(quant);
			user.sendMessage(cc(prefix + " &aMarket > Bought " + quant + " of " + market.getItemID() + " for " + dec.format(market.getPrice()*quant)));
		}
		if (slot == 8) {
			UserGUI.openGUI(user, new MarketShopGUI(user,market.getOwner(), 1));
		}
		if (8 < slot && slot < 17) {
			int quant = quantities.get((slot-9)/2);
			if (market.getStock() == market.maxStock()) {
				user.sendMessage(cc(prefix + " &cMarket > This market is at maximum stock!"));
				return;
			}
			
			if (market.getStock()+quant > market.maxStock())
				quant = market.maxStock()-market.getStock();
			
			if (findItems(user,market.getItemStack(),quant) == null) {
				user.sendMessage(cc(prefix + " &cMarket > You do not have enough of this item!"));
				return;
			}
			
			Entity entity = new Entity(user);
			
			if (entity.hasAccess(market)) {
				user.sendMessage(cc(prefix + " &aMarket > Deposited " + quant + " of " + market.getItemID()));
				market.addStock(quant);
				return;
			}
			
			if (market.getOwner().getBalance() < market.getPrice()*market.getSellMultiplier()*quant) {
				user.sendMessage(cc(prefix + " &cMarket > Owner of this market cannot afford this transaction!"));
				return;
			}
			Core.getEconomy().depositPlayer(user, market.getPrice()*market.getSellMultiplier()*quant);
			market.getOwner().removeBalance(market.getPrice()*market.getSellMultiplier()*quant);
			market.addStock(quant);
			user.sendMessage(cc(prefix + " &aMarket > Sold " + quant + " of " + market.getItemID() + " for " + dec.format(market.getPrice()*market.getSellMultiplier()*quant)));
		}
	}

	public void stringInput(String string) {}

	public boolean hasAccess(Player player) { return player.hasPermission("market.open");}

	private ItemStack findItems(Player player, ItemStack find, int quant) {
		for (ItemStack inv : player.getInventory().getContents()) {
			if (inv != null && inv.getType() == find.getType() && inv.hasItemMeta() == find.hasItemMeta() && inv.getAmount() >= quant) {
				if (inv.hasItemMeta() && inv.getItemMeta().getDisplayName() == find.getItemMeta().getDisplayName() && inv.getItemMeta().getLore() == find.getItemMeta().getLore()) {
					inv.setAmount(inv.getAmount()-quant);
					ItemStack found = inv.clone();
					found.setAmount(quant);
					return found;
				}
				else {
					inv.setAmount(inv.getAmount()-quant);
					ItemStack found = inv.clone();
					found.setAmount(quant);
					return found;
				}
			}
		}
		return null;
	}
	
}

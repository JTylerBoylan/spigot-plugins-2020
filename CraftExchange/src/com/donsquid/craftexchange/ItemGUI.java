package com.donsquid.craftexchange;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemGUI extends GUI {

	private Ticker TICKER;
	
	private boolean advanced_pricing;
	private int amount;
	private float bid, ask;
	
	public ItemGUI(GUI PREVIOUS, Player USER, Ticker TICKER) {
		super(PREVIOUS, USER);
		this.TICKER = TICKER;
		
		this.advanced_pricing = false;
		this.amount = 1;
		this.bid = TICKER.getBid(amount);
		this.ask = TICKER.getAsk(amount);
		
		super.SIZE = 9;
	}
	
	public Ticker getTicker() {
		return TICKER;
	}

	@Override
	public Inventory getInventory() {
		
		super.SIZE = advanced_pricing ? 18 : 9;
		
		if (!advanced_pricing) {
			this.bid = TICKER.getBid(amount);
			this.ask = TICKER.getAsk(amount);
		} else {
			if (bid < A.getPlugin().getConfig().getDouble("lowest-bid"))
				bid = (float) A.getPlugin().getConfig().getDouble("lowest-bid");
			if (ask < A.getPlugin().getConfig().getDouble("lowest-ask"))
				ask = (float) A.getPlugin().getConfig().getDouble("lowest-ask");
		}
		
		Inventory inv = Bukkit.createInventory(USER, SIZE, ChatColor.translateAlternateColorCodes('&', "&2&l" + TICKER.getTicker()));
		
		ItemStack back_tile = GUI.createTile(new ItemStack(Material.COMPASS), 1, "&4&lBACK", "&7Click to go back.");
		inv.setItem(0, back_tile);
		
		ItemStack bid_tile = GUI.createTile(new ItemStack(Material.REDSTONE), amount, "&c&lSELL (" + (bid*amount) + ")", "&7Click to sell " + amount + " " + TICKER.getTicker() +" for " + bid + "/" + TICKER.getTicker());
		inv.setItem(3, bid_tile);
		
		ItemStack ask_tile = GUI.createTile(new ItemStack(Material.SLIME_BALL), amount, "&a&lBUY (" + (ask*amount) + ")", "&7Click to buy " + amount + " " + TICKER.getTicker() +" for " + bid + "/" + TICKER.getTicker());
		inv.setItem(5, ask_tile);
		
		ItemStack ticker = TICKER.getItemStack();
		inv.setItem(4, ticker);
		
		ItemStack adv = GUI.createTile(new ItemStack(Material.BOOK_AND_QUILL), 1, "&bAdvanced Pricing", "&7Click to set price.");
		inv.setItem(8, adv);
		
		if (advanced_pricing) {
			float[] prices = getTickerPrices();
			
			for (int i = 0; i < 9; i++) {
				ItemStack p = GUI.createTile(new ItemStack(Material.COAL_BLOCK), 1, "&bReset", "&7Click to reset to $" + midPrice(amount));
				if (i < 4) {
					p = GUI.createTile(new ItemStack(ask+prices[i] < 0 ? Material.STAINED_GLASS : Material.REDSTONE_BLOCK), 1, "&cSubtract $" + prices[i], "&7Price: &a$" + ask,"&7New Price: &a$" + (ask+prices[i]));
				}
				if (i > 4) {
					p = GUI.createTile(new ItemStack(Material.EMERALD_BLOCK), 1, "&aAdd $" + prices[i-1],"&7Price: &a$" + ask,  "&7New Price: &a$" + (bid+prices[i-1]));
				}
				
				inv.setItem(9+i, p);
			}
			
		}
		
		return inv;
	}

	@Override
	protected void clickSlot(int slot) {
		
		if (slot == 0)
			PREVIOUS.open();
		
		if (slot == 3) {
			if (TICKER.getHighestBidders().size() == 0 && !advanced_pricing) {
				A.sendMessage(USER, "no-buyers");
				return;
			}
			if (bid < A.getPlugin().getConfig().getDouble("lowest-bid")) {
				A.sendMessage(USER, "bid-too-low");
				return;
			}
			TICKER.Sell(USER, bid*amount, amount);
			open();
		}
		
		if (slot == 4) {
			amount *= 2;
			if (amount == 128) {
				amount = 1;
			}
			open();
		}
		
		if (slot == 5) {
			if (TICKER.getLowestAskers().size() == 0 && !advanced_pricing) {
				A.sendMessage(USER, "no-sellers");
				return;
			}
			if (ask < A.getPlugin().getConfig().getDouble("lowest-ask")) {
				A.sendMessage(USER, "ask-too-low");
				return;
			}
			TICKER.Buy(USER, ask*amount, amount);
			open();
		}
		
		if (slot == 8) {
			advanced_pricing = advanced_pricing ? false : true;
			bid = midPrice(amount);
			ask = midPrice(amount);
			open();
		}
		
		if (advanced_pricing && slot > 8) {
			
			int s = slot-9;
			
			if (slot > 13)
				s--;
			
			if (slot == 13) {
				bid = midPrice(amount);
				ask = midPrice(amount);
				open();
				return;
			}
			
			float price = getTickerPrices()[s];
			
			bid += price;
			ask += price;
			
			if (bid < 0)
				bid = 0;
			if (ask < 0)
				ask = 0;
			
			open();
			
		}
		
	}
	
	public float midPrice(int amount) {
		return (TICKER.getAsk(amount) + TICKER.getBid(amount)) / 2;
	}
	
	public float[] getTickerPrices() {
		String prices = A.getPlugin().getConfig().getString("ticker-prices");
		if (prices == null)
			return new float[8];
		String[] price = prices.split(" ");
		float[] p = new float[8];
		for (int i = 0; i < 8; i++) {
			try {
				p[i] = Float.parseFloat(price[i]);
			} catch (NumberFormatException e) {
				p[i] = 0;
			}
		}
		return p;
	}

}

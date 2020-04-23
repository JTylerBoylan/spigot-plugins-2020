package com.donsquid.craftexchange;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ExchangeGUI extends GUI {
	
	private Exchange EXCHANGE;
	
	private ArrayList<Ticker> TICKERS;
	
	private int PAGE, SORT;
	
	public ExchangeGUI(GUI PREVIOUS, Player USER, Exchange EXCHANGE) {
		super(PREVIOUS, USER);
		this.EXCHANGE = EXCHANGE;
		this.PAGE = 0;
		this.SORT = 0;
		this.TICKERS = EXCHANGE.getTickers();
		super.SIZE = A.getFileConfig().getInt("exchange-inventory-size");
	}
	
	public Exchange getExchange() {
		return EXCHANGE;
	}
	
	public int getPage() {
		return PAGE;
	}
	
	public int getSort() {
		return SORT;
	}

	@Override
	public Inventory getInventory() {
		
		Inventory inventory = Bukkit.createInventory(USER, SIZE, ChatColor.translateAlternateColorCodes('&', EXCHANGE.getTitle()));
		
		TICKERS = sort(SORT);
		
		for (int i = 0; i < SIZE-9; i++) {
			
			if (PAGE*SIZE+i >= TICKERS.size())
				continue;
			
			Ticker ticker = TICKERS.get((PAGE*SIZE)+i);
			
			ItemStack tile = GUI.createTile(ticker.getItemStack(), 1, "&2&l" + ticker.getTicker(), "&aBid $" + A.floatFormat(ticker.getBid(1)) + "&7 : &c$" + A.floatFormat(ticker.getAsk(1)) + " Ask", "&7Volume: " + ticker.getVolume());
			
			inventory.setItem(i, tile);
		}
		
		ItemStack last_page = GUI.createTile(new ItemStack(Material.BOOK), PAGE-1, "&b&lLAST PAGE", "&7Click to go to the last page.");
		ItemStack next_page = GUI.createTile(new ItemStack(Material.BOOK), PAGE+1, "&b&lNEXT PAGE", "&7Click to go to the next page.");
		
		if (PAGE != 0)
			inventory.setItem(SIZE-9, last_page);
		if ((PAGE+1)*54 <= TICKERS.size())
			inventory.setItem(SIZE-1, next_page);
		
		ItemStack sort = GUI.createTile(new ItemStack(Material.COMPASS), 1, "&b&lSORT", "&7Click to change sort.");
		inventory.setItem(SIZE-5, sort);
		
		return inventory;
	}

	@Override
	protected void clickSlot(int slot) {
		
		if (slot == SIZE-9 && PAGE != 0) {
			PAGE--;
			open();
		}
		
		if (slot == SIZE-1 && (PAGE+1)*54 <= TICKERS.size()) {
			PAGE++;
			open();
		}
		
		if (slot == SIZE-5) {
			SORT++;
			open();
		}
		
		if (slot >= 0 && slot < SIZE-9 && (PAGE*54 + slot) < TICKERS.size()) {
			
			Ticker ticker = TICKERS.get( PAGE*54 + slot );
			
			ItemGUI tickerGUI = new ItemGUI(this, USER, ticker);
			tickerGUI.open();
			
		}
		
	}
	
	private ArrayList<Ticker> sort(int sort){
		
		ArrayList<Ticker> tickers = EXCHANGE.getTickers();
		
		boolean sorted = false;
		Ticker temp;
		
		while(!sorted) {
			
			sorted = true;
			for (int i = 0; i < tickers.size()-1; i++)
				if (tickers.get(i).getValue(sort) < tickers.get(i+1).getValue(sort)) {
					
					temp = tickers.get(i);
					tickers.set(i, tickers.get(i+1));
					tickers.set(i+1, temp);
					sorted = false;
					
				}
			
		}
		
		return tickers;
		
	}

}

package com.donsquid.craftexchange;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.donsquid.craftexchange.Order.OrderType;

public class FileManager {
	
	private File DATA_FOLDER;
	
	public FileManager(File DATA_FOLDER) {
		this.DATA_FOLDER = DATA_FOLDER;
	}
	
	public File getTickerFile(String TICKER) {
		TICKER = TICKER.toUpperCase();
		return new File(DATA_FOLDER, "tickers/" + TICKER + ".yml");
	}
	
	public File getExchangeFile(String CODE) {
		CODE = CODE.toUpperCase();
		return new File(DATA_FOLDER, "exchanges/" + CODE + ".yml");
	}
	
	public ArrayList<Ticker> loadTickers(){
		
		ArrayList<Ticker> tickers = new ArrayList<Ticker>();
		
		File ticker_folder = new File(DATA_FOLDER, "tickers/");
		
		if(!ticker_folder.exists()) {
			return tickers;
		}
		
		for (File file : ticker_folder.listFiles()) {
			
			String TCKR = file.getName().substring(0, file.getName().length()-4);
			
			FileConfiguration ticker_config = YamlConfiguration.loadConfiguration(file);
			
			ItemStack ITEM = ticker_config.getItemStack("item-stack");
			
			int VOLUME = ticker_config.getInt("volume");
			
			ArrayList<Order> BUY_ORDERS = new ArrayList<Order>();
			ArrayList<Order> SELL_ORDERS = new ArrayList<Order>();
			
			//LOADING IN ORDER FILES
			
			for (String orders : ticker_config.getStringList("order-list")) {
				
				Order order = Order.fromString(orders);
				
				if (order.getType() == OrderType.BUY)
					BUY_ORDERS.add(order);
				else if (order.getType() == OrderType.SELL)
					SELL_ORDERS.add(order);
				
			}
			
			tickers.add(new Ticker(TCKR, ITEM, BUY_ORDERS, SELL_ORDERS, VOLUME));
		}
		
		return tickers;
		
	}
	
	public ArrayList<Exchange> loadExchanges(ArrayList<Ticker> tickers){
		
		ArrayList<Exchange> exchanges = new ArrayList<Exchange>();
		
		File exchange_folder = new File(DATA_FOLDER.getPath(), "exchanges/");
		
		if(!exchange_folder.exists()) {
			return exchanges;
		}
		
		for (File file : exchange_folder.listFiles()) {
			
			String CODE = file.getName().substring(0, file.getName().length()-4);
			
			FileConfiguration exchange_config = YamlConfiguration.loadConfiguration(file);
			
			String TITLE = exchange_config.getString("title");
			
			String PERMISSION = exchange_config.getString("permission");
			
			int VOLUME = exchange_config.getInt("volume");
			
			ArrayList<Ticker> TICKERS = new ArrayList<Ticker>();
			
			for (String file_ticker : exchange_config.getStringList("ticker-list"))
				for (Ticker ticker : tickers) 
					if (ticker.getTicker().equals(file_ticker))
						TICKERS.add(ticker);
			
			exchanges.add(new Exchange(CODE, TITLE, PERMISSION, TICKERS, VOLUME));
		}
		
		return exchanges;
	}
	
	public void save(FileConfiguration fc, File f) {
		try {
			fc.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}

package com.donsquid.craftexchange;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Exchange {

	private String CODE;
	
	private String TITLE;
	
	private String PERMISSION;
	
	private ArrayList<Ticker> TICKERS;
	
	private int VOLUME;
	
	private File FILE;
	
	public Exchange(String CODE) {
		this.CODE = CODE.toUpperCase();
		this.TITLE = CODE;
		this.PERMISSION = "exchange." + CODE.toLowerCase();
		this.TICKERS = new ArrayList<Ticker>();
		this.VOLUME = 0;
		this.FILE = A.getFileManager().getExchangeFile(CODE);
		save();
	}
	
	public Exchange(String CODE, String TITLE, String PERMISSION, ArrayList<Ticker> TICKERS, int VOLUME) {
		this.CODE = CODE.toUpperCase();
		this.TITLE = TITLE;
		this.PERMISSION = PERMISSION;
		this.TICKERS = TICKERS;
		this.VOLUME = VOLUME;
		this.FILE = A.getFileManager().getExchangeFile(CODE);
	}
	
	public String getCode() {
		return CODE;
	}
	
	public String getTitle() {
		return TITLE;
	}
	
	public ArrayList<Ticker> getTickers(){
		return TICKERS;
	}
	
	public String getPermission() {
		return PERMISSION;
	}
	
	public int getVolume() {
		return VOLUME;
	}
	
	public ExchangeGUI getGUI(Player user) {
		return new ExchangeGUI(null,user,this);
	}
	
	public boolean hasTicker(Ticker ticker) {
		for (Ticker t : TICKERS) {
			if (t == ticker)
				return true;
		}
		return false;
	}
	
	public boolean hasTicker(String tckr) {
		for (Ticker t : TICKERS) {
			if (t.getTicker().equals(tckr.toUpperCase()))
				return true;
		}
		return false;
	}
	
	public FileConfiguration getConfig() {
		return YamlConfiguration.loadConfiguration(FILE);
	}
	
	public void save() {
		FileConfiguration exchange_config = getConfig();
		exchange_config.set("title", TITLE);
		
		exchange_config.set("permission", PERMISSION);
		
		exchange_config.set("volume", VOLUME);
		
		ArrayList<String> ticker_list = new ArrayList<String>();
		
		for (Ticker ticker : TICKERS)
			ticker_list.add(ticker.getTicker());
		
		exchange_config.set("ticker-list", ticker_list);
		
		try {
			exchange_config.save(FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addTicker(Ticker ticker) {
		TICKERS.add(ticker);
		save();
	}
	
	public void removeTicker(Ticker ticker) {
		TICKERS.remove(ticker);
		save();
	}
	
	public void setCode(String CODE) {
		this.CODE = CODE;
		save();
	}
	
	public void setTitle(String TITLE) {
		this.TITLE = TITLE;
		save();
	}
	
	public void setTickers(ArrayList<Ticker> TICKERS) {
		this.TICKERS = TICKERS;
		save();
	}
	
	public void setPermission(String PERMISSION) {
		this.PERMISSION = PERMISSION;
		save();
	}
	
	public void addToVolume(int amount) {
		VOLUME += amount;
		save();
	}
	
	public static CommandExecutor getCommandExecutor() {
		
		CommandExecutor executor = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
				
				if (!(sender instanceof Player)) {
					A.sendMessage(sender, "sender-not-player");
					return true;
				}
				
				Player player = (Player) sender;
				
				if (label.equalsIgnoreCase("newexchange")) {
					
					if (!player.hasPermission("craftexchange.newexchange") && !player.hasPermission("craftexchange.*exchange") && !player.hasPermission("craftexchange.*")) {
						A.sendMessage(player, "no-permission");
						return true;
					}
					
					if (args.length == 0) {
						A.sendMessage(player, "newexchange-usage");
						return true;
					}
					
					String code = args[0];
					
					if (A.getExchange(code) != null) {
						A.sendMessage(player, "exchange-already-exists");
						return true;
					}
					
					Exchange exchange = new Exchange(code);
					
					A.addExchange(exchange);
					
					A.sendMessage(player, "newexchange-success");
					
					return true;
				}
				
				if (label.equalsIgnoreCase("deleteexchange")) {
					
					if (!player.hasPermission("craftexchange.deleteexchange") && !player.hasPermission("craftexchange.*exchange") && !player.hasPermission("craftexchange.*")) {
						A.sendMessage(player, "no-permission");
						return true;
					}
					
					if (args.length == 0) {
						A.sendMessage(player, "deleteexchange-usage");
						return true;
					}
					
					String code = args[0];
					
					Exchange exchange = A.getExchange(code);
					
					if (exchange == null) {
						A.sendMessage(player, "exchange-unknown");
						return true;
					}
					
					A.removeExchange(code);
					
					A.sendMessage(player, "deleteexchange-success");
					
					return true;
				}
				
				if (label.equalsIgnoreCase("setexchangetitle")) {
					
					if (!player.hasPermission("craftexchange.setexchangetitle") && !player.hasPermission("craftexchange.*exchange") && !player.hasPermission("craftexchange.*")) {
						A.sendMessage(player, "no-permission");
						return true;
					}
					
					if (args.length < 2) {
						A.sendMessage(player, "setexchangetitle-usage");
						return true;
					}
					
					String code = args[0];
					
					Exchange exchange = A.getExchange(code);
					
					if (exchange == null) {
						A.sendMessage(player, "exchange-unknown");
						return true;
					}
					
					String title = "";
					for (int i = 1; i < args.length; i++)
						title += args[i] + " ";
					
					exchange.setTitle(title);
					
					A.sendMessage(player, "setexchangetitle-success");
					
					return true;
				}
				
				if (label.equalsIgnoreCase("listexchanges")) {
					
					if (!player.hasPermission("craftexchange.listexchanges") && !player.hasPermission("craftexchange.*exchange") && !player.hasPermission("craftexchange.*")) {
						A.sendMessage(player, "no-permission");
					}
					
					int size = A.getExchanges().size();
					
					if (args.length > 0) {
						try {
							if (Integer.parseInt(args[0]) < size)
								size = Integer.parseInt(args[0]);
						} catch (NumberFormatException e) {
							// NOTHING
						}
					}
					
					A.sendMessage(player, "listexchanges-text");
					
					for (int i = 0; i < size; i++) {
						Exchange e = A.getExchanges().get(i);
						player.sendMessage( "- " + e.getCode() + " : " + e.getTitle() + " : " + e.getPermission());
					}
					
					return true;
					
				}
				
				return false;
			}
		};
		
		return executor;
	}
	
}

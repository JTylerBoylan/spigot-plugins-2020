package com.donsquid.craftexchange;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class A extends JavaPlugin {

	private static Plugin plugin;
	
	private static FileManager manager;
	
	private static ArrayList<Ticker> tickers;
	private static ArrayList<Exchange> exchanges;
	
	private static FileConfiguration config;
	
	private static File messages_file = null;
	private static FileConfiguration messages = null;
	
	private static File pending_file = null;
	private static FileConfiguration pending = null;
	
    private static Economy econ = null;
    
    private static final Logger log = Logger.getLogger("Minecraft");
	
	public void onEnable() {
		
		plugin = this;
		
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
		
		config = this.getConfig();
		
		loadMessages();
		
		pending_file = new File(A.getPlugin().getDataFolder(), "pending_orders.yml");
		pending = YamlConfiguration.loadConfiguration(pending_file);
		
		manager = new FileManager(this.getDataFolder());
		
		tickers = manager.loadTickers();
		exchanges = manager.loadExchanges(tickers);
		
		this.getCommand("craftexchange").setExecutor(getCommandExecutor());
		
		this.getCommand("newticker").setExecutor(Ticker.getCommandExecutor());
		this.getCommand("setticker").setExecutor(Ticker.getCommandExecutor());
		this.getCommand("deleteticker").setExecutor(Ticker.getCommandExecutor());
		this.getCommand("listtickers").setExecutor(Ticker.getCommandExecutor());
		
		this.getCommand("newexchange").setExecutor(Exchange.getCommandExecutor());
		this.getCommand("deleteexchange").setExecutor(Exchange.getCommandExecutor());
		this.getCommand("setexchangetitle").setExecutor(Exchange.getCommandExecutor());
		this.getCommand("listexchanges").setExecutor(Exchange.getCommandExecutor());
		
		this.getCommand("myorders").setExecutor(Order.getCommandExecutor());
		this.getCommand("cancelorder").setExecutor(Order.getCommandExecutor());
		
		this.getServer().getPluginManager().registerEvents(GUI.getListener(), this);
		this.getServer().getPluginManager().registerEvents(getListener(), this);
		
	}
	
	public void onDisable() {
		
	}
	
	public static FileConfiguration getFileConfig() {
		return config;
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
	
	public static FileManager getFileManager() {
		return manager;
	}
	
	public static ArrayList<Exchange> getExchanges() {
		return exchanges;
	}
	
	public static ArrayList<Ticker> getTickers() {
		return tickers;
	}
	
	public static Exchange getExchange(String code) {
		code = code.toUpperCase();
		for (Exchange exchange : exchanges) {
			if (exchange.getCode().equals(code))
				return exchange;
		}
		
		return null;
	}
	
	public static Ticker getTicker(String tckr) {
		tckr = tckr.toUpperCase();
		for (Ticker ticker : tickers)
			if (ticker.getTicker().equals(tckr))
				return ticker;
		
		return null;
	}
	
	public static void addTicker(Ticker ticker) {
		tickers.add(ticker);
	}
	
	public static void removeTicker(String tckr) {
		Ticker ticker = getTicker(tckr);
		tickers.remove(ticker);
		manager.getTickerFile(tckr).delete();
		for (Exchange e : getExchanges()) {
			if (e.getTickers().contains(ticker)) {
				e.removeTicker(ticker);
				e.save();
			}
		}
	}
	
	public static void addExchange(Exchange exchange) {
		exchanges.add(exchange);
	}
	
	public static void removeExchange(String code) {
		Exchange exchange = getExchange(code);
		exchanges.remove(exchange);
	}
	
	public static FileConfiguration getMessages() {
		return messages;
	}
	
	public void loadMessages() {
		if (messages_file == null) {
			messages_file = new File(this.getDataFolder(), "messages.yml");
		}
		
		messages = YamlConfiguration.loadConfiguration(messages_file);
		
		Reader defConfigStream;
		try {
			defConfigStream = new InputStreamReader(this.getResource("messages.yml"), "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			messages.setDefaults(defConfig);
		}
		
	}
	
	public static void addPendingOrder(Order order) {
		
		if (pending.getStringList("pending") == null)
			pending.set("pending", new ArrayList<String>());
		
		ArrayList<String> pend = new ArrayList<String>(pending.getStringList("pending"));
		pend.add(order.toString());
		pending.set("pending", pend);
		
		try {
			pending.save(pending_file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendMessage(CommandSender player, String key) {
		
		for (String message : messages.getStringList(key)) {
			
			boolean fixed = false;
			
			while (!fixed) {
				
				fixed = true;
				
				String[] words = message.split(" ");
				
				message = new String();
				for (String word : words) {
					if (word.startsWith("^")) {
						word = messages.getString(word.substring(1));
						fixed = false;
					}
					message += word + " ";
				}
				
			}
			
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
			
		}
	}
	
	public static Listener getListener() {
		
		Listener listener = new Listener() {
			
			@EventHandler
			public void playerJoin(PlayerJoinEvent e) {
				
				String uid = e.getPlayer().getUniqueId().toString();
				
				if (pending.getStringList("pending") == null)
					return;
				
				ArrayList<String> pend = (ArrayList<String>) pending.getStringList("pending");
				
				for (int i = 0; i < pend.size(); i++) {
					
					String o = pend.get(i);
					
					if (o.contains(uid)) {
						
						Order order = Order.fromString(o);
						order.fill(order.getAmount());
					
						pend.remove(o);
						
						i--;
						
					}
					
				}
				
				pending.set("pending", pend);
				
				try {
					pending.save(pending_file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
			
		};
		
		return listener;
		
	}
	
	public static CommandExecutor getCommandExecutor() {
		
		CommandExecutor executor = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
				
				if (!(sender instanceof Player)) {
					sendMessage(sender, "sender-not-player");
					return true;
				}
				
				Player player = (Player) sender;
				
				String flag = "-open";
				String code = "DEFAULT";
				
				if (args.length > 0) {
					if (args[0].startsWith("-")) {
						flag = args[0];
						if (args.length > 1)
							code = args[1];
					} else
						code = args[0];
				}
				
				Exchange exchange = getExchange(code);
				
				if (exchange == null) {
					
					if (code.equalsIgnoreCase("help")) {
						flag = "-help";
						code = "DEFAULT";
					} else {
						sendMessage(player, "exchange-unknown");
						sendMessage(player, "craftexchange-usage");
						return true;
					}
				}
				
				switch (flag) {
				
				case "-open":
					
					if (!player.hasPermission(exchange.getPermission()) && !player.hasPermission("craftexchange.*")) {
						sendMessage(player, "craftexchange-no-permission");
						return true;
					}
					
					exchange.getGUI(player).open();
					
					break;
					
				case "-add":
					
					if (!player.hasPermission(exchange.getPermission() + ".edit") && !player.hasPermission("craftexchange.*")) {
						sendMessage(player, "craftexchange-no-permission-edit");
						return true;
					}
					
					if (args.length < 3) {
						sendMessage(player, "craftexchange-add-usage");
						return true;
					}
					
					String tckr = args[2];
					
					Ticker ticker = A.getTicker(tckr);
					
					if (ticker == null) {
						sendMessage(player, "ticker-unknown");
						sendMessage(player, "craftexchange-add-usage");
						return true;
					}
					
					exchange.addTicker(ticker);
					
					sendMessage(player, "craftexchange-add-success");
					
					return true;
					
				case "-remove":
					
					if (!player.hasPermission(exchange.getPermission() + ".edit") && !player.hasPermission("craftexchange.*")) {
						sendMessage(player, "craftexchange-no-permission-edit");
						return true;
					}
					
					if (args.length < 3) {
						sendMessage(player, "craftexchange-remove-usage");
						return true;
					}
					
					String tckr_ = args[2];
					
					Ticker ticker_ = A.getTicker(tckr_);
					
					if (ticker_ == null) {
						sendMessage(player, "ticker-unknown");
						sendMessage(player, "craftexchange-remove-usage");
						return true;
					}
					
					if (!exchange.hasTicker(ticker_)) {
						sendMessage(player, "ticker-not-in-exchange");
						return true;
					}
					
					exchange.removeTicker(ticker_);
					
					sendMessage(player, "craftexchange-remove-success");
					
					return true;
					
				case "-help":
					A.sendMessage(player, "help-text");
					return true;
					
				default:
					sendMessage(player, "craftexchange-invalid-flag");
					return true;
				}
					
				return false;
			}
		};
		
		return executor;
	}
	
	public static float floatFormat(float f) {
		if (f > Float.MAX_VALUE/100 || f < Float.MIN_VALUE/100)
			return f;
		return ((int)(f *100))/100.00f;
	}
	
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    public static Economy getEconomy() {
    	return econ;
    }
	
}

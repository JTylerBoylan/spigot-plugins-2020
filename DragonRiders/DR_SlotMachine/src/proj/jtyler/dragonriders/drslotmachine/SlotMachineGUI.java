package proj.jtyler.dragonriders.drslotmachine;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SlotMachineGUI {

	private static String gui_name = DR_SlotMachine.get().getConfig().getString("gui.name");
	
	private static final int[] wheel_slots = new int[] {
		12 , 13 , 14 , // Next Item
		21 , 22 , 23 , // Current Item
		30 , 31 , 32   // Previous Item
	};
	
	private static DR_SlotMachine main = null;
	
	private Listener listener = null;
	
	private Inventory gui = null;
	
	private SlotMachine machine = null;
	private SlotMachineWheel wheel = null;
	private Player player = null;
	public SlotMachineGUI(SlotMachine machine, SlotMachineWheel wheel, Player player) {
		this.machine = machine;
		this.wheel = wheel;
		this.player = player;
		main = DR_SlotMachine.get();
		main.getServer().getPluginManager().registerEvents(listener(), main);
	}
	
	private void finish() {
		if (player.getOpenInventory().getTopInventory().equals(gui)) {
			DR_SlotMachine.get().getServer().getScheduler().runTask(main, new Runnable() {
				@Override
				public void run() {
					player.closeInventory();
				}
			});
		}
		HandlerList.unregisterAll(listener());
	}
	
	public Inventory getGUI() {
		if (gui == null) {
			gui = Bukkit.createInventory(player, 45, ChatColor.translateAlternateColorCodes('&', gui_name.replaceAll("%slot_id%", machine.getID())));
			setRow(0,filler(1, "&bSpinning..."));
			setRow(1,filler(1, "&bSpinning..."));
			setRow(2,filler(4, "&c&l<><><>"));
			setRow(3,filler(1, "&bSpinning..."));
			setRow(4,filler(1, "&bSpinning..."));
		}
		return gui;
	}
	
	public void open() {
		player.openInventory(getGUI());
	}
	
	private boolean isWheelSlot(int slot) {
		for (int i : wheel_slots)
			if (i == slot)
				return true;
		return false;
	}
	
	private void setRow(int row, ItemStack item) {
		int start = row*9;
		int end = start+9;
		for (int i = start; i < end; i++)
			if (!isWheelSlot(i))
				getGUI().setItem(i, item);
	}
	
	private void setWheel1Next(SlotItem item) {
		getGUI().setItem(wheel_slots[0], item.toItemStack());
	}
	private void setWheel2Next(SlotItem item) {
		getGUI().setItem(wheel_slots[1], item.toItemStack());
	}
	private void setWheel3Next(SlotItem item) {
		getGUI().setItem(wheel_slots[2], item.toItemStack());
	}
	
	private void setWheel1Current(SlotItem item) {
		getGUI().setItem(wheel_slots[3], item.toItemStack());
	}
	private void setWheel2Current(SlotItem item) {
		getGUI().setItem(wheel_slots[4], item.toItemStack());
	}
	private void setWheel3Current(SlotItem item) {
		getGUI().setItem(wheel_slots[5], item.toItemStack());
	}
	
	private void setWheel1Previous(SlotItem item) {
		getGUI().setItem(wheel_slots[6], item.toItemStack());
	}
	private void setWheel2Previous(SlotItem item) {
		getGUI().setItem(wheel_slots[7], item.toItemStack());
	}
	private void setWheel3Previous(SlotItem item) {
		getGUI().setItem(wheel_slots[8], item.toItemStack());
	}
	
	private int[] runnables = null;
	public void spin() {
		
		setWheel1Next(wheel.peekWheel1Next());
		setWheel2Next(wheel.peekWheel2Next());
		setWheel3Next(wheel.peekWheel3Next());
		
		setWheel1Current(wheel.peekWheel1Current());
		setWheel2Current(wheel.peekWheel2Current());
		setWheel3Current(wheel.peekWheel3Current());
		
		setWheel1Previous(wheel.peekWheel1Previous());
		setWheel2Previous(wheel.peekWheel2Previous());
		setWheel3Previous(wheel.peekWheel3Previous());
		
		runnables = new int[] {
				
			Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
				
				int count = 0;
				
				@Override
				public void run() {
					
					if (count < 16) {
						
						wheel.nextWheel1();
						
						setWheel1Next(wheel.peekWheel1Next());
						setWheel1Current(wheel.peekWheel1Current());
						setWheel1Previous(wheel.peekWheel1Previous());
						
					} else if (count == 16) {
						complete();
					}
					
					player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF, 1, 1);
					
					count++;
				}
				
			}, 20L, 5L),
			
			Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
				
				int count = 0;
				
				@Override
				public void run() {
					
					if (count < 16) {
						
						wheel.nextWheel2();
						
						setWheel2Next(wheel.peekWheel2Next());
						setWheel2Current(wheel.peekWheel2Current());
						setWheel2Previous(wheel.peekWheel2Previous());
						
					} else if (count == 16) {
						complete();
					}
					
					count++;
				}
				
			}, 30L, 5L),
			
			Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
				
				int count = 0;
				
				@Override
				public void run() {
					
					if (count < 16) {
						
						wheel.nextWheel3();
						
						setWheel3Next(wheel.peekWheel3Next());
						setWheel3Current(wheel.peekWheel3Current());
						setWheel3Previous(wheel.peekWheel3Previous());
						
					} else if (count == 16) {
						complete();
					}
					
					count++;
				}
				
			}, 40L, 5L)
				
				
		};
		
	}
	
	private int completed = 0;
	private void complete() {
		completed++;
		if (completed == 3) {
			for (int i : runnables)
				Bukkit.getScheduler().cancelTask(i);
			results();
		}
	}
	
	private void results() {
		if (wheel.peekWheel1Current().matches(wheel.peekWheel2Current()) 
				&& wheel.peekWheel2Current().matches(wheel.peekWheel3Current())
				&& wheel.peekWheel3Current().matches(wheel.peekWheel1Current())) {
			SlotItem natural = wheel.peekWheel1Current();
			if (natural.isWildCard())
				natural = wheel.peekWheel2Current();
			if (natural.isWildCard())
				natural = wheel.peekWheel3Current();
			if (natural.isWildCard()) {
				if (wheel.peekWheel1Current().equals(wheel.peekWheel2Current()))
					natural = wheel.peekWheel1Current();
				else if (wheel.peekWheel2Current().equals(wheel.peekWheel3Current()))
					natural = wheel.peekWheel2Current();
				else if (wheel.peekWheel3Current().equals(wheel.peekWheel1Current()))
					natural = wheel.peekWheel3Current();
				else
					natural = wheel.peekWheel1Current();
			}
			
			float reward = natural.getMultiplier() * machine.getCost();
			
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', DR_SlotMachine.get().getPrefix() + "You won! Reward: $" + reward));
			DR_SlotMachine.getEconomy().depositPlayer(player, reward);
			playWinAnimation();
		} else {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', DR_SlotMachine.get().getPrefix() + "You lost. Reward: $0"));
			playLoseAnimation();
		}
	}
	
	private int animation;
	
	private void playWinAnimation() {
		animation = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {

			int count = 0;
			
			@Override
			public void run() {
				
				if (count < 12) {
					
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
					
					setRow(0,filler(count, "&b&lWINNER!"));
					setRow(1,filler(count+1, "&b&lWINNER!"));
					setRow(2,filler(count+2, "&c&lWINNER!"));
					setRow(3,filler(count+3, "&b&lWINNER!"));
					setRow(4,filler(count+4, "&b&lWINNER!"));
					
				} else {
					completeAnimation();
				}
				
				
				count++;
			}
			
		}, 0L, 5L);
	}
	
	private void playLoseAnimation() {
		
		player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 1);
		
		setRow(0,filler(4, "&c&lLoser."));
		setRow(1,filler(4, "&c&lLoser."));
		setRow(2,filler(4, "&b&lLoser."));
		setRow(3,filler(4, "&c&lLoser."));
		setRow(4,filler(4, "&c&lLoser."));
		
		animation = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {

			int count = 0;
			
			@Override
			public void run() {
				
				if (count < 12) {
					
				} else {
					completeAnimation();
				}
				
				count++;
			}
			
		}, 0L, 5L);
	}
	
	private void completeAnimation() {
		Bukkit.getScheduler().cancelTask(animation);
		finish();
	}
	
	private ItemStack filler(int filler, String name) {
		int type = filler%5;
		Material material = Material.WHITE_STAINED_GLASS_PANE;
		switch (type) {
		case 0:
			material = Material.BLUE_STAINED_GLASS_PANE;
			break;
		case 1:
			material = Material.GREEN_STAINED_GLASS_PANE;
			break;
		case 2:
			material = Material.MAGENTA_STAINED_GLASS_PANE;
			break;
		case 3:
			material = Material.YELLOW_STAINED_GLASS_PANE;
			break;
		case 4:
			material = Material.RED_STAINED_GLASS_PANE;
			break;
		case 5:
			material = Material.ORANGE_STAINED_GLASS_PANE;
			break;
		}
		
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		item.setItemMeta(meta);
		return item;
	}
	
	private Listener listener() {
		if (listener == null)
			listener = new Listener() {
				@EventHandler
				public void click(InventoryClickEvent e) {
					if (e.getInventory().equals(getGUI())) {
						e.setCancelled(true);
					}
				}
				@EventHandler
				public void close(InventoryCloseEvent e) {
					if (e.getInventory().equals(getGUI())) {
						main.getServer().getScheduler().runTaskLater(main, new Runnable() {
							@Override
							public void run() {
								e.getPlayer().openInventory(getGUI());
							}
						}, 2L);
					}
				}
			};
		return listener;
	}
	
	
	
}

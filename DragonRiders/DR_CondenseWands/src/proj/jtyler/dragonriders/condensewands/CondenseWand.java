package proj.jtyler.dragonriders.condensewands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class CondenseWand {
	
	public static ItemStack getCondenseWand(int uses) {
		
		ItemStack wand = new ItemStack(Material.getMaterial(CondenseWands.getInstance().getConfig().getString("condense-wand.material")));
		
		net.minecraft.server.v1_15_R1.ItemStack nmsWand = CraftItemStack.asNMSCopy(wand);
		
		NBTTagCompound wandcompound = nmsWand.hasTag() ? nmsWand.getTag() : new NBTTagCompound();
		
		NBTTagCompound wanddata = new NBTTagCompound();
		wanddata.setInt("uses", uses);
		wanddata.setInt("condensed", 0);
		wanddata.setString("random", UUID.randomUUID().toString());
		
		wandcompound.set("CondenseWand", wanddata);
		
		nmsWand.setTag(wandcompound);
		
		wand = CraftItemStack.asBukkitCopy(nmsWand);
		
		ItemMeta wand_meta = wand.getItemMeta();
		wand_meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', CondenseWands.getInstance().getConfig().getString("condense-wand.display-name")));
		ArrayList<String> lore = new ArrayList<String>();
		for (String s : CondenseWands.getInstance().getConfig().getStringList("condense-wand.lore"))
			lore .add(ChatColor.translateAlternateColorCodes('&', s.replaceAll("%uses%", uses + "").replaceAll("%condensed%", "0")));
		wand_meta.setLore(lore);
		if (CondenseWands.getInstance().getConfig().getBoolean("condense-wand.enchantment"))
			wand_meta.addEnchant(Enchantment.LUCK, 1, true);
		wand_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		wand.setItemMeta(wand_meta);
		return wand;
	}
	
	public static void refresh(ItemStack wand) {
		int uses = getUses(wand);
		int condensed = getCondensed(wand);
		
		ItemMeta wand_meta = wand.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		for (String s : CondenseWands.getInstance().getConfig().getStringList("condense-wand.lore"))
			lore .add(ChatColor.translateAlternateColorCodes('&', s.replaceAll("%uses%", uses + "").replaceAll("%condensed%", condensed + "")));
		wand_meta.setLore(lore);
		
		wand.setItemMeta(wand_meta);
	}
	
	public static boolean isCondenseWand(ItemStack wand) {
		net.minecraft.server.v1_15_R1.ItemStack nmsWand = CraftItemStack.asNMSCopy(wand);
		return (nmsWand.hasTag() && nmsWand.getTag().hasKey("CondenseWand")) ? nmsWand.getTag().getCompound("CondenseWand").getInt("uses") > 0 : false;
	}
	
	public static int getUses(ItemStack wand) {
		net.minecraft.server.v1_15_R1.ItemStack nmsWand = CraftItemStack.asNMSCopy(wand);
		return (nmsWand.hasTag() && nmsWand.getTag().hasKey("CondenseWand")) ? nmsWand.getTag().getCompound("CondenseWand").getInt("uses") : 0;
	}
	
	public static int getCondensed(ItemStack wand) {
		net.minecraft.server.v1_15_R1.ItemStack nmsWand = CraftItemStack.asNMSCopy(wand);
		return (nmsWand.hasTag() && nmsWand.getTag().hasKey("CondenseWand")) ? nmsWand.getTag().getCompound("CondenseWand").getInt("condensed") : 0;
	}
	
	public static ItemStack use(ItemStack wand, int condensed) {
		int uses = getUses(wand);
		int cond = getCondensed(wand);
		net.minecraft.server.v1_15_R1.ItemStack nmsWand = CraftItemStack.asNMSCopy(wand);
		NBTTagCompound wandcompound = nmsWand.getTag();
		NBTTagCompound wanddata = wandcompound.getCompound("CondenseWand");
		wanddata.setInt("uses", uses-1);
		wanddata.setInt("condensed", cond + condensed);
		wandcompound.set("CondenseWand", wanddata);
		nmsWand.setTag(wandcompound);
		wand = CraftItemStack.asBukkitCopy(nmsWand);
		refresh(wand);
		return wand;
	}
	
}

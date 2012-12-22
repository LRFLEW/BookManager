package com.LRFLEW.bukkit.book;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class BookMakeUse {
	
	public static final ItemStack[] cost = new ItemStack[] { 
		new ItemStack(Material.BOOK, 1),
		new ItemStack(Material.INK_SACK, 1, (short)0),
		new ItemStack(Material.FEATHER, 1)
	};
	
	public static boolean useMaterials(Player player, int amount) {
		if (!player.getGameMode().equals(GameMode.CREATIVE)) {
			Inventory inv = player.getInventory();
			if (hasItems(inv, amount, cost)) {
				for (int i=0; i < amount; i++) inv.removeItem(cost);
			} else {
				player.sendMessage("You need " + amount + " book(s), " + amount + " ink sack(s), " +
						"and " + amount + " feather(s)");
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasItems(Inventory inv, int times, ItemStack... items) {
        return (inv.contains(Material.BOOK, times) && inv.contains(Material.INK_SACK, times) && inv.contains(Material.FEATHER, times));
    }
	
}

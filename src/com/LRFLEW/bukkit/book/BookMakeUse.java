package com.LRFLEW.bukkit.book;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftInventory;
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
				player.sendMessage("You need a book, ink sack, " +
						"and a feather");
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasItems(Inventory inv, int times, ItemStack... items) {
		
		for (ItemStack is : items) {
			ItemStack item = is.clone();
			item.setAmount(is.getAmount() * times);
            int toDelete = item.getAmount();
            CraftInventory cinv = (CraftInventory) inv;

            while (true) {
                int first = cinv.first(item, false);

                // Drat! we don't have this type in the inventory
                if (first == -1) {
                    return false;
                } else {
                    ItemStack itemStack = inv.getItem(first);
                    int amount = itemStack.getAmount();

                    if (amount < toDelete) {
                        toDelete -= amount;
                    } else {
                        break;
                    }
                }

                // Bail when done
                if (toDelete <= 0) {
                    break;
                }
            }
		}
		return true;
		
    }
	
}

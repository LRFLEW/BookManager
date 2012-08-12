package com.LRFLEW.bukkit.book;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
	
	private static final ItemStack[] cost = new ItemStack[] { 
		new ItemStack(Material.BOOK, 1),
		new ItemStack(Material.INK_SACK, 1, (short)0),
		new ItemStack(Material.FEATHER, 1)
	};
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		String name = cmd.getName();
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must use command \"" +name + "\" as a player");
			return true;
		}
		Player player = (Player) sender;
		ItemStack is = player.getItemInHand();
		if (is.getType() != Material.WRITTEN_BOOK) return false;
		
		if (name.equals("copybook")) {
			if (!player.getGameMode().equals(GameMode.CREATIVE) && 
					!player.hasPermission("bookmanager.copybook.free")) {
				Inventory inv = player.getInventory();
				if (hasItems(inv, cost)) {
					inv.removeItem(cost);
				} else {
					sender.sendMessage("You need a book, ink sack, " +
							"and a feather to copy a book");
					return true;
				}
			}
			
			int times = 1;
			if (args.length >= 1) {
				try {
					times = Integer.parseInt(args[0]);
					if (times <= 0) return false;
				} catch (NumberFormatException e) {
					return false;
				}
			}
			for (int i=0; i < times; i++) player.getInventory().addItem(is);
			sender.sendMessage("Written Book has been copied " + times + " times");
			return true;
		}
		
		CraftItemStack cis = (CraftItemStack) is;
		NBTTagCompound tc = cis.getHandle().getTag();
		
		if (name.equals("unsign")) {
			if (player.getName() != tc.getString("author")
					&& !player.hasPermission("bookmanager.unsign.other")) {
				sender.sendMessage(
						"You don't have permission to unsign other people's books");
				return true;
			}
			is.setType(Material.BOOK_AND_QUILL);
			tc.remove("author");
			tc.remove("title");
			sender.sendMessage("The book has been successfully unsigned");
		} else if (name.equals("savetxt")) {
			NBTTagList list = tc.getList("pages");
			String[] book = new String[list.size()];
			for (int i=0; i < book.length; i++) {
				book[i] = list.get(i).toString();
			}
			File folder = new File(getDataFolder(), tc.getString("title"));
			while (folder.exists()) folder = new File(folder + "_");
			folder.mkdirs();
			for (int i=0; i < book.length; i++) {
				File page = new File(folder, (i + 1) + ".txt");
				try {
					page.createNewFile();
					FileWriter fw = new FileWriter(page);
					fw.write(book[i]);
					fw.close();
				} catch (IOException e) {
					sender.sendMessage("Unable to create txt files (IOException)");
					return true;
				}
				
			}
			sender.sendMessage("Book successfuly saved to plugin folder");
		} else {
			if (args.length < 1) return false;
			if (name.equals("rnbook")) {
				if (player.getName() != tc.getString("author")
						&& !player.hasPermission("bookmanager.rnbook.other")) {
					sender.sendMessage(
							"You don't have permission to rename other people's books");
					return true;
				}
				tc.setString("title", args[0]);
				sender.sendMessage("The book has been successfully renamed");
			} else if (name.equals("rnauth")) {
				tc.setString("author", args[0]);
				sender.sendMessage("The book's author has been successfully changed");
			}
		}
		
		return true;
	}
	
	public boolean hasItems(Inventory inv, ItemStack... items) {
		
		for (ItemStack item : items) {
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

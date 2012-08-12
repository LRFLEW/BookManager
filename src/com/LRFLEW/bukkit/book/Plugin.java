package com.LRFLEW.bukkit.book;

import net.minecraft.server.NBTTagCompound;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
	
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
	
}

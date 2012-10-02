package com.LRFLEW.bukkit.book;

import net.minecraft.server.NBTTagCompound;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BMPlugin extends JavaPlugin {
	
	VaultHook econ;
	
	@Override
	public void onEnable() {
		econ = new VaultHook();
		getConfig();
	}
	
	@Override
	public void onDisable() {
		econ = null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		String name = cmd.getName();
		
		if (name.equals("deletebook")) {
			if (args.length < 1) return false;
			BookSave.deleteBook(sender, getDataFolder(), args[0]);
			return true;
		} else if (name.equals("listbooks")) {
			BookSave.listBooks(sender, getDataFolder());
			return true;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must use command \"" +name + "\" as a player");
			return true;
		}
		Player player = (Player) sender;
		
		if (name.equals("loadbook")) {
			if (args.length < 1) return false;
			BookSave.loadBook(econ, player, getDataFolder(), args[0]);
			return true;
		}
		
		ItemStack is = player.getItemInHand();
		if (!is.getType().equals(Material.WRITTEN_BOOK)) return false;

        CraftItemStack cis = (CraftItemStack) is;
        NBTTagCompound tc = cis.getHandle().getTag();

        if (name.equals("copybook")) {
            String author = tc.getString("author");
            if(!sender.getName().equals(author) && !sender.hasPermission("bookmanager.copy.other")){
                sender.sendMessage("You don't have permission to copy other people's books");
                return true;
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
			
			if (!player.hasPermission("bookmanager.copybook.free")) {
				if (getConfig().getBoolean("copy-mat"))
					if (BookMakeUse.useMaterials(player, times)) return true;
				double d = getConfig().getDouble("copy-cost");
				if (d > 0) econ.spendMoney(player, d);
			}

			for (int i=0; i < times; i++) player.getInventory().addItem(is.clone());
			sender.sendMessage("Written Book has been copied " + times + " times");
			return true;
		}

		if (name.equals("unsign")) {
			if (!player.getName().equals(tc.getString("author"))
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
				if (!player.getName().equals(tc.getString("author"))
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
				
			} else if (name.equals("savebook")) {
				BookSave.saveBook(tc, getDataFolder(), sender, args[0]);
				return true;
			}
		}
		
		return true;
	}
	
}

package com.LRFLEW.bukkit.book;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;

public class BookSave {
	
	public static void saveBook (NBTTagCompound tc, File dir,
			CommandSender sender, String name) {
		try {
			File folder = new File(dir, name);
			if (folder.exists()) {
				sender.sendMessage("There is already a save by that name, sorry");
				return;
			}
			folder.mkdirs();
			
			YamlConfiguration yc = new YamlConfiguration();
			yc.set("title", tc.getString("title"));
			yc.set("author", tc.getString("author"));
			yc.set("available", false);
			yc.set("mat", true);
			yc.set("cost", 0);
			yc.save(new File(folder, "conf.yml"));
			
			NBTTagList pages = tc.getList("pages");
			String[] book = new String[pages.size()];
			for (int i=0; i < book.length; i++) {
				book[i] = pages.get(i).toString();
			}
			for (int i=0; i < book.length; i++) {
				File page = new File(folder, (i + 1) + ".txt");
				page.createNewFile();
				FileWriter fw = new FileWriter(page);
				fw.write(book[i]);
				fw.close();
			}
			sender.sendMessage("Book successfuly saved");
			
		} catch (IOException e) {
			sender.sendMessage("Unable to save the book (IOException)");
		}
	}
	
	public static void deleteBook (CommandSender sender, File dir, String name) {
		File folder = new File(dir, name);
		if (!folder.exists()) {
			sender.sendMessage("There is no saved book by that name");
			return;
		}
		
		deleteFolder(folder);
		sender.sendMessage("Book deleted");
	}
	
	public static void deleteFolder (File folder) {
		if (folder.isDirectory()) for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				deleteFolder(f);
			} else {
				f.delete();
			}
		}
		folder.delete();
	}
	
	public static void listBooks (CommandSender sender, File dir) {
		StringBuilder builder = new StringBuilder("Books: ");
		YamlConfiguration yc;
		for (File f : dir.listFiles()) {
			if (!f.isDirectory() || !new File(f, "1.txt").exists()) break;
			yc = YamlConfiguration.loadConfiguration(new File(f, "conf.yml"));
			if (!sender.hasPermission("bookmanager.loadbook.all")) {
				if (!yc.getBoolean("available", false)) break;
			}
			builder.append(f.getName());
			if (!sender.hasPermission("bookmanager.loadbook.free")) {
				if (yc.getBoolean("free", false)) builder.append(" (free)");
			}
			builder.append(", ");
		}
		sender.sendMessage(builder.toString());
	}
	
	public static void loadBook (VaultHook econ, Player player, File dir, String name) {
		
		File folder = new File(dir, name);
		if (!folder.exists()) {
			player.sendMessage("There is no saved book by that name");
			return;
		}
		
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(
				new File(folder, "conf.yml"));
		if (!yc.getBoolean("available", false) &&
				!player.hasPermission("bookmanager.loadtxt.all")) {
			player.sendMessage("You cannot access that book, sorry :(");
			return;
		}
		if (!player.hasPermission("bookmanager.loadtxt.free")) {
			if (yc.getBoolean("mat"))
				if (BookMakeUse.useMaterials(player, 1)) return;
			double d = yc.getDouble("cost");
			if (d > 0) econ.spendMoney(player, d);
		}
		
		NBTTagCompound tc = new NBTTagCompound();
		
		tc.setString("title", yc.getString("title", "Titleless"));
		tc.setString("author", yc.getString("author", "Herobrine"));
		
		NBTTagList pages = new NBTTagList();
		File page;
		for (int i=1; (page = new File(folder, i + ".txt")).exists(); i++) try {
			int len;
			char[] chr = new char[1024];
			final StringBuilder builder = new StringBuilder();
			final FileReader reader = new FileReader(page);
			try {
				while ((len = reader.read(chr)) > 0) {
					builder.append(chr, 0, len);
				}
			} finally {
				reader.close();
			}
			pages.add(new NBTTagString(null, builder.toString()));
		} catch (IOException e) {
			player.sendMessage("Unable to load the book (IOException)");
			return;
		}
		tc.set("pages", pages);
		CraftItemStack is = new CraftItemStack(Material.WRITTEN_BOOK, 1);
		is.getHandle().setTag(tc);
		player.getInventory().addItem(is);
		
	}
}

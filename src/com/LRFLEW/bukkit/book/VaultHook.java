package com.LRFLEW.bukkit.book;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
	public final Economy econ;
	
	public VaultHook() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            econ = null;
            return;
        }
        RegisteredServiceProvider<Economy> rsp = 
        		Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
        	econ = null;
            return;
        }
        econ = rsp.getProvider();
	}
	
	public boolean spendMoney(Player player, double amount) {
		if (econ == null) return false;
		if (!econ.has(player.getName(), amount)) {
			player.sendMessage("You need " + econ.format(amount) + " to do that");
			return true;
		}
		econ.withdrawPlayer(player.getName(), amount);
		return false;
	}
	
	public String formatList(double amount, boolean mat) {
		StringBuilder builder = new StringBuilder();
		boolean close = false;
		if (econ != null && amount != 0) {
			builder.append("(");
			builder.append(amount);
			close = true;
		}
		if (mat) {
			if (close) builder.append(", + ");
			else builder.append("(");
			builder.append("materials");
			close = true;
		}
		if (close) builder.append(")");
		
		return builder.toString();
	}
	
}

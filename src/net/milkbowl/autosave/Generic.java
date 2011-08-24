/**
 * 
 * Copyright 2011 Morgan Humes
 * 
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ or send
 * a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View,
 * California, 94041, USA.
 * 
 */

package net.milkbowl.autosave;

import java.util.List;

import org.bukkit.ChatColor;

public class Generic {
    public static boolean stringArrayContains(String base, String[] comparesWith) {

        for (int i = 0; i < comparesWith.length; i++) {
            try {
                if (base.compareTo(comparesWith[i]) == 0) {
                    return true;
                }
            } catch (NullPointerException npe) {
                return false;
            }
        }

        return false;
    }
    
	public static String join(String glue, List<?> s) {
		try {
			if(s == null) {
				return "";
			}
			
			int k = s.size();
			if (k == 0) {
				return null;
			}
			StringBuilder out = new StringBuilder();
			out.append(s.get(0).toString());
			for (int x = 1; x < k; ++x) {
				out.append(glue).append(s.get(x).toString());
			}
			return out.toString();
		} catch (NullPointerException npe) {
			return "";
		}
	}
	
	// Parse Colors
	public static String parseColor(String message) {
		message = message.replace("&0", ChatColor.BLACK + "");
		message = message.replace("&1", ChatColor.DARK_BLUE + "");
		message = message.replace("&2", ChatColor.DARK_GREEN + "");
		message = message.replace("&3", ChatColor.DARK_AQUA + "");
		message = message.replace("&4", ChatColor.DARK_RED + "");
		message = message.replace("&5", ChatColor.DARK_PURPLE + "");
		message = message.replace("&6", ChatColor.GOLD + "");
		message = message.replace("&7", ChatColor.GRAY + "");
		message = message.replace("&8", ChatColor.DARK_GRAY + "");
		message = message.replace("&9", ChatColor.BLUE + "");
		message = message.replace("&a", ChatColor.GREEN + "");
		message = message.replace("&b", ChatColor.AQUA + "");
		message = message.replace("&c", ChatColor.RED + "");
		message = message.replace("&d", ChatColor.LIGHT_PURPLE + "");
		message = message.replace("&e", ChatColor.YELLOW + "");
		message = message.replace("&f", ChatColor.WHITE + "");
		return message;
	}
}

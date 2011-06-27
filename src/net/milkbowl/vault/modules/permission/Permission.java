/**
 * Copyright 2011 Morgan Humes
 *
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ or send
 * a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View,
 * California, 94041, USA.
 *
 */

package net.milkbowl.vault.modules.permission;

import java.util.logging.Logger;

import org.bukkit.entity.Player;

public interface Permission {
    
    public static final Logger log = Logger.getLogger("Minecraft");

    public String getName();
    public boolean isEnabled();
    public boolean hasPermission(Player player, String permission);
    public boolean inGroup(String worldName, String playerName, String groupName);
    public int getInfoInt(String world, String playerName, String node);

}

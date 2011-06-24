package net.milkbowl.vault.v0.modules.permission;

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

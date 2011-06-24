package net.milkbowl.vault.v0.modules.permission.plugins;

import net.milkbowl.vault.v0.modules.permission.Permission;

import org.bukkit.entity.Player;


public class Permission_None implements Permission {
    private String name = "Local Fallback Permissions";

    @Override
    public boolean isEnabled() {
        // This method is essentially static, it is always enabled if LS is!
        return true;
    }

    @Override
    public boolean hasPermission(Player player, String permission) {
        // Allow OPs to everything
        if(player.isOp()) {
            return true;
        }
        
        // Allow everyone user & manager
        if(permission.startsWith("localshops.user") || permission.startsWith("localshops.manager")) {
            return true;
        }
        
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    /* 
     * @see com.milkbukkit.localshops.modules.permission.Permission#inGroup(java.lang.String, java.lang.String, java.lang.String)
     * Users can never be in groups without a permissions plugin - always resolves false if checked.
     */
    @Override
    public boolean inGroup(String worldName, String playerName, String groupName) {
        return false;
    }

    /* 
     *
     */
    @Override
    public int getInfoInt(String world, String playerName, String node) {
        return -1;
    }

}

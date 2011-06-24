package net.milkbowl.vault.v0.modules.permission;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Logger;

import net.milkbowl.vault.v0.modules.permission.plugins.Permission_None;
import net.milkbowl.vault.v0.modules.permission.plugins.Permission_Permissions;
import net.milkbowl.vault.v0.modules.permission.plugins.Permission_PermissionsEx;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class PermissionManager {
    
    private JavaPlugin plugin = null;
    private TreeMap<Integer,Permission> perms = new TreeMap<Integer,Permission>();
    private Permission activePermission = null;
    private static final Logger log = Logger.getLogger("Minecraft");

    public PermissionManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public boolean load() {
        
        // Try to load PermissionsEx
        if(packageExists(new String[] { "ru.tehkode.permissions.bukkit.PermissionsEx" })) {
            Permission ePerms = new Permission_PermissionsEx(plugin);
            perms.put(8, ePerms);
            log.info(String.format("[%s][Permission] PermissionsEx found: %s", plugin.getDescription().getName(), ePerms.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Permission] PermissionsEx not found.", plugin.getDescription().getName()));
        }

        // Try to load Permissions (Phoenix)
        if (packageExists(new String[] { "com.nijikokun.bukkit.Permissions.Permissions" })) {
            Permission nPerms = new Permission_Permissions(plugin);
            perms.put(9, nPerms);
            log.info(String.format("[%s][Permission] Permissions (Phoenix) found: %s", plugin.getDescription().getName(), nPerms.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Permission] Permissions (Phoenix) not found.", plugin.getDescription().getName()));
        }
        
        // Try to load Local Fallback Permissions (aka None)
        {
            Permission lPerms = new Permission_None();
            perms.put(10, lPerms);
            log.info(String.format("[%s][Permission] Local Fallback Permissions found: %s", plugin.getDescription().getName(), lPerms.isEnabled() ? "Loaded" : "Waiting"));
        }
        
        if(perms.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    private boolean packageExists(String[] packages) {
        try {
            for (String pkg : packages) {
                Class.forName(pkg);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private Permission getPermission() {
        if(activePermission == null) {
            Iterator<Permission> it = perms.values().iterator();
            while(it.hasNext()) {
                Permission p = it.next();
                if(p.isEnabled()) {
                    return p;
                }
            }
            return null;
        } else {
            return activePermission;
        }
    }
    
    public boolean hasPermission(Player player, String permission) {
        boolean rVal = getPermission().hasPermission(player, permission);
        return rVal;
    }
    
    public boolean inGroup(String worldName, String playerName, String groupName) {
        boolean rVal = getPermission().inGroup(worldName, playerName, groupName);
        return rVal;
    }
    
    public int getInfoIntLow(String world, String playerName, String node) {
        int rVal = getPermission().getInfoInt(world, playerName, node);
        return rVal;
    }

}
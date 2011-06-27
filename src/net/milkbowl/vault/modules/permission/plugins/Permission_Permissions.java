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

package net.milkbowl.vault.modules.permission.plugins;

import net.milkbowl.vault.modules.permission.Permission;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.nijikokun.bukkit.Permissions.Permissions;

public class Permission_Permissions implements Permission {
    private String name = "Permissions (Phoenix)";
    private Plugin plugin = null;
    private PluginManager pluginManager = null;
    private Permissions permission = null;
    private PermissionServerListener permissionServerListener = null;

    public Permission_Permissions(Plugin plugin) {
        this.plugin = plugin;
        pluginManager = this.plugin.getServer().getPluginManager();

        permissionServerListener = new PermissionServerListener(this);

        this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
        this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if (permission == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("Permissions");
            if (perms != null) {
                if (perms.isEnabled()) {
                    permission = (Permissions) perms;
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        if(permission == null) {
            return false;
        } else {
            return permission.isEnabled();
        }
    }

    @Override
    public boolean hasPermission(Player player, String permission) {
        return this.permission.getHandler().has(player, permission);
    }

    @Override
    public boolean inGroup(String worldName, String playerName, String groupName) {
        return this.permission.getHandler().inGroup(worldName, playerName, groupName);
    }

    private class PermissionServerListener extends ServerListener {
        Permission_Permissions permission = null;

        public PermissionServerListener(Permission_Permissions permission) {
            this.permission = permission;
        }

        public void onPluginEnable(PluginEnableEvent event) {
            if (permission.permission == null) {
                Plugin perms = plugin.getServer().getPluginManager().getPlugin("Permissions");

                if (perms != null) {
                    if (perms.isEnabled()) {
                        permission.permission = (Permissions) perms;
                        log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), permission.name));
                    }
                }
            }
        }

        public void onPluginDisable(PluginDisableEvent event) {
            if (permission.permission != null) {
                if (event.getPlugin().getDescription().getName().equals("Permissions")) {
                    permission.permission = null;
                    log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), permission.name));
                }
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see com.milkbukkit.localshops.modules.permission.Permission#numChestsAllowed(java.util.List, java.lang.String, java.lang.String)
     */
    @Override
    public int getInfoInt(String world, String playerName, String node) {
        return this.permission.getHandler().getPermissionInteger(world, playerName, node);
    }


}

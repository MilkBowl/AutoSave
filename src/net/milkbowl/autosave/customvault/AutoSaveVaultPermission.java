/**
 * Copyright 2011 Morgan Humes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.milkbowl.autosave.customvault;

import net.milkbowl.vault.modules.permission.Permission;

import org.bukkit.entity.Player;


public class AutoSaveVaultPermission implements Permission {
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

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

package net.milkbowl.autosave;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class AutoSavePlayerListener extends PlayerListener {

	private AutoSave plugin = null;
	private int players = 0;
	
	public AutoSavePlayerListener(AutoSave plugin, int playerCount) {
		this.plugin = plugin;
		this.players = playerCount;
		
		if(players > 0) {
			plugin.startSaveThread();
		}
	}
	
	public void onPlayerJoin(PlayerJoinEvent event) {
		players++;
		
		if(players == 1) {
			plugin.startSaveThread();
		}
	}
	
	public void onPlayerQuit(PlayerQuitEvent event) {
		players--;
		
		if(players == 0) {
			plugin.stopSaveThread();
	                plugin.performSave();			
		}
	}
	
	public void onPlayerKick(PlayerKickEvent event) {
	    players--;
	    
	    if(players == 0) {
	        plugin.stopSaveThread();
	        plugin.performSave();
	    }
	}
}

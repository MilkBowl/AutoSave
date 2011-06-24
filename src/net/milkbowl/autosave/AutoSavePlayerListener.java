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

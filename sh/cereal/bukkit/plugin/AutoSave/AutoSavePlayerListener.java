package sh.cereal.bukkit.plugin.AutoSave;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class AutoSavePlayerListener extends PlayerListener {

	private AutoSave plugin = null;
	private int players = 0;
	
	public AutoSavePlayerListener(AutoSave plugin) {
		this.plugin = plugin;
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
		}
	}
}

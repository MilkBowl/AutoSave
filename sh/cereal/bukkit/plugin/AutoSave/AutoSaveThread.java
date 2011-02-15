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

package sh.cereal.bukkit.plugin.AutoSave;

import java.util.logging.Logger;

import org.bukkit.ChatColor;

public class AutoSaveThread extends Thread {
	
	protected final Logger log = Logger.getLogger("Minecraft");
	private boolean run = true;
	private AutoSave plugin = null;
	private AutoSaveConfig config = null;
	
	// Constructor to define number of seconds to sleep
	AutoSaveThread(AutoSave plugin, AutoSaveConfig config) {
		this.plugin = plugin;
		this.config = config;
	}
	
	// Allows for the thread to naturally exit if value is false
	public void setRun(boolean run) {
		this.run = run;
	}
	
	// The code to run...weee
    public void run() {
    	if(config == null) {
    		return;
    	}
    	
    	log.info(String.format("[%s] AutoSaveThread Started: Interval is %s seconds", plugin.getDescription().getName(), config.interval));
    	while(run) {
    		// Do our Sleep stuff!
			for (int i = 0; i < config.interval; i++) {
				try {
					if(!run) {
						return;
					}
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.info("Could not sleep!");
				}
			}
			
			// Save the world
			plugin.save();
			if(config.broadcast) {
				plugin.getServer().broadcastMessage(String.format("%s%s", ChatColor.BLUE, config.announceMessage));
			}
		}
    }

}

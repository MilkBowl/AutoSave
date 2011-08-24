/**
 * 
 * Copyright 2011 Morgan Humes
 * 
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ or send
 * a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View,
 * California, 94041, USA.
 * 
 */

package net.milkbowl.autosave;

import java.util.Date;
import java.util.logging.Logger;

public class AutoSaveThread extends Thread {

    protected final Logger log = Logger.getLogger("Minecraft");
    private boolean run = true;
    private boolean saveInProgress = false;
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
        if (config == null) {
            return;
        }

        log.info(String.format("[%s] AutoSaveThread Started: Interval is %d seconds, Warn Times are %s", plugin.getDescription().getName(), config.varInterval, Generic.join(",", config.varWarnTimes)));
        while (run) {
            // Do our Sleep stuff!
            for (int i = 0; i < config.varInterval; i++) {
                try {
                    if (!run) {
                        if (config.varDebug) {
                            log.info(String.format("[%s] Graceful quit of AutoSaveThread", plugin.getDescription().getName()));
                        }
                        return;
                    }
                    boolean warn = false;
                    for (int w : config.varWarnTimes) {
                        if (w != 0 && w + i == config.varInterval) {
                            warn = true;
                        }
                    }

                    if (warn) {
                        // Perform warning
                        if (config.varDebug) {
                            log.info(String.format("[%s] Warning Time Reached: %d seconds to go.", plugin.getDescription().getName(), config.varInterval - i));
                        }
                        plugin.getServer().broadcastMessage(config.messageWarning);
                        log.info(String.format("[%s] %s", plugin.getDescription().getName(), config.messageWarning));
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.info("Could not sleep!");
                }
            }

            switch(config.varMode) {
            	case ASYNCHRONOUS:
					plugin.getServer().getScheduler()
							.scheduleAsyncDelayedTask(plugin, new Runnable() {
	
								public void run() {
									plugin.performSave();
									plugin.lastSave = new Date();
								}
							});
            		break;
            	case SYNCHRONOUS:
					plugin.getServer().getScheduler()
							.scheduleSyncDelayedTask(plugin, new Runnable() {
								public void run() {
									plugin.performSave();
									plugin.lastSave = new Date();
								}
							});
            		break;
            	default:
            		log.warning(String.format("[%s] Invalid configuration mode!", plugin.getDescription().getName()));
            		break;
            }
        }
    }

}

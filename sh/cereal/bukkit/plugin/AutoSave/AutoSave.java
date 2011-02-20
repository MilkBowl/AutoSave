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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class AutoSave extends JavaPlugin {
	protected final Logger log = Logger.getLogger("Minecraft");
	public static PermissionHandler PERMISSIONS = null;
	private static final String CONFIG_FILE_NAME = "plugins/AutoSave/config.properties";
	private PluginDescriptionFile pdfFile = null;
	private AutoSaveThread saveThread = null;
	private AutoSaveConfig config = new AutoSaveConfig();

	@Override
	public void onDisable() {
		// Stop thread
		stopSaveThread();
		
		// Write Config File
		writeConfigFile();
		
		log.info(String.format("[%s] Version %s is disabled!", pdfFile.getName(), pdfFile.getVersion()));
	}

	@Override
	public void onEnable() {
		// Get Plugin Info
		pdfFile = this.getDescription();
		
		// Notify on logger load
		log.info(String.format("[%s] Version %s is enabled!", pdfFile.getName(), pdfFile.getVersion()));
		
		// Ensure our folder exists...
		File dir = new File("plugins/AutoSave");
		dir.mkdir();
		
		// Load configuration 
		loadConfigFile();
		
		// Start our thread
		startSaveThread();
	}
	
	public void obtainPermissions() {
		// Test if Permissions exists
		if (config.permissions) {
			Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
			if (PERMISSIONS == null) {
				if (test != null) {
					PERMISSIONS = ((Permissions) test).getHandler();
					log.info(String.format("[%s] %s", pdfFile.getName(), "Permission system acquired."));
				} else {
					log.info(String.format("[%s] %s", pdfFile.getName(), "Permission system not enabled. Disabling plugin."));
					this.getServer().getPluginManager().disablePlugin(this);
					return;
				}
			}
		}
	}
	
	public void writeConfigFile() {
		// Write properties file
		log.info(String.format("[%s] Saving config file", pdfFile.getName()));
		Properties props = new Properties();
		props.setProperty("broadcast.message", config.broadcastMessage);
		props.setProperty("broadcast.enable", String.valueOf(config.broadcast));
		props.setProperty("interval", String.valueOf(config.interval));		
		props.setProperty("permissions", String.valueOf(config.permissions));
		props.setProperty("command.insufficentpermissions", config.commandInsufficientPermissions);
		props.setProperty("command.save", config.commandSave);
		props.setProperty("command.starting", config.commandStarting);
		props.setProperty("command.statusfail", config.commandStatusFail);
		props.setProperty("command.statusoff", config.commandStatusOff);
		props.setProperty("command.statussuccess", config.commandStatusSuccess);
		props.setProperty("command.stopping", config.commandStopping);
		props.setProperty("command.intervalnotanumber", config.commandIntervalNotANnumber);
		props.setProperty("command.intervalchangesuccess", config.commandIntervalChangeSuccess);
		props.setProperty("command.intervallookup", config.commandIntervalLookup);
		props.setProperty("command.broadcastchangesuccess", config.commandBroadcastChangeSuccess);
		props.setProperty("command.broadcastlookup", config.commandBroadcastLookup);
		props.setProperty("command.on", config.commandOn);
		props.setProperty("command.off", config.commandOff);
		props.setProperty("command.broadcastnotvalid", config.commandBroadcastNotValid);
		props.setProperty("command.version", config.commandVersion);
		
		try {
			props.storeToXML(new FileOutputStream(CONFIG_FILE_NAME), null);
		} catch (FileNotFoundException e) {
			// Shouldn't happen...report and continue
			log.info(String.format("[%s] FileNotFoundException while saving config file", pdfFile.getName()));
		} catch (IOException e) {
			// Report and continue
			log.info(String.format("[%s] IOException while saving config file", pdfFile.getName()));
		}		
	}
	
	public void loadConfigFile() {
		log.info(String.format("[%s] Loading config file", pdfFile.getName()));
		File confFile = new File(CONFIG_FILE_NAME);
		if(!confFile.exists()) {
			writeConfigFile();
		}
		
		Properties props = new Properties();
		try {
			props.loadFromXML(new FileInputStream(confFile));
		} catch(FileNotFoundException e) {
			// Hmmm, shouldnt happen...
			log.info(String.format("[%s] FileNotFoundException while loading config file", pdfFile.getName()));
		} catch(InvalidPropertiesFormatException e) {
			// Report and continue
			log.info(String.format("[%s] InvalidPropertieFormatException while loading config file", pdfFile.getName()));
		} catch(IOException e) {
			// Report and continue
			log.info(String.format("[%s] IOException while loading config file", pdfFile.getName()));
		}
		config.interval = Integer.parseInt(props.getProperty("interval", String.valueOf(config.interval)));
		config.broadcastMessage = props.getProperty("announce.message", config.broadcastMessage);
		config.broadcast = Boolean.parseBoolean(props.getProperty("broadcast.enable", String.valueOf(config.broadcast)));
		config.permissions = Boolean.parseBoolean(props.getProperty("permissions", String.valueOf(config.permissions)));
		config.commandInsufficientPermissions = props.getProperty("command.insufficentpermissions", config.commandInsufficientPermissions);
		config.commandSave = props.getProperty("command.save", config.commandSave); 
		config.commandStarting = props.getProperty("command.starting", config.commandStarting);
		config.commandStatusFail = props.getProperty("command.statusfail", config.commandStatusFail);
		config.commandStatusOff = props.getProperty("command.statusoff", config.commandStatusOff);
		config.commandStatusSuccess = props.getProperty("command.statussuccess", config.commandStatusSuccess);
		config.commandStopping = props.getProperty("command.stopping", config.commandStopping);
		config.commandIntervalNotANnumber = props.getProperty("command.intervalnotanumber", config.commandIntervalNotANnumber);
		config.commandIntervalChangeSuccess = props.getProperty("command.intervalchangesuccess", config.commandIntervalChangeSuccess);
		config.commandIntervalLookup = props.getProperty("command.intervallookup", config.commandIntervalLookup);
		config.commandBroadcastChangeSuccess = props.getProperty("command.broadcastchangesuccess", config.commandBroadcastChangeSuccess);
		config.commandBroadcastLookup = props.getProperty("command.broadcastlookup", config.commandBroadcastLookup);
		config.commandOn = props.getProperty("command.on", config.commandOn);
		config.commandOff = props.getProperty("command.off", config.commandOff);
		config.commandBroadcastNotValid = props.getProperty("command.broadcastnotvalid", config.commandBroadcastNotValid);
		config.commandVersion = props.getProperty("command.version", config.commandVersion);
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
    	if(!(sender instanceof Player)) {
        	return false;
        }
        
    	String commandName = command.getName().toLowerCase();       
        Player player = (Player) sender;

        if (commandName.equals("save")) {
        	if(args.length == 0) {
        		// Check Permissions
				if (config.permissions) {
					obtainPermissions();
					if (!PERMISSIONS.has(player, "autosave.save")) {
						// Permission check failed!
						sender.sendMessage(String.format("%s%s", ChatColor.RED, config.commandInsufficientPermissions));
						return false;
					}
				}
				
				// Perform save
				sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.commandSave));
				return save();
        	} else if(args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
				// Check Permissions
        		if (config.permissions) {
					obtainPermissions();
					if (!PERMISSIONS.has(player, "autosave.toggle")) {
						// Permission check failed!
						sender.sendMessage(String.format("%s%s", ChatColor.RED, config.commandInsufficientPermissions));
						return false;
					}
				}
				
				// Start thread
				if(saveThread == null) {
					sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.commandStarting));
					return startSaveThread();
				} else { // Stop thread
					sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.commandStopping));
					return stopSaveThread();
				}       		
        	} else if(args.length == 1 && args[0].equalsIgnoreCase("status")) {
				// Check Permissions
        		if (config.permissions) {
					obtainPermissions();
					if (!PERMISSIONS.has(player, "autosave.status")) {
						// Permission check failed!
						sender.sendMessage(String.format("%s%s", ChatColor.RED, config.commandInsufficientPermissions));
						return false;
					}
				}
        		
        		// Get Thread Status
        		if(saveThread == null) {
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.commandStatusOff));
        		} else {
            		if(saveThread.isAlive()) {
            			Date lastSaved = saveThread.getLastSave();
            			if(lastSaved == null) {
            				sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.commandStatusNotRun));
            				return true;
            			} else {
            				sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.commandStatusSuccess.replaceAll("\\{%DATE%\\}", lastSaved.toString())));
            				return true;
            			}
            		} else {
            			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.commandStatusFail));
            			return true;
            		}        			
        		}
        	} else if(args.length >= 1 && args[0].equalsIgnoreCase("interval")) {
				// Check Permissions
        		if (config.permissions) {
					obtainPermissions();
					if (!PERMISSIONS.has(player, "autosave.interval")) {
						// Permission check failed!
						sender.sendMessage(String.format("%s%s", ChatColor.RED, config.commandInsufficientPermissions));
						return false;
					}
				}
        		
        		if(args.length == 1) {
        			// Report interval!
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.commandIntervalLookup.replaceAll("\\{%INTERVAL%\\}", String.valueOf(config.interval))));
        			return true;
        		} else if(args.length == 2) {
        			// Change interval!
        			try {
        				int newInterval = Integer.parseInt(args[1]);
        				config.interval = newInterval;
        				sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.commandIntervalChangeSuccess.replaceAll("\\{%INTERVAL%\\}", String.valueOf(config.interval))));
        				return true;
        			} catch(NumberFormatException e) {
        				sender.sendMessage(String.format("%s%s", ChatColor.RED, config.commandIntervalNotANnumber));
        				return false;
        			}
        		}
        	} else if(args.length >= 1 && args[0].equalsIgnoreCase("broadcast")) {
				// Check Permissions
        		if (config.permissions) {
					obtainPermissions();
					if (!PERMISSIONS.has(player, "autosave.broadcast")) {
						// Permission check failed!
						sender.sendMessage(String.format("%s%s", ChatColor.RED, config.commandInsufficientPermissions));
						return false;
					}
				}
        		
        		if(args.length == 1) {
        			// Report broadcast status!
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.commandBroadcastLookup.replaceAll("\\{%BROADCAST%\\}", String.valueOf(config.broadcast ? config.commandOn : config.commandOff))));
        			return true;
        		} else if(args.length == 2) {
        			// Change broadcast status!
        				boolean newSetting = false;
        				if(args[1].equalsIgnoreCase(config.commandOn)) {
        					newSetting = true;
        				} else if(args[1].equalsIgnoreCase(config.commandOff)) {
        					newSetting = false;
        				} else {
        					sender.sendMessage(String.format("%s%s", ChatColor.RED, config.commandBroadcastNotValid.replaceAll("\\{%ON%\\}", config.commandOn).replaceAll("\\{%OFF%\\}", config.commandOff)));
        					return false;
        				}
        				config.broadcast = newSetting;
        				sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.commandBroadcastChangeSuccess.replaceAll("\\{%BROADCAST%\\}", String.valueOf(config.broadcast ? config.commandOn : config.commandOff))));
        				return true;
        		}        		
        	} else if(args.length == 1 && args[0].equalsIgnoreCase("version")) {
				// Check Permissions
        		if (config.permissions) {
					obtainPermissions();
					if (!PERMISSIONS.has(player, "autosave.version")) {
						// Permission check failed!
						sender.sendMessage(String.format("%s%s", ChatColor.RED, config.commandInsufficientPermissions));
						return false;
					}
				}
        		
        		sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.commandVersion.replaceAll("\\{%VERSION%\\}", pdfFile.getVersion())));
        		return true;
        	}
        }
        return false;
    }
    
    public boolean startSaveThread() {
		saveThread = new AutoSaveThread(this, config);
		saveThread.start();
    	return true;
    }
    
    public boolean stopSaveThread() {
		saveThread.setRun(false);
		try {
			saveThread.join(5000);
			saveThread = null;
			return true;
		} catch (InterruptedException e) {
			log.info("Could not stop AutoSaveThread");
			return false;
		}
    }
    
    public boolean save() {
    	return CommandHelper.queueConsoleCommand(getServer(), "save-all");
    }
	
}

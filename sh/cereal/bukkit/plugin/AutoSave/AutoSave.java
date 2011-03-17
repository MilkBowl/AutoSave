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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
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
	
	private static HashMap<String, BukkitVersion> recommendedBuilds = new HashMap<String, BukkitVersion>();
	static {
		recommendedBuilds.put("git-Bukkit-0.0.0-544-g6c6c30a-b556jnks (MC: 1.3)", new BukkitVersion("git-Bukkit-0.0.0-544-g6c6c30a-b556jnks (MC: 1.3)", true, 556, true));
		recommendedBuilds.put("git-Bukkit-0.0.0-516-gdf87bb3-b531jnks (MC: 1.3)", new BukkitVersion("git-Bukkit-0.0.0-516-gdf87bb3-b531jnks (MC: 1.3)", true, 531, true));
		recommendedBuilds.put("git-Bukkit-0.0.0-512-g63bc855-b527jnks (MC: 1.3)", new BukkitVersion("git-Bukkit-0.0.0-512-g63bc855-b527jnks (MC: 1.3)", true, 527, true));
		recommendedBuilds.put("git-Bukkit-0.0.0-511-g5fae618-b526jnks (MC: 1.3)", new BukkitVersion("git-Bukkit-0.0.0-511-g5fae618-b526jnks (MC: 1.3)", true, 526, true));
		recommendedBuilds.put("git-Bukkit-0.0.0-506-g4e9d448-b522jnks (MC: 1.3)", new BukkitVersion("git-Bukkit-0.0.0-506-g4e9d448-b522jnks (MC: 1.3)", true, 522, true));
		recommendedBuilds.put("git-Bukkit-0.0.0-493-g8b5496e-b493jnks (MC: 1.3)", new BukkitVersion("git-Bukkit-0.0.0-493-g8b5496e-b493jnks (MC: 1.3)", true, 493, true));
	}

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
		
		// Check Server Version String
		if(recommendedBuilds.containsKey(getServer().getVersion())) {
			// Known Build
			BukkitVersion ver = recommendedBuilds.get(getServer().getVersion());
			log.info(String.format("[%s] Server Version is %s%d",  pdfFile.getName(), ver.recommendedBuild ? "Recommended Build " : "Build ", ver.buildNumber, ver.supported ? "is supported" : "is NOT supported"));
		} else {
			// Unknown Build -- Warn
			log.warning(String.format("[%s] UNKNOWN SERVER VERSION: It has NOT been tested and %s MAY NOT function properly: %s",  pdfFile.getName(), pdfFile.getName(), getServer().getVersion()));
		}
		
		// Notify on logger load
		log.info(String.format("[%s] Version %s is enabled!", pdfFile.getName(), pdfFile.getVersion()));
		
		// Ensure our folder exists...
		File dir = new File("plugins/AutoSave");
		dir.mkdir();
		
		// Load configuration 
		loadConfigFile();
		
		// Test the waters, make sure we are running a build that has the methods we NEED
		try {
			// Check Server
			Class<?> s = Class.forName("org.bukkit.Server");
			s.getMethod("savePlayers", new Class[] {});
			
			// Check World
			Class<?> w = Class.forName("org.bukkit.World");
			w.getMethod("save", new Class[] {});
		} catch(ClassNotFoundException e) {
			// Do error stuff
			log.severe(String.format("[%s] ERROR: Server version is incompatible with %s!", pdfFile.getName(), pdfFile.getName()));
			log.severe(String.format("[%s] Could not find class \"%s\", disabling!", pdfFile.getName(), e.getMessage()));
			
			// Clean up
			getPluginLoader().disablePlugin(this);
			return;			
		} catch(NoSuchMethodException e) {
			// Do error stuff
			log.severe(String.format("[%s] ERROR: Server version is incompatible with %s!", pdfFile.getName(), pdfFile.getName()));
			log.severe(String.format("[%s] Could not find method \"%s\", disabling!", pdfFile.getName(), e.getMessage()));
			
			// Clean up
			getPluginLoader().disablePlugin(this);
			return;			
		}
		
		// Start our thread
		startSaveThread();
	}
	
	public void obtainPermissions() {
		// Test if Permissions exists
		if (config.varPermissions) {
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
		
		// Messages
		props.setProperty("message.broadcast", config.messageBroadcast);
		props.setProperty("message.insufficentpermissions", config.messageInsufficientPermissions);
		props.setProperty("message.saveworlds", config.messageSaveWorlds);
		props.setProperty("message.saveplayers", config.messageSavePlayers);
		props.setProperty("message.starting", config.messageStarting);
		props.setProperty("message.statusfail", config.messageStatusFail);
		props.setProperty("message.statusoff", config.messageStatusOff);
		props.setProperty("message.statussuccess", config.messageStatusSuccess);
		props.setProperty("message.stopping", config.messageStopping);
		props.setProperty("message.intervalnotanumber", config.messageIntervalNotANnumber);
		props.setProperty("message.intervalchangesuccess", config.messageIntervalChangeSuccess);
		props.setProperty("message.intervallookup", config.messageIntervalLookup);
		props.setProperty("message.broadcastchangesuccess", config.messageBroadcastChangeSuccess);
		props.setProperty("message.broadcastlookup", config.messageBroadcastLookup);
		props.setProperty("message.broadcastnotvalid", config.messageBroadcastNotValid);
		props.setProperty("message.worldchangesuccess", config.messageWorldChangeSuccess);
		props.setProperty("message.worldlookup", config.messageWorldLookup);
		props.setProperty("message.version", config.messageVersion);
		
		// Values
		props.setProperty("value.on", config.valueOn);
		props.setProperty("value.off", config.valueOff);
		
		// Variables
		props.setProperty("var.debug", String.valueOf(config.varDebug));
		props.setProperty("var.interval", String.valueOf(config.varInterval));
		props.setProperty("var.permissions", String.valueOf(config.varPermissions));
		props.setProperty("var.broadcast", String.valueOf(config.varBroadcast));
		if(config.varWorlds == null) {
			props.setProperty("var.worlds", "*");
		} else {
			props.setProperty("var.worlds", Generic.join(",", config.varWorlds));
		}
		
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
		
		/**
		 * Attempt to load Version 1.0.3 and before values if present, otherwise load 1.1 version
		 */
		
		// Messages
		if(props.containsKey("announce.message")) {
			config.messageBroadcast = props.getProperty("announce.message", config.messageBroadcast);
		} else {
			config.messageBroadcast = props.getProperty("message.broadcast", config.messageBroadcast);
		}
		if(props.containsKey("command.insufficentpermissions")) {
			config.messageInsufficientPermissions = props.getProperty("command.insufficentpermissions", config.messageInsufficientPermissions);
		} else {
			config.messageInsufficientPermissions = props.getProperty("message.insufficentpermissions", config.messageInsufficientPermissions);
		}
		if(props.containsKey("command.saveworlds")) {
			config.messageSaveWorlds = props.getProperty("command.saveworlds", config.messageSaveWorlds);
		} else {
			config.messageSaveWorlds = props.getProperty("message.saveworlds", config.messageSaveWorlds);
		}
		if(props.containsKey("command.saveplayers")) {
			config.messageSavePlayers = props.getProperty("command.saveplayers", config.messageSavePlayers);
		} else {
			config.messageSavePlayers = props.getProperty("message.saveplayers", config.messageSavePlayers);
		}
		if(props.containsKey("command.starting")) {
			config.messageStarting = props.getProperty("command.starting", config.messageStarting);
		} else {
			config.messageStarting = props.getProperty("message.starting", config.messageStarting);
		}
		if(props.containsKey("command.statusfail")) {
			config.messageStatusFail = props.getProperty("command.statusfail", config.messageStatusFail);
		} else {
			config.messageStatusFail = props.getProperty("message.statusfail", config.messageStatusFail);
		}
		if(props.containsKey("command.statusoff")) {
			config.messageStatusOff = props.getProperty("command.statusoff", config.messageStatusOff);
		} else {
			config.messageStatusOff = props.getProperty("cmessage.statusoff", config.messageStatusOff);
		}
		if(props.containsKey("command.statussuccess")) {
			config.messageStatusSuccess = props.getProperty("command.statussuccess", config.messageStatusSuccess);
		} else {
			config.messageStatusSuccess = props.getProperty("message.statussuccess", config.messageStatusSuccess);
		}
		if(props.containsKey("command.stopping")) {
			config.messageStopping = props.getProperty("command.stopping", config.messageStopping);
		} else {
			config.messageStopping = props.getProperty("message.stopping", config.messageStopping);
		}
		if(props.containsKey("command.intervalnotanumber")) {
			config.messageIntervalNotANnumber = props.getProperty("command.intervalnotanumber", config.messageIntervalNotANnumber);
		} else {
			config.messageIntervalNotANnumber = props.getProperty("message.intervalnotanumber", config.messageIntervalNotANnumber);
		}
		if(props.containsKey("command.intervalchangesuccess")) {
			config.messageIntervalChangeSuccess = props.getProperty("command.intervalchangesuccess", config.messageIntervalChangeSuccess);
		} else {
			config.messageIntervalChangeSuccess = props.getProperty("message.intervalchangesuccess", config.messageIntervalChangeSuccess);
		}
		if(props.containsKey("command.intervallookup")) {
			config.messageIntervalLookup = props.getProperty("command.intervallookup", config.messageIntervalLookup);
		} else {
			config.messageIntervalLookup = props.getProperty("message.intervallookup", config.messageIntervalLookup);
		}
		if(props.containsKey("command.broadcastchangesuccess")) {
			config.messageBroadcastChangeSuccess = props.getProperty("command.broadcastchangesuccess", config.messageBroadcastChangeSuccess);
		} else {
			config.messageBroadcastChangeSuccess = props.getProperty("message.broadcastchangesuccess", config.messageBroadcastChangeSuccess);
		}
		if(props.containsKey("command.broadcastlookup")) {
			config.messageBroadcastLookup = props.getProperty("command.broadcastlookup", config.messageBroadcastLookup);
		} else {
			config.messageBroadcastLookup = props.getProperty("message.broadcastlookup", config.messageBroadcastLookup);
		}
		if(props.containsKey("command.broadcastnotvalid")) {
			config.messageBroadcastNotValid = props.getProperty("command.broadcastnotvalid", config.messageBroadcastNotValid);
		} else {
			config.messageBroadcastNotValid = props.getProperty("message.broadcastnotvalid", config.messageBroadcastNotValid);
		}
		if(props.containsKey("command.version")) {
			config.messageVersion = props.getProperty("command.version", config.messageVersion);
		} else {
			config.messageVersion = props.getProperty("message.version", config.messageVersion);
		}
		config.messageDebugChangeSuccess = props.getProperty("message.debugchangesuccess", config.messageDebugChangeSuccess);
		config.messageDebugLookup = props.getProperty("message.debuglookup", config.messageDebugLookup);
		config.messageDebugNotValid = props.getProperty("message.debugnotvalue", config.messageDebugNotValid);
		config.messageWorldChangeSuccess = props.getProperty("message.worldchangesuccess", config.messageWorldChangeSuccess);
		config.messageWorldLookup = props.getProperty("message.worldlookup", config.messageWorldLookup);
		
		// Values
		if(props.containsKey("command.on")) {
			config.valueOn = props.getProperty("command.on", config.valueOn);
		} else {
			config.valueOn = props.getProperty("value.on", config.valueOn);
		}
		if(props.containsKey("command.off")) {
			config.valueOff = props.getProperty("command.off", config.valueOff);
		} else {
			config.valueOff = props.getProperty("value.off", config.valueOff);
		}
		
		// Variables
		if(props.containsKey("debug")) {
			config.varDebug = Boolean.parseBoolean(props.getProperty("debug", String.valueOf(config.varDebug)));
		} else {
			config.varDebug = Boolean.parseBoolean(props.getProperty("var.debug", String.valueOf(config.varDebug)));
		}
		if(props.containsKey("broadcast.enable")) {
			config.varBroadcast = Boolean.parseBoolean(props.getProperty("broadcast.enable", String.valueOf(config.varBroadcast)));
		} else {
			config.varBroadcast = Boolean.parseBoolean(props.getProperty("var.broadcast", String.valueOf(config.varBroadcast)));
		}
		if(props.containsKey("permissions")) {
			config.varPermissions = Boolean.parseBoolean(props.getProperty("permissions", String.valueOf(config.varPermissions)));
		} else {
			config.varPermissions = Boolean.parseBoolean(props.getProperty("var.permissions", String.valueOf(config.varPermissions)));
		}
		if(props.containsKey("interval")) {
			config.varInterval = Integer.parseInt(props.getProperty("interval", String.valueOf(config.varInterval)));
		} else {
			config.varInterval = Integer.parseInt(props.getProperty("var.interval", String.valueOf(config.varInterval)));
		}
		
		String tmpWorlds = props.getProperty("var.worlds", "*");
		config.varWorlds = new ArrayList<String>(Arrays.asList(tmpWorlds.split(",")));
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {       
    	String commandName = command.getName().toLowerCase();
    	Player player = null;
    	if((sender instanceof Player)) {
    		player = (Player) sender;
        }

        if (commandName.equals("autosave")) {
        	if(args.length == 0) {
        		// Check Permissions
				if (player != null && config.varPermissions) {
					obtainPermissions();
					if (!PERMISSIONS.has(player, "autosave.save")) {
						// Permission check failed!
						sender.sendMessage(String.format("%s%s", ChatColor.RED, config.messageInsufficientPermissions));
						return false;
					}
				}
				
				// Perform save
				// Players
				savePlayers();
				sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.messageSavePlayers));
				// Worlds
				int worlds = saveWorlds();
				sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.messageSaveWorlds.replaceAll("\\{%NUMSAVED%\\}", String.valueOf(worlds))));
				if(worlds > 0) {
					return true;
				} else {
					return false;
				}
        	} else if(args.length == 1 && args[0].equalsIgnoreCase("help")) {
        		// Shows help for allowed commands
        		if(player != null) {
        			// /save
					if (PERMISSIONS.has(player, "autosave.save")) {
						sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "/save - Saves all players & worlds"));
					}
					
					// /save help
					sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "/save help - Displays this dialogue"));
					
					// /save toggle
					if (PERMISSIONS.has(player, "autosave.toggle")) { 
						sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "/save toggle - Toggles the AutoSave system"));
					}
					
					// /save status
					if (PERMISSIONS.has(player, "autosave.status")) {
						sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "/save status - Reports thread status and last run time"));
					}
					
					// /save interval
					if (PERMISSIONS.has(player, "autosave.interval")) {
						sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "/save interval [value] - Sets & retrieves the save interval"));
					}
					
					// /save broadcast
					if (PERMISSIONS.has(player, "autosave.broadcast")) {
						sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "/save broadcast [on|off] - Sets & retrieves the broadcast value"));
					}
					
					// /save version
					if (PERMISSIONS.has(player, "autosave.version")) {
						sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "/save version - Prints the version of AutoSave"));
					}
        		} else {
        			// save
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "save - Saves all players & worlds"));
        			// save help
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "save help - Displays this dialog"));
        			// save toggle
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "save toggle - Toggles the AutoSave system"));
        			// save status
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "save status - Reports thread status and last run time"));
        			// save interval
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "save interval [value] - Sets & retrieves the save interval"));
        			// save addworld
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "save addworld [value] - Adds world to save list"));
        			// save remworld
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "save remworld [value] - Removes world from save list"));
        			// save world
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "save world - Shows worlds on the save list"));
        			// save broadcast
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "save broadcast [on|off] - Sets & retrieves the broadcast value"));
        			// save debug
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "save debug [on|off] - Sets & retrieves the debug value"));
        			// save version
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "save version - Prints the version of AutoSave"));        			
        		}
        	} else if(args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
				// Check Permissions
        		if (player != null && config.varPermissions) {
					obtainPermissions();
					if (!PERMISSIONS.has(player, "autosave.toggle")) {
						// Permission check failed!
						sender.sendMessage(String.format("%s%s", ChatColor.RED, config.messageInsufficientPermissions));
						return false;
					}
				}
				
				// Start thread
				if(saveThread == null) {
					sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.messageStarting));
					return startSaveThread();
				} else { // Stop thread
					sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.messageStopping));
					return stopSaveThread();
				}       		
        	} else if(args.length == 1 && args[0].equalsIgnoreCase("status")) {
				// Check Permissions
        		if (player != null && config.varPermissions) {
					obtainPermissions();
					if (!PERMISSIONS.has(player, "autosave.status")) {
						// Permission check failed!
						sender.sendMessage(String.format("%s%s", ChatColor.RED, config.messageInsufficientPermissions));
						return false;
					}
				}
        		
        		// Get Thread Status
        		if(saveThread == null) {
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.messageStatusOff));
        		} else {
            		if(saveThread.isAlive()) {
            			Date lastSaved = saveThread.getLastSave();
            			if(lastSaved == null) {
            				sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.messageStatusNotRun));
            				return true;
            			} else {
            				sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.messageStatusSuccess.replaceAll("\\{%DATE%\\}", lastSaved.toString())));
            				return true;
            			}
            		} else {
            			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.messageStatusFail));
            			return true;
            		}        			
        		}
        	} else if(args.length >= 1 && args[0].equalsIgnoreCase("interval")) {
				// Check Permissions
        		if (player != null && config.varPermissions) {
					obtainPermissions();
					if (!PERMISSIONS.has(player, "autosave.interval")) {
						// Permission check failed!
						sender.sendMessage(String.format("%s%s", ChatColor.RED, config.messageInsufficientPermissions));
						return false;
					}
				}
        		
        		if(args.length == 1) {
        			// Report interval!
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.messageIntervalLookup.replaceAll("\\{%INTERVAL%\\}", String.valueOf(config.varInterval))));
        			return true;
        		} else if(args.length == 2) {
        			// Change interval!
        			try {
        				int newInterval = Integer.parseInt(args[1]);
        				config.varInterval = newInterval;
        				sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.messageIntervalChangeSuccess.replaceAll("\\{%INTERVAL%\\}", String.valueOf(config.varInterval))));
        				return true;
        			} catch(NumberFormatException e) {
        				sender.sendMessage(String.format("%s%s", ChatColor.RED, config.messageIntervalNotANnumber));
        				return false;
        			}
        		}
        	} else if(args.length >= 1 && args[0].equalsIgnoreCase("broadcast")) {
				// Check Permissions
        		if (player != null && config.varPermissions) {
					obtainPermissions();
					if (!PERMISSIONS.has(player, "autosave.broadcast")) {
						// Permission check failed!
						sender.sendMessage(String.format("%s%s", ChatColor.RED, config.messageInsufficientPermissions));
						return false;
					}
				}
        		
        		if(args.length == 1) {
        			// Report broadcast status!
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.messageBroadcastLookup.replaceAll("\\{%BROADCAST%\\}", String.valueOf(config.varBroadcast ? config.valueOn : config.valueOff))));
        			return true;
        		} else if(args.length == 2) {
        				// Change broadcast status!
        				boolean newSetting = false;
        				if(args[1].equalsIgnoreCase(config.valueOn)) {
        					newSetting = true;
        				} else if(args[1].equalsIgnoreCase(config.valueOff)) {
        					newSetting = false;
        				} else {
        					sender.sendMessage(String.format("%s%s", ChatColor.RED, config.messageBroadcastNotValid.replaceAll("\\{%ON%\\}", config.valueOn).replaceAll("\\{%OFF%\\}", config.valueOff)));
        					return false;
        				}
        				config.varBroadcast = newSetting;
        				sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.messageBroadcastChangeSuccess.replaceAll("\\{%BROADCAST%\\}", String.valueOf(config.varBroadcast ? config.valueOn : config.valueOff))));
        				return true;
        		}
        	} else if(args.length >= 1 && args[0].equalsIgnoreCase("debug")) {
				// Check Permissions
        		if (player != null && config.varPermissions) {
					obtainPermissions();
					if (!PERMISSIONS.has(player, "autosave.debug")) {
						// Permission check failed!
						sender.sendMessage(String.format("%s%s", ChatColor.RED, config.messageInsufficientPermissions));
						return false;
					}
				}
        		
        		if(args.length == 1) {
        			// Report debug status!
        			sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.messageDebugLookup.replaceAll("\\{%DEBUG%\\}", String.valueOf(config.varDebug ? config.valueOn : config.valueOff))));
        			return true;
        		} else if(args.length == 2) {
    				// Change debug status!
    				boolean newSetting = false;
    				if(args[1].equalsIgnoreCase(config.valueOn)) {
    					newSetting = true;
    				} else if(args[1].equalsIgnoreCase(config.valueOff)) {
    					newSetting = false;
    				} else {
    					sender.sendMessage(String.format("%s%s", ChatColor.RED, config.messageDebugNotValid.replaceAll("\\{%ON%\\}", config.valueOn).replaceAll("\\{%OFF%\\}", config.valueOff)));
    					return false;
    				}
    				config.varDebug = newSetting;
    				sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.messageDebugChangeSuccess.replaceAll("\\{%DEBUG%\\}", String.valueOf(config.varDebug ? config.valueOn : config.valueOff))));
    				return true;        			
        		}
        	} else if(args.length == 2 && args[0].equalsIgnoreCase("addworld")) {
				// Check Permissions
        		if (player != null && config.varPermissions) {
					obtainPermissions();
					if (!PERMISSIONS.has(player, "autosave.world.add")) {
						// Permission check failed!
						sender.sendMessage(String.format("%s%s", ChatColor.RED, config.messageInsufficientPermissions));
						return false;
					}
				}
        		
        		config.varWorlds.add(args[1]);
        		sender.sendMessage(config.messageWorldChangeSuccess.replaceAll("\\{%WORLDS%\\}", Generic.join(", ", config.varWorlds)));
        		
        		return true;
        	} else if(args.length == 2 && args[0].equalsIgnoreCase("remworld")) {
				// Check Permissions
        		if (player != null && config.varPermissions) {
					obtainPermissions();
					if (!PERMISSIONS.has(player, "autosave.world.rem")) {
						// Permission check failed!
						sender.sendMessage(String.format("%s%s", ChatColor.RED, config.messageInsufficientPermissions));
						return false;
					}
				}
        		
        		config.varWorlds.remove(args[1]);
        		sender.sendMessage(config.messageWorldChangeSuccess.replaceAll("\\{%WORLDS%\\}", Generic.join(", ", config.varWorlds)));
        		
        		return true;
        	} else if(args.length == 1 && args[0].equalsIgnoreCase("world")) {
				// Check Permissions
        		if (player != null && config.varPermissions) {
					obtainPermissions();
					if (!PERMISSIONS.has(player, "autosave.world")) {
						// Permission check failed!
						sender.sendMessage(String.format("%s%s", ChatColor.RED, config.messageInsufficientPermissions));
						return false;
					}
				}
        		
        		sender.sendMessage(config.messageWorldLookup.replaceAll("\\{%WORLDS%\\}", Generic.join(", ", config.varWorlds)));
        		
        		return true;
        	} else if(args.length == 1 && args[0].equalsIgnoreCase("version")) {
				// Check Permissions
        		if (player != null && config.varPermissions) {
					obtainPermissions();
					if (!PERMISSIONS.has(player, "autosave.version")) {
						// Permission check failed!
						sender.sendMessage(String.format("%s%s", ChatColor.RED, config.messageInsufficientPermissions));
						return false;
					}
				}
        		
        		sender.sendMessage(String.format("%s%s", ChatColor.BLUE, config.messageVersion.replaceAll("\\{%VERSION%\\}", pdfFile.getVersion())));
        		return true;
        	}
        } else {
        	sender.sendMessage(String.format("Unknown command \"%s\" handled by %s", commandName, pdfFile.getName()));
        }
        return false;
    }
    
    public boolean startSaveThread() {
		saveThread = new AutoSaveThread(this, config);
		saveThread.start();
    	return true;
    }
    
    public boolean stopSaveThread() {
		if (saveThread != null) {
			saveThread.setRun(false);
			try {
				saveThread.join(5000);
				saveThread = null;
				return true;
			} catch (InterruptedException e) {
				log.info(String.format("[%s] Could not stop AutoSaveThread", pdfFile.getName()));
				return false;
			}
		} else {
			return true;
		}
    }
    
    public void savePlayers() {
    	// Save the players
    	if(config.varDebug) {
    		log.info(String.format("[%s] Saving players", pdfFile.getName()));
    	}
    	this.getServer().savePlayers();
    }
    
    public int saveWorlds(List<String> worldNames) {
    	// Save our worlds...
    	int i = 0;
    	List<World> worlds = this.getServer().getWorlds();
    	for(World world : worlds) {
    		if(worldNames.contains(world.getName())) {
    			if(config.varDebug) {
    				log.info(String.format("[%s] Saving the world: %s", pdfFile.getName(), world.getName()));
    			}
    			world.save();
    			i++;
    		}
    	}    	
    	return i;
    }
    
    public int saveWorlds() {
    	// Save our worlds
    	int i = 0;
    	List<World> worlds = this.getServer().getWorlds();
    	for(World world : worlds) {
    		if(config.varDebug) {
    			log.info(String.format("[%s] Saving the world: %s", pdfFile.getName(), world.getName()));
    		}
    		world.save();
    		i++;
    	}
    	return i;
    	//return CommandHelper.queueConsoleCommand(getServer(), "save-all");
    }
	
}

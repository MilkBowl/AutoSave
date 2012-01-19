/**
 * 
 * Copyright 2011 MilkBowl (https://github.com/MilkBowl)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

package net.milkbowl.autosave;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoSave extends JavaPlugin {
	private static final Logger log = Logger.getLogger("Minecraft");

	private AutoSaveThread saveThread = null;
	private ReportThread reportThread = null;
	private AutoSaveConfig config;
	protected Date lastSave = null;
	protected int numPlayers = 0;
	protected boolean saveInProgress = false;
	protected Boolean bukkitHasSetAutoSave;

	@Override
	public void onDisable() {

		// Save config
		config.save();
		
		// Perform a Save NOW!
		performSave();

		// Enable built-in world saving for ASynchronous Mode
		if (config.varMode == Mode.ASYNCHRONOUS) {
			for (World world : getServer().getWorlds()) {
				if(bukkitHasSetAutoSave) {
					world.setAutoSave(true);
				} else {
					// this should be false because canSave is really noSave
					((CraftWorld) world).getHandle().savingDisabled = false;
				}
			}
		}

		long timeA = 0;
		if (config.varDebug) {
			timeA = System.currentTimeMillis();
		}
		// Stop thread
		if (config.varDebug) {
			log.info(String.format("[%s] Stopping Save Thread",
					getDescription().getName()));
		}
		stopThread(ThreadType.SAVE);
		stopThread(ThreadType.REPORT);

		if (config.varDebug) {
			long timeB = System.currentTimeMillis();
			long millis = timeB - timeA;
			long durationSeconds = TimeUnit.MILLISECONDS.toSeconds(millis);

			log.info(String.format(
					"[%s] Version %s was disabled in %d seconds",
					getDescription().getName(), getDescription().getVersion(),
					durationSeconds));
		} else {
			log.info(String.format("[%s] Version %s is disabled!",
					getDescription().getName(), getDescription().getVersion()));
		}
	}

	@Override
	public void onEnable() {
		// Load Configuration
		config = new AutoSaveConfig(getConfiguration());
		config.load();

		// Test the waters, make sure we are running a build that has the
		// methods we NEED
		try {
			// Check Server
			org.bukkit.Server.class.getMethod("savePlayers", new Class[] {});

			// Check World
			org.bukkit.World.class.getMethod("save", new Class[] {});
		} catch (NoSuchMethodException e) {
			// Do error stuff
			log.severe(String.format("[%s] ERROR: Server version is incompatible with %s!", getDescription().getName(), getDescription().getName()));
			log.severe(String.format("[%s] Could not find method \"%s\", disabling!", getDescription().getName(), e.getMessage()));

			// Clean up
			getPluginLoader().disablePlugin(this);
			return;
		}
		
		// Test the waters further, see if setAutoSave exists!
		try {
			org.bukkit.World.class.getMethod("setAutoSave", boolean.class);
			bukkitHasSetAutoSave = true;
		} catch (NoSuchMethodException e) {
			// Do nothing, we will work around it anyways
			bukkitHasSetAutoSave = false;
		}

		// Disable built-in world saving for ASynchronous Mode
		if (config.varMode == Mode.ASYNCHRONOUS) {
			for (World world : getServer().getWorlds()) {
				if (bukkitHasSetAutoSave) {
					world.setAutoSave(false);
				} else {
					// this should be true because canSave is really noSave
					((CraftWorld) world).getHandle().savingDisabled = true;
				}
			}
		}

		// Make an HTTP request for anonymous statistic collection
		startThread(ThreadType.REPORT);

		// Start AutoSave Thread
		startThread(ThreadType.SAVE);

		// Notify on logger load
		log.info(String.format("[%s] Version %s is enabled: %s", getDescription().getName(), getDescription().getVersion(), config.varUuid.toString()));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();
		Player player = null;
		if ((sender instanceof Player)) {
			// Player, lets check if player isOp()
			player = (Player) sender;
			// Check Permissions
			if (!player.isOp()) {
				sendMessage(sender, config.messageInsufficientPermissions);
				return true;
			}
		} else if (sender instanceof ConsoleCommandSender) {
			// Success, this was from the Console
		} else {
			// Unknown, ignore these people with a pretty message
			sendMessage(sender, config.messageInsufficientPermissions);
			return true;
		}

		if (commandName.equals("autosave")) {
			if (args.length == 0) {
				performSave();
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
				// Shows help for allowed commands

				// /save
				sendMessage(sender, "&f/save&7 - &3Saves all players & worlds");

				// /save help
				sendMessage(sender, "&f/save help&7 - &3Displays this dialogue");

				// /save toggle
				sendMessage(sender,
						"&f/save toggle&7 - &3Toggles the AutoSave system");

				// /save status
				sendMessage(sender,
						"&f/save status&7 - &3Reports thread status and last run time");

				// /save interval
				sendMessage(sender,
						"&f/save interval&7 [value] - &3Sets & retrieves the save interval");

				// /save broadcast
				sendMessage(sender,
						"&f/save broadcast&7 [on|off] - &3Sets & retrieves the broadcast value");

				// /save report
				sendMessage(sender,
						"&f/save report&7 [on|off] - &3Sets & retrieves the report value");

				// /save warn
				sendMessage(sender,
						"&f/save warn&7 [value] - &3Sets & retrieves the warn time in seconds");

				// /save version
				sendMessage(sender,
						"&f/save version&7 - &3Prints the version of AutoSave");
			} else if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
				// Start thread
				if (saveThread == null) {
					sendMessage(sender, config.messageStarting);
					return startThread(ThreadType.SAVE);
				} else { // Stop thread
					sendMessage(sender, config.messageStopping);
					return stopThread(ThreadType.SAVE);
				}
			} else if (args.length == 1 && args[0].equalsIgnoreCase("status")) {
				// Get Thread Status
				if (saveThread == null) {
					sendMessage(sender, config.messageStatusOff);
				} else {
					if (saveThread.isAlive()) {
						if (lastSave == null) {
							sendMessage(sender, config.messageStatusNotRun);
							return true;
						} else {
							sendMessage(sender,
									config.messageStatusSuccess.replaceAll(
											"\\$\\{DATE\\}",
											lastSave.toString()));
							return true;
						}
					} else {
						sendMessage(sender, config.messageStatusFail);
						return true;
					}
				}
			} else if (args.length >= 1 && args[0].equalsIgnoreCase("interval")) {
				if (args.length == 1) {
					// Report interval!
					sendMessage(
							sender,
							config.messageInfoLookup.replaceAll(
									"\\$\\{VARIABLE\\}", "Interval")
									.replaceAll("\\$\\{VALUE\\}",
											String.valueOf(config.varInterval)));
					return true;
				} else if (args.length == 2) {
					// Change interval!
					try {
						int newInterval = Integer.parseInt(args[1]);
						config.varInterval = newInterval;
						sendMessage(sender,
								config.messageInfoChangeSuccess.replaceAll(
										"\\$\\{VARIABLE\\}", "Interval"));
						return true;
					} catch (NumberFormatException e) {
						sendMessage(sender, config.messageInfoNaN);
						return false;
					}
				}
			} else if (args.length >= 1 && args[0].equalsIgnoreCase("warn")) {
				if (args.length == 1) {
					// Report interval!
					sendMessage(
							sender,
							config.messageInfoListLookup.replaceAll(
									"\\$\\{VARIABLE\\}", "Warn").replaceAll(
									"\\$\\{VALUE\\}",
									Generic.join(", ", config.varWarnTimes)));
					return true;
				} else if (args.length == 2) {
					// Change interval!
					try {
						ArrayList<Integer> tmpWarn = new ArrayList<Integer>();
						for (String s : args[1].split(",")) {
							tmpWarn.add(Integer.parseInt(s));
						}
						config.varWarnTimes = tmpWarn;
						sendMessage(sender,
								config.messageInfoChangeSuccess.replaceAll(
										"\\$\\{VARIABLE\\}", "Warn"));
						return true;
					} catch (NumberFormatException e) {
						sendMessage(sender, config.messageInfoNaN);
						return false;
					}
				}
			} else if (args.length >= 1
					&& args[0].equalsIgnoreCase("broadcast")) {
				if (args.length == 1) {
					// Report broadcast status!
					sendMessage(
							sender,
							config.messageInfoLookup
									.replaceAll("\\$\\{VARIABLE\\}",
											"Broadcast")
									.replaceAll(
											"\\$\\{VALUE\\}",
											String.valueOf(config.varBroadcast ? config.valueOn
													: config.valueOff)));
					return true;
				} else if (args.length == 2) {
					// Change broadcast status!
					boolean newSetting = false;
					if (args[1].equalsIgnoreCase(config.valueOn)) {
						newSetting = true;
					} else if (args[1].equalsIgnoreCase(config.valueOff)) {
						newSetting = false;
					} else {
						sendMessage(sender,
								config.messageInfoInvalid.replaceAll(
										"\\$\\{VALIDSETTINGS\\}", String
												.format("%s, %s",
														config.valueOn,
														config.valueOff)));
						return false;
					}
					config.varBroadcast = newSetting;
					sendMessage(sender,
							config.messageInfoChangeSuccess.replaceAll(
									"\\$\\{VARIABLE\\}", "AutoSave Broadcast"));
					return true;
				}
			} else if (args.length >= 1 && args[0].equalsIgnoreCase("debug")) {
				if (args.length == 1) {
					// Report debug status!
					sendMessage(
							sender,
							config.messageInfoLookup
									.replaceAll("\\$\\{VARIABLE\\}", "Debug")
									.replaceAll(
											"\\$\\{VALUE\\}",
											String.valueOf(config.varDebug ? config.valueOn
													: config.valueOff)));
					return true;
				} else if (args.length == 2) {
					// Change debug status!
					boolean newSetting = false;
					if (args[1].equalsIgnoreCase(config.valueOn)) {
						newSetting = true;
					} else if (args[1].equalsIgnoreCase(config.valueOff)) {
						newSetting = false;
					} else {
						sendMessage(sender,
								config.messageInfoInvalid.replaceAll(
										"\\$\\{VALIDSETTINGS\\}", String
												.format("%s, %s",
														config.valueOn,
														config.valueOff)));
						return false;
					}
					config.varDebug = newSetting;
					sendMessage(sender,
							config.messageInfoChangeSuccess.replaceAll(
									"\\$\\{VARIABLE\\}", "Debug"));
					return true;
				}
			} else if (args.length >= 1 && args[0].equalsIgnoreCase("report")) {
				if (args.length == 1) {
					// Report report status!
					sendMessage(
							sender,
							config.messageInfoLookup
									.replaceAll("\\$\\{VARIABLE\\}", "Report")
									.replaceAll(
											"\\$\\{VALUE\\}",
											String.valueOf(config.varReport ? config.valueOn
													: config.valueOff)));
					return true;
				} else if (args.length == 2) {
					// Change report status!
					boolean newSetting = false;
					if (args[1].equalsIgnoreCase(config.valueOn)) {
						startThread(ThreadType.REPORT);
						newSetting = true;
					} else if (args[1].equalsIgnoreCase(config.valueOff)) {
						stopThread(ThreadType.REPORT);
						newSetting = false;
					} else {
						sendMessage(sender,
								config.messageInfoInvalid.replaceAll(
										"\\$\\{VALIDSETTINGS\\}", String
												.format("%s, %s",
														config.valueOn,
														config.valueOff)));
						return false;
					}
					config.varReport = newSetting;
					sendMessage(sender,
							config.messageInfoChangeSuccess.replaceAll(
									"\\$\\{VARIABLE\\}", "Report"));
					return true;
				}
			} else if (args.length == 2 && args[0].equalsIgnoreCase("addworld")) {
				config.varWorlds.add(args[1]);
				sendMessage(sender, config.messageInfoChangeSuccess.replaceAll(
						"\\$\\{VARIABLE\\}", "Worlds"));
				return true;
			} else if (args.length == 2 && args[0].equalsIgnoreCase("remworld")) {
				config.varWorlds.remove(args[1]);
				sendMessage(sender, config.messageInfoChangeSuccess.replaceAll(
						"\\$\\{VARIABLE\\}", "Worlds"));
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("world")) {
				sendMessage(
						sender,
						config.messageInfoListLookup.replaceAll(
								"\\$\\{VARIABLE\\}", "Worlds").replaceAll(
								"\\$\\{VALUE\\}",
								Generic.join(", ", config.varWorlds)));
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("version")) {
				sendMessage(sender, String.format(
						"%s%s",
						ChatColor.BLUE,
						config.messageVersion.replaceAll("\\$\\{VERSION\\}",
								getDescription().getVersion()).replaceAll(
								"\\$\\{UUID\\}", config.varUuid.toString())));
				return true;
			}
		} else {
			sendMessage(sender, String.format(
					"Unknown command \"%s\" handled by %s", commandName,
					getDescription().getName()));
		}
		return false;
	}

	protected boolean startThread(ThreadType type) {
		switch (type) {
		case REPORT:
			if (reportThread == null || !reportThread.isAlive()) {
				reportThread = new ReportThread(this, config.varUuid,
						config.varDebug);
				reportThread.start();
			}
			return true;
		case SAVE:
			if (saveThread == null || !saveThread.isAlive()) {
				saveThread = new AutoSaveThread(this, config);
				saveThread.start();
			}
			return true;
		default:
			return false;
		}
	}

	protected boolean stopThread(ThreadType type) {
		switch (type) {
		case REPORT:
			if (reportThread == null) {
				return true;
			} else {
				reportThread.setRun(false);
				try {
					reportThread.join(5000);
					reportThread = null;
					return true;
				} catch (InterruptedException e) {
					warn("Could not stop ReportThread", e);
					return false;
				}
			}
		case SAVE:
			if (saveThread == null) {
				return true;
			} else {
				saveThread.setRun(false);
				try {
					saveThread.join(5000);
					saveThread = null;
					return true;
				} catch (InterruptedException e) {
					warn("Could not stop AutoSaveThread", e);
					return false;
				}
			}
		default:
			return false;
		}
	}

	private void savePlayers() {
		// Save the players
		debug("Saving players");
		this.getServer().savePlayers();
	}

	private int saveWorlds(List<String> worldNames) {
		// Save our worlds...
		int i = 0;
		List<World> worlds = this.getServer().getWorlds();
		for (World world : worlds) {
			if (worldNames.contains(world.getName())) {
				debug(String.format("Saving world: %s", world.getName()));
				world.save();
				i++;
			}
		}
		return i;
	}

	private int saveWorlds() {
		// Save our worlds
		int i = 0;
		List<World> worlds = this.getServer().getWorlds();
		for (World world : worlds) {
			debug(String.format("Saving world: %s", world.getName()));
			world.save();
			i++;
		}
		return i;
	}

	public void performSave() {
		if (saveInProgress) {
			warn("Multiple concurrent saves attempted!  Save interval is likely too short!");
			return;
		}

		if (getServer().getOnlinePlayers().length == 0) {
			// No players online, don't bother saving.
			debug("Skipping save, no players online.");
			return;
		}

		// Lock
		saveInProgress = true;

		broadcast(config.messageBroadcastPre);

		// Save the players
		savePlayers();
		debug("Saved Players");

		// Save the worlds
		int saved = 0;
		if (config.varWorlds.contains("*")) {
			saved += saveWorlds();
		} else {
			saved += saveWorlds(config.varWorlds);
		}

		debug(String.format("Saved %d Worlds", saved));

		lastSave = new Date();
		broadcast(config.messageBroadcastPost);

		// Release
		saveInProgress = false;
	}

	public void sendMessage(CommandSender sender, String message) {
		if (!message.equals("")) {
			sender.sendMessage(Generic.parseColor(message));
		}
	}

	public void broadcast(String message) {
		if (!message.equals("")) {
			getServer().broadcastMessage(Generic.parseColor(message));
			log.info(String.format("[%s] %s", getDescription().getName(), Generic.stripColor(message)));
		}
	}

	public void debug(String message) {
		if (config.varDebug) {
			log.info(String.format("[%s] %s", getDescription().getName(), Generic.stripColor(message)));
		}
	}

	public void warn(String message) {
		log.warning(String.format("[%s] %s", getDescription().getName(), Generic.stripColor(message)));
	}

	public void warn(String message, Exception e) {
		log.log(Level.WARNING, String.format("[%s] %s", getDescription().getName(), Generic.stripColor(message)), e);
	}

}

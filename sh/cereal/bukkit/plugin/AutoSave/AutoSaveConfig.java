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

public class AutoSaveConfig {

	// Messages
	public String broadcastMessage = "World Auto-Saving";
	public String commandStatusFail = "Auto Save has stopped, check the server logs for more info";
	public String commandStatusNotRun = "Auto Save is running but has not yet saved.";
	public String commandStatusSuccess = "Auto Save is running and last saved at {%DATE%}.";
	public String commandStatusOff = "Auto Save is not running (disabled)";
	public String commandInsufficientPermissions = "You do not have access to that command.";
	public String commandStopping = "Stopping Auto Saves";
	public String commandStarting = "Starting Auto Saves";
	public String commandSave = "World Saving";
	public String commandIntervalNotANnumber = "You must enter a valid number, ex: /save interval 300";
	public String commandIntervalChangeSuccess = "Auto Save interval is now {%INTERVAL%}";
	public String commandIntervalLookup = "Auto Save interval is {%INTERVAL%}";
	public String commandBroadcastChangeSuccess = "Auto Save broadcast is now {%BROADCAST%}";
	public String commandBroadcastLookup = "Auto Save broadcast is {%BROADCAST%}";
	public String commandOn = "on";
	public String commandOff = "off";
	public String commandBroadcastNotValid = "You must enter a valid setting ({%ON%}, {%OFF%})";
	public String commandVersion = "AutoSave v{%VERSION%}";
	
	// Values
	public int interval = 300;
	public boolean broadcast = true;
	public boolean permissions = true;
}

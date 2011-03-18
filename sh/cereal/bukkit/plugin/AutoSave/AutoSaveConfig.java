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

import java.util.ArrayList;
import java.util.UUID;

public class AutoSaveConfig {

	// Messages
	public String messageBroadcast = "World Auto-Saving";
	public String messageStatusFail = "Auto Save has stopped, check the server logs for more info";
	public String messageStatusNotRun = "Auto Save is running but has not yet saved.";
	public String messageStatusSuccess = "Auto Save is running and last saved at {%DATE%}.";
	public String messageStatusOff = "Auto Save is not running (disabled)";
	public String messageInsufficientPermissions = "You do not have access to that command.";
	public String messageStopping = "Stopping Auto Saves";
	public String messageStarting = "Starting Auto Saves";
	public String messageSaveWorlds = "{%NUMSAVED%} Worlds Saved";
	public String messageSavePlayers = "Players Saved";
	public String messageIntervalNotANnumber = "You must enter a valid number, ex: /save interval 300";
	public String messageIntervalChangeSuccess = "Auto Save interval is now {%INTERVAL%}";
	public String messageIntervalLookup = "Auto Save interval is {%INTERVAL%}";
	public String messageWarnNotANnumber = "You must enter a valid number, ex: /save warn 300";
	public String messageWarnChangeSuccess = "Auto Save warning time is now {%WARN%}";
	public String messageWarnLookup = "Auto Save warning time is {%WARN%}";	
	public String messageBroadcastChangeSuccess = "Auto Save broadcast is now {%BROADCAST%}";
	public String messageBroadcastLookup = "Auto Save broadcast is {%BROADCAST%}";
	public String messageBroadcastNotValid = "You must enter a valid setting ({%ON%}, {%OFF%})";
	public String messageDebugChangeSuccess = "Auto Save debug is now {%DEBUG%}";
	public String messageDebugLookup = "Auto Save debug is {%DEBUG%}";
	public String messageDebugNotValid = "You must enter a valid setting ({%ON%}, {%OFF%})";
	public String messageWorldChangeSuccess = "World Save List is now {%WORLDS%}";
	public String messageWorldLookup = "World Save List is {%WORLDS%}";
	public String messageVersion = "AutoSave v{%VERSION%}";
	public String messageWarning = "Warning, AutoSave will commence soon.";
	public String messageReportLookup = "Auto Save report is {%REPORT%}";
	public String messageReportNotValid = "You must enter a valid setting ({%ON%}, {%OFF%})";
	public String messageReportChangeSuccess = "Auto Save report is now {%REPORT%}";
	
	// Values
	public String valueOn = "on";
	public String valueOff = "off";	
	
	// Variables
	public UUID varUuid;
	public boolean varReport = true;
	public int varInterval = 300;
	public int varWarnTime = 0;
	public boolean varBroadcast = true;
	public boolean varPermissions = true;
	public boolean varDebug = false;
	public ArrayList<String> varWorlds = null;
}

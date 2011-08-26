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

import java.util.List;
import java.util.UUID;

import org.bukkit.util.config.Configuration;

public class AutoSaveConfig {

	private Configuration config;

	public AutoSaveConfig(Configuration config) {
		this.config = config;
	}

	// Messages
	protected String messageBroadcastPre = "&9AutoSaving";
	protected String messageBroadcastPost = "&9AutoSave Complete";
	protected String messageStatusFail = "&9AutoSave has stopped, check the server logs for more info";
	protected String messageStatusNotRun = "&9AutoSave is running but has not yet saved.";
	protected String messageStatusSuccess = "&9AutoSave is running and last saved at ${DATE}.";
	protected String messageStatusOff = "&9AutoSave is not running (disabled)";
	protected String messageInsufficientPermissions = "&cYou do not have access to that command.";
	protected String messageStopping = "&9AutoSave Stopping";
	protected String messageStarting = "&9AutoSave Starting";
	protected String messageInfoNaN = "&cYou must enter a valid number, ex: 300";
	protected String messageInfoChangeSuccess = "&9${VARIABLE} has been updated.";
	protected String messageInfoLookup = "&9${VARIABLE} is ${VALUE}";
	protected String messageInfoListLookup = "&9${VARIABLE} is set to [${VALUE}]";
	protected String messageInfoInvalid = "&cYou must enter a valid setting (${VALIDSETTINGS})";
	protected String messageVersion = "&9AutoSave v${VERSION}, Instance ${UUID}";
	protected String messageWarning = "&9Warning, AutoSave will commence soon.";

	// Values
	protected String valueOn = "on";
	protected String valueOff = "off";

	// Variables
	protected UUID varUuid;
	protected boolean varReport = true;
	protected int varInterval = 300;
	protected List<Integer> varWarnTimes = null;
	protected boolean varBroadcast = true;
	protected boolean varDebug = false;
	protected List<String> varWorlds = null;
	protected Mode varMode = Mode.SYNCHRONOUS;

	public void load() {

		// Messages
		messageBroadcastPre = config.getString("messages.broadcast.pre", messageBroadcastPre);
		messageBroadcastPost = config.getString("messages.broadcast.post", messageBroadcastPost);
		messageStatusFail = config.getString("messages.status.fail", messageStatusFail);
		messageStatusNotRun = config.getString("messages.status.notrun", messageStatusNotRun);
		messageStatusSuccess = config.getString("messages.status.success", messageStatusSuccess);
		messageStatusOff = config.getString("messages.status.off", messageStatusOff);
		messageInsufficientPermissions = config.getString("messages.insufficentpermissions", messageInsufficientPermissions);
		messageStopping = config.getString("messages.stopping", messageStopping);
		messageStarting = config.getString("messages.starting", messageStarting);
		messageInfoNaN = config.getString("messages.info.nan", messageInfoNaN);
		messageInfoChangeSuccess = config.getString("messages.info.changesuccess", messageInfoChangeSuccess);
		messageInfoLookup = config.getString("messages.info.lookup", messageInfoLookup);
		messageInfoListLookup = config.getString("messages.info.listlookup", messageInfoListLookup);
		messageInfoInvalid = config.getString("messages.info.invalid", messageInfoInvalid);
		messageVersion = config.getString("messages.version", messageVersion);
		messageWarning = config.getString("messages.warning", messageWarning);

		// Values
		valueOn = config.getString("value.on", valueOn);
		valueOff = config.getString("value.off", valueOff);

		// Variables
		varDebug = config.getBoolean("var.debug", varDebug);
		varBroadcast = config.getBoolean("var.broadcast", varBroadcast);
		varInterval = config.getInt("var.interval", varInterval);
		varMode = Mode.valueOf(config.getString("var.mode", varMode.name()));

		varWorlds = config.getStringList("var.worlds", null);
		if (varWorlds.size() == 0) {
			varWorlds.add("*");
			config.setProperty("var.worlds", varWorlds);
		}

		varWarnTimes = config.getIntList("var.warntime", null);
		if (varWarnTimes.size() == 0) {
			varWarnTimes.add(0);
			config.setProperty("var.warntime", varWarnTimes);
		}

		varUuid = UUID.fromString(config.getString("var.uuid", UUID.randomUUID().toString()));
		varReport = config.getBoolean("var.report", varReport);

		config.save();
	}
	
	public void save() {
		// Values
		config.setProperty("value.on", valueOn);
		config.setProperty("value.off", valueOff);

		// Variables
		config.setProperty("var.debug", varDebug);
		config.setProperty("var.broadcast", varBroadcast);
		config.setProperty("var.interval", varInterval);
		config.setProperty("var.mode", varMode.name());
		config.setProperty("var.worlds", varWorlds);
		config.setProperty("var.warntime", varWarnTimes);
		config.setProperty("var.report", varReport);
		
		config.save();
	}
}
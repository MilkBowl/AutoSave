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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.util.config.Configuration;

public class AutoSaveConfig {
	
	Configuration config;
	
	public AutoSaveConfig(Configuration config) {
		this.config = config;
	}

    // Messages
    protected String messageBroadcastPre = "&9World AutoSaving";
    protected String messageBroadcastPost = "&9World AutoSave Complete";
    protected String messageStatusFail = "&9AutoSave has stopped, check the server logs for more info";
    protected String messageStatusNotRun = "&9AutoSave is running but has not yet saved.";
    protected String messageStatusSuccess = "&9AutoSave is running and last saved at {%DATE%}.";
    protected String messageStatusOff = "&9AutoSave is not running (disabled)";
    protected String messageInsufficientPermissions = "&cYou do not have access to that command.";
    protected String messageStopping = "&9Stopping AutoSave";
    protected String messageStarting = "&9Starting AutoSave";
    protected String messageSaveWorlds = "&9{%NUMSAVED%} Worlds Saved";
    protected String messageSavePlayers = "&9Players Saved";
    protected String messageInfoNaN = "&cYou must enter a valid number, ex: 300";
    protected String messageInfoChangeSuccess = "&9${VARIABLE} has been updated.";
    protected String messageInfoLookup = "&9${VARIABLE} is ${VALUE}";
    protected String messageInfoListLookup = "&9${VARIABLE} is set to [${VALUE}]";
    protected String messageInfoInvalid = "&cYou must enter a valid setting (${VALIDSETTINGS})";
    protected String messageVersion = "&9AutoSave v{%VERSION%}, Instance {%UUID%}";
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
    	messageSaveWorlds = config.getString("messages.save.worlds", messageSaveWorlds);
    	messageSavePlayers = config.getString("messages.save.players", messageSavePlayers);
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
        if(varWorlds == null) {
        	varWorlds = new ArrayList<String>();
        	varWorlds.add("*");
        	config.setProperty("var.worlds", varWorlds);
        }
        
        varWarnTimes = config.getIntList("var.warntime", null);
        if(varWorlds == null) {
        	varWarnTimes = new ArrayList<Integer>();
        	varWarnTimes.add(0);
        	config.setProperty("var.warntime", varWarnTimes);
        }

        varUuid = UUID.fromString(config.getString("var.uuid", UUID.randomUUID().toString()));
        varReport = config.getBoolean("var.report", varReport);
        
        config.save();
    }
}
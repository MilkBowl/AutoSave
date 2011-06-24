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

package net.milkbowl.autosave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;

public class AutoSaveConfig {

    // Colors
    private static final Map<String, ChatColor> COLOR_MAP = new HashMap<String, ChatColor>();
    static {
        COLOR_MAP.put("%AQUA%", ChatColor.AQUA);
        COLOR_MAP.put("%BLACK%", ChatColor.BLACK);
        COLOR_MAP.put("%BLUE%", ChatColor.BLUE);
        COLOR_MAP.put("%DARK_AQUA%", ChatColor.DARK_AQUA);
        COLOR_MAP.put("%DARK_BLUE%", ChatColor.DARK_BLUE);
        COLOR_MAP.put("%DARK_GRAY%", ChatColor.DARK_GRAY);
        COLOR_MAP.put("%DARK_GREEN%", ChatColor.DARK_GREEN);
        COLOR_MAP.put("%DARK_PURPLE%", ChatColor.DARK_PURPLE);
        COLOR_MAP.put("%DARK_RED%", ChatColor.DARK_RED);
        COLOR_MAP.put("%GOLD%", ChatColor.GOLD);
        COLOR_MAP.put("%GRAY%", ChatColor.GRAY);
        COLOR_MAP.put("%GREEN%", ChatColor.GREEN);
        COLOR_MAP.put("%LIGHT_PURPLE%", ChatColor.LIGHT_PURPLE);
        COLOR_MAP.put("%RED%", ChatColor.RED);
        COLOR_MAP.put("%WHITE%", ChatColor.WHITE);
        COLOR_MAP.put("%YELLOW%", ChatColor.YELLOW);
    }

    // Messages
    protected String messageBroadcastPre = "%BLUE%World Auto-Saving";
    protected String messageBroadcastPost = "%BLUE%World Auto-Save Complete";
    protected String messageStatusFail = "%BLUE%Auto Save has stopped, check the server logs for more info";
    protected String messageStatusNotRun = "%BLUE%Auto Save is running but has not yet saved.";
    protected String messageStatusSuccess = "%BLUE%Auto Save is running and last saved at {%DATE%}.";
    protected String messageStatusOff = "%BLUE%Auto Save is not running (disabled)";
    protected String messageInsufficientPermissions = "%RED%You do not have access to that command.";
    protected String messageStopping = "%BLUE%Stopping Auto Saves";
    protected String messageStarting = "%BLUE%Starting Auto Saves";
    protected String messageSaveWorlds = "%BLUE%{%NUMSAVED%} Worlds Saved";
    protected String messageSavePlayers = "%BLUE%Players Saved";
    protected String messageIntervalNotANnumber = "%RED%You must enter a valid number, ex: /save interval 300";
    protected String messageIntervalChangeSuccess = "%BLUE%Auto Save interval is now {%INTERVAL%}";
    protected String messageIntervalLookup = "%BLUE%Auto Save interval is {%INTERVAL%}";
    protected String messageWarnNotANnumber = "%RED%You must enter a valid number, ex: /save warn 300";
    protected String messageWarnChangeSuccess = "%BLUE%Auto Save warning time is now {%WARN%}";
    protected String messageWarnLookup = "%BLUE%Auto Save warning time is {%WARN%}";
    protected String messageBroadcastChangeSuccess = "%BLUE%Auto Save broadcast is now {%BROADCAST%}";
    protected String messageBroadcastLookup = "%BLUE%Auto Save broadcast is {%BROADCAST%}";
    protected String messageBroadcastNotValid = "%RED%You must enter a valid setting ({%ON%}, {%OFF%})";
    protected String messageDebugChangeSuccess = "%BLUE%Auto Save debug is now {%DEBUG%}";
    protected String messageDebugLookup = "%BLUE%Auto Save debug is {%DEBUG%}";
    protected String messageDebugNotValid = "%RED%You must enter a valid setting ({%ON%}, {%OFF%})";
    protected String messageWorldChangeSuccess = "%BLUE%World Save List is now {%WORLDS%}";
    protected String messageWorldLookup = "%BLUE%World Save List is {%WORLDS%}";
    protected String messageVersion = "%BLUE%AutoSave v{%VERSION%}, Instance {%UUID%}";
    protected String messageWarning = "%BLUE%Warning, AutoSave will commence soon.";
    protected String messageReportLookup = "%BLUE%Auto Save report is {%REPORT%}";
    protected String messageReportNotValid = "%RED%You must enter a valid setting ({%ON%}, {%OFF%})";
    protected String messageReportChangeSuccess = "%BLUE%Auto Save report is now {%REPORT%}";

    // Values
    public String valueOn = "on";
    public String valueOff = "off";

    // Variables
    public UUID varUuid;
    public boolean varReport = true;
    public int varInterval = 300;
    public ArrayList<Integer> varWarnTimes = null;
    public boolean varBroadcast = true;
    public boolean varPermissions = true;
    public boolean varDebug = false;
    public ArrayList<String> varWorlds = null;

    // Parse Colors
    private String parseColor(String s) {
        for (String key : COLOR_MAP.keySet()) {
            s = s.replaceAll(key, COLOR_MAP.get(key).toString());
        }

        return s;
    }

    // Accessors & Mutators
    public String getMessageBroadcastPre() {
        return parseColor(messageBroadcastPre);
    }
    public void setMessageBroadcastPre(String messageBroadcastPre) {
        this.messageBroadcastPre = messageBroadcastPre;
    }
    
    public String getMessageBroadcastPost() {
        return parseColor(messageBroadcastPost);
    }
    public void setMessageBroadcastPost(String messageBroadcastPost) {
        this.messageBroadcastPost = messageBroadcastPost;
    }
    
    public String getMessageStatusFail() {
        return parseColor(messageStatusFail);
    }
    public void setMessageStatusFail(String messageStatusFail) {
        this.messageStatusFail = messageStatusFail;
    }
    
    public String getMessageStatusNotRun() {
        return parseColor(messageStatusNotRun);
    }
    public void setMessageStatusNotRun(String messageStatusNotRun) {
        this.messageStatusNotRun = messageStatusNotRun;
    }
    
    public String getMessageStatusSuccess() {
        return parseColor(messageStatusSuccess);
    }
    public void setMessageStatusSuccess(String messageStatusSuccess) {
        this.messageStatusSuccess = messageStatusSuccess;
    }
    
    public String getMessageStatusOff() {
        return parseColor(messageStatusOff);
    }
    public void setMessageStatusOff(String messageStatusOff) {
        this.messageStatusOff = messageStatusOff;
    }
    
    public String getMessageInsufficientPermissions() {
        return parseColor(messageInsufficientPermissions);
    }
    public void setMessageInsufficientPermissions(String messageInsufficientPermissions) {
        this.messageInsufficientPermissions = messageInsufficientPermissions;
    }
    
    public String getMessageStopping() {
        return parseColor(messageStopping);
    }
    public void setMessageStopping(String messageStopping) {
        this.messageStopping = messageStopping;
    }
    
    public String getMessageStarting() {
        return parseColor(messageStarting);
    }
    public void setMessageStarting(String messageStarting) {
        this.messageStarting = messageStarting;
    }
    
    public String getMessageSaveWorlds() {
        return parseColor(messageSaveWorlds);
    }
    public void setMessageSaveWorlds(String messageSaveWorlds) {
        this.messageSaveWorlds = messageSaveWorlds;
    }
    
    public String getMessageSavePlayers() {
        return parseColor(messageSavePlayers);
    }
    public void setMessageSavePlayers(String messageSavePlayers) {
        this.messageSavePlayers = messageSavePlayers;
    }
    
    public String getMessageIntervalNotANnumber() {
        return parseColor(messageIntervalNotANnumber);
    }
    public void setMessageIntervalNotANnumber(String messageIntervalNotANnumber) {
        this.messageIntervalNotANnumber = messageIntervalNotANnumber;
    }
    
    public String getMessageIntervalChangeSuccess() {
        return parseColor(messageIntervalChangeSuccess);
    }
    public void setMessageIntervalChangeSuccess(String messageIntervalChangeSuccess) {
        this.messageIntervalChangeSuccess = messageIntervalChangeSuccess;
    }
    
    public String getMessageIntervalLookup() {
        return parseColor(messageIntervalLookup);
    }
    public void setMessageIntervalLookup(String messageIntervalLookup) {
        this.messageIntervalLookup = messageIntervalLookup;
    }
    
    public String getMessageWarnNotANnumber() {
        return parseColor(messageWarnNotANnumber);
    }
    public void getMessageWarnNotANnumber(String messageWarnNotANnumber) {
        this.messageWarnNotANnumber = messageWarnNotANnumber;
    }
    
    public String getMessageWarnChangeSuccess() {
        return parseColor(messageWarnChangeSuccess);
    }
    public void setMessageWarnChangeSuccess(String messageWarnChangeSuccess) {
        this.messageWarnChangeSuccess = messageWarnChangeSuccess;
    }
    
    public String getMessageWarnLookup() {
        return parseColor(messageWarnLookup);
    }
    public void setMessageWarnLookup(String messageWarnLookup) {
        this.messageWarnLookup = messageWarnLookup;
    }
    
    public String getMessageBroadcastChangeSuccess() {
        return parseColor(messageBroadcastChangeSuccess);
    }
    public void setMessageBroadcastChangeSuccess(String messageBroadcastChangeSuccess) {
        this.messageBroadcastChangeSuccess = messageBroadcastChangeSuccess;
    }
    
    public String getMessageBroadcastLookup() {
        return parseColor(messageBroadcastLookup);
    }
    public void setMessageBroadcastLookup(String messageBroadcastLookup) {
        this.messageBroadcastLookup = messageBroadcastLookup;
    }
    
    public String getMessageBroadcastNotValid() {
        return parseColor(messageBroadcastNotValid);
    }
    public void setMessageBroadcastNotValid(String messageBroadcastNotValid) {
        this.messageBroadcastNotValid = messageBroadcastNotValid;
    }
    
    public String getMessageDebugChangeSuccess() {
        return parseColor(messageDebugChangeSuccess);
    }
    public void setMessageDebugChangeSuccess(String messageDebugChangeSuccess) {
        this.messageDebugChangeSuccess = messageDebugChangeSuccess;
    }
    
    public String getMessageDebugLookup() {
        return parseColor(messageDebugLookup);
    }
    public void setMessageDebugLookup(String messageDebugLookup) {
        this.messageDebugLookup = messageDebugLookup;
    }
    
    public String getMessageDebugNotValid() {
        return parseColor(messageDebugNotValid);
    }
    public void setMessageDebugNotValid(String messageDebugNotValid) {
        this.messageDebugNotValid = messageDebugNotValid;
    }
    
    public String getMessageWorldChangeSuccess() {
        return parseColor(messageWorldChangeSuccess);
    }
    public void setMessageWorldChangeSuccess(String messageWorldChangeSuccess) {
        this.messageWorldChangeSuccess = messageWorldChangeSuccess;
    }
    
    public String getMessageWorldLookup() {
        return parseColor(messageWorldLookup);
    }
    public void setMessageWorldLookup(String messageWorldLookup) {
        this.messageWorldLookup = messageWorldLookup;
    }
    
    public String getMessageVersion() {
        return parseColor(messageVersion);
    }
    public void setMessageVersion(String messageVersion) {
        this.messageVersion = messageVersion;
    }
    
    public String getMessageWarning() {
        return parseColor(messageWarning);
    }
    public void setMessageWarning(String messageWarning) {
        this.messageWarning = messageWarning;
    }
    
    public String getMessageReportLookup() {
        return parseColor(messageReportLookup);
    }
    public void setMessageReportLookup(String messageReportLookup) {
        this.messageReportLookup = messageReportLookup;
    }
    
    public String getMessageReportNotValid() {
        return parseColor(messageReportNotValid);
    }
    public void setMessageReportNotValid(String messageReportNotValid) {
        this.messageReportNotValid = messageReportNotValid;
    }
    
    public String getMessageReportChangeSuccess() {
        return parseColor(messageReportChangeSuccess);
    }
    public void setMessageReportChangeSuccess(String messageReportChangeSuccess) {
        this.messageReportChangeSuccess = messageReportChangeSuccess;
    }
}
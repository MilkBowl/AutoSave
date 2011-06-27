/**
 * Copyright 2011 Morgan Humes
 *
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ or send
 * a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View,
 * California, 94041, USA.
 *
 */

package net.milkbowl.vault.modules.economy;

import java.util.logging.Logger;

public interface Economy {
    
    public static final Logger log = Logger.getLogger("Minecraft");

    public boolean isEnabled();
    public String getName();
    public String format(double amount);
    public EconomyResponse getBalance(String playerName);
    public EconomyResponse withdrawPlayer(String playerName, double amount);
    public EconomyResponse depositPlayer(String playerName, double amount);
}
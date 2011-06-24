package net.milkbowl.vault.v0.modules.economy;

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
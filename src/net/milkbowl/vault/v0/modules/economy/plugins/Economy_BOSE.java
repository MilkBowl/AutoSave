package net.milkbowl.vault.v0.modules.economy.plugins;

import net.milkbowl.vault.v0.modules.economy.Economy;
import net.milkbowl.vault.v0.modules.economy.EconomyResponse;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;


import cosine.boseconomy.BOSEconomy;

public class Economy_BOSE implements Economy {
    private String name = "BOSEconomy";
    private Plugin plugin = null;
    private PluginManager pluginManager = null;
    private BOSEconomy economy = null;
    private EconomyServerListener economyServerListener = null;

    public Economy_BOSE(Plugin plugin) {
        this.plugin = plugin;
        pluginManager = this.plugin.getServer().getPluginManager();

        economyServerListener = new EconomyServerListener(this);

        this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, economyServerListener, Priority.Monitor, plugin);
        this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, economyServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if (economy == null) {
            Plugin bose = plugin.getServer().getPluginManager().getPlugin("BOSEconomy");
            if (bose != null && bose.isEnabled()) {
                economy = (BOSEconomy) bose;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEnabled() {
        if(economy == null) {
            return false;
        } else {
            return economy.isEnabled();
        }
    }

    @Override
    public EconomyResponse getBalance(String playerName) {
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;
        
        balance = (double) economy.getPlayerMoney(playerName);
        type = EconomyResponse.ResponseType.SUCCESS;

        return new EconomyResponse(balance, balance, type, errorMessage);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;
        
        if(amount < 0) {
            errorMessage = "Cannot withdraw negative funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;
            balance = (double) economy.getPlayerMoney(playerName);
            
            return new EconomyResponse(balance, balance, type, errorMessage);
        }
        
        amount = Math.ceil(amount);
        balance = (double) economy.getPlayerMoney(playerName);
        if(balance - amount < 0) {
            errorMessage = "Insufficient funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;
            balance = (double) economy.getPlayerMoney(playerName);
            
            return new EconomyResponse(balance, balance, type, errorMessage);
        }
        if(economy.setPlayerMoney(playerName, (int) (balance - amount), false)) {
            type = EconomyResponse.ResponseType.SUCCESS;
            balance = (double) economy.getPlayerMoney(playerName);
            
            return new EconomyResponse(amount, balance, type, errorMessage);
        } else {
            errorMessage = "Error withdrawing funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;
            balance = (double) economy.getPlayerMoney(playerName);
            
            return new EconomyResponse(amount, balance, type, errorMessage);
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;
        
        if(amount < 0) {
            errorMessage = "Cannot deposit negative funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;
            balance = (double) economy.getPlayerMoney(playerName);
            
            return new EconomyResponse(balance, balance, type, errorMessage);
        }
        amount = Math.ceil(amount);
        balance = (double) economy.getPlayerMoney(playerName);
        if(economy.setPlayerMoney(playerName, (int) (balance + amount), false)) {
            type = EconomyResponse.ResponseType.SUCCESS;
            balance = (double) economy.getPlayerMoney(playerName);
            
            return new EconomyResponse(amount, balance, type, errorMessage);
        } else {
            errorMessage = "Error withdrawing funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;
            balance = (double) economy.getPlayerMoney(playerName);
            
            return new EconomyResponse(balance, balance, type, errorMessage);
        }
    }

    public String getMoneyNamePlural() {
        return economy.getMoneyNamePlural();
    }

    public String getMoneyNameSingular() {
        return economy.getMoneyName();
    }
    
    private class EconomyServerListener extends ServerListener {
        Economy_BOSE economy = null;
        
        public EconomyServerListener(Economy_BOSE economy) {
            this.economy = economy;
        }
        
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin bose = plugin.getServer().getPluginManager().getPlugin("BOSEconomy");

                if (bose != null && bose.isEnabled()) {
                    economy.economy = (BOSEconomy) bose;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
        
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals("Essentials")) {
                    economy.economy = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }

    @Override
    public String format(double amount) {
        if (amount == 1) {
            return String.format("%f %s", amount, getMoneyNameSingular());
        } else {
            return String.format("%f %s", amount, getMoneyNamePlural());
        }
    }
}

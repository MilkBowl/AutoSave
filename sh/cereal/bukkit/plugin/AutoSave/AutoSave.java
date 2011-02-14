package sh.cereal.bukkit.plugin.AutoSave;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoSave extends JavaPlugin {
	protected final Logger log = Logger.getLogger("Minecraft");
	private PluginDescriptionFile pdfFile = this.getDescription();
	private AutoSaveThread saveThread = null;

	public AutoSave(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		saveThread.setRun(false);
		try {
			saveThread.join(5000);
		} catch (InterruptedException e) {
			log.info("Could not stop AutoSaveThread");
		}
		
		log.info(String.format("[%s] Version %s is disabled!", pdfFile.getName(), pdfFile.getVersion()));
	}

	@Override
	public void onEnable() {
		// Notify on logger load
		log.info(String.format("[%s] Version %s is enabled!", pdfFile.getName(), pdfFile.getVersion()));
		
		// Load configuration
		
		// Start our thread
		saveThread = new AutoSaveThread(this);
		saveThread.start();
		log.info(String.format("[%s] AutoSaveThread started", pdfFile.getName()));
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String commandName = command.getName().toLowerCase();


        if (commandName.equals("save")) {
        	sender.sendMessage(String.format("%s%s", ChatColor.BLUE, "Save Complete"));
        	return save();
        }
        return false;
    }
    
    public boolean save() {
    	return CommandHelper.queueConsoleCommand(getServer(), "save-all");
    }
	
}
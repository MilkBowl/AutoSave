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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class ReportThread extends Thread {
	private String statsBaseUrl;
	protected final Logger log = Logger.getLogger("Minecraft");
	private static final int REPORT_DELAY = 21600;
	
	private JavaPlugin plugin = null;
	private UUID uuid = null;
	private boolean debug = false;
	private boolean run = true;
	
	public ReportThread(JavaPlugin plugin, UUID uuid, boolean debug) {
		this.plugin = plugin;
		this.uuid = uuid;
		this.debug = debug;
		this.statsBaseUrl = "http://stats.cereal.sh/";
	}
	
	public ReportThread(JavaPlugin plugin, UUID uuid, boolean debug, String statsBaseUrl) {
		this(plugin, uuid, debug);
		this.statsBaseUrl = statsBaseUrl;
	}
	
	public void setRun(boolean run) {
		this.run = run;
	}
	
	public void run() {
		// Obtain values
		String osName = System.getProperty("os.name");
		String osArch = System.getProperty("os.arch");
		String osVersion = System.getProperty("os.version");
		String java = System.getProperty("java.vendor") + " " + System.getProperty("java.version");
		String pluginName = plugin.getDescription().getName();
		String pluginVersion = plugin.getDescription().getVersion();
		String bukkitVersion = plugin.getServer().getVersion();
		
		if(debug) {
			log.info(String.format("[%s] Start of ReportThread", pluginName));
		}
		
		while (true) {
			try {
				URL statsUrl = new URL( String.format(
									"%s?uuid=%s&plugin=%s&version=%s&bVersion=%s&osName=%s&osArch=%s&osVersion=%s&java=%s",
									statsBaseUrl,
									URLEncoder.encode(uuid.toString(), "ISO-8859-1"),
									URLEncoder.encode(pluginName, "ISO-8859-1"),
									URLEncoder.encode(pluginVersion, "ISO-8859-1"),
									URLEncoder.encode(bukkitVersion, "ISO-8859-1"),
									URLEncoder.encode(osName, "ISO-8859-1"),
									URLEncoder.encode(osArch, "ISO-8859-1"),
									URLEncoder.encode(osVersion, "ISO-8859-1"),
									URLEncoder.encode(java, "ISO-8859-1")
								));
				URLConnection conn = statsUrl.openConnection();
				conn.setRequestProperty("User-Agent", String.format("%s %s:%s", "BukkitReport", pluginName, pluginVersion));
				String inputLine;
				BufferedReader in = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));

				if (debug) {
					// Output the contents -- wee
					log.info(String.format("[%s] StatsURL: %s", pluginName, statsUrl.toString()));
					while ((inputLine = in.readLine()) != null) {
						if (inputLine.equals("<pre>") || inputLine.equals("</pre>")) {
							continue;
						}
						log.info(String.format("[%s] %s", pluginName, inputLine));
					}
				}

				in.close();
				
				// Sleep for 6 hours...
				for (int i = 0; i < REPORT_DELAY; i++) {
					if(!run) {
						if(debug) {
							log.info(String.format("[%s] Graceful quit of ReportThread", pluginName));
						}
						return;
					}
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				// Ignore it...really its just not important
			}
		}
	}
}

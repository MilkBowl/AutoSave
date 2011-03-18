package sh.cereal.bukkit.plugin.AutoSave;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Logger;

public class ReportThread extends Thread {
	private static final String STATS_URL = "http://stats.cereal.sh/";
	protected final Logger log = Logger.getLogger("Minecraft");
	private static final int REPORT_DELAY = 21600;
	
	private AutoSave plugin = null;
	private AutoSaveConfig config = null;
	private boolean run = true;
	
	public ReportThread(AutoSave plugin, AutoSaveConfig config) {
		this.plugin = plugin;
		this.config = config;
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
		
		if(config.varDebug) {
			log.info(String.format("[%s] Start of ReportThread", pluginName));
		}
		
		while (true) {
			try {
				URL statsUrl = new URL( String.format(
									"%s?uuid=%s&plugin=%s&version=%s&bVersion=%s&osName=%s&osArch=%s&osVersion=%s&java=%s",
									STATS_URL,
									URLEncoder.encode(config.varUuid.toString(), "ISO-8859-1"),
									URLEncoder.encode(pluginName, "ISO-8859-1"),
									URLEncoder.encode(pluginVersion, "ISO-8859-1"),
									URLEncoder.encode(bukkitVersion, "ISO-8859-1"),
									URLEncoder.encode(osName, "ISO-8859-1"),
									URLEncoder.encode(osArch, "ISO-8859-1"),
									URLEncoder.encode(osVersion, "ISO-8859-1"),
									URLEncoder.encode(java, "ISO-8859-1")
								));
				URLConnection conn = statsUrl.openConnection();
				String inputLine;
				BufferedReader in = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));

				if (config.varDebug) {
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
						if(config.varDebug) {
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

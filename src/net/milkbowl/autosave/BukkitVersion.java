package net.milkbowl.autosave;

public class BukkitVersion {
	public String version = "";
	public boolean recommendedBuild = false;
	public int buildNumber = 0;
	public boolean supported = false;
	
	public BukkitVersion(String version, boolean recommendedBuild, int buildNumber, boolean supported) {
		this.version = version;
		this.recommendedBuild = recommendedBuild;
		this.buildNumber = buildNumber;
		this.supported = supported;
	}
}

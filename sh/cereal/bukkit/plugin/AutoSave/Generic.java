package sh.cereal.bukkit.plugin.AutoSave;

import java.util.List;

public class Generic {
    public static boolean stringArrayContains(String base, String[] comparesWith) {

        for (int i = 0; i < comparesWith.length; i++) {
            try {
                if (base.compareTo(comparesWith[i]) == 0) {
                    return true;
                }
            } catch (NullPointerException npe) {
                return false;
            }
        }

        return false;
    }
    
	public static String combine(String glue, List<String> s) {
		int k = s.size();
		if (k == 0) {
			return null;
		}
		StringBuilder out = new StringBuilder();
		out.append(s.get(0));
		for (int x = 1; x < k; ++x) {
			out.append(glue).append(s.get(x));
		}
		return out.toString();
	}
}
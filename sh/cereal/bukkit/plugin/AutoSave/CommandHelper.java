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

package sh.cereal.bukkit.plugin.AutoSave;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;

import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftServer;


public class CommandHelper {

	private static final Logger log = Logger.getLogger("Minecraft");
	
	public static boolean queueConsoleCommand(Server server, String cmd) {
		
		if(server instanceof CraftServer) {
			CraftServer cs = (CraftServer) server;
			Field f;
			try {
				f = CraftServer.class.getDeclaredField("console");
			} catch(NoSuchFieldException ex) {
				log.info("NoSuchFieldException");
				return false;
			} catch(SecurityException e) {
				log.info("SecurityException");
				return false;
			}
			
			MinecraftServer ms;
			try {
				f.setAccessible(true);
				ms = (MinecraftServer) f.get(cs);
			} catch (IllegalArgumentException ex) {
				log.info("IllegalArgumentException");
				return false;
			} catch (IllegalAccessException ex) {
				log.info("IllegalAccessException");
				return false;
			}
			if ((!ms.g) && (MinecraftServer.a(ms))) {
				log.info(String.format("Performing console command %s", cmd));
				ms.a(cmd, ms);
				return true;
			}
		}
		
		return false;
		
	}
	
}

package hr.fer.ooup.lab3.plugins;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import javax.swing.*;

public class PluginFactory {
	
	public static String pluginPath = "./src/hr/fer/ooup/lab3/plugins/pluginsFolder";
	
	public static List<Plugin> loadPlugins() {
		List<Plugin> plugins = new LinkedList<Plugin>();
		File pluginsDirectory = new File(pluginPath);
		
		if (pluginsDirectory.isDirectory() == false) {
			//ovo nije direktorij, vrati praznu listu; nema pluginova
			return new LinkedList<Plugin>();
		}
		
		//iteriraj po svim fileovima i sve ih spremi u listu
		for(File file: pluginsDirectory.listFiles()) {
			String filename = file.toString();
			
			//ova 6 je zbog src-a, a 5 zbog .java
			filename = filename.substring(6).replace('\\', '.');
			filename = filename.substring(0, filename.length()-5);
			
			try {
				Class<?> item = Class.forName(filename);
				Constructor<?> constructor = item.getConstructor();
				Plugin plugin = (Plugin)constructor.newInstance();
				plugins.add(plugin);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e + "There is something wrong with loading plugins: " + e.getMessage(), "PLUGIN ERROR", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			
			}
		}
		
		return plugins;
	}
}

package com.nasonfish.randomplacer;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

	FileConfiguration config;
	File configFile = null;
	
	private RandomPlacer plugin;
	
	
	public Config(RandomPlacer plugin){
		this.plugin = plugin;
		config = plugin.getConfig();
	}
	
	
	public FileConfiguration getConfig() {
		this.config = plugin.getConfig();
		config.addDefault("RandomPlacer.limit.positiveX", 500);
		config.addDefault("RandomPlacer.limit.positiveZ", 500);
		config.addDefault("RandomPlacer.limit.negativeX", -500);
		config.addDefault("RandomPlacer.limit.negativeZ", -500);
		config.addDefault("RandomPlacer.Teleported", "You are now at {x}, {z}");
		config.addDefault("Error.Cooldown", "You must wait for randomplacer to cooldown! ({cooldown} second{s} left)");
		config.addDefault("RandomPlacer.cooldown", 5);
		config.options().copyDefaults(true);
		plugin.saveConfig();
		return config;
	}
}
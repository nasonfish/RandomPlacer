package me.zippy120;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

	FileConfiguration config;
	File configFile = null;
	
	private static RandomPlacer plugin;
	
	
	public Config(RandomPlacer plugin){
		Config.plugin = plugin;
		config = plugin.getConfig();
	}
	
	
	public FileConfiguration getConfig() {
		FileConfiguration config = plugin.getConfig();
		config.addDefault("RandomPlacer.limit.x", 500);
		config.addDefault("RandomPlacer.limit.z", 500);
		config.addDefault("RandomPlacer.limit.nx", -500);
		config.addDefault("RandomPlacer.limit.nz", -500);
		config.addDefault("Error.NumberConflict", "The numbers set in the config do not work. Setting to 1000...");
		config.addDefault("Error.NNumberNotSet", "The negative numbers in the config are not set. Defaulting to -1000...");
		config.addDefault("Error.NumberNotSet", "The numbers in the config are not set. Defaulting to 1000...");
		config.addDefault("Error.ConsoleSender", "You must be a player to execute this command.");
		config.addDefault("RandomPlacer.Teleported", "You are now at {x}, {z}");
		config.addDefault("Error.Cooldown", "You must wait for randomplacer to cooldown! ({cooldown} second{s} left)");
		config.addDefault("RandomPlacer.cooldown", 5);
		config.options().copyDefaults(true);
		plugin.saveConfig();
		return config;
	}
}
package com.nasonfish.randomplacer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RandomPlacer extends JavaPlugin {
	
	public static Logger log;
	
	Random r = new Random();
	
	Map<String, Long> cooldown = new HashMap<String, Long>();
	
	FileConfiguration config;
	
	Limits limits;
	
	Config ourConfig;
	
	/**
	 * 
	 */
	public void onEnable(){
		log = this.getLogger();
		log.info("Loading RandomPlacer config...");
		ourConfig = new Config(this);
		this.config = ourConfig.getConfig();
		limits = new Limits(
				config.getInt("RandomPlacer.limit.positiveX"),
				config.getInt("RandomPlacer.limit.positiveZ"),
				config.getInt("RandomPlacer.limit.negativeX"),
				config.getInt("RandomPlacer.limit.negativeZ")
				);
		
	}
	

	 /**
	  * 
	  */
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		// We don't need to check if the command is tpr, we only have one command.
		
		if(reload(args, sender)){
			return true;
		}
		
		if(!hasPermission(sender)){
			return true;
		}
		
		// Alright, so they're trying to teleport.
		if(notPlayer(sender)){ // They can't use this from the console
			return true;
		}
		
		Player player = (Player) sender;
		
		if(isCooldown(player)){ // They might be on cooldown from last time they used it.
			return true;
		}
		
		Limits theLimits = this.limits;
		
		if(hasSetCoordsForWorld(player.getWorld())){
			theLimits = getLimitsForWorld(player.getWorld());
		}
		
		Location teleportTo = getLocationToTeleport(theLimits, player.getWorld());
		cooldown.put(player.getName(), System.currentTimeMillis() + (config.getInt("RandomPlacer.cooldown") * 1000));
		player.teleport(teleportTo);
		player.sendMessage( pMsg(config.getString("RandomPlacer.Teleported").replace("{x}", teleportTo.getBlockX() + "").replace("{z}", teleportTo.getBlockZ()+ "")));
		return true;
	}
	
	/**
	 * Check if the user has permission to use /tpr
	 * @param sender
	 * @return
	 */
	private boolean hasPermission(CommandSender sender) {
		if(sender.hasPermission("RandomPlacer.use")){
			return true;
		}
		sender.sendMessage(pErr("You do not have permission to use RandomPlacer!"));
		return false;
	}


	/**
	 * Get where we should teleport the player to
	 * @param lims
	 * @param world
	 * @return
	 */
	private Location getLocationToTeleport(Limits lims, World world) {
		int x = r.nextInt(lims.getHighX() - lims.getLowX() + 1) + lims.getLowX();
		int z = r.nextInt(lims.getHighZ() - lims.getLowZ() + 1) + lims.getLowZ();
		int y = world.getHighestBlockYAt(x, z) + 2;
		return new Location(world, x, y, z);
	}
	
	/**
	 * Get the limits for a specified world they exist and the numbers are not backwards.
	 * @param world
	 * @return
	 */
	private Limits getLimitsForWorld(World world) {
		Limits lims = new Limits(
				config.getInt("RandomPlacer."+world.getName()+"limit.postiveX"),
				config.getInt("RandomPlacer."+world.getName()+"limit.Z"),
				config.getInt("RandomPlacer."+world.getName()+"limit.negativeX"),
				config.getInt("RandomPlacer."+world.getName()+"limit.negativeZ")
				);
		
		if(lims.getHighX() < lims.getLowX() || lims.getHighZ() < lims.getLowZ()){
			log.info("RandomPlacer's config values for " + world + " do not work, as a higher number conflicts with a lower one.");
			log.info("Please check your config for this error, then reload the config with /tpr reload.");
			return this.limits;
		}
		
		return lims;
	}
	
	/**
	 * Check if the player is on cooldown
	 * @param player
	 * @return
	 */
	private boolean isCooldown(Player player) {
		
		if(!cooldown.containsKey(player.getName())){
			return false;
		}
		
		if(cooldown.get(player.getName()) >= System.currentTimeMillis()){
			player.sendMessage(pErr("You must wait for RandomPlacer to cooldown! (" + getNextUseTime(player) + " more seconds)."));
			return true;
		}
		
		cooldown.remove(player.getName());
		return false;
		
	}
	
	/**
	 * if all the coords for this world are set.
	 * @param world
	 * @return
	 */
	private boolean hasSetCoordsForWorld(World world){
		if(config.isSet("RandomPlacer."+world.getName()+"limit.x"))
			if(config.isSet("RandomPlacer."+world.getName()+"limit.z"))
				if(config.isSet("RandomPlacer."+world.getName()+"limit.nx"))
					if(config.isSet("RandomPlacer."+world.getName()+"limit.nz"))
						return true;
		return false;
	}
	
	/**
	 * Check if they are not a player.
	 * @param sender
	 * @return
	 */
	private boolean notPlayer(CommandSender sender) {
		if(!(sender instanceof Player)){
			sender.sendMessage(pErr("You must be a player to use RandomPlacer."));
		}
		return !(sender instanceof Player);
	}
	/**
	 * 
	 * @param sender 
	 * @param reload 
	 * @return
	 */
	private boolean reload(String[] reload, CommandSender sender) {
		if(reload.length > 0 && reload[0].equalsIgnoreCase("reload")){
			if(sender.hasPermission("RandomPlacer.reload")){
				this.config = ourConfig.getConfig();
				sender.sendMessage(pMsg( "The Config has been reloaded."));
				limits = new Limits(
						config.getInt("RandomPlacer.limit.x"),
						config.getInt("RandomPlacer.limit.z"),
						config.getInt("RandomPlacer.limit.nx"),
						config.getInt("RandomPlacer.limit.nz")
						);
				return true;
			} else {
				sender.sendMessage(this.pErr("You do not have permission to reload the configuration."));
				return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * @param player
	 * @return
	 */
	private int getNextUseTime(Player player) {
		return (int) ((cooldown.get(player.getName()) - System.currentTimeMillis()) / 1000) + 1; // We round up, so we need the + 1. (Java rounds down)
	}
	/**
	 * 
	 * @param message
	 * @return
	 */
	public String pMsg(String message){
		return ChatColor.GOLD + "[RandomPlacer]: " + ChatColor.YELLOW + message;
	}
	
	/**
	 * 
	 * @param message
	 * @return
	 */
	public String pErr(String message){
		return ChatColor.GOLD + "[RandomPlacer]: " + ChatColor.RED + message;
	}
	
}

package me.zippy120;

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
	
	Logger log;
	Random r = new Random();
	Map<Player, Long> Cooldown = new HashMap<Player, Long>();
	FileConfiguration config;
	/**
	 * 
	 */
	public void onEnable(){
		log = this.getLogger();
		log.info("RandomPlacer has been enabled!");
		log.info("Loading config...");
		Config config = new Config(this);
		this.config = config.getConfig();
		
	}
	/**
	 * 
	 */
	public void onDisable(){
		log.info("RandomPlacer has been disabled.");
	}
	 /**
	  * 
	  */
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		//TODO CLEAN THIS UP!!!!!!!
		
	if ((sender instanceof Player)) {
		Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("tpr")){
				if(args.length == 1)
					if(args[0].equalsIgnoreCase("reload")){
						Config config = new Config(this);
						this.config = config.getConfig();
						player.sendMessage(pMsg( "Config Reloaded."));
						return true;
					}
				if ((!(Cooldown.containsKey(player))) || (Cooldown.containsKey(player) && Cooldown.get(player) <= ((System.currentTimeMillis())))){
					int xlimit = config.getInt("RandomPlacer.limit.x");
					int zlimit = config.getInt("RandomPlacer.limit.z");
					int nxlimit = config.getInt("RandomPlacer.limit.nx");
					int nzlimit = config.getInt("RandomPlacer.limit.nz");
					if(config.isSet("RandomPlacer."+player.getWorld().getName()+"limit.x"))
						xlimit = config.getInt("RandomPlacer."+player.getWorld().getName()+"limit.x");
					if(config.isSet("RandomPlacer."+player.getWorld().getName()+"limit.z"))
						zlimit = config.getInt("RandomPlacer."+player.getWorld().getName()+"limit.z");
					if(config.isSet("RandomPlacer."+player.getWorld().getName()+"limit.nx"))
						nxlimit = config.getInt("RandomPlacer."+player.getWorld().getName()+"limit.nx");
					if(config.isSet("RandomPlacer."+player.getWorld().getName()+"limit.nz"))
						nzlimit = config.getInt("RandomPlacer."+player.getWorld().getName()+"limit.nz");
					if (nxlimit > xlimit || nzlimit > zlimit){
						log.info(config.getString("Error.NumberConflict"));
						xlimit = 1000;
						nxlimit = -1000;
						zlimit = 1000;
						nzlimit = -1000;
					}
					int x = r.nextInt(xlimit - nxlimit + 1) + nxlimit;
					int z = r.nextInt(zlimit - nzlimit + 1) + nzlimit;
					int y = player.getWorld().getHighestBlockYAt(x, z);
					World world = player.getWorld();
					if(config.getString("RandomPlacer.world") != "all"){
						String wName = config.getString("RandomPlacer.world");
						for(World w : this.getServer().getWorlds()){
							if (w.getName().equalsIgnoreCase(wName)){
								world = w;
								break;
							}
						}
					}
					Location l = new Location(world, x, y + 2, z);
					Cooldown.put(player, System.currentTimeMillis() + (config.getInt("RandomPlacer.cooldown") * 1000));
					player.teleport(l);
					player.sendMessage( pMsg(config.getString("RandomPlacer.Teleported").replace("{x}", x + "").replace("{z}", z+ "")));
			} else {
				player.sendMessage( pErr(config.getString("Error.Cooldown").replace("{cooldown}", getNextUseTime(player) + "").replace("{s}", getNextUseTime(player) == 1 ? "" : "s")));
			}
			}
				} else log.info(config.getString("Error.ConsoleSender"));
	return true;
	}
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	private int getNextUseTime(Player player) {
		return (int) ((Cooldown.get(player) - System.currentTimeMillis()) / 1000);
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

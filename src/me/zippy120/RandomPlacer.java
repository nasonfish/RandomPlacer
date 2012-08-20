package me.zippy120;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
	
	public void onEnable(){
		
		log = this.getLogger();
		log.info("RandomPlacer has been enabled!");
		log.info("Loading config...");
		Config config = new Config(this);
		this.config = config.getConfig();
		
	}
 
	public void onDisable(){
		log.info("RandomPlacer has been disabled.");
	}
	 
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		
	if ((sender instanceof Player)) {
		Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("tpr")){
				if ((!(Cooldown.containsKey(player))) || (Cooldown.containsKey(player) && Cooldown.get(player) <= ((System.currentTimeMillis())))){
					int xlimit;
					int zlimit;
					int nxlimit;
					int nzlimit;
					xlimit = config.getInt("RandomPlacer.limit.x");					
					zlimit = config.getInt("RandomPlacer.limit.z");
					nxlimit = config.getInt("RandomPlacer.limit.nx");					
					nzlimit = config.getInt("RandomPlacer.limit.nz");
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
					Location l = new Location(player.getWorld(), x, y + 2, z);
					Cooldown.put(player, System.currentTimeMillis() + (config.getInt("RandomPlacer.cooldown") * 1000));
					player.teleport(l);
					player.sendMessage(ChatColor.GOLD + "[RandomPlacer]: " + ChatColor.YELLOW + config.getString("RandomPlacer.Teleported").replace("{x}", x + "").replace("{z}", z+ ""));
			} else {
				player.sendMessage(ChatColor.GOLD + "[RandomPlacer]: " + ChatColor.RED + config.getString("Error.Cooldown").replace("{cooldown}", "" + ((Cooldown.get(player) - System.currentTimeMillis()) / 1000)));
			}
			}
				} else log.info(config.getString("Error.ConsoleSender"));
	return true;
	}
	
}

package com.goreacraft.plugins.goreaitemspawn;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class GoreaItemspawn extends JavaPlugin{

	public static GoreaItemspawn gg;
	public static Plugin worldedit;
	public static List<String> aliases;
	
	public final Logger logger = Logger.getLogger("minecraft");

	static File spawnPointsFile;
	static YamlConfiguration spawnPoints = new YamlConfiguration();
	
	static HashMap<Integer, List<Object>> tasks = new HashMap<Integer, List<Object>>();
	static HashMap<String, Integer> tasksids = new HashMap<String, Integer>();
	//static HashMap<Integer, Entity> entityids = new HashMap<Integer, Entity>();
	protected static boolean wholeworld;
	protected static boolean unique;
	protected static boolean startspawnonsrestart;
	
	public void onEnable()
    {
		PluginDescriptionFile pdfFile = this.getDescription();
    	this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " has been enabled! " + pdfFile.getWebsite());
		getConfig().options().copyDefaults(true);
      	getConfig().options().header("If you need help with this plugin you can contact goreacraft on teamspeak ip: goreacraft.com\n Website http://www.goreacraft.com");
      	saveConfig();
 
      	worldedit= getServer().getPluginManager().getPlugin("WorldEdit");
		gg = this;
		
		spawnPointsFile = new File(getDataFolder(), "SpawnPoints.yml");
		aliases = getCommand("goreaitemspawn").getAliases();
			loadconfigs();
		if(getConfig().getBoolean("Start all spawnpoints on startup"))
		{
			Methods.startSpawnPoints();
			System.out.println("[GoreaItemspawn] Starting spawnpoints on startup enabled.");
		}
		
			//====================================== METRICS STUFF =====================================================
	      	 try {
	      		    Metrics metrics = new Metrics(this);
	      		    metrics.start();
	      		    System.out.println("[GoreaItemspawn] Metrics started");
	      		} catch (IOException e) {
	      		   System.out.println(e); // Failed to submit the stats :-(
	      		}
	      	 
	      	if(getConfig().getBoolean("ChechUpdates"))
	      	{
	   		//new Updater(79646);
	      	}
    }
	
	
	void loadconfigs(){	
	    spawnPoints = YamlConfiguration.loadConfiguration(spawnPointsFile);
	    wholeworld= getConfig().getBoolean("Check if unique in whole world");
	    unique= getConfig().getBoolean("Only one item at a time");
	    if(!spawnPointsFile.exists())
	    	{
	    	try {spawnPointsFile.createNewFile(); } 
	    	catch (IOException e) { e.printStackTrace();}	    	
	    	}
	   // Methods.setpoints();
	}

	public void onDisable()
    {
		PluginDescriptionFile pdfFile = this.getDescription();
    	this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " has been disabled!" + pdfFile.getWebsite());
    	//tasks.clear();
    	//tasksids.clear();
    	//Methods.ents.clear();
    	Bukkit.getScheduler().cancelTasks(gg); 
    }
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {        
        	Player player = ((Player) sender).getPlayer();
        	if(!player.isOp())
        	{
        		player.sendMessage(ChatColor.RED +"You dont have permissions to use this commands");
        		return true;
        	}        	
        	if (aliases.contains(label))
            {
        		if (args.length == 0)
            	{
        			showhelpplayer(player);        		
            	}
            	
        		if (args.length == 1)
            	{
        			if (args[0].equalsIgnoreCase("reload"))
        					{
        					Bukkit.getScheduler().cancelTasks(gg);
        					tasks.clear();
        					tasksids.clear();
        					Methods.ents.clear();
        					gg.reloadConfig();
        					loadconfigs();
        					sender.sendMessage(ChatColor.GREEN + "Reloaded the configs");
        					return true;
        					}
        			if (args[0].equalsIgnoreCase("check"))
					{
        				//player.sendMessage("hasItemMeta "+player.getItemInHand().getItemMeta());
        				
        				
					}
        			if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))
					{
				
        				showhelpplayer(player);
					return true;
					}
        			
        			/*if (args[0].equals("start"))
					{
        				//Bukkit.getScheduler().cancelTasks(gg);
        				Methods.startSpawnPoints();
        				player.sendMessage(ChatColor.GREEN +"Starting " + ChatColor.GOLD + spawnPoints.getKeys(false).size() + ChatColor.GREEN + " spawnpoints");
        				return true;
					}
        			
        			if (args[0].equals("stop"))
					{
        				
        				Bukkit.getScheduler().cancelTasks(gg);
        				player.sendMessage(ChatColor.RED +"Forcefully cancel all spawnitems tasks");
        				return true;
        				
					}*/
        			
        			if ( args[0].equalsIgnoreCase("list"))	
            		{
        				player.sendMessage(ChatColor.YELLOW +"" + ChatColor.ITALIC + "|ID|----|Item:Meta x ammount|--|Timer|--|Location|");
        				for(String key: spawnPoints.getKeys(false))
        				{
        				
        				
        					
        					String message = ChatColor.GOLD + key + ChatColor.RESET +" > " +ChatColor.GREEN+ spawnPoints.getConfigurationSection(key).getString("Item") 
        							+ ":" + spawnPoints.getConfigurationSection(key).getString("Meta")
        							+ "x" + spawnPoints.getConfigurationSection(key).getString("Amount")
        							+ ChatColor.YELLOW +"  T: " +ChatColor.RESET + spawnPoints.getConfigurationSection(key).getString("Timer")
        							+ ChatColor.YELLOW +"  W: " +ChatColor.RESET + spawnPoints.getConfigurationSection(key).getString("World")
        							+ ChatColor.YELLOW +"  Loc: " +ChatColor.GRAY+ ""+ChatColor.ITALIC+spawnPoints.getConfigurationSection(key).getVector("Location");
        					player.sendMessage(message);
        					
        				}
        				
        				
        				return true;
            		}
        		
            	}
        		
        	
        	
        	if (args.length == 2)
        	{        		
        		//==================
        		if ( args[0].equalsIgnoreCase("remove"))	
            	{
        			if(spawnPoints.isConfigurationSection(args[1]))
        			{
        				if(tasksids.containsKey(args[1]))
                		{            				
                			if(Bukkit.getScheduler().isQueued(tasksids.get(args[1])))
                			{
                				getServer().getScheduler().cancelTask(tasksids.get(args[1]));
                				player.sendMessage("Entry "+ ChatColor.GOLD + args[1]+ ChatColor.RESET + " stoped!");
                			}
                		}
        				spawnPoints.set(args[1], null);
        				player.sendMessage("Entry "+ ChatColor.GOLD + args[0]+ ChatColor.RESET + " removed!");
        				try {spawnPoints.save(spawnPointsFile);} 
        				catch (IOException e) {e.printStackTrace();}
        					
        			} else { player.sendMessage(ChatColor.RED +"There is no entry " + args[1]);}
        			return true;        			
            	} else
        			
        			//=================
        		if (args[0].equalsIgnoreCase("start"))
    			{
        			if (args[1].equalsIgnoreCase("all"))
        			{
        				Methods.startSpawnPoints();
        				player.sendMessage(ChatColor.GREEN +"Starting " + ChatColor.GOLD + spawnPoints.getKeys(false).size() + ChatColor.GREEN + " spawnpoints");
        				return true;
        				
        			}
        			
        			
        			
            		if(spawnPoints.getKeys(false).contains(args[1]))
            		{
            			new Runnables(args[1]);
            			player.sendMessage("Entry "+ ChatColor.GOLD + args[1]+ ChatColor.RESET + " started!");
            		} else player.sendMessage(ChatColor.RED +"There is no spawn entry with this name");
            		return true;
    			} else
    				
        			//=================
        		if (args[0].equalsIgnoreCase("tp"))
    			{
            		if(spawnPoints.getKeys(false).contains(args[1]))
            		{            		
            			player.teleport(spawnPoints.getVector(args[1] + ".Location").toLocation(Bukkit.getWorld(spawnPoints.getString(args[1] + ".World"))));            				
            			player.sendMessage("You teleported to "+ ChatColor.GOLD + args[1]+ ChatColor.RESET + " itemspawn location!");
            		} else player.sendMessage(ChatColor.RED +"There is no spawn entry with this name");
            		return true;
    			} else
        			
        			//=====================
            	if (args[0].equalsIgnoreCase("stop"))
    			{
            		if (args[1].equalsIgnoreCase("all"))
        			{            		
            			Bukkit.getScheduler().cancelTasks(gg);
            			player.sendMessage(ChatColor.RED +"Forcefully cancel all spawnitems tasks");
            			return true;
        			}
            		
            		if(tasksids.containsKey(args[1]))
            		{            				
            			if(Bukkit.getScheduler().isQueued(tasksids.get(args[1])))
            			{
            				getServer().getScheduler().cancelTask(tasksids.get(args[1]));
            				player.sendMessage("Entry "+ ChatColor.GOLD + args[1]+ ChatColor.RESET + " stoped!");
            			}
            			else player.sendMessage(ChatColor.RED +"There is no running task with this name");
            		return true;
            		}            			
    			} 
        		return false;
        		} else
        		
        		
        	if (args.length >= 3)
        	{	
            		//========================    
    			if (args[0].equals("create"))
        			{
		        		if ( player.getItemInHand().getType() != Material.AIR &&  player.getItemInHand() != null)	
		        		{		
		        			long timer = 100;        				
		        			try{ timer = Long.parseLong(args[2]);}
		        			catch (Exception ee){
		        					player.sendMessage(ChatColor.RED +"The timer argument should be a number, setting it to 100 ticks by default");
		        					player.sendMessage(ChatColor.GREEN +"Type '/gi help' for more help");
		        					
		        				}
								player.sendMessage("Setting spawnpoint " + args[1]);	
								HashMap<String, Object> values = new HashMap<String,Object>();
								 short meta = player.getItemInHand().getDurability();
								values.put("Item", player.getItemInHand().getData().getItemType().name());
								values.put("Meta", meta);
								values.put("Amount", player.getItemInHand().getAmount());
								values.put("World", player.getLocation().getWorld().getName());
								values.put("Location", player.getLocation().getBlock().getLocation().toVector());//Methods.locationToString(player.getLocation()));
								values.put("Timer", timer);
								spawnPoints.createSection(args[1], values);
								 player.sendMessage(ChatColor.GREEN +"Spawnpoint added with name: " + args[1]);
								//Methods.setpoints();
		        			} else { player.sendMessage(ChatColor.RED +"Please hold the items you want to spawn in your hand");return false;}
		        		
		        		try {
		        			spawnPoints.save(spawnPointsFile);
		        			return true;
		        		} catch (IOException e) {
		        			e.printStackTrace();
		        		}
		        		
        			}
    			return false;
        	}
        }
        } else
        if (!(sender instanceof Player))
        {
	        if (aliases.contains(label))
	        {
				if (args.length == 1)
		    	{
					if (args[0].equals("reload"))
						{
						Bukkit.getScheduler().cancelTasks(gg);
    					tasks.clear();
    					tasksids.clear();
    					Methods.ents.clear();
    					gg.reloadConfig();
    					loadconfigs();    					   					
						System.out.println("[Goreaitemspawn] Reloaded the configs");
						return true;
						}
					if ( args[0].equalsIgnoreCase("list"))	
            		{
						System.out.println("|ID|----|Item:Meta x ammount|--|Timer|--|Location|");
        				for(String key: spawnPoints.getKeys(false))
        				{
        					String message = key +" > " + spawnPoints.getConfigurationSection(key).getString("Item") 
        							+ ":" + spawnPoints.getConfigurationSection(key).getString("Meta")
        							+ "x" + spawnPoints.getConfigurationSection(key).getString("Amount")
        							+"  T: " + spawnPoints.getConfigurationSection(key).getString("Timer")
        							+"  W: " + spawnPoints.getConfigurationSection(key).getString("World")
        							+ "  Loc: " +spawnPoints.getConfigurationSection(key).getVector("Location");
        					System.out.println(message);        					
        				}
        				
        				
        				return true;
            		}
					if (args[0].equals("help") || args[0].equals("?"))
					{
						
						showhelpconsole();
						//System.out.println("[Goreaitemspawn] Will be a nice help page here soon.");
						//showhelpplayer(player);
					return true;
					}
		    	}
				if (args.length == 2)
			    	{	
					if (args[0].equalsIgnoreCase("start"))
	    			{
	        			if (args[1].equalsIgnoreCase("all"))
	        			{
	        				Methods.startSpawnPoints();
	        				System.out.println("[Goreaitemspawn] Starting "+ spawnPoints.getKeys(false).size() +" spawnpoints");
	        				return true;
	        				
	        			}
	        			
	        			
	        			
	            		if(spawnPoints.getKeys(false).contains(args[1]))
	            		{
	            			new Runnables(args[1]);
	            			System.out.println("Entry "+  args[1]+ " started!");
	            		} else System.out.println("There is no spawn entry with this name");
	            		return true;
					}
					
					if ( args[0].equalsIgnoreCase("remove"))	
	            	{
	        			if(spawnPoints.isConfigurationSection(args[1]))
	        			{
	        				if(tasksids.containsKey(args[1]))
	                		{            				
	                			if(Bukkit.getScheduler().isQueued(tasksids.get(args[1])))
	                			{
	                				getServer().getScheduler().cancelTask(tasksids.get(args[1]));
	                				System.out.println("Entry "+ args[1] + " stoped!");
	                			}
	                		}
	        				spawnPoints.set(args[1], null);
	        				System.out.println("Entry "+ args[0]+ " removed!");
	        				try {spawnPoints.save(spawnPointsFile);} 
	        				catch (IOException e) {e.printStackTrace();}
	        					
	        			} else { System.out.println("There is no entry " + args[1]);}
	        			return true;  
	            	}
					if (args[0].equalsIgnoreCase("stop"))
	    			{
	            		if (args[1].equalsIgnoreCase("all"))
	        			{            		
	            			Bukkit.getScheduler().cancelTasks(gg);
	            			System.out.println("Forcefully cancel all spawnitems tasks");
	            			return true;
	        			}
	            		
	            		if(tasksids.containsKey(args[1]))
	            		{            				
	            			if(Bukkit.getScheduler().isQueued(tasksids.get(args[1])))
	            			{
	            				getServer().getScheduler().cancelTask(tasksids.get(args[1]));
	            				System.out.println("Entry "+  args[1] + " stoped!");
	            			}
	            			else System.out.println("There is no running task with this name");
	            		return true;
	            		}            			
	    			} 
				
			    }
	    	
		
	        }
        }
		return false;
    
        
        
	
}


	private void showhelpplayer(Player player){
		
		player.sendMessage( ChatColor.YELLOW + "......................................................." + ChatColor.GOLD + " Plugin made by: "+ ChatColor.YELLOW + ".......................................................");
    	player.sendMessage( ChatColor.YELLOW + "     o   \\ o /  _ o              \\ /               o_   \\ o /   o");
    	player.sendMessage( ChatColor.YELLOW + "    /|\\     |      /\\   __o        |        o__    /\\      |     /|\\");
    	player.sendMessage( ChatColor.YELLOW + "    / \\   / \\    | \\  /) |       /o\\       |  (\\   / |    / \\   / \\");
    	player.sendMessage( ChatColor.YELLOW + "......................................................." + ChatColor.GOLD + ChatColor.BOLD + " GoreaCraft  "+ ChatColor.YELLOW + ".......................................................");
    	
    	player.sendMessage("");
    	player.sendMessage( ChatColor.YELLOW + "Aliases: " + ChatColor.LIGHT_PURPLE +  aliases );    		
		
    	player.sendMessage( ChatColor.YELLOW + "/gi :" + ChatColor.RESET + " Nothing for now");
    	
    	player.sendMessage( ChatColor.YELLOW + "/gi help/? :" + ChatColor.RESET + " Shows this! Do it again, i know you want to... ");
    	player.sendMessage( ChatColor.YELLOW + "/gi reload :" + ChatColor.RESET + " Reloads the configs.");
    	player.sendMessage( ChatColor.YELLOW + "/gi list :" + ChatColor.RESET + " Lists all itemspawn entrys");
    	
    	player.sendMessage( ChatColor.YELLOW + "/gi create <name_ofEntry> <timer_inTicks> :" + ChatColor.RESET + " Adds a new spawning entry, hold the items in your hand");
    	player.sendMessage( ChatColor.YELLOW + "/gi remove <name_ofEntry> :" + ChatColor.RESET + " Remove this entry");
    	player.sendMessage( ChatColor.YELLOW + "/gi start <name_ofEntry> :" + ChatColor.RESET + " Starts that entry to spawn items");
    	player.sendMessage( ChatColor.YELLOW + "/gi stop <name_ofEntry> :" + ChatColor.RESET + " Stops that entry from spawning items");
    	player.sendMessage( ChatColor.YELLOW + "/gi tp <name_ofEntry> :" + ChatColor.RESET + " Teleports you to spawnpoint location");
    	player.sendMessage( ChatColor.YELLOW + "/gi start all:" + ChatColor.RESET + " Starts all entrys to spawn items");
    	player.sendMessage( ChatColor.YELLOW + "/gi stop all:" + ChatColor.RESET + " Stops all entrys from spawning items");
    	

	}
	
private void showhelpconsole(){
		
	System.out.println(  "......................................................." + ChatColor.GOLD + " Plugin made by: "+  ".......................................................");
    	System.out.println( "     o   \\ o /  _ o              \\ /               o_   \\ o /   o");
    	System.out.println(  "    /|\\     |      /\\   __o        |        o__    /\\      |     /|\\");
    	System.out.println(  "    / \\   / \\    | \\  /) |       /o\\       |  (\\   / |    / \\   / \\");
    	System.out.println(  "......................................................." + ChatColor.GOLD + ChatColor.BOLD + " GoreaCraft  "+  ".......................................................");
    	
    	System.out.println("");
    	System.out.println(  "Aliases: " + ChatColor.LIGHT_PURPLE +  aliases );    		
		
    	System.out.println(  "/gi :" + ChatColor.RESET + " Nothing for now");
    	
    	System.out.println(  "/gi help/? :" + ChatColor.RESET + " Shows this! Do it again, i know you want to... ");
    	System.out.println(  "/gi reload :" + ChatColor.RESET + " Reloads the configs.");
    	System.out.println(  "/gi list :" + ChatColor.RESET + " Lists all itemspawn entrys");
    	
    //	System.out.println(  "/gi create <name_ofEntry> <timer_inTicks> :" + ChatColor.RESET + " Adds a new spawning entry, hold the items in your hand");
    	System.out.println(  "/gi remove <name_ofEntry> :" + ChatColor.RESET + " Remove this entry");
    	System.out.println(  "/gi start <name_ofEntry> :" + ChatColor.RESET + " Starts that entry to spawn items");
    	System.out.println(  "/gi stop <name_ofEntry> :" + ChatColor.RESET + " Stops that entry from spawning items");
    //	System.out.println(  "/gi tp <name_ofEntry> :" + ChatColor.RESET + " Teleports you to spawnpoint location");
    	System.out.println(  "/gi start all:" + ChatColor.RESET + " Starts all entrys to spawn items");
    	System.out.println(  "/gi stop all:" + ChatColor.RESET + " Stops all entrys from spawning items");
    	

	}
	
	/*private String[] split(String string){
		String[] aaa = string.split(" ");
		
		return aaa;
	}
*/
}

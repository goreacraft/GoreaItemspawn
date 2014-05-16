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
import org.bukkit.util.Vector;

public class GoreaItemspawn extends JavaPlugin{

	public static GoreaItemspawn gg;
	public static Plugin worldedit;
	public static List<String> aliases;
	
	public final Logger logger = Logger.getLogger("minecraft");

	static File spawnPointsFile;
	static YamlConfiguration spawnPoints = new YamlConfiguration();
	
	static HashMap<Integer, List<Object>> tasks = new HashMap<Integer, List<Object>>();
	static HashMap<String, Integer> tasksids = new HashMap<String, Integer>();
	static HashMap<String, Integer> normal = new HashMap<String, Integer>();
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
    	normal.clear();
    }
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {        
        	Player player = ((Player) sender).getPlayer();
        	/*if(!player.isOp())
        	{
        		player.sendMessage(ChatColor.RED +"You dont have permissions to use this commands");
        		return true;
        	} */       	
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
        				if( player.hasPermission("gi.reload") || player.isOp())
        				 	{
        					Bukkit.getScheduler().cancelTasks(gg);
        					tasks.clear();
        					tasksids.clear();
        					Methods.ents.clear();
        					gg.reloadConfig();
        					normal.clear();
        					loadconfigs();
        					sender.sendMessage(ChatColor.GREEN + "Reloaded the configs");
        					return true;
        					} else player.sendMessage(ChatColor.RED + "You dont have the permission gi.reload");
        				}
        			
        			if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))
					{
				
        				showhelpplayer(player);
					return true;
					}

        			if ( args[0].equalsIgnoreCase("list"))
        				{	
        				if (player.hasPermission("gi.list") || player.isOp())	
            			{
        				player.sendMessage(ChatColor.YELLOW +"" + ChatColor.ITALIC + "ID/ Running/ Items/ Timer/ World/ Location ");
        				for(String key: spawnPoints.getKeys(false))
        				{
        					String running = ChatColor.RED + "Idle";
        					if(tasksids.keySet().contains(key))
        					{
        						
        						running = ChatColor.GREEN + "Active";
        					}
        				
        					
        					String message = ChatColor.GOLD + key + ChatColor.RESET +" > "+ running 
        							+ " " + ChatColor.YELLOW + "Items:" +ChatColor.RESET+ spawnPoints.getConfigurationSection(key+".Items").getKeys(false).size() 
        														
        							+ ChatColor.YELLOW +"  T:" +ChatColor.RESET + spawnPoints.getConfigurationSection(key).getString("Timer")
        							+ ChatColor.YELLOW +"  W:" +ChatColor.RESET + spawnPoints.getConfigurationSection(key).getString("World")
        							+ ChatColor.YELLOW +"  Loc:" +ChatColor.GRAY+ ""+ChatColor.ITALIC+spawnPoints.getConfigurationSection(key).getVector("Location") ;
        					player.sendMessage(message);
        					
        				}
        				
        				
        				return true;
            		}else player.sendMessage(ChatColor.RED + "You dont have the permission gi.list");
            	}
            }
        		
        	
        	
        	if (args.length == 2)
        	{        
        		
        		if ( args[0].equalsIgnoreCase("list"))	
        		{
        			if((player.hasPermission("gi.list") || player.isOp()))
        			{
        			if(spawnPoints.isConfigurationSection(args[1]))        		
        			{
        			
	    				player.sendMessage(ChatColor.YELLOW +"" + ChatColor.ITALIC + "|ID|----|Nr. items|--|Timer|--|Location|");
	    				//for(String key: spawnPoints.getKeys(false))
	    			String message = ChatColor.GOLD + args[1] + ChatColor.RESET +" > " + ChatColor.GREEN + spawnPoints.getConfigurationSection(args[1]+".Items").getKeys(false).size() 
	    							+ ChatColor.GRAY+ ""+ChatColor.ITALIC+ " items"  
	    							+ ChatColor.YELLOW +"  T: " +ChatColor.RESET + spawnPoints.getConfigurationSection(args[1]).getString("Timer")
	    							+ ChatColor.YELLOW +"  W: " +ChatColor.RESET + spawnPoints.getConfigurationSection(args[1]).getString("World")
	    							+ ChatColor.YELLOW +"  Loc: " +ChatColor.GRAY+ ""+ChatColor.ITALIC+spawnPoints.getConfigurationSection(args[1]).getVector("Location");
	    					player.sendMessage(message);
	    					
	    					//player.sendMessage("Items);
	    					
	    					for (String item: spawnPoints.getConfigurationSection(args[1]+".Items").getKeys(false))
	    					{
	    						
	    						player.sendMessage(ChatColor.GOLD+  item.replace("-", ":") + ChatColor.GRAY+ ""+ChatColor.ITALIC+ " x " +ChatColor.RESET+ spawnPoints.getInt(args[1]+".Items."+ item));
	    						
	    					}
	    					
	    				
    				
        			} else player.sendMessage(ChatColor.RED +"There is no spawnpoint with this name");
        		}else player.sendMessage(ChatColor.RED + "You dont have the permission gi.list");
    				return true;
        		}
        		if (args[0].equalsIgnoreCase("toggle")) 
        		{	        				
        		if (player.hasPermission("gi.toggle") || player.isOp())
					{
        			if(spawnPoints.isConfigurationSection(args[1]))
        			{
        				if(spawnPoints.getString(args[1] + ".Type").equals("random"))
        				{
        					spawnPoints.set(args[1] + ".Type", "normal");
        					 player.sendMessage("Spawning type changed to" +  ChatColor.YELLOW +" normal " + ChatColor.RESET+ "for spawnpoint: " + ChatColor.YELLOW + args[1]);
        				} else 
        					{
        					
        					spawnPoints.set(args[1] + ".Type", "random");
        					player.sendMessage("Spawning type changed to " +  ChatColor.YELLOW +" random " + ChatColor.RESET+ "for spawnpoint: " + ChatColor.YELLOW + args[1]);
        					}
        				
        				
        				savetofile();
        				
        			} else { player.sendMessage(ChatColor.RED +"There is no entry " + args[1]);}
				}else player.sendMessage(ChatColor.RED + "You dont have the permission gi.toggle");
        		return true;
    				
				}
        		//==================
        		if ( args[0].equalsIgnoreCase("delete"))  
        		{
        			if (player.hasPermission("gi.delete") || player.isOp())	
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
        			
        			}else player.sendMessage(ChatColor.RED + "You dont have the permission gi.reload");
        			return true;        			
            	} else
            	if ( args[0].equalsIgnoreCase("remove") )
            		{
            		if (player.hasPermission("gi.remove") || player.isOp())	
                		{
            			if ( player.getItemInHand().getType() != Material.AIR &&  player.getItemInHand() != null)	
		        			{
		            			if(spawnPoints.isConfigurationSection(args[1]))
		            			{
		            				
		            				if(spawnPoints.getConfigurationSection(args[1] + ".Items").contains(player.getItemInHand().getType().toString().concat("-" + player.getItemInHand().getDurability())))
		            						{
		            					player.sendMessage(ChatColor.GREEN +"Item found .. removing it from the list");
		            					spawnPoints.set(args[1]+ ".Items."+ player.getItemInHand().getType().toString().concat("-" + player.getItemInHand().getDurability()), null);
		            					
		            					
		            						}else player.sendMessage(ChatColor.RED + "The item in your hand was not found in this spawnpoint items list");
		            				if(spawnPoints.getConfigurationSection(args[1] + ".Items").getKeys(false).size()<1)
		            				{
		            					player.sendMessage(ChatColor.GREEN +"This was the last item in the list... removing the spawnpoint");
		            					if(tasksids.containsKey(args[1]))
		                        		{            				
		                        			if(Bukkit.getScheduler().isQueued(tasksids.get(args[1])))
		                        			{
		                        				getServer().getScheduler().cancelTask(tasksids.get(args[1]));
		                        				player.sendMessage("Entry "+ ChatColor.GOLD + args[1]+ ChatColor.RESET + " stoped!");
		                        			}
		                        		}
		            					spawnPoints.set(args[1], null);
		            				}
		            				//player.sendMessage("Entry "+ ChatColor.GOLD + args[0]+ ChatColor.RESET + " removed!");
		            				try {spawnPoints.save(spawnPointsFile);} 
		            				catch (IOException e) {e.printStackTrace();}
		            					
		            			} else { player.sendMessage(ChatColor.RED +"There is no spawnpoint " + args[1]);}
		        		} else { player.sendMessage(ChatColor.RED +"Please hold the items you want to spawn in your hand");return false;}
                	}else player.sendMessage(ChatColor.RED + "You dont have the permission gi.remove");
            			return true;        			
                	} else
                		
                	if ( args[0].equalsIgnoreCase("move") )
                		{	
                			if (player.hasPermission("gi.move") || player.isOp())	
                			{
		                		if(spawnPoints.isConfigurationSection(args[1]))
			            		{
		                				Vector loc = player.getLocation().getBlock().getLocation().toVector();
		                				spawnPoints.set(args[1] + ".Location", loc);
		                				player.sendMessage("Spawnpoint "+ ChatColor.YELLOW + args[1]+ChatColor.RESET  +" moved to your location");
		                				
		                				
			            		}else { player.sendMessage(ChatColor.RED +"There is no spawnpoint " + args[1]);}
                    	}else player.sendMessage(ChatColor.RED + "You dont have the permission gi.move");
                		return true;	
                    }else
        			
        			//=================
        		if (args[0].equalsIgnoreCase("start") ) 
        		{	
        			if (player.hasPermission("gi.start") || player.isOp())
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
		            		} else player.sendMessage(ChatColor.RED +"There is no spawnpoint with this name");
    				}else player.sendMessage(ChatColor.RED + "You dont have the permission gi.start");
            		return true;
    			} else
    				
        			//=================
        		if (args[0].equalsIgnoreCase("tp")  )
        			{	
        			if (player.hasPermission("gi.tp") || player.isOp())
		    			{
		            		if(spawnPoints.getKeys(false).contains(args[1]))
		            		{            		
		            			player.teleport(spawnPoints.getVector(args[1] + ".Location").toLocation(Bukkit.getWorld(spawnPoints.getString(args[1] + ".World"))));            				
		            			player.sendMessage("You teleported to "+ ChatColor.GOLD + args[1]+ ChatColor.RESET + " itemspawn location!");
		            		} else player.sendMessage(ChatColor.RED +"There is no spawn entry with this name");
		    			}else player.sendMessage(ChatColor.RED + "You dont have the permission gi.tp");
            		return true;
    			} else
        			
        			//=====================
            	if (args[0].equalsIgnoreCase("stop") )
            	{		
            		if (player.hasPermission("gi.stop") || player.isOp())
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
	            		}
            		
            		}else player.sendMessage(ChatColor.RED + "You dont have the permission gi.stop");
            		return true;
    			}  else
        		
        		
        			
        			if (args[0].equals("add"))
        			{	
        				if (player.hasPermission("gi.add") || player.isOp())
        				{
				        		if ( player.getItemInHand().getType() != Material.AIR &&  player.getItemInHand() != null)	
				        		{		
				        			if(spawnPoints.isConfigurationSection(args[1]))
				        			{
										player.sendMessage("Adding new item to spawnpoint: " + args[1]);									
										spawnPoints.createSection(args[1] + ".Items." + player.getItemInHand().getType().toString().concat("-" + player.getItemInHand().getDurability()) );
										spawnPoints.set(args[1] + ".Items." + player.getItemInHand().getType().toString().concat("-" + player.getItemInHand().getDurability()), player.getItemInHand().getAmount());
									//	MaterialData ddd = player.getItemInHand().getData();
										 player.sendMessage(ChatColor.GREEN +"Spawnpoint added with name: " + args[1]);		        			
										
						        		try {
						        			player.sendMessage("Saving to file ");
						        			spawnPoints.save(spawnPointsFile);
						        			return true;
						        		} catch (IOException e) {
						        			e.printStackTrace();
						        		}
						        		
						        		
				        			}else { player.sendMessage(ChatColor.RED +"There is no spawnpoint with this name, see: /gi list");return true;}
				        		} else { player.sendMessage(ChatColor.RED +"Please hold the items you want to spawn in your hand");return false;}
		        		
        				}else player.sendMessage(ChatColor.RED + "You dont have the permission gi.reload");
        			}
        			return false;
        	} 
        		
        	if (args.length >= 3)
        	{	
            		//========================    
    			if (args[0].equals("create")  )
    				{	
    				if (player.hasPermission("gi.create") || player.isOp())
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
									HashMap<String, Integer> items = new HashMap<String,Integer>();
									items.put(player.getItemInHand().getType().toString().concat("-" + player.getItemInHand().getDurability()), player.getItemInHand().getAmount());
									values.put("Items", items);
									values.put("Type", "random");
									values.put("World", player.getLocation().getWorld().getName());
									values.put("Location", player.getLocation().getBlock().getLocation().toVector());
									values.put("Timer", timer);
									spawnPoints.createSection(args[1], values);
									player.sendMessage(ChatColor.GREEN +"Spawnpoint added with name: " + args[1]);
			        		} else { player.sendMessage(ChatColor.RED +"Please hold the items you want to spawn in your hand");return false;}
		        		
		        		try {
		        			spawnPoints.save(spawnPointsFile);
		        			return true;
		        		} catch (IOException e) {
		        			e.printStackTrace();
		        		}
        			}else player.sendMessage(ChatColor.RED + "You dont have the permission gi.create");
        			}
    			//========================    
    			
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
    	player.sendMessage( ChatColor.YELLOW + "/gi list <spawn_point>:" + ChatColor.RESET + " Lists all items in this spawnpoint");
    	player.sendMessage( ChatColor.YELLOW + "/gi toggle <spawn_point>:" + ChatColor.RESET + " Changes the spawning sistem between random and normal");
    	player.sendMessage( ChatColor.YELLOW + "/gi create <name_ofEntry> <timer_inTicks> :" + ChatColor.RESET + " Adds a new spawning entry, hold the items in your hand");
    	player.sendMessage( ChatColor.YELLOW + "/gi delete <name_ofEntry> :" + ChatColor.RESET + " Remove this entry");
    	player.sendMessage( ChatColor.YELLOW + "/gi move <spawn_point>:" + ChatColor.RESET + " Changes this spawnpoint location to your current location");
    	player.sendMessage( ChatColor.YELLOW + "/gi add <spawn_point>:" + ChatColor.RESET + " Adds the item in your hand to this spawnpoint list");
    	player.sendMessage( ChatColor.YELLOW + "/gi remove <spawn_point>:" + ChatColor.RESET + " Removes the item in your hand from this spawnpoint list");   	
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
    	System.out.println(  "/gi delete <name_ofEntry> :" + ChatColor.RESET + " Remove this entry");
    	System.out.println(  "/gi start <name_ofEntry> :" + ChatColor.RESET + " Starts that entry to spawn items");
    	System.out.println(  "/gi stop <name_ofEntry> :" + ChatColor.RESET + " Stops that entry from spawning items");
    //	System.out.println(  "/gi tp <name_ofEntry> :" + ChatColor.RESET + " Teleports you to spawnpoint location");
    	System.out.println(  "/gi start all:" + ChatColor.RESET + " Starts all entrys to spawn items");
    	System.out.println(  "/gi stop all:" + ChatColor.RESET + " Stops all entrys from spawning items");
    	

	}
	private void savetofile(){
		try {			
			spawnPoints.save(spawnPointsFile);			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}

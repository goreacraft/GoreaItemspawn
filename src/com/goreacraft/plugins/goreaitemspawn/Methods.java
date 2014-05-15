package com.goreacraft.plugins.goreaitemspawn;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Methods {

	static HashMap<String,UUID> ents = new HashMap<String,UUID>();
	
	public static void startSpawnPoints()
	{
		{
			Set<String> points = GoreaItemspawn.spawnPoints.getKeys(false);	
			{
				for (String point : points)		
				{									
				new Runnables(point);
				}	
			}
		}
		
	}
	
	/*protected static void spawnItem(String taskid) 
		{
			SpawnPoint sp = GoreaItemspawn.tasks.get(taskid);
			Location loc = sp.getLocation();
			Chunk c = loc.getChunk();
			if(!ents.isEmpty())
			{
			UUID id = ents.get(nr); 
			
			for (Entity e: c.getEntities()) 
			{
			    if (e.getUniqueId() == id) 
			    {
					return;
			    }
			}
		}
		Item item = Bukkit.getServer().getWorld(sp.getWorldName()).dropItem(loc.add(0.5,0.6,0.5), new ItemStack(sp.getItemid(),0,sp.getMeta()));//sp.getItemStack());
		item.setVelocity(new Vector(0, 0, 0));
		ents.put(nr, item.getUniqueId());

	}*/
	protected static boolean check(Location loc){
		
		if (loc.add(0.2,0.2,0.2).getBlock().isEmpty())
		return true;
		else return false;
		
	}
	
	static Location locationfromString(String string)
	 {
		 String[] locs = string.split(" ");
		 
		 //add world
		 Location loc = new Location(
				 Bukkit.getWorld(locs[0]), 
				 Double.parseDouble(locs[1]), 
				 Double.parseDouble(locs[2]), 
				 Double.parseDouble(locs[3]));
		
		return loc;
		 
	 }
	
	static String cleanlocationString(String string)
	 {
		 String[] locs = string.split(" ");		 
		 int x = (int) Double.parseDouble(locs[1]);
		 int y = (int) Double.parseDouble(locs[2]);
		 int z = (int) Double.parseDouble(locs[3]);		 
			String clean = 	locs[0] + " " + 
				 x + "," +  
				 y + "," +  
				 z;		 
		return clean;		 
	 }
	
	static String locationToString(Location loc)
	
	{
		double x = loc.getBlockX();
		double z = loc.getBlockZ();
		double y = loc.getBlockY();
		String location = loc.getWorld().getName() + " " + x + " "+ y + " " + z;		
		return location;
	}

/*	public static void setpoints() 
	{
		Set<String> points = GoreaItemspawn.spawnPoints.getKeys(false);			
		{
			for (String point : points)
			{
				SpawnPoint entry = new SpawnPoint();
				entry.setMaterial(Material.getMaterial(GoreaItemspawn.spawnPoints.getString(point+".Item")));
				entry.setMeta(Short.parseShort(GoreaItemspawn.spawnPoints.getString(point+".Meta")));
				entry.setAmount(GoreaItemspawn.spawnPoints.getInt(point+".Amount"));
				entry.setLocation(GoreaItemspawn.spawnPoints.getString(point+".Location"));
				entry.setTimer(GoreaItemspawn.spawnPoints.getLong(point+".Timer"));
				GoreaItemspawn.tasks.put(point, entry);
			}
		}
		
	}*/
	
	
	public static Player findPlayerByString(String name) 
	{
		for ( Player player : Bukkit.getServer().getOnlinePlayers())
		{
			if(player.getName().equals(name)) 
			{
				return player;
			}
		}
		
		return null;
	}
}

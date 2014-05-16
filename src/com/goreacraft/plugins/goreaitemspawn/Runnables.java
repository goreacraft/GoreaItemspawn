package com.goreacraft.plugins.goreaitemspawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Runnables{

	//protected static MetadataValue value=1;
	private long timer = 100;

	
	Runnables(final String entryname)
	{
		if(GoreaItemspawn.normal.containsKey(entryname))
		{
			GoreaItemspawn.normal.remove(entryname);
			
		}
		
		timer = GoreaItemspawn.spawnPoints.getLong(entryname + ".Timer");
			int taskid = new BukkitRunnable() 
			{				
				@Override
				public void run() 
				{					
					
					List<Object> data = GoreaItemspawn.tasks.get(getTaskId());
					String entry = data.get(0).toString();
					String world = GoreaItemspawn.spawnPoints.getString(entry + ".World");
					Location loc = GoreaItemspawn.spawnPoints.getVector(entry + ".Location").toLocation(Bukkit.getWorld(world));					
					boolean go =true;
					if(Bukkit.getServer().getWorld(world).getChunkAt(loc).isLoaded())
						{
						if(!GoreaItemspawn.wholeworld)
							{						
							if(GoreaItemspawn.unique)							
								for(Entity ent :Bukkit.getServer().getWorld(world).getChunkAt(loc).getEntities())
								{
									if(ent.getType().equals(EntityType.DROPPED_ITEM))
									{	
										if(ent.hasMetadata(entryname))
										{
											go=false;	
											break;
											
										}
									
									}						
								}	
						
							} else {
								if(GoreaItemspawn.unique)							
									for(Entity ent :Bukkit.getServer().getWorld(world).getEntities())
									{
										if(ent.getType().equals(EntityType.DROPPED_ITEM))
										{	
											if(ent.hasMetadata(entryname))
											{
												go=false;	
												break;
												
											}
										
										}						
									}	
								}
					
				if(go)	
				{
					ItemStack itemstack = getRandomItemstack(entryname);
					Item item = Bukkit.getServer().getWorld(world).dropItem(loc.add(0.5,0.6,0.5), itemstack);//sp.getItemStack());
					item.setVelocity(new Vector(0, 0, 0));
					item.setMetadata(entryname,new MyMetadata(GoreaItemspawn.gg, getTaskId()));
					
							List<Object> value = new ArrayList<Object>();	
							value.add(entryname);
							value.add(item.getEntityId());
							GoreaItemspawn.tasks.put(getTaskId(), value);
							GoreaItemspawn.tasksids.put(entryname, getTaskId());
				} 	else {
					//Methods.findPlayerByString("goreacraft").sendMessage("skip");
				}
				
			} //else Methods.findPlayerByString("goreacraft").sendMessage("chunk unloaded");
			}				    		  
		}.runTaskTimer(GoreaItemspawn.gg, 0,timer ).getTaskId();
			
			ItemStack itemstack = getRandomItemstack(entryname);
			String world = GoreaItemspawn.spawnPoints.getString(entryname + ".World");
			Location loc = GoreaItemspawn.spawnPoints.getVector(entryname + ".Location").toLocation(Bukkit.getWorld(world));
			Item item = Bukkit.getServer().getWorld(world).dropItem(loc.add(0.5,0.6,0.5), itemstack);
			item.setVelocity(new Vector(0, 0, 0));
			item.setMetadata(entryname,new MyMetadata(GoreaItemspawn.gg, taskid));
			List<Object> value = new ArrayList<Object>();			
			value.add(entryname);
			value.add(item.getEntityId());
			GoreaItemspawn.tasks.put(taskid, value);
			GoreaItemspawn.tasksids.put(entryname, taskid);

	}
	
	
	public static ItemStack getRandomItemstack(String name){
		
		Set<String> itemmat = GoreaItemspawn.spawnPoints.getConfigurationSection(name + ".Items").getKeys(false);		
		String[] itemsmat = itemmat.toArray(new String[itemmat.size()]);
		int nr=0;
		if(GoreaItemspawn.spawnPoints.getString(name + ".Type").equals("normal"))
		{
			
			if(GoreaItemspawn.normal.containsKey(name))
					{
						if(GoreaItemspawn.normal.get(name)+1==itemmat.size())
						{
							//Methods.findPlayerByString("goreacraft").sendMessage(" " + (GoreaItemspawn.normal.get(name)+1==itemmat.size()));
							nr=0;							
						} else {
							nr=GoreaItemspawn.normal.get(name)+1;
						}
						
					}
			//Methods.findPlayerByString("goreacraft").sendMessage("Normal" + nr);
			GoreaItemspawn.normal.put(name, nr);
		}else			
			{
			//random
			nr =randomWithRange(0,itemmat.size()-1);
			}
		
		//Methods.findPlayerByString("goreacraft").sendMessage("111" + itemsmat.length);
		String[] data1  = new String[1];
		data1 = itemsmat[nr].toString().split("-");	
		
		//Methods.findPlayerByString("goreacraft").sendMessage("data1[0] " + data1[0]);
		//Methods.findPlayerByString("goreacraft").sendMessage("data1[1] " + data1[1]);
		//MaterialData ddd = (MaterialData) data1[0];

		Material material =Material.getMaterial(data1[0]);//.getNewData(Byte.parseByte(data1[1]));
		//Material material = Material.getMaterial(data1[0].toString());
		short meta= Byte.valueOf(data1[1]);
		
		//Methods.findPlayerByString("goreacraft").sendMessage("material " + material);
		//Methods.findPlayerByString("goreacraft").sendMessage("meta " + meta);
		int ammount = GoreaItemspawn.spawnPoints.getInt(name + ".Items." + itemsmat[0]);
		ItemStack itemstack =new ItemStack(material,ammount,meta);
		return itemstack;
	}
	static int randomWithRange(int min, int max)
	{
	   int range = (max - min) + 1;     
	   return (int)(Math.random() * range) + min;
	}
	
	
}

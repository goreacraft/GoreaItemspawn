package com.goreacraft.plugins.goreaitemspawn;

import java.util.ArrayList;
import java.util.List;

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
		timer = GoreaItemspawn.spawnPoints.getLong(entryname + ".Timer");
			int taskid = new BukkitRunnable() 
			{				
				@Override
				public void run() 
				{					
					ItemStack itemstack = getItemstackbyid(getTaskId());
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
		}.runTaskTimer(GoreaItemspawn.gg, 0,	timer ).getTaskId();
			

			
			
			
			ItemStack itemstack = getItemstack(entryname);
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
	
	public static ItemStack getItemstackbyid(int id){
		
		List<Object> data = GoreaItemspawn.tasks.get(id);
		String entry = data.get(0).toString();
					
					Material material =Material.getMaterial(GoreaItemspawn.spawnPoints.getString(entry + ".Item"));
					short meta = (short) GoreaItemspawn.spawnPoints.getInt(entry + ".Meta");
					int ammount = GoreaItemspawn.spawnPoints.getInt(entry + ".Amount");
					ItemStack itemstack =new ItemStack(material,ammount,meta);
					return itemstack;
		
	}
	
	public static ItemStack getItemstack(String name){
		Material material =Material.getMaterial(GoreaItemspawn.spawnPoints.getString(name + ".Item"));
		short meta = (short) GoreaItemspawn.spawnPoints.getInt(name + ".Meta");
		int ammount = GoreaItemspawn.spawnPoints.getInt(name + ".Amount");
		ItemStack itemstack =new ItemStack(material,ammount,meta);
		return itemstack;
	}

}

package com.goreacraft.plugins.goreaitemspawn;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SpawnPoint {
	
	private Material material;
	
	private short meta;
	
	private int ammont = 1;
	
	private String location;
	
	private long timer = 200;

	
	public Material getItemid() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public short getMeta() {
		return meta;
	}

	public void setMeta(short meta) {
		this.meta = meta;
	}

	public int getAmount() {
		return ammont;
	}

	public void setAmount(int ammont) {
		this.ammont = ammont;
	}
	
	public String getWorldName() {
		String[] aaa = location.split(" ");
		return aaa[0];
	}

	public Location getLocation() {
		return Methods.locationfromString(location);
	}
	public String getStringLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public long getTimer() {
		return timer;
	}

	public void setTimer(long timer) {
		this.timer = timer;
	}
	
	public ItemStack getItemStack() {
		return new ItemStack(material,ammont,meta);
	}

}

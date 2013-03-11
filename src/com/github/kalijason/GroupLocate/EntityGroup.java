package com.github.kalijason.GroupLocate;

import java.util.List;

import org.bukkit.entity.Entity;

public class EntityGroup {

	public EntityGroup(int size, Entity center, List<Entity> neighbors) {
		super();
		this.size = size;
		this.center = center;
		this.neighbors = neighbors;
	}

	int size;
	Entity center;
	List<Entity> neighbors;

	public int getSize() {
		return size;
	}

	public Entity getCenter() {
		return center;
	}

	public List<Entity> getNeighbors() {
		return neighbors;
	}

}

package com.github.kalijason.GroupLocate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class GroupLocateCommandExecutor implements CommandExecutor {
	String[] helpMsg;
	double distance;
	boolean tp;
	String type;

	ArrayList<Location> lastLocations;

	GroupLocateCommandExecutor() {
		helpMsg = new String[7];
		helpMsg[0] = "Welcome to the LagLocate Help System";
		helpMsg[1] = "Usage:";
		helpMsg[2] = "  /LagLocate <distance> [items|creatures|all] [(true|false)tp]";
		helpMsg[3] = "    Returns the coordinates of the largest stack of found";
		helpMsg[4] = "    [items|creatures|all] (default: items) in the loaded chunks,";
		helpMsg[5] = "    within <distance> blocks of each other.";
		helpMsg[6] = "    Optionally teleports you there. (default: false)";

		lastLocations = new ArrayList<Location>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("LagLocate")) {
			if (args.length == 0) {
				sender.sendMessage(helpMsg);
				return true;
			} else {
				if (args.length > 2) {
					sender.sendMessage("Too many arguments.");
					return false;
				} else {
					// tp command
					if (args[0].toLowerCase().equals("tp")) {
						// get position
						try {
							int pos = Integer.parseInt(args[1]);
							Player player = (Bukkit.getServer().getPlayer(sender.getName()));
							Location target = lastLocations.get(pos - 1);
							sender.sendMessage("Telepoting to position " + pos);
							player.teleport(target);
						} catch (Exception e) {
							sender.sendMessage("Argument <distance> cannot be cast to Integer.");
							return false;
						}
						// look up
					} else {
						// clear result before searching
						lastLocations.clear();

						// get distance
						try {
							distance = Double.parseDouble(args[1]);
						} catch (Exception e) {
							sender.sendMessage("Argument <distance> cannot be cast to Integer.");
							return false;
						}

						if (args.length > 1) {
							type = args[0];

							if (!type.toLowerCase().contains("item") && !type.toLowerCase().contains("creature") && !type.toLowerCase().contains("monster")
									&& !type.toLowerCase().contains("animals") && !type.toLowerCase().contains("villager")
									&& !type.toLowerCase().contains("all") && !type.toLowerCase().contains("player")) {
								sender.sendMessage("Invalid type.");
								return false;
							}
						} else {
							type = "item";
						}

						sender.sendMessage("Searching for lag inducing setups:");
						sender.sendMessage("  Distance: " + distance);
						sender.sendMessage("  Type: " + type);

						Player commandSender = Bukkit.getPlayer(sender.getName());
						List<Entity> entities = commandSender.getWorld().getEntities();
						sender.sendMessage("  Total Entities:" + entities.size());

						List<Entity> nearEntities;

						// determine type
						Class<?> classType = getEntityType(type);

						// get Filtered entities
						List<Entity> filteredEntities = getFilteredEntities(classType, entities);
						sender.sendMessage("  Filtered Entities:" + filteredEntities.size());

						// construct the list of groups (centers)
						ArrayList<EntityGroup> list = new ArrayList<EntityGroup>();
						for (Entity entity : filteredEntities) {
							// find neighbors of this entity
							nearEntities = new ArrayList<Entity>();

							for (Entity e : entity.getNearbyEntities(distance, distance, distance)) {
								if (classType.isInstance(e)) {
									nearEntities.add(e);
								}
							}
							list.add(new EntityGroup(nearEntities.size(), entity, nearEntities));

						}

						// search group
						final int maxResult = 5;
						for (int i = 1; i <= maxResult; i++) {
							// sort to fetch top
							Collections.sort(list, new EntityGroupComparable());

							if (list.size() > 0) {
								EntityGroup e = list.get(0);
								list.remove(0);
								int clusterSize = e.getSize() + 1;
								if (clusterSize > 0) {
									lastLocations.add(e.getCenter().getLocation().clone());
									sender.sendMessage(i + ") size:(" + clusterSize + ") X: " + e.getCenter().getLocation().getBlockX() + " Y: "
											+ e.getCenter().getLocation().getBlockY() + " Z: " + e.getCenter().getLocation().getBlockZ());
								}
								// remove duplication in neighbors
								removeDuplications(e.getNeighbors(), list);
							}

						}

					}
				}

				return true;
			}
		}
		return false;
	}

	private Class<?> getEntityType(String type) {
		if (type != null) {
			String lowerType = type.toLowerCase();
			if (lowerType.contains("item")) {
				return Item.class;
			} else if (lowerType.contains("creature")) {
				return LivingEntity.class;
			} else if (lowerType.contains("monster")) {
				return Monster.class;
			} else if (lowerType.contains("animals")) {
				return Animals.class;
			} else if (lowerType.contains("villager")) {
				return Villager.class;
			} else if (lowerType.contains("player")) {
				return Player.class;
			}
		}
		return Entity.class;
	}

	private void removeDuplications(List<Entity> neighbors, List<EntityGroup> list) {
		// remove duplication in neighbors
		for (Entity neighbor : neighbors) {
			// do a reverse iteration for best performance in
			// deletion
			for (ListIterator<EntityGroup> iter = list.listIterator(list.size()); iter.hasPrevious();) {
				EntityGroup e2 = iter.previous();
				if (e2.center.equals(neighbor)) {
					iter.remove();
				}
			}
		}
	}

	private List<Entity> getFilteredEntities(Class<?> classType, List<Entity> entities) {
		List<Entity> filteredEntities = new ArrayList<Entity>();
		for (Entity entity : entities) {
			if (classType.isInstance(entity)) {
				filteredEntities.add(entity);
			}
		}
		return filteredEntities;
	}
}

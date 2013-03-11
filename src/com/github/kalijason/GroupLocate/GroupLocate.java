package com.github.kalijason.GroupLocate;

import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public class GroupLocate extends JavaPlugin {
    Entity[] items;
    Entity[] creatures;

    @Override
    public void onEnable() {
        getLogger().info("Enabled GroupLocate");
        getCommand("GroupLocate").setExecutor(new GroupLocateCommandExecutor());
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled GroupLocate");
    }
}

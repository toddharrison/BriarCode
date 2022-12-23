package com.briarcraft.fakeblock;

import com.briarcraft.fakeblock.api.data.FakeBlock;
import com.briarcraft.fakeblock.api.data.Group;
import com.briarcraft.fakeblock.api.service.GroupService;
import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import com.briarcraft.fakeblock.config.GroupConfig;
import com.briarcraft.fakeblock.config.PlayerGroupConfig;
import com.briarcraft.fakeblock.data.PlayerGroup;
import com.briarcraft.fakeblock.listener.FakeBlockPacketListener;
import com.briarcraft.fakeblock.listener.FlyingKickListener;
import com.briarcraft.fakeblock.service.*;
import com.comphenix.protocol.ProtocolLibrary;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;
import java.util.stream.Collectors;

public class FakeBlockPlugin extends JavaPlugin {
    static {
        ConfigurationSerialization.registerClass(Group.class, "Group");
        ConfigurationSerialization.registerClass(FakeBlock.class, "FakeBlock");
        ConfigurationSerialization.registerClass(PlayerGroup.class, "PlayerGroup");
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        // Load configuration
        val groupConfig = new GroupConfig(this);
        val groups = groupConfig.load().stream().collect(Collectors.toMap(Group::getName, Function.identity()));
        val playerGroupConfig = new PlayerGroupConfig(this);
        val playerGroups = playerGroupConfig.load().stream().collect(Collectors.toMap(PlayerGroup::getPlayerId, PlayerGroup::getGroupNames));

        // Get external APIs
        val sessionManager = WorldEdit.getInstance().getSessionManager();
        val protocolManager = ProtocolLibrary.getProtocolManager();

        // Create services
        val worldEditService = new WorldEditService(sessionManager, BukkitAdapter::adapt);
        val protocolLibService = new ProtocolLibService(protocolManager);
        val chunkService = new ChunkService();

        // Register all API services
        val pluginManager = getServer().getPluginManager();
        val servicesManager = getServer().getServicesManager();
        val groupService = new GroupServiceImpl(pluginManager, servicesManager, chunkService, groupConfig, groups);
        servicesManager.register(GroupService.class, groupService, this, ServicePriority.Normal);
        val playerGroupService = new PlayerGroupServiceImpl(pluginManager, groupService, protocolLibService, playerGroupConfig, Bukkit::getPlayer, playerGroups);
        servicesManager.register(PlayerGroupService.class, playerGroupService, this, ServicePriority.Normal);

        // Register all listeners
        pluginManager.registerEvents(new FlyingKickListener(), this);
        val packetAdapter = new FakeBlockPacketListener(this, protocolLibService, groupService, playerGroupService);
        protocolManager.addPacketListener(packetAdapter);

        // Register all commands
        getCommand(FakeBlockCommand.NAME).setExecutor(new FakeBlockCommand(worldEditService, groupService, playerGroupService));
    }

    @Override
    public void onDisable() {
        // Unregister all commands
        val command = getServer().getPluginCommand(FakeBlockCommand.NAME);
        command.setExecutor(null);

        // Unregister all listeners
        val protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.removePacketListeners(this);

        // Unregister all API services
        getServer().getServicesManager().unregisterAll(this);

        // Unregister all listeners
        HandlerList.unregisterAll(this);
    }
}

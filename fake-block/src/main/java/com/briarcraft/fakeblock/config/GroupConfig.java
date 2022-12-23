package com.briarcraft.fakeblock.config;

import com.briarcraft.fakeblock.api.data.Group;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GroupConfig implements Config<Group> {
    private final @Nonnull Plugin plugin;

    @Override
    public @Nonnull List<Group> load() {
        val groupsConfigFile = new File(plugin.getDataFolder(), "groups.yml");
        val config = YamlConfiguration.loadConfiguration(groupsConfigFile);
        val list = config.getList("groups");
        if (list == null) return List.of();
        else return list.stream()
                .filter(o -> o instanceof Group)
                .map(o -> (Group)o)
                .collect(Collectors.toList());
    }

    @Override
    public void save(final @Nonnull Collection<Group> groups) {
        val config = new YamlConfiguration();
        config.set("groups", groups.stream().sorted().toList());
        try {
            config.save(new File(plugin.getDataFolder(), "groups.yml"));
        } catch (final @Nonnull IOException e) {
            e.printStackTrace();
        }
    }
}

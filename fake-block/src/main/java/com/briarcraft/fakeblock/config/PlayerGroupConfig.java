package com.briarcraft.fakeblock.config;

import com.briarcraft.fakeblock.data.PlayerGroup;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PlayerGroupConfig implements Config<PlayerGroup> {
    private final @Nonnull Plugin plugin;

    @Override
    public @Nonnull List<PlayerGroup> load() {
        val groupsConfigFile = new File(plugin.getDataFolder(), "playerGroups.yml");
        val config = YamlConfiguration.loadConfiguration(groupsConfigFile);
        val list = config.getList("playerGroups");
        if (list == null) return List.of();
        else return list.stream()
                .filter(o -> o instanceof PlayerGroup)
                .map(o -> (PlayerGroup)o)
                .collect(Collectors.toList());
    }

    @Override
    public void save(final @Nonnull Collection<PlayerGroup> playerGroups) {
        val config = new YamlConfiguration();
        config.set("playerGroups", playerGroups.stream().sorted().toList());
        try {
            config.save(new File(plugin.getDataFolder(), "playerGroups.yml"));
        } catch (final @Nonnull IOException e) {
            e.printStackTrace();
        }
    }
}

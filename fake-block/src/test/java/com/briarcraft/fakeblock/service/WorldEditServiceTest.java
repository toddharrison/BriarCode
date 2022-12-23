package com.briarcraft.fakeblock.service;

import com.briarcraft.fakeblock.service.WorldEditService;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import lombok.val;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorldEditServiceTest {
    private @Mock SessionManager sessionManager;
    private @Mock LocalSession localSession;
    private @Mock Player player;
    private @Mock BukkitPlayer bukkitPlayer;
    private @Mock World world;
    private @Mock Region region;

    private WorldEditService service;

    @BeforeEach
    public void setup() {
        service = new WorldEditService(sessionManager, (player) -> bukkitPlayer);
    }

    @Nested
    public class GetPlayerSelectionTest {
        @Test
        public void call() throws Exception {
            // Arrange
            when(sessionManager.get(bukkitPlayer)).thenReturn(localSession);
            when(localSession.getSelectionWorld()).thenReturn(world);
            when(localSession.getSelection(world)).thenReturn(region);

            // Act
            val response = service.getPlayerSelection(player);

            // Assert
            assertEquals(region, response);
        }

        @Test
        public void callWithIncompleteRegion() throws Exception {
            // Arrange
            val error = new IncompleteRegionException();

            when(sessionManager.get(bukkitPlayer)).thenReturn(localSession);
            when(localSession.getSelectionWorld()).thenReturn(world);
            when(localSession.getSelection(world)).thenThrow(error);

            // Act
            val response = service.getPlayerSelection(player);

            // Assert
            assertNull(response);
        }
    }
}

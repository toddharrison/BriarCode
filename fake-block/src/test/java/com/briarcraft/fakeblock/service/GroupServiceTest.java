package com.briarcraft.fakeblock.service;

import com.briarcraft.fakeblock.config.GroupConfig;
import com.briarcraft.fakeblock.api.data.ChunkPosition;
import com.briarcraft.fakeblock.api.data.Chunklet;
import com.briarcraft.fakeblock.api.data.FakeBlock;
import com.briarcraft.fakeblock.api.event.CreateFakeBlockGroupEvent;
import com.briarcraft.fakeblock.api.event.DeleteFakeBlockGroupEvent;
import com.briarcraft.fakeblock.api.event.UpdateFakeBlockGroupEvent;
import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import com.comphenix.protocol.wrappers.BlockPosition;
import lombok.val;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {
    private @Mock PluginManager pluginManager;
    private @Mock ServicesManager servicesManager;
    private @Mock ChunkService chunkService;
    private @Mock GroupConfig groupConfig;
    private @Mock RegisteredServiceProvider<PlayerGroupService> playerGroupServiceProvider;
    private @Mock PlayerGroupService playerGroupService;
    private @Mock World world;
    private @Mock BlockData blockData;
    private @Mock Chunklet chunklet;

    private GroupServiceImpl service;

    @BeforeEach
    public void setup() {
        service = new GroupServiceImpl(pluginManager, servicesManager, chunkService, groupConfig, Map.of());
    }

    @Nested
    public class GetGroupNamesTest {
        @Test
        public void call() {
            // Arrange
            val groupName = "1";
            val fakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData)
            );

            // Act
            service.create(groupName, world, fakeBlocks);
            val response = service.getGroupNames();

            // Assert
            assertEquals(Set.of(groupName), response);
        }
    }

    @Nested
    public class CreateTest {
        @Test
        public void call() {
            // Arrange
            val groupName = "1";
            val fakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData)
            );

            // Act
            val response = service.create(groupName, world, fakeBlocks);

            // Assert
            assertTrue(response);

            verify(pluginManager).callEvent(any());
            verify(chunkService).toChunklets(any());
        }

        @Test
        public void callCancelled() {
            // Arrange
            val groupName = "1";
            val fakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData)
            );

            doAnswer(invocation -> {
                CreateFakeBlockGroupEvent event = invocation.getArgument(0);
                event.setCancelled(true);
                return null;
            }).when(pluginManager).callEvent(any(CreateFakeBlockGroupEvent.class));

            // Act
            val response = service.create(groupName, world, fakeBlocks);

            // Assert
            assertFalse(response);

            verify(pluginManager).callEvent(any());
            verify(chunkService, never()).toChunklets(any());
        }

        @Test
        public void callWithDuplicateGroup() {
            // Arrange
            val groupName = "1";
            val fakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData)
            );

            // Act
            service.create(groupName, world, fakeBlocks);
            val response = service.create(groupName, world, fakeBlocks);

            // Assert
            assertFalse(response);
        }
    }

    @Nested
    public class AddBlocksTest {
        @Test
        public void call() {
            // Arrange
            val groupName = "1";
            val fakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData)
            );
            val newFakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(1, 1, 1), blockData)
            );

            service.create(groupName, world, fakeBlocks);
            reset(pluginManager);
            reset(chunkService);

            // Act
            val response = service.addBlocks(groupName, newFakeBlocks);

            // Assert
            assertTrue(response);
            assertEquals(Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData),
                    new FakeBlock(new BlockPosition(1, 1, 1), blockData)
            ), service.getBlocks(groupName));

            verify(pluginManager).callEvent(any());
            verify(chunkService).toChunklets(any());
        }

        @Test
        public void callCancelled() {
            // Arrange
            val groupName = "1";
            val fakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData)
            );
            val newFakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(1, 1, 1), blockData)
            );

            service.create(groupName, world, fakeBlocks);
            reset(pluginManager);
            reset(chunkService);

            doAnswer(invocation -> {
                UpdateFakeBlockGroupEvent event = invocation.getArgument(0);
                event.setCancelled(true);
                return null;
            }).when(pluginManager).callEvent(any(UpdateFakeBlockGroupEvent.class));

            // Act
            val response = service.addBlocks(groupName, newFakeBlocks);

            // Assert
            assertFalse(response);
            assertEquals(fakeBlocks, service.getBlocks(groupName));

            verify(pluginManager).callEvent(any());
            verify(chunkService, never()).toChunklets(any());
        }

        @Test
        public void callWithNoGroup() {
            // Arrange
            val groupName = "1";
            val newFakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(1, 1, 1), blockData)
            );

            // Act
            val response = service.addBlocks(groupName, newFakeBlocks);

            // Assert
            assertFalse(response);
        }
    }

    @Nested
    public class RemoveBlocksTest {
        @Test
        public void call() {
            // Arrange
            val groupName = "1";
            val fakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData),
                    new FakeBlock(new BlockPosition(1, 1, 1), blockData)
            );
            val positions = Set.of(
                    new BlockPosition(0, 0, 0)
            );

            service.create(groupName, world, fakeBlocks);
            reset(pluginManager);
            reset(chunkService);

            // Act
            val response = service.removeBlocks(groupName, positions);

            // Assert
            assertEquals(Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData)
            ), response);
            assertEquals(Set.of(
                    new FakeBlock(new BlockPosition(1, 1, 1), blockData)
            ), service.getBlocks(groupName));

            verify(pluginManager).callEvent(any());
            verify(chunkService).toChunklets(any());
        }

        @Test
        public void callCancelled() {
            // Arrange
            val groupName = "1";
            val fakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData),
                    new FakeBlock(new BlockPosition(1, 1, 1), blockData)
            );
            val positions = Set.of(
                    new BlockPosition(0, 0, 0)
            );

            service.create(groupName, world, fakeBlocks);
            reset(pluginManager);
            reset(chunkService);

            doAnswer(invocation -> {
                UpdateFakeBlockGroupEvent event = invocation.getArgument(0);
                event.setCancelled(true);
                return null;
            }).when(pluginManager).callEvent(any(UpdateFakeBlockGroupEvent.class));

            // Act
            val response = service.removeBlocks(groupName, positions);

            // Assert
            assertTrue(response.isEmpty());
            assertEquals(fakeBlocks, service.getBlocks(groupName));

            verify(pluginManager).callEvent(any());
            verify(chunkService, never()).toChunklets(any());
        }

        @Test
        public void callWithNoBlockOverlap() {
            // Arrange
            val groupName = "1";
            val fakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData),
                    new FakeBlock(new BlockPosition(1, 1, 1), blockData)
            );
            val positions = Set.of(
                    new BlockPosition(2, 2, 2)
            );

            // Act
            service.create(groupName, world, fakeBlocks);
            val response = service.removeBlocks(groupName, positions);

            // Assert
            assertTrue(response.isEmpty());
            assertEquals(Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData),
                    new FakeBlock(new BlockPosition(1, 1, 1), blockData)
            ), service.getBlocks(groupName));
        }

        @Test
        public void callWithNoGroup() {
            // Arrange
            val groupName = "1";
            val positions = Set.of(
                    new BlockPosition(0, 0, 0)
            );

            // Act
            val response = service.removeBlocks(groupName, positions);

            // Assert
            assertTrue(response.isEmpty());
        }
    }

    @Nested
    public class DeleteTest {
        @Test
        public void call() {
            // Arrange
            val groupName = "1";
            val chunkPosition = new ChunkPosition(0, 0);
            val fakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData)
            );
            val playerId = UUID.randomUUID();

            when(servicesManager.getRegistration(PlayerGroupService.class)).thenReturn(playerGroupServiceProvider);
            when(playerGroupServiceProvider.getProvider()).thenReturn(playerGroupService);
            when(playerGroupService.getPlayersVisibleTo(groupName)).thenReturn(Set.of(playerId));
            when(chunkService.toChunklets(any())).thenReturn(Set.of(chunklet));
            when(chunkService.toChunk(any())).thenReturn(chunkPosition);

            service.create(groupName, world, fakeBlocks);
            val startingChunklets = service.getChunklets(Set.of(groupName), world, chunkPosition);
            assertEquals(List.of(chunklet), startingChunklets);
            reset(pluginManager);

            // Act
            val response = service.delete(groupName);

            // Assert
            assertEquals(fakeBlocks, response);
            val responseChunklets = service.getChunklets(Set.of(groupName), world, chunkPosition);
            assertTrue(responseChunklets.isEmpty());

            verify(pluginManager).callEvent(any());
            verify(playerGroupService).hideGroup(groupName, playerId);
        }

        @Test
        public void callCancelled() {
            // Arrange
            val groupName = "1";
            val chunkPosition = new ChunkPosition(0, 0);
            val fakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData)
            );
            val playerId = UUID.randomUUID();

            when(servicesManager.getRegistration(PlayerGroupService.class)).thenReturn(playerGroupServiceProvider);
            when(chunkService.toChunklets(any())).thenReturn(Set.of(chunklet));
            when(chunkService.toChunk(any())).thenReturn(chunkPosition);

            service.create(groupName, world, fakeBlocks);
            val startingChunklets = service.getChunklets(Set.of(groupName), world, chunkPosition);
            assertEquals(List.of(chunklet), startingChunklets);
            reset(pluginManager);

            doAnswer(invocation -> {
                DeleteFakeBlockGroupEvent event = invocation.getArgument(0);
                event.setCancelled(true);
                return null;
            }).when(pluginManager).callEvent(any(DeleteFakeBlockGroupEvent.class));

            // Act
            val response = service.delete(groupName);

            // Assert
            assertTrue(response.isEmpty());

            verify(pluginManager).callEvent(any());
            verify(playerGroupService, never()).hideGroup(groupName, playerId);
        }

        @Test
        public void callWithNoPlayerGroupService() {
            // Arrange
            val groupName = "1";
            val fakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData)
            );

            when(servicesManager.getRegistration(PlayerGroupService.class)).thenReturn(null);

            // Act
            service.create(groupName, world, fakeBlocks);
            val response = service.delete(groupName);

            // Assert
            assertTrue(response.isEmpty());
        }

        @Test
        public void callWithNoGroup() {
            // Arrange
            val groupName = "1";

            // Act
            val response = service.delete(groupName);

            // Assert
            assertTrue(response.isEmpty());
        }
    }

    @Nested
    public class GetBlocksTest {
        @Test
        public void call() {
            // Arrange
            val groupName = "1";
            val fakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData)
            );

            // Act
            service.create(groupName, world, fakeBlocks);
            val response = service.getBlocks(groupName);

            // Assert
            assertEquals(Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData)
            ),response);
        }

        @Test
        public void callWithNoGroup() {
            // Arrange
            val groupName = "1";

            // Act
            val response = service.getBlocks(groupName);

            // Assert
            assertTrue(response.isEmpty());
        }
    }

    @Nested
    public class GetChunkletsTest {
        @Test
        public void call() {
            // Arrange
            val groupName = "1";
            val groups = Set.of(groupName);
            val chunkPosition = new ChunkPosition(0, 0);
            val fakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData)
            );
            val chunkletPosition = new BlockPosition(0, 0, 0);

            when(chunkService.toChunklets(any())).thenReturn(Set.of(chunklet));
            when(chunkService.toChunk(chunkletPosition)).thenReturn(chunkPosition);
            when(chunklet.getPosition()).thenReturn(chunkletPosition);

            // Act
            service.create(groupName, world, fakeBlocks);
            val response = service.getChunklets(groups, world, chunkPosition);

            // Assert
            assertEquals(1, response.size());
            assertEquals(chunklet, response.get(0));
        }

        @Test
        public void callWithNoDataForGroup() {
            // Arrange
            val groupName = "1";
            val otherGroupName = "2";
            val groups = Set.of(otherGroupName);
            val chunkPosition = new ChunkPosition(0, 0);
            val fakeBlocks = Set.of(
                    new FakeBlock(new BlockPosition(0, 0, 0), blockData)
            );
            val chunkletPosition = new BlockPosition(0, 0, 0);

            when(chunkService.toChunklets(any())).thenReturn(Set.of(chunklet));
            when(chunkService.toChunk(chunkletPosition)).thenReturn(chunkPosition);
            when(chunklet.getPosition()).thenReturn(chunkletPosition);

            // Act
            service.create(groupName, world, fakeBlocks);
            val response = service.getChunklets(groups, world, chunkPosition);

            // Assert
            assertEquals(0, response.size());
        }

        @Test
        public void callWithNoDataForWorld() {
            // Arrange
            val groupName = "1";
            val groups = Set.of(groupName);
            val chunkPosition = new ChunkPosition(0, 0);

            // Act
            val response = service.getChunklets(groups, world, chunkPosition);

            // Assert
            assertEquals(0, response.size());
        }
    }
}

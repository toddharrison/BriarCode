package com.briarcraft.fakeblock.service;

import com.briarcraft.fakeblock.api.service.GroupService;
import com.briarcraft.fakeblock.config.PlayerGroupConfig;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerGroupServiceTest {
    private @Mock PluginManager pluginManager;
    private @Mock GroupService groupService;
    private @Mock ProtocolLibService protocolLibService;
    private @Mock PlayerGroupConfig playerGroupConfig;
    private @Mock Player player;

    private PlayerGroupServiceImpl service;

    @BeforeEach
    public void setup() {
        service = new PlayerGroupServiceImpl(pluginManager, groupService, protocolLibService, playerGroupConfig, (uuid) -> player, Map.of());
    }

    @Nested
    public class IsVisibleTest {
        @Test
        public void call() {
            // Arrange
            val groupName = "1";
            val playerId = UUID.randomUUID();

            when(player.getUniqueId()).thenReturn(playerId);
            when(groupService.getGroupNames()).thenReturn(Set.of(groupName));

            // Act
            service.showGroup(groupName, player);
            val response = service.isShown(groupName, player);

            // Assert
            assertTrue(response);
        }

        @Test
        public void callWithNoGroupNames() {
            // Arrange
            val groupName = "1";

            when(groupService.getGroupNames()).thenReturn(Set.of());

            // Act
            service.showGroup(groupName, player);
            val response = service.isShown(groupName, player);

            // Assert
            assertFalse(response);
        }

        @Test
        public void callWithNoServiceProvider() {
            // Arrange
            val groupName = "1";

            // Act
            service.showGroup(groupName, player);
            val response = service.isShown(groupName, player);

            // Assert
            assertFalse(response);
        }
    }

    @Nested
    public class GetVisibleGroupsTest {
        @Test
        public void call() {
            // Arrange
            val groupName = "1";
            val playerId = UUID.randomUUID();

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName));
            when(player.getUniqueId()).thenReturn(playerId);

            // Act
            service.showGroup(groupName, player);
            val response = service.getConfiguredGroups(player);

            // Assert
            assertEquals(Map.of(groupName, true), response);
        }
    }

    @Nested
    public class GetPLayersVisibleToTest {
        private @Mock Player player1;
        private @Mock Player player2;

        @Test
        public void call() {
            // Arrange
            val groupName = "1";
            val playerId = UUID.randomUUID();

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName));
            when(player.getUniqueId()).thenReturn(playerId);

            // Act
            service.showGroup(groupName, player);
            val response = service.getPlayersConfiguredIn(groupName);

            // Assert
            assertEquals(Set.of(playerId), response);
        }

        @Test
        public void callWithMultiplePlayers() {
            // Arrange
            val groupName1 = "1";
            val groupName2 = "2";
            val playerId1 = UUID.randomUUID();
            val playerId2 = UUID.randomUUID();

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName1, groupName2));
            when(player1.getUniqueId()).thenReturn(playerId1);
            when(player2.getUniqueId()).thenReturn(playerId2);

            // Act
            service.showGroup(groupName1, player1);
            service.showGroup(groupName2, player2);
            val response = service.getPlayersConfiguredIn(groupName1);

            // Assert
            assertEquals(Set.of(playerId1), response);
        }
    }

    @Nested
    public class ShowGroupTest {
        @Test
        public void call() {
            // Arrange
            val groupName = "1";
            val playerId = UUID.randomUUID();

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName));
            when(player.getUniqueId()).thenReturn(playerId);

            // Act
            val response = service.showGroup(groupName, player);

            // Assert
            assertTrue(response);
        }

        @Test
        public void callWhenGroupAlreadyVisible() {
            // Arrange
            val groupName = "1";
            val playerId = UUID.randomUUID();

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName));
            when(player.getUniqueId()).thenReturn(playerId);

            // Act
            service.showGroup(groupName, player);
            val response = service.showGroup(groupName, player);

            // Assert
            assertFalse(response);
        }

        @Test
        public void callWithMultipleGroups() {
            // Arrange
            val groupName1 = "1";
            val groupName2 = "2";
            val playerId = UUID.randomUUID();

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName1, groupName2));
            when(player.getUniqueId()).thenReturn(playerId);

            // Act
            service.showGroup(groupName1, player);
            val response = service.showGroup(groupName2, player);

            // Assert
            assertTrue(response);
            assertEquals(Map.of(groupName1, true, groupName2, true), service.getConfiguredGroups(player));
        }

        @Test
        public void callWithEventCancelled() {
            // Arrange
            val groupName = "1";

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName));
            doAnswer(invocation -> {
                invocation.getArgument(0, Cancellable.class).setCancelled(true);
                return null;
            }).when(pluginManager).callEvent(any());

            // Act
            val response = service.showGroup(groupName, player);

            // Assert
            assertFalse(response);
        }

        @Test
        public void callWithNoGroupService() {
            // Arrange
            val groupName = "1";

            // Act
            val response = service.showGroup(groupName, player);

            // Assert
            assertFalse(response);
        }
    }

    @Nested
    public class ShowGroupUUIDTest {
        @Test
        public void call() {
            // Arrange
            val groupName = "1";
            val playerId = UUID.randomUUID();

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName));
            when(player.getUniqueId()).thenReturn(playerId);

            // Act
            val response = service.showGroup(groupName, playerId);

            // Assert
            assertTrue(response);
        }

        @Test
        public void callWhenGroupAlreadyVisible() {
            // Arrange
            val groupName = "1";
            val playerId = UUID.randomUUID();

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName));
            when(player.getUniqueId()).thenReturn(playerId);

            // Act
            service.showGroup(groupName, playerId);
            val response = service.showGroup(groupName, playerId);

            // Assert
            assertFalse(response);
        }

        @Test
        public void callWithUnresolvablePlayer() {
            // Arrange
            service = new PlayerGroupServiceImpl(pluginManager, groupService, protocolLibService, playerGroupConfig, (uuid) -> null, Map.of());
            val groupName = "1";
            val playerId = UUID.randomUUID();

            // Act
            val response = service.showGroup(groupName, playerId);

            // Assert
            assertFalse(response);
        }
    }

    @Nested
    public class HideGroupTest {
        @Test
        public void call() {
            // Arrange
            val groupName = "1";
            val playerId = UUID.randomUUID();

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName));
            when(player.getUniqueId()).thenReturn(playerId);

            // Act
            service.showGroup(groupName, player);
            val response = service.hideGroup(groupName, player);

            // Assert
            assertTrue(response);
        }

        @Test
        public void callWithMultipleGroups() {
            // Arrange
            val groupName1 = "1";
            val groupName2 = "2";
            val playerId = UUID.randomUUID();

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName1, groupName2));
            when(player.getUniqueId()).thenReturn(playerId);

            // Act
            service.showGroup(groupName1, player);
            val response = service.hideGroup(groupName2, player);

            // Assert
            assertTrue(response);
        }

        @Test
        public void callWithNoShownGroups() {
            // Arrange
            val groupName = "1";
            val playerId = UUID.randomUUID();

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName));
            when(player.getUniqueId()).thenReturn(playerId);

            // Act
            val response = service.hideGroup(groupName, player);

            // Assert
            assertTrue(response);
        }

        @Test
        public void callWithEventCancelled() {
            // Arrange
            val groupName = "1";
            val playerId = UUID.randomUUID();

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName));
            when(player.getUniqueId()).thenReturn(playerId);

            service.showGroup(groupName, player);

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName));
            doAnswer(invocation -> {
                invocation.getArgument(0, Cancellable.class).setCancelled(true);
                return null;
            }).when(pluginManager).callEvent(any());

            // Act
            service.showGroup(groupName, player);
            val response = service.hideGroup(groupName, player);

            // Assert
            assertFalse(response);
        }

        @Test
        public void callWithNoGroupService() {
            // Arrange
            val groupName = "1";

            // Act
            service.showGroup(groupName, player);
            val response = service.hideGroup(groupName, player);

            // Assert
            assertFalse(response);
        }
    }

    @Nested
    public class HideGroupUUIDTest {
        @Test
        public void call() {
            // Arrange
            val groupName = "1";
            val playerId = UUID.randomUUID();

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName));
            when(player.getUniqueId()).thenReturn(playerId);

            // Act
            service.showGroup(groupName, player);
            val response = service.hideGroup(groupName, playerId);

            // Assert
            assertTrue(response);
        }

        @Test
        public void callWhenGroupAlreadyHidden() {
            // Arrange
            val groupName = "1";
            val playerId = UUID.randomUUID();

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName));
            when(player.getUniqueId()).thenReturn(playerId);

            // Act
            service.showGroup(groupName, player);
            service.hideGroup(groupName, player);
            val response = service.hideGroup(groupName, playerId);

            // Assert
            assertFalse(response);
        }

        @Test
        public void callWithUnresolvablePlayer() {
            // Arrange
            service = new PlayerGroupServiceImpl(pluginManager, groupService, protocolLibService, playerGroupConfig, (uuid) -> null, Map.of());
            val groupName = "1";
            val playerId = UUID.randomUUID();

            // Act
            val response = service.hideGroup(groupName, playerId);

            // Assert
            assertFalse(response);
        }
    }

    @Nested
    public class IsGroupPresentTest {
        @Test
        public void call() {
            // Arrange
            val groupName = "1";

            when(groupService.getGroupNames()).thenReturn(Set.of(groupName));

            // Act
            val response = service.isGroupPresent(groupName);

            // Assert
            assertNotNull(response);
            assertTrue(response);
        }

        @Test
        public void callWithMissingGroupName() {
            // Arrange
            val groupName = "1";

            when(groupService.getGroupNames()).thenReturn(Set.of());

            // Act
            val response = service.isGroupPresent(groupName);

            // Assert
            assertNotNull(response);
            assertFalse(response);
        }
    }
}

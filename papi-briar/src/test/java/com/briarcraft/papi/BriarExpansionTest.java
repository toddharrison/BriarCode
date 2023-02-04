package com.briarcraft.papi;

import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.briarcraft.papi.BriarExpansion.PLAYER_MIDNIGHTS_SINCE_LAST_PLAYED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SuppressWarnings("deprecation")
@ExtendWith(MockitoExtension.class)
public class BriarExpansionTest {
    private @Mock OfflinePlayer player;

    private BriarExpansion expansion;

    @BeforeEach
    public void setup() {
        expansion = new BriarExpansion();
    }

    @Nested
    public class OnPlaceholderRequestTest {
        @Test
        public void test1() {
            // Arrange
            final var lastSeen = millis(2022, 9, 17, 0, 0);
            final var now = millis(2022, 9, 17, 0, 0);

            expansion.setTimeResolver(() -> now);
            when(player.getLastPlayed()).thenReturn(lastSeen);

            // Act
            final var response = expansion.onRequest(player, PLAYER_MIDNIGHTS_SINCE_LAST_PLAYED);

            // Assert
            assertEquals("0", response);
        }

        @Test
        public void test2() {
            // Arrange
            final var lastSeen = millis(2022, 9, 16, 23, 59);
            final var now = millis(2022, 9, 17, 0, 0);

            expansion.setTimeResolver(() -> now);
            when(player.getLastPlayed()).thenReturn(lastSeen);

            // Act
            final var response = expansion.onRequest(player, PLAYER_MIDNIGHTS_SINCE_LAST_PLAYED);

            // Assert
            assertEquals("1", response);
        }

        @Test
        public void test3() {
            // Arrange
            final var lastSeen = millis(2022, 9, 16, 23, 59);
            final var now = millis(2022, 9, 17, 0, 1);

            expansion.setTimeResolver(() -> now);
            when(player.getLastPlayed()).thenReturn(lastSeen);

            // Act
            final var response = expansion.onRequest(player, PLAYER_MIDNIGHTS_SINCE_LAST_PLAYED);

            // Assert
            assertEquals("1", response);
        }

        @Test
        public void test4() {
            // Arrange
            final var lastSeen = millis(2022, 9, 17, 0, 1);
            final var now = millis(2022, 9, 17, 23, 59);

            expansion.setTimeResolver(() -> now);
            when(player.getLastPlayed()).thenReturn(lastSeen);

            // Act
            final var response = expansion.onRequest(player, PLAYER_MIDNIGHTS_SINCE_LAST_PLAYED);

            // Assert
            assertEquals("0", response);
        }

        @Test
        public void test5() {
            // Arrange
            final var lastSeen = millis(2022, 9, 17, 0, 1);
            final var now = millis(2022, 9, 18, 0, 0);

            expansion.setTimeResolver(() -> now);
            when(player.getLastPlayed()).thenReturn(lastSeen);

            // Act
            final var response = expansion.onRequest(player, PLAYER_MIDNIGHTS_SINCE_LAST_PLAYED);

            // Assert
            assertEquals("1", response);
        }

        @Test
        public void test6() {
            // Arrange
            final var lastSeen = millis(2022, 9, 17, 8, 0);
            final var now = millis(2022, 9, 20, 9, 0);

            expansion.setTimeResolver(() -> now);
            when(player.getLastPlayed()).thenReturn(lastSeen);

            // Act
            final var response = expansion.onRequest(player, PLAYER_MIDNIGHTS_SINCE_LAST_PLAYED);

            // Assert
            assertEquals("3", response);
        }

        @Test
        public void test7() {
            // Arrange
            final var lastSeen = millis(2022, 9, 23, 23, 38);
            final var now = millis(2022, 9, 24, 0, 42);

            expansion.setTimeResolver(() -> now);
            when(player.getLastPlayed()).thenReturn(lastSeen);

            // Act
            final var response = expansion.onRequest(player, PLAYER_MIDNIGHTS_SINCE_LAST_PLAYED);

            // Assert
            assertEquals("1", response);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private long millis(final int year, final int month, final int day, final int hour, final int minute) {
        return LocalDateTime.of(year, month, day, hour, minute).toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}

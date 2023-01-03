package com.briarcraft.fakeblock.api.event;

import com.briarcraft.fakeblock.api.data.FakeBlock;
import com.briarcraft.fakeblock.api.data.Group;
import com.comphenix.protocol.wrappers.BlockPosition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * This CancellableEvent is called when an existing Group is modified, either through an add or remove of FakeBlocks.
 */
@RequiredArgsConstructor
@Getter
public class UpdateFakeBlockGroupEvent extends CancellableEvent {
    private final @Nonnull Group group;
    private final @Nonnull Set<FakeBlock> addedFakeBlocks;
    private final @Nonnull Set<BlockPosition> removedPositions;
}

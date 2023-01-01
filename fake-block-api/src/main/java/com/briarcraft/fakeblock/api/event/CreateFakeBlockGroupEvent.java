package com.briarcraft.fakeblock.api.event;

import com.briarcraft.fakeblock.api.data.Group;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
@Getter
public class CreateFakeBlockGroupEvent extends CancellableEvent {
    private final @Nonnull Group group;
}

package com.briarcraft.fakeblock.config;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public interface Config<T> {
    @Nonnull List<T> load();

    void save(final @Nonnull Collection<T> data);
}

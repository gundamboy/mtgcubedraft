package com.ragingclaw.mtgcubedraftsimulator.utils;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class NotLoggingTree extends Timber.Tree {
    @Override
    protected void log(final int priority, final String tag, @NotNull final String message, final Throwable throwable) {

    }
}

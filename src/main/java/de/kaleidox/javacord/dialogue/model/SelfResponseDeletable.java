package de.kaleidox.javacord.dialogue.model;

import org.jetbrains.annotations.Contract;

public interface SelfResponseDeletable<Self extends SelfResponseDeletable> {
    @Contract(value = "_ -> this", mutates = "this")
    Self withResponseDeletion(boolean status);

    boolean getResponseDeletionStatus();

    @Contract(value = "-> this", mutates = "this")
    default Self enableResponseDeletion() {
        return withResponseDeletion(true);
    }

    @Contract(value = "-> this", mutates = "this")
    default Self disableResponseDeletion() {
        return withResponseDeletion(false);
    }
}

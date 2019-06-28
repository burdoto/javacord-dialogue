package de.kaleidox.javacord.dialogue.model;

public interface SelfResponseDeletable<Self extends SelfResponseDeletable> {
    Self withResponseDeletion(boolean status);

    boolean getResponseDeletionStatus();

    default Self enableResponseDeletion() {
        return withResponseDeletion(true);
    }

    default Self disableResponseDeletion() {
        return withResponseDeletion(false);
    }
}

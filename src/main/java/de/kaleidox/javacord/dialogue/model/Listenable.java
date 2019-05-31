package de.kaleidox.javacord.dialogue.model;

import java.util.concurrent.CompletableFuture;

public interface Listenable<R> {
    CompletableFuture<R> listenAsync();

    default R listenBlocking() {
        return listenAsync().join();
    }
}

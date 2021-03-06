package de.kaleidox.javacord.dialogue.input.text;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import de.kaleidox.javacord.dialogue.exception.ResponseTimeoutException;
import de.kaleidox.javacord.dialogue.input.SingleInputAction;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.event.ListenerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextInput extends SingleInputAction<TextInput, String> {
    private final AtomicLong sentMessage;

    private CompletableFuture<String> responseFuture;
    private ListenerManager<MessageCreateListener> listenerManager;

    public TextInput(@NotNull TextChannel context) {
        this(context, null);
    }

    public TextInput(@NotNull TextChannel context, @Nullable Supplier<EmbedBuilder> embedBaseSupplier) {
        super(context, embedBaseSupplier);

        this.sentMessage = new AtomicLong(-1);
    }

    @Override
    public CompletableFuture<String> listenAsync() {
        if (active) throw new IllegalStateException("TextInput is already listening!");

        this.active = true;

        return getChannel()
                .sendMessage(makeEmbed())
                .thenApply(msg -> {
                    sentMessage.set(msg.getId());
                    return msg;
                })
                .thenApply(Message::getChannel)
                .thenCompose(chl -> {
                    responseFuture = new CompletableFuture<>();
                    listenerManager = chl.addMessageCreateListener(this::input);
                    if (timeout != -1 && timeUnit != null)
                        listenerManager.removeAfter(timeout, timeUnit)
                                .addRemoveHandler(() -> {
                                    long msg = sentMessage.get();
                                    if (msg != -1) api.getCachedMessageById(msg).ifPresent(Message::delete);

                                    if (!responseFuture.isDone()) {
                                        if (defaultValue == null)
                                            responseFuture.completeExceptionally(new ResponseTimeoutException());
                                        else responseFuture.complete(defaultValue);
                                    }
                                });
                    return responseFuture;
                });
    }

    private synchronized void input(MessageCreateEvent event) {
        if (target != null && !event.getMessageAuthor()
                .asUser()
                .map(usr -> Objects.equals(target, usr))
                .orElse(false))
            return;

        responseFuture.complete(event.getMessageContent());
        listenerManager.remove();

        if (responseDeletion)
            event.deleteMessage("Response Deletion");
    }
}

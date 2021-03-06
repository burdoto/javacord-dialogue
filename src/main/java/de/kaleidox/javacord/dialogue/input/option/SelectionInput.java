package de.kaleidox.javacord.dialogue.input.option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;

import de.kaleidox.javacord.dialogue.exception.ResponseTimeoutException;
import de.kaleidox.javacord.dialogue.input.SingleInputAction;
import de.kaleidox.javacord.dialogue.input.option.model.EmojiOption;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.reaction.SingleReactionEvent;
import org.javacord.api.util.event.ListenerManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SelectionInput<R> extends SingleInputAction<SelectionInput<R>, R> {
    private final List<EmojiOption<R>> options;
    private CompletableFuture<R> responseFuture;
    private List<ListenerManager<?>> listenerManagers;

    public SelectionInput(@NotNull TextChannel context) {
        this(context, null);
    }

    public SelectionInput(@NotNull TextChannel context, @Nullable Supplier<EmbedBuilder> embedBaseSupplier) {
        super(context, embedBaseSupplier);

        this.options = new ArrayList<>();
    }

    @Contract(value = "_, _, _, _ -> this", mutates = "this")
    public SelectionInput<R> addOption(String name, String description, String emoji, R value) {
        return addOption(new EmojiOption<>(name, description, emoji, value));
    }

    @Contract(value = "null -> fail; _ -> this", mutates = "this")
    public SelectionInput<R> addOption(EmojiOption<R> option) {
        if (active) throw new IllegalStateException("SelectionInput is already listening!");
        if (options.stream().anyMatch(opt -> opt.getEmoji().equals(option.getEmoji())))
            throw new IllegalArgumentException("Emoji [" + option.getEmoji() + "] is already used!");

        options.add(option);

        return this;
    }

    @Contract(value = "null -> fail; _ -> this", mutates = "this")
    public SelectionInput<R> removeOptionIf(Predicate<EmojiOption<R>> filter) {
        if (active) throw new IllegalStateException("SelectionInput is already listening!");

        options.removeIf(filter);

        return this;
    }

    public List<EmojiOption<R>> getOptions() {
        return Collections.unmodifiableList(options);
    }

    @Override
    public CompletableFuture<R> listenAsync() {
        if (active) throw new IllegalStateException("SelectionInput is already listening!");

        active = true;

        addEmbedModifier(embed -> {
            embed.removeAllFields();

            for (EmojiOption<R> option : options)
                embed.addField(option.getEmoji() + " - " + option.getName(), option.getDescription());
        });

        return getChannel()
                .sendMessage(makeEmbed())
                .thenCompose(msg -> {
                    TextChannel chl = msg.getChannel();

                    for (EmojiOption<R> option : options)
                        msg.addReaction(option.getEmoji());

                    responseFuture = new CompletableFuture<>();
                    listenerManagers = new ArrayList<>();

                    listenerManagers.add(chl.addReactionAddListener(this::handleReaction));
                    listenerManagers.add(chl.addReactionRemoveListener(this::handleReaction));

                    listenerManagers.get(0)
                            .addRemoveHandler(() -> {
                                msg.delete();

                                if (!responseFuture.isDone()) {
                                    if (defaultValue == null)
                                        responseFuture.completeExceptionally(new ResponseTimeoutException());
                                    else responseFuture.complete(defaultValue);
                                }
                            });

                    if (timeout != -1 && timeUnit != null)
                        api.getThreadPool()
                                .getScheduler()
                                .schedule(() -> listenerManagers.forEach(ListenerManager::remove), timeout, timeUnit);

                    return responseFuture;
                });
    }

    private synchronized void handleReaction(SingleReactionEvent event) {
        if (!Objects.equals(target, event.getUser()))
            return;

        Optional<EmojiOption<R>> option = options.stream()
                .filter(opt -> event.getEmoji()
                        .asUnicodeEmoji()
                        .map(opt.getEmoji()::equals)
                        .orElse(false))
                .findFirst();

        if (!option.isPresent()) return;

        option.ifPresent(opt -> responseFuture.complete(opt.getValue()));
        listenerManagers.forEach(ListenerManager::remove);

        if (responseDeletion)
            event.getMessage().ifPresent(Message::delete);
    }
}

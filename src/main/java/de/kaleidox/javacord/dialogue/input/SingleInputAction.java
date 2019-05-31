package de.kaleidox.javacord.dialogue.input;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import de.kaleidox.javacord.dialogue.model.Listenable;
import de.kaleidox.javacord.dialogue.model.SelfDefaultable;
import de.kaleidox.javacord.dialogue.model.SelfTargetable;
import de.kaleidox.javacord.dialogue.model.SelfTimeoutable;
import de.kaleidox.javacord.util.ui.embed.DefaultEmbedFactory;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SingleInputAction<Self extends SingleInputAction, R>
        implements SelfTimeoutable<Self>, SelfDefaultable<Self, R>, SelfTargetable<Self, User>, Listenable<R> {
    protected final DiscordApi api;
    protected final long context;
    protected final Supplier<EmbedBuilder> embedBaseSupplier;
    protected final Collection<Consumer<EmbedBuilder>> modifiers;

    protected long timeout = -1;
    protected @Nullable TimeUnit timeUnit;
    protected @Nullable R defaultValue;
    protected @Nullable User target;

    protected boolean active = false;

    protected SingleInputAction(@NotNull TextChannel context, @Nullable Supplier<EmbedBuilder> embedBaseSupplier) {
        Objects.requireNonNull(context, "Context must not be null!");

        this.api = context.getApi();
        this.context = context.getId();
        this.embedBaseSupplier = embedBaseSupplier == null ? DefaultEmbedFactory.INSTANCE : embedBaseSupplier;

        this.modifiers = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public Self addEmbedModifier(Consumer<EmbedBuilder> embedModifier) {
        if (active) throw new IllegalStateException("InputAction was already executed!");

        modifiers.add(embedModifier);

        return (Self) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Self withTimeout(long time, TimeUnit unit) {
        if (active) throw new IllegalStateException("InputAction was already executed!");

        this.timeout = time;
        this.timeUnit = unit;

        return (Self) this;
    }

    @Override
    public Optional<Duration> getTimeout() {
        return timeout != -1 && timeUnit != null
                ? Optional.of(Duration.ofMillis(timeUnit.toMillis(timeout)))
                : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Self withDefaultValue(R value) {
        if (active) throw new IllegalStateException("InputAction was already executed!");

        this.defaultValue = value;

        return (Self) this;
    }

    @Override
    public Optional<R> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Self withTarget(User target) {
        if (active) throw new IllegalStateException("InputAction was already executed!");

        this.target = target;

        return (Self) this;
    }

    @Override
    public Optional<User> getTarget() {
        return Optional.ofNullable(target);
    }

    protected EmbedBuilder makeEmbed() {
        EmbedBuilder embed = embedBaseSupplier.get();

        for (Consumer<EmbedBuilder> modifier : modifiers)
            modifier.accept(embed);

        return embed;
    }

    protected TextChannel getChannel() {
        return api.getChannelById(context)
                .flatMap(Channel::asTextChannel)
                .orElseThrow(NoSuchElementException::new);
    }
}

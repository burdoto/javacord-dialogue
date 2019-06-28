package de.kaleidox.javacord.dialogue.input.option;

import java.util.function.Predicate;
import java.util.function.Supplier;

import de.kaleidox.javacord.dialogue.input.option.model.EmojiOption;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BooleanInput extends SelectionInput<Boolean> {
    public static String YES_EMOJI = "✅";
    public static String NO_EMOJI = "❌";

    public BooleanInput(@NotNull TextChannel context) {
        this(context, null);
    }

    public BooleanInput(@NotNull TextChannel context, @Nullable Supplier<EmbedBuilder> embedBuilderSupplier) {
        super(context, embedBuilderSupplier);

        super.addOption("Yes", "Yes", YES_EMOJI, true);
        super.addOption("No", "No", NO_EMOJI, true);
    }

    /**
     * Always fails, because a BooleanInput has a predefined set of Options.
     *
     * @throws IllegalStateException Always, because a {@code BooleanInput} has a predefined set of Options.
     */
    @Override
    @Deprecated
    @Contract("_, _, _, _ -> fail")
    public SelectionInput<Boolean> addOption(String name, String description, String emoji, Boolean value)
            throws IllegalStateException {
        throw new IllegalStateException("Cannot modify options of BooleanInput!");
    }

    /**
     * Always fails, because a BooleanInput has a predefined set of Options.
     *
     * @throws IllegalStateException Always, because a {@code BooleanInput} has a predefined set of Options.
     */
    @Override
    @Deprecated
    @Contract("_ -> fail")
    public SelectionInput<Boolean> addOption(EmojiOption<Boolean> option)
            throws IllegalStateException {
        throw new IllegalStateException("Cannot modify options of BooleanInput!");
    }

    /**
     * Always fails, because a BooleanInput has a predefined set of Options.
     *
     * @throws IllegalStateException Always, because a {@code BooleanInput} has a predefined set of Options.
     */
    @Override
    @Deprecated
    @Contract("_ -> fail")
    public SelectionInput<Boolean> removeOptionIf(Predicate<EmojiOption<Boolean>> filter)
            throws IllegalStateException {
        throw new IllegalStateException("Cannot modify options of BooleanInput!");
    }
}

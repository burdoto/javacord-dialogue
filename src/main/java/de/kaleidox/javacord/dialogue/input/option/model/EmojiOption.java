package de.kaleidox.javacord.dialogue.input.option.model;

public class EmojiOption<T> extends Option<T> {
    private final String emoji;

    public EmojiOption(String name, String description, String emoji, T value) {
        super(name, description, value);
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }
}

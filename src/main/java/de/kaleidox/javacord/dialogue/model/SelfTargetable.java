package de.kaleidox.javacord.dialogue.model;

import java.util.Optional;

import org.jetbrains.annotations.Contract;

public interface SelfTargetable<Self extends SelfTargetable, Z> {
    @Contract(value = "_ -> this", mutates = "this")
    Self withTarget(Z target);

    Optional<Z> getTarget();

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Contract(value = "-> this", mutates = "this")
    default Self removeTarget() {
        withTarget(null);
        return (Self) this;
    }
}

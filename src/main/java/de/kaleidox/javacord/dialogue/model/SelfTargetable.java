package de.kaleidox.javacord.dialogue.model;

import java.util.Optional;

public interface SelfTargetable<Self extends SelfTargetable, Z> {
    Self withTarget(Z target);

    Optional<Z> getTarget();

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    default Self removeTarget() {
        withTarget(null);
        return (Self) this;
    }
}

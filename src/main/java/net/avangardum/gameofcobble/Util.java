package net.avangardum.gameofcobble;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Util {
    /** If the object is an instance of the given class, cast it to that class, otherwise return null. */
    public static <T> @Nullable T as(@NotNull Class<T> cl, @Nullable Object obj) {
        return cl.isInstance(obj) ? cl.cast(obj) : null;
    }

    public static <T> @NotNull T assertNotNull(@Nullable T value) {
        assert value != null;
        return value;
    }
}

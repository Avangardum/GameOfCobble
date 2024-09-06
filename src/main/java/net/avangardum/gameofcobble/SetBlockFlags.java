package net.avangardum.gameofcobble;

/** Flags for {@link net.minecraft.world.level.Level#setBlock Level.setBlock}. See setBlock docs for more info. */
@SuppressWarnings("unused")
final class SetBlockFlags {
    public static final int NONE = 0;
    public static final int BLOCK_UPDATE = 1;
    public static final int SEND_TO_CLIENTS = 2;
    public static final int PREVENT_RERENDER = 4;
    public static final int RERENDER_ON_MAIN_THREAD = 8;
    public static final int PREVENT_NEIGHBOR_REACTIONS = 16;
    public static final int PREVENT_NEIGHBOR_REACTIONS_FROM_SPAWNING_DROPS = 32;
    public static final int BLOCK_MOVED = 64;
}

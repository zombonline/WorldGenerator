package uk.bradleyjones.worldgenerator.world;

public enum TileType {
    NONE,
    AIR,
    BEDROCK,
    GRASS,
    DIRT,
    STONE,
    SAND,
    WATER,
    GRAVEL,
    SNOW,
    FLOWER,
    LEAVES,
    LOG,
    SEAWEED,
    SEAWEED_BOTTOM,
    HANGING_RED_MOSS,
    HANGING_MOSS,
    RED_MUSHROOM,
    PINK_MUSHROOM,
    CACTUS,
    IRON_ORE,
    COAL_ORE,
    DIAMOND_ORE,
    CLAY,
    COPPER_ORE,
    QUARTZ,
    AMETHYST_ORE,
    LAPIS_ORE,
    RED_CLAY;

    public TileType getBackground() {
        return switch (this) {
            case PINK_MUSHROOM, RED_MUSHROOM, FLOWER, CACTUS, HANGING_MOSS, HANGING_RED_MOSS -> AIR;
            case SEAWEED, SEAWEED_BOTTOM -> WATER;
            case DIAMOND_ORE -> STONE;
            default -> this;
        };
    }
}
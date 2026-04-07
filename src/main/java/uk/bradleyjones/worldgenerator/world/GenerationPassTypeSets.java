package uk.bradleyjones.worldgenerator.world;

import java.util.EnumSet;

public class GenerationPassTypeSets {
    public static final EnumSet<GenerationPassType> ALL          = EnumSet.allOf(GenerationPassType.class);
    public static final EnumSet<GenerationPassType> NO_WATER     = EnumSet.complementOf(EnumSet.of(GenerationPassType.WATER));
    public static final EnumSet<GenerationPassType> TERRAIN_ONLY = EnumSet.of(GenerationPassType.HEIGHTMAP, GenerationPassType.BIOME, GenerationPassType.CAVES, GenerationPassType.SUBSTANCE);
    public static final EnumSet<GenerationPassType> SOLID_ONLY   = EnumSet.of(GenerationPassType.HEIGHTMAP, GenerationPassType.BIOME);
    public static final EnumSet<GenerationPassType> NO_DECOR = EnumSet.complementOf(EnumSet.of(GenerationPassType.DECORATIONS));}

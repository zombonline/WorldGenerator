package uk.bradleyjones.worldgenerator.world;

import java.util.EnumSet;

public enum GenerationPassType {
    HEIGHTMAP,      // noise-based surface shape
    BIOME,          // tile assignment per depth (surface/subsurface/stone)
    SUBSTANCE,      // ore/material overrides
    CAVES,          // cave carving
    WATER,          // water placement
    DECORATIONS     // decoration layer


}


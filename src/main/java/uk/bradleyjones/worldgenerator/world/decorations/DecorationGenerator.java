package uk.bradleyjones.worldgenerator.world.decorations;

import uk.bradleyjones.worldgenerator.world.TileType;
import uk.bradleyjones.worldgenerator.world.World;
import uk.bradleyjones.worldgenerator.world.biomes.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DecorationGenerator {

    private final TileType[][] grid;
    private final int width;
    private final int height;

    public DecorationGenerator(World world, long seed, List<Decoration> decorations) {
        this.width = world.getWorldConfig().width;
        this.height = world.getWorldConfig().height;
        this.grid = new TileType[width][height];
        generate(world, seed, decorations);
    }

    private void generate(World world, long seed, List<Decoration> decorations) {
        Random random = new Random(seed);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileType tile = world.getTile(x, y, true);
                TileType below = world.getTile(x, y + 1, true);
                TileType above = world.getTile(x, y - 1, true);

                if (tile != TileType.AIR && tile != TileType.WATER) continue;
                if (grid[x][y] != null) continue;

                Biome biome = world.getBiomeAt(x);

                List<Decoration> eligible = new ArrayList<>();
                for (Decoration d : decorations) {
                    switch (d.placementType) {
                        case FLOOR -> {
                            if (tile != TileType.AIR) continue;
                            if (below == TileType.AIR || below == TileType.WATER) continue;
                        }
                        case CEILING -> {
                            if (tile != TileType.AIR) continue;
                            if (above == TileType.AIR || above == TileType.WATER) continue;
                        }
                        case UNDERWATER -> {
                            if (tile != TileType.WATER) continue;
                        }
                        case UNDERWATER_FLOOR -> {
                            if (tile != TileType.WATER) continue;
                            if (below == TileType.AIR || below == TileType.WATER) continue;
                        }
                        case UNDERWATER_CEILING -> {
                            if (tile != TileType.WATER) continue;
                            if (above == TileType.AIR || above == TileType.WATER) continue;
                        }
                    }

                    // Required surface check
                    if (d.requiredSurface != null) {
                        TileType surface = switch (d.placementType) {
                            case FLOOR, UNDERWATER_FLOOR -> below;
                            case CEILING, UNDERWATER_CEILING -> above;
                            default -> null;
                        };
                        if (d.requiredSurface != surface) continue;
                    }

                    if (!d.allowedBiomes.contains(biome.id) && !d.allowedBiomes.isEmpty()) continue;
                    eligible.add(d);
                }

                if (eligible.isEmpty()) continue;

                float totalChance = 0;
                for (Decoration d : eligible) totalChance += d.chance;

                float roll = random.nextFloat();
                if (roll > totalChance) continue;

                Decoration chosen = null;
                float cursor = 0;
                for (Decoration d : eligible) {
                    cursor += d.chance;
                    if (roll < cursor) {
                        chosen = d;
                        break;
                    }
                }

                if (chosen == null) continue;

                int rootY = switch (chosen.placementType) {
                    case FLOOR, UNDERWATER_FLOOR -> y + 1;
                    case CEILING, UNDERWATER_CEILING -> y - 1;
                    case UNDERWATER -> y;
                };

                boolean canPlace = true;

                // Footprint solid ground check for floor placements
                if (chosen.placementType == PlacementType.FLOOR || chosen.placementType == PlacementType.UNDERWATER_FLOOR) {
                    int maxDy = chosen.getCells().stream().mapToInt(c -> c.dy).max().orElse(0);
                    for (DecorationCell cell : chosen.getCells()) {
                        if (cell.dy == maxDy) {
                            int cx = x + cell.dx;
                            int cy = rootY + cell.dy;
                            TileType tileBelow = world.getTile(cx, cy + 1, true);
                            if (tileBelow == TileType.AIR || tileBelow == TileType.WATER) {
                                canPlace = false;
                                break;
                            }
                        }
                    }
                }

                if (!canPlace) continue;

                // Bounds and occupancy check
                for (DecorationCell cell : chosen.getCells()) {
                    int cx = x + cell.dx;
                    int cy = rootY + cell.dy;
                    if (cx < 0 || cx >= width || cy < 0 || cy >= height) {
                        canPlace = false;
                        break;
                    }
                    if (grid[cx][cy] != null) {
                        canPlace = false;
                        break;
                    }
                }

                if (!canPlace) continue;

                // Place
                for (DecorationCell cell : chosen.getCells()) {
                    grid[x + cell.dx][rootY + cell.dy] = cell.tileType;
                }
            }
        }
    }
    public TileType getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return null;
        return grid[x][y]; // null means no decoration here
    }

}
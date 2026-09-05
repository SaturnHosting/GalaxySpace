package galaxyspace.systems.SolarSystem.moons.deimos.world.gen;

import java.util.Random;

import galaxyspace.core.GSBlocks;
import galaxyspace.core.configs.GSConfigDimensions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenDeimosDryIce implements IWorldGenerator {

    private static final int DRY_ICE_META = 4;

    private static final double CLIMATE_SCALE = 260.0D;
    private static final double COLD_THRESHOLD = 0.25D;

    private NoiseGeneratorSimplex climate;
    private long noiseSeed = Long.MIN_VALUE;

    private void ensureNoise(World world) {
        long seed = world.getSeed();
        if (climate == null || noiseSeed != seed) {
            climate = new NoiseGeneratorSimplex(new Random(seed ^ 0xDE1305C01DL));
            noiseSeed = seed;
        }
    }

    public double coldAt(int worldX, int worldZ) {
        double v = climate.getValue(worldX / CLIMATE_SCALE, worldZ / CLIMATE_SCALE);
        if (v <= COLD_THRESHOLD) return 0.0D;
        return (v - COLD_THRESHOLD) / (1.0D - COLD_THRESHOLD);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

        if (world.provider.getDimension() != GSConfigDimensions.dimensionIDDeimos) return;
        ensureNoise(world);

        final IBlockState dryIce = GSBlocks.SURFACE_ICE.getStateFromMeta(DRY_ICE_META);
        final int baseX = chunkX << 4;
        final int baseZ = chunkZ << 4;

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = baseX + dx;
                int z = baseZ + dz;

                double cold = coldAt(x, z);
                if (cold <= 0.0D) continue;

                int topY = -1;
                for (int y = 100; y > 0; y--) {
                    if (!world.isAirBlock(new BlockPos(x, y, z))) { topY = y; break; }
                }
                if (topY < 1) continue;

                BlockPos top = new BlockPos(x, topY, z);
                if (world.getBlockState(top).getBlock() == Blocks.AIR) continue;

                float chance = 0.30F + 0.60F * (float) cold;
                if (random.nextFloat() < chance) {
                    world.setBlockState(top, dryIce, 2);

                    if (cold > 0.6D && random.nextInt(5) == 0) {
                        world.setBlockState(top.up(), dryIce, 2);
                    }
                }
            }
        }
    }
}
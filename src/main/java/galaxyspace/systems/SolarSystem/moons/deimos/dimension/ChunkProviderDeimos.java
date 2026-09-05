package galaxyspace.systems.SolarSystem.moons.deimos.dimension;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome;
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes;
import asmodeuscore.core.astronomy.dimension.world.gen.MapGenCaves;
import galaxyspace.core.GSBlocks;
import galaxyspace.systems.SolarSystem.moons.deimos.world.gen.BiomeDecoratorDeimos;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorSimplex;

 
public class ChunkProviderDeimos extends ChunkProviderSpaceLakes {

    private static boolean DEBUG = true;
    private static int dbgCount = 0;

    private static final double REGION_SCALE     = 3400.0D;
    private static final double REGION_GAIN      = 2.0D;
    private static final int    ELEVATION_LEVELS = 1;
    private static final double PLATEAU_AMPLITUDE = 46.0D;

    private static final double STEEP_SCALE = 1400.0D;
    private static final double SHARP_MIN   = 2.6D;
    private static final double SHARP_MAX   = 8.0D;

    private static final double FLAT_SCALE     = 2600.0D;
    private static final double FLAT_THRESHOLD = 0.18D;
    private static final double FLAT_BAND      = 0.20D;
    private static final double FLAT_DAMP      = 0.92D;

    private static final double CANYON_SCALE       = 1500.0D; 
    private static final double CANYON_STRETCH     = 2.2D; 
    private static final double CANYON_DIR_SCALE   = 2600.0D;
    private static final double CANYON_ANGLE_RANGE = 0.6D;
    private static final double CANYON_WIDTH = 0.034D;
    private static final double CANYON_WALL  = 0.026D;
    private static final double CANYON_DEPTH = 26.0D;
    private static final double CANYON_WARP_SCALE = 320.0D;
    private static final double CANYON_WARP       = 85.0D;
    private static final double CANYON_SEG_SCALE     = 1500.0D;
    private static final double CANYON_SEG_THRESHOLD = 0.14D;
    private static final double CANYON_SEG_BAND      = 0.16D;

    private static final double DUNE_SCALE   = 70.0D;
    private static final double DUNE_AMP     = 2.6D;
    private static final double CRATER_SCALE = 130.0D;
    private static final double CRATER_START = 0.32D;
    private static final double CRATER_FULL  = 0.78D;
    private static final double CRATER_DEPTH = 6.0D;

    private static final double WARP_SCALE    = 900.0D;
    private static final double WARP_STRENGTH = 320.0D;
    private static final double DETAIL_SCALE     = 42.0D;
    private static final double DETAIL_AMPLITUDE = 2.5D;

    private static final double CLIMATE_SCALE = 5200.0D;
    private static final double COLD_START = 0.06D;
    private static final double COLD_FULL  = 0.58D;
    private static final double MOTTLE_SCALE = 85.0D;
    private static final double MOTTLE_AMT   = 0.30D;
    private static final int    DRY_ICE_META = 4;

    private static final int MIN_SURFACE = 6;
    private static final int MAX_SURFACE = 245;

    private final NoiseGeneratorSimplex terrainNoise;
    private final NoiseGeneratorSimplex warpNoise;
    private final NoiseGeneratorSimplex detailNoise;
    private final NoiseGeneratorSimplex climateNoise;
    private final NoiseGeneratorSimplex steepNoise;
    private final NoiseGeneratorSimplex mottleNoise;
    private final NoiseGeneratorSimplex flatNoise;
    private final NoiseGeneratorSimplex canyonNoise;
    private final NoiseGeneratorSimplex duneNoise;
    private final NoiseGeneratorSimplex craterNoise;
    private final NoiseGeneratorSimplex canyonWarpNoise;
    private final NoiseGeneratorSimplex canyonSegNoise;
    private final NoiseGeneratorSimplex canyonDirNoise;

    private final MapGenCaves caveGenerator =
        new MapGenCaves(GSBlocks.DEIMOS_BLOCKS.getStateFromMeta(0), GSBlocks.DEIMOS_BLOCKS.getStateFromMeta(1));

    public ChunkProviderDeimos(World par1World, long seed, boolean mapFeaturesEnabled)
    {
        super(par1World, seed, mapFeaturesEnabled);
        this.terrainNoise = new NoiseGeneratorSimplex(new Random(seed ^ 0x7E44A17E44A1L));
        this.warpNoise    = new NoiseGeneratorSimplex(new Random(seed ^ 0x00A17E9B2CL));
        this.detailNoise  = new NoiseGeneratorSimplex(new Random(seed + 4242L));
        this.climateNoise = new NoiseGeneratorSimplex(new Random(seed ^ 0x00C01DC01DL));
        this.steepNoise   = new NoiseGeneratorSimplex(new Random(seed + 7777L));
        this.mottleNoise  = new NoiseGeneratorSimplex(new Random(seed + 313L));
        this.flatNoise    = new NoiseGeneratorSimplex(new Random(seed + 5150L));
        this.canyonNoise  = new NoiseGeneratorSimplex(new Random(seed + 2718L));
        this.duneNoise    = new NoiseGeneratorSimplex(new Random(seed + 1618L));
        this.craterNoise  = new NoiseGeneratorSimplex(new Random(seed + 8675L));
        this.canyonWarpNoise = new NoiseGeneratorSimplex(new Random(seed + 6060L));
        this.canyonSegNoise  = new NoiseGeneratorSimplex(new Random(seed + 4884L));
        this.canyonDirNoise  = new NoiseGeneratorSimplex(new Random(seed + 2020L));
    }

    @Override
    protected List<MapGenBaseMeta> getWorldGenerators()
    {
        List<MapGenBaseMeta> generators = Lists.newArrayList();
        generators.add(this.caveGenerator);
        return generators;
    }

    @Override
    protected BiomeDecoratorSpace getBiomeGenerator() {
        return new BiomeDecoratorDeimos();
    }

    @Override
    protected Biome[] getBiomesForGeneration() {
        return new Biome[]{ ACBiome.ACSpace };
    }

    private static double clampD(double v, double lo, double hi) { return v < lo ? lo : (v > hi ? hi : v); }

    private static double smoothstep(double a, double b, double x) {
        double t = clampD((x - a) / (b - a), 0.0D, 1.0D);
        return t * t * (3.0D - 2.0D * t);
    }

    private static double sCurve(double t, double k) {
        double b = Math.tanh(0.5D * k);
        if (b == 0.0D) return t;
        return 0.5D + 0.5D * Math.tanh((t - 0.5D) * k) / b;
    }

    private static double smoothTerrace(double x, int levels, double sharpness) {
        if (levels < 1) return x;
        double s = x * levels;
        double base = Math.floor(s);
        double frac = s - base;
        return (base + sCurve(frac, sharpness)) / levels;
    }

    private double coldAt(int wx, int wz) {
        double base = climateNoise.getValue(wx / CLIMATE_SCALE, wz / CLIMATE_SCALE);
        double mottle = mottleNoise.getValue(wx / MOTTLE_SCALE, wz / MOTTLE_SCALE) * MOTTLE_AMT;
        return smoothstep(COLD_START, COLD_FULL, base + mottle);
    }

    private double flatnessAt(int wx, int wz) {
        double f = flatNoise.getValue(wx / FLAT_SCALE, wz / FLAT_SCALE);
        return smoothstep(FLAT_THRESHOLD, FLAT_THRESHOLD + FLAT_BAND, f);
    }

    private double regionSharp(int wx, int wz) {
        double steep = steepNoise.getValue(wx / STEEP_SCALE, wz / STEEP_SCALE);
        return SHARP_MIN + (steep * 0.5D + 0.5D) * (SHARP_MAX - SHARP_MIN);
    }

    private double canyonCarve(int wx, int wz) {
        double ang = canyonDirNoise.getValue(wx / CANYON_DIR_SCALE, wz / CANYON_DIR_SCALE) * CANYON_ANGLE_RANGE;
        double cos = Math.cos(ang), sin = Math.sin(ang);
        double wob = canyonWarpNoise.getValue(wx / CANYON_WARP_SCALE, wz / CANYON_WARP_SCALE) * CANYON_WARP;
        double px = wx + wob, pz = wz + wob;
        double rx = (px * cos - pz * sin) / (CANYON_SCALE * CANYON_STRETCH);
        double rz = (px * sin + pz * cos) / CANYON_SCALE;
        double ridge = Math.abs(canyonNoise.getValue(rx, rz));
        double inside = smoothstep(CANYON_WIDTH, CANYON_WIDTH - CANYON_WALL, ridge);
        if (inside <= 0.0D) return 0.0D;
        double seg = canyonSegNoise.getValue(wx / CANYON_SEG_SCALE, wz / CANYON_SEG_SCALE);
        double segMask = smoothstep(CANYON_SEG_THRESHOLD, CANYON_SEG_THRESHOLD + CANYON_SEG_BAND, seg);
        return CANYON_DEPTH * inside * segMask;
    }

    private double regolithBlanket(int wx, int wz) {
        double dune = duneNoise.getValue(wx / DUNE_SCALE, wz / DUNE_SCALE) * DUNE_AMP
                    + duneNoise.getValue(wx / (DUNE_SCALE * 0.42D), wz / (DUNE_SCALE * 0.42D)) * (DUNE_AMP * 0.4D);
        double cr = craterNoise.getValue(wx / CRATER_SCALE, wz / CRATER_SCALE);
        double bowl = -CRATER_DEPTH * smoothstep(CRATER_START, CRATER_FULL, cr);
        return dune + bowl;
    }

    private double heightField(int wx, int wz) {
        double flat = flatnessAt(wx, wz);

        double wxo = warpNoise.getValue(wx / WARP_SCALE, wz / WARP_SCALE) * WARP_STRENGTH;
        double wzo = warpNoise.getValue((wx + 1000) / WARP_SCALE, (wz - 1000) / WARP_SCALE) * WARP_STRENGTH;
        double n = terrainNoise.getValue((wx + wxo) / REGION_SCALE, (wz + wzo) / REGION_SCALE);
        n = clampD(n * REGION_GAIN, -1.0D, 1.0D);

        double base = smoothTerrace(n, ELEVATION_LEVELS, regionSharp(wx, wz)) * PLATEAU_AMPLITUDE;
        base *= (1.0D - FLAT_DAMP * flat);

        double canyon = canyonCarve(wx, wz);
        double reg = regolithBlanket(wx, wz) * (1.0D - 0.7D * flat);
        double d = detailNoise.getValue(wx / DETAIL_SCALE, wz / DETAIL_SCALE)
                   * DETAIL_AMPLITUDE * (1.0D - 0.85D * flat);

        return base - canyon + reg + d;
    }

    @Override
    public void onChunkProvider(int cX, int cZ, ChunkPrimer primer) {

        final IBlockState regolith = GSBlocks.DEIMOS_BLOCKS.getStateFromMeta(0);
        final IBlockState stone    = GSBlocks.DEIMOS_BLOCKS.getStateFromMeta(1);
        final IBlockState dryIce   = GSBlocks.SURFACE_ICE.getStateFromMeta(DRY_ICE_META);
        final IBlockState air      = Blocks.AIR.getDefaultState();

        int minTop = Integer.MAX_VALUE, maxTop = Integer.MIN_VALUE;
        boolean sawTerrain = false;

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int wx = (cX << 4) + dx;
                int wz = (cZ << 4) + dz;

                int y0 = -1;
                for (int y = 245; y > 1; y--) {
                    if (primer.getBlockState(dx, y, dz).getBlock() != Blocks.AIR) { y0 = y; break; }
                }
                if (y0 < 1) continue;
                sawTerrain = true;

                int yt = (int) Math.round(y0 + heightField(wx, wz));
                yt = (int) clampD(yt, MIN_SURFACE, MAX_SURFACE);

                if (yt > y0) {
                    for (int y = y0 + 1; y <= yt; y++) primer.setBlockState(dx, y, dz, stone);
                } else if (yt < y0) {
                    for (int y = y0; y > yt; y--) primer.setBlockState(dx, y, dz, air);
                }
                primer.setBlockState(dx, yt, dz, regolith);

                if (yt < minTop) minTop = yt;
                if (yt > maxTop) maxTop = yt;

                double cold = coldAt(wx, wz);
                if (cold > 0.02D && this.rand.nextFloat() < (float) cold) {
                    primer.setBlockState(dx, yt, dz, dryIce);
                }
            }
        }

        if (DEBUG && dbgCount < 12) {
            dbgCount++;
            System.out.println("[Deimos gen] chunk (" + cX + "," + cZ + ") terrain=" + sawTerrain
                + " surfaceY " + (sawTerrain ? (minTop + ".." + maxTop) : "none")
                + " spread " + (sawTerrain ? (maxTop - minTop) : 0));
        }
    }

    @Override
    public void onPopulate(int cX, int cZ) {
    }

    @Override
    public int getCraterProbability() { return 1100; }

    @Override
    public double getHeightModifier() { return 10; }

    @Override
    public double getMountainHeightModifier() { return 4; }

    @Override
    public double getSmallFeatureHeightModifier() { return 4; }

    @Override
    public double getValleyHeightModifier() { return 2; }

    @Override
    public int getWaterLevel() { return 70; }

    @Override
    protected IBlockState getGrassBlock() { return GSBlocks.DEIMOS_BLOCKS.getStateFromMeta(0); }

    @Override
    protected IBlockState getDirtBlock() { return GSBlocks.DEIMOS_BLOCKS.getStateFromMeta(1); }

    @Override
    protected IBlockState getStoneBlock() { return GSBlocks.DEIMOS_BLOCKS.getStateFromMeta(1); }

    @Override
    protected boolean enableBiomeGenBaseBlock() { return false; }

    @Override
    public boolean canGenerateWaterBlock() { return false; }

    @Override
    public boolean canGenerateIceBlock() { return true; }

    @Override
    protected IBlockState getWaterBlock() { return null; }

    @Override
    protected GenType getGenType() { return GenType.GC; }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {
    }

    @Override
    public void getCraterAdditions(ChunkPrimer primer, int x, int y, int z)
    {
        if (this.rand.nextInt(8) == 0 && primer.getBlockState(x, y - 1, z).getBlock() != Blocks.AIR)
        {
            primer.setBlockState(x, y - 1, z, GSBlocks.SURFACE_ICE.getStateFromMeta(DRY_ICE_META));
        }
    }
}
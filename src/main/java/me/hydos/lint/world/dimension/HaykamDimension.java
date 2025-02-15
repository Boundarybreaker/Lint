package me.hydos.lint.world.dimension;

import me.hydos.lint.core.Dimensions;
import me.hydos.lint.world.dimension.biomes.HaykamBiomeSource;
import me.hydos.lint.world.dimension.chunk.HaykamChunkGen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public class HaykamDimension extends Dimension {

    private static final Vec3d FOG_COLOR = new Vec3d(87f / 255f, 0.9999, 194f / 255f);

    public HaykamDimension(World world, DimensionType type) {
        super(world, type, 0.5f);
    }

    @Override
    public ChunkGenerator<?> createChunkGenerator() {
        OverworldChunkGeneratorConfig generatorConfig = new OverworldChunkGeneratorConfig();
        return new HaykamChunkGen(world, new HaykamBiomeSource(this.world.getSeed()), generatorConfig);
    }

    public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean checkMobSpawnValidity) {
        for (int i = chunkPos.getStartX(); i <= chunkPos.getEndX(); ++i) {
            for (int j = chunkPos.getStartZ(); j <= chunkPos.getEndZ(); ++j) {
                BlockPos blockPos = this.getTopSpawningBlockPosition(i, j, checkMobSpawnValidity);
                if (blockPos != null) {
                    return blockPos;
                }
            }
        }

        return null;
    }

    public BlockPos getTopSpawningBlockPosition(int x, int z, boolean checkMobSpawnValidity) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, 0, z);
        Biome biome = this.world.getBiome(mutable);
        BlockState blockState = biome.getSurfaceConfig().getTopMaterial();
        if (checkMobSpawnValidity && !blockState.getBlock().matches(BlockTags.VALID_SPAWN)) {
            return null;
        } else {
            WorldChunk worldChunk = this.world.getChunk(x >> 4, z >> 4);
            int i = worldChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, x & 15, z & 15);
            if (i < 0) {
                return null;
            } else if (worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x & 15, z & 15) > worldChunk.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, x & 15, z & 15)) {
                return null;
            } else {
                for (int j = i + 1; j >= 0; --j) {
                    mutable.set(x, j, z);
                    BlockState blockState2 = this.world.getBlockState(mutable);
                    if (!blockState2.getFluidState().isEmpty()) {
                        break;
                    }

                    if (blockState2.equals(blockState)) {
                        return mutable.up().toImmutable();
                    }
                }

                return null;
            }
        }
    }

    @Override
    public float getSkyAngle(long worldTime, float tickDelta) {
        // Returns a sky angle ranging between 0 and 1.
        // This is a very simple implementation that approximates the overworld sky angle, but is easier to understand.
        // In the overworld, the sky does not quite move at a constant rate, see the OverworldDimension code for details.
        final int dayLength = 24000;
        double daysPassed = ((double) worldTime + tickDelta) / dayLength;
        return (float) MathHelper.fractionalPart(daysPassed - 0.25);
    }

    @Override
    public boolean hasVisibleSky() {
        return true;
    }

    // Fog color RGB
    @Environment(EnvType.CLIENT)
    @Override
    public Vec3d getFogColor(float skyAngle, float tickDelta) {
        return FOG_COLOR;
    }

    @Override
    public boolean canPlayersSleep() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean isFogThick(int x, int z) {
        return true;
    }

    @Override
    public DimensionType getType() {
        return Dimensions.HAYKAM;
    }
}
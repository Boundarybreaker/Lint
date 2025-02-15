package me.hydos.lint.world.dimension.features;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class PortalFeature extends Feature<DefaultFeatureConfig> {
	public PortalFeature() {
		super(DefaultFeatureConfig::deserialize);
	}

	@Override
	public boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGen, Random rand, BlockPos pos, DefaultFeatureConfig config) {
		pos = pos.up();

		while (world.isAir(pos) && pos.getY() > 2) {
			pos = pos.down();
		}

		makeBase(world, pos);
		makePillar(world, pos.add(1, 0, 1));
		makePillar(world, pos.add(3, 0, 1));
		makePillar(world, pos.add(1, 0, 3));
		makePillar(world, pos.add(3, 0, 3));
		makeRoof(world, pos);
		this.setBlockState(world, pos.add(2, 1, 2), me.hydos.lint.core.Blocks.RETURN_HOME.getDefaultState());
		return true;
	}

	private void makeBase(IWorld world, BlockPos pos) {
		final int startX = pos.getX();
		final int startZ = pos.getZ();
		final int startY = pos.getY();

		BlockPos.Mutable aPos = new BlockPos.Mutable();
		aPos.setY(startY);

		for (int xo = 0; xo < 5; ++xo) {
			aPos.setX(startX + xo);

			for (int zo = 0; zo < 5; ++zo) {
				aPos.setZ(startZ + zo);

				this.setBlockState(world, aPos, BASE);
			}
		}
	}

	private void makeRoof(IWorld world, BlockPos iPos) {
		BlockPos.Mutable aPos = new BlockPos.Mutable();
		final int startX = iPos.getX();
		final int startZ = iPos.getZ();

		aPos.setY(iPos.getY() + 4);

		for (int xo = 1; xo < 4; ++xo) {
			aPos.setX(startX + xo);

			for (int zo = 1; zo < 4; ++zo) {
				aPos.setZ(startZ + zo);

				this.setBlockState(world, aPos, SLAB_LOWER);
			}
		}

		aPos.setY(iPos.getY() + 3);

		for (int i = 1; i < 4; ++i) {
			aPos.setX(startX + 4);
			aPos.setZ(startZ + i);
			this.setBlockState(world, aPos, SLAB_UPPER);
			aPos.setX(startX);
			this.setBlockState(world, aPos, SLAB_UPPER);
			aPos.setX(startX + i);
			aPos.setZ(startZ + 4);
			this.setBlockState(world, aPos, SLAB_UPPER);
			aPos.setZ(startZ);
			this.setBlockState(world, aPos, SLAB_UPPER);
		}


		final int y = iPos.getY() + 3;

		this.setBlockState(world, new BlockPos(startX, y, startZ), SLAB_LOWER);
		this.setBlockState(world, new BlockPos(startX + 4, y, startZ), SLAB_LOWER);
		this.setBlockState(world, new BlockPos(startX + 4, y, startZ + 4), SLAB_LOWER);
		this.setBlockState(world, new BlockPos(startX, y, startZ + 4), SLAB_LOWER);
	}

	private void makePillar(IWorld world, BlockPos pos) {
		for (int i = 1; i < 4; ++i) {
			this.setBlockState(world, pos.up(i), PILLAR);
		}
	}

	private static final BlockState BASE = Blocks.CHISELED_QUARTZ_BLOCK.getDefaultState();
	private static final BlockState PILLAR = Blocks.QUARTZ_PILLAR.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.Y);
	private static final BlockState SLAB_LOWER = Blocks.QUARTZ_SLAB.getDefaultState();
	private static final BlockState SLAB_UPPER = Blocks.QUARTZ_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.TOP);
}

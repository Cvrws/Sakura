package cc.unknown.event.impl;

import cc.unknown.event.CancellableEvent;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockAABBEvent extends CancellableEvent {
	private final World world;
	private final Block block;
	private final BlockPos blockPos;
	private AxisAlignedBB boundingBox;
	private final AxisAlignedBB maskBoundingBox;

	public BlockAABBEvent(World world, Block block, BlockPos blockPos, AxisAlignedBB boundingBox, AxisAlignedBB maskBoundingBox) {
		this.world = world;
		this.block = block;
		this.blockPos = blockPos;
		this.boundingBox = boundingBox;
		this.maskBoundingBox = maskBoundingBox;
	}

	public AxisAlignedBB getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(AxisAlignedBB boundingBox) {
		this.boundingBox = boundingBox;
	}

	public World getWorld() {
		return world;
	}

	public Block getBlock() {
		return block;
	}

	public BlockPos getBlockPos() {
		return blockPos;
	}

	public AxisAlignedBB getMaskBoundingBox() {
		return maskBoundingBox;
	}
}

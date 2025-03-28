package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;

import cc.unknown.mixin.impl.IBlockPos;
import cc.unknown.util.structure.Vector3d;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;

@Mixin(BlockPos.class)
public class MixinBlockPos extends Vec3i implements IBlockPos {

    public MixinBlockPos(int x, int y, int z) {
        super(x, y, z);
    }

	@Override
	public boolean equalsVector(Vector3d vec) {
        return ((Math.floor(vec.getX()) == getX() && Math.floor(vec.getY()) == getY() && Math.floor(vec.getZ()) == getZ()));
	}
}

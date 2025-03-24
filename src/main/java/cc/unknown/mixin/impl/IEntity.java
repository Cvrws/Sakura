package cc.unknown.mixin.impl;

import cc.unknown.util.structure.Vector3d;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public interface IEntity {
	Vector3d getCustomPositionVector();
	Vec3 getLookCustom(float yaw, float pitch);
	MovingObjectPosition rayTraceCustom(double blockReachDistance, float yaw, float pitch);
}

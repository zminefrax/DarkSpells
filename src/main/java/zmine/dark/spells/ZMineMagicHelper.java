package zmine.dark.spells;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import zmine.dark.spells.data.DarkPointData;

import java.util.List;

public class ZMineMagicHelper {

    // maxAngleDegrees — насколько точно нужно навестись (например 3 градуса)
    public static Vec3 getAimedPoint(ServerPlayer player, double maxAngleDegrees) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        List<DarkPointData> points = player.getData(ZMineAttachments.MAGIC_DATA).getPoints();
        if (points == null || points.isEmpty()) return null;

        ResourceLocation currentDim = player.level().dimension().location();

        Vec3 best = null;
        double bestAngle = Math.toRadians(maxAngleDegrees);

        for (DarkPointData dp : points) {
            // Пропускаем точки из других измерений
            if (!dp.dimensionId().equals(currentDim)) continue;

            Vec3 pos = dp.position();
            Vec3 toMarker = pos.subtract(eyePos);
            double dist = toMarker.length();
            if (dist < 0.1) continue;

            Vec3 dirToMarker = toMarker.scale(1.0 / dist);
            double dot = lookVec.dot(dirToMarker);

            if (dot > 1.0) dot = 1.0;
            if (dot < -1.0) dot = -1.0;

            double angle = Math.acos(dot);

            if (angle < bestAngle) {
                bestAngle = angle;
                best = pos;
            }
        }

        return best;
    }

}

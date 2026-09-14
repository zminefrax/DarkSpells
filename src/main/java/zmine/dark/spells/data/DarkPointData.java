package zmine.dark.spells.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record DarkPointData(Vec3 position, ResourceLocation dimensionId) {

    // Удобно, если нужно сравнивать точки
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DarkPointData(Vec3 position1, ResourceLocation id))) return false;
        return position.equals(position1) && dimensionId.equals(id);
    }
}

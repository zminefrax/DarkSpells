package zmine.dark.spells;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;
import zmine.dark.spells.data.DarkPointData;

import java.util.ArrayList;
import java.util.List;

public class ZMineMagicData implements INBTSerializable<CompoundTag> {

    private List<DarkPointData> points = new ArrayList<>();
    public List<DarkPointData> getPoints() { return points; }
    public void setPoints(List<DarkPointData> points) { this.points = points; }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        ListTag list = new ListTag();

        for (DarkPointData p : points) {
            CompoundTag pointTag = new CompoundTag();
            pointTag.putDouble("x", p.position().x);
            pointTag.putDouble("y", p.position().y);
            pointTag.putDouble("z", p.position().z);
            pointTag.putString("dim", p.dimensionId().toString());
            list.add(pointTag);
        }

        nbt.put("DarkPoints", list);
        return nbt;
    }


    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        points = new ArrayList<>();
        ListTag list = nbt.getList("DarkPoints", Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            double x = entry.getDouble("x");
            double y = entry.getDouble("y");
            double z = entry.getDouble("z");

            String dimStr = entry.getString("dim");
            if (dimStr.isEmpty()) {
                dimStr = "minecraft:overworld";
            }
            ResourceLocation dim = ResourceLocation.parse(dimStr);

            points.add(new DarkPointData(new Vec3(x, y, z), dim));
        }
    }
}

package zmine.dark.spells;

import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class CastData implements ICastDataSerializable {
    public CastData() {}

    @Override
    public void writeToBuffer(FriendlyByteBuf buf) {}

    @Override
    public void readFromBuffer(FriendlyByteBuf buf) {}

    public void write(CompoundTag tag) {}

    public void read(CompoundTag tag) {}

    // ЭТОГО НЕ ХВАТАЛО:
    @Override
    public void reset() {
        // Если у тебя нет полей — просто ничего не делай
        // Если есть поля (например, ticksLeft, active и т.п.) — сбрось их в дефолтные значения
    }


    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return null;
    }


    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {

    }
}


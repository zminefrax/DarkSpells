package zmine.dark.spells;

import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.UUID;

public class LifeDrainCastData implements ICastDataSerializable { // ТОЛЬКО этот интерфейс
    private UUID targetUuid;
    private boolean active = true;

    public UUID getTargetUuid() {
        return targetUuid;
    }

    public void setTargetUuid(UUID targetUuid) {
        this.targetUuid = targetUuid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public void writeToNBT(CompoundTag tag) {
        if (targetUuid != null) tag.putUUID("target", targetUuid);
        tag.putBoolean("active", active);
    }


    public void readFromNBT(CompoundTag tag) {
        if (tag.hasUUID("target")) targetUuid = tag.getUUID("target");
        active = tag.getBoolean("active");
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buf) {
        // Сначала пишем флаг наличия цели
        boolean hasTarget = (targetUuid != null);
        buf.writeBoolean(hasTarget);

        if (hasTarget) {
            buf.writeUUID(targetUuid); // Пишем только если true
        }
    }

    @Override
    public void readFromBuffer(FriendlyByteBuf buf) {
        boolean hasTarget = buf.readBoolean(); // Читаем флаг первым делом

        if (hasTarget) {
            this.targetUuid = buf.readUUID(); // Читаем только если true
        } else {
            this.targetUuid = null;
        }
        // Дальше читаем остальные поля...
    }

    // Если IDE настаивает на этих методах из INBTSerializable
    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        writeToNBT(tag); // Делегируем логику в наш основной метод
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        readFromNBT(tag); // Делегируем логику в наш основной метод
    }

    @Override
    public void reset() {
        targetUuid = null;
        active = false;
    }

}


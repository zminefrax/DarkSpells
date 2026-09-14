package zmine.dark.spells;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import zmine.dark.spells.data.DarkPointData;

import java.util.List;

import static zmine.dark.spells.DarkSpells.MODID;

public record SyncDarkPointsPayload(List<DarkPointData> points) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncDarkPointsPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "sync_dark_points"));

    // StreamCodec для одной точки
    private static final StreamCodec<RegistryFriendlyByteBuf, DarkPointData> POINT_CODEC = new StreamCodec<>() {
        @Override
        public DarkPointData decode(RegistryFriendlyByteBuf buf) {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            ResourceLocation dim = ResourceLocation.parse(buf.readUtf());
            return new DarkPointData(new Vec3(x, y, z), dim);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, DarkPointData dp) {
            buf.writeDouble(dp.position().x);
            buf.writeDouble(dp.position().y);
            buf.writeDouble(dp.position().z);
            buf.writeUtf(dp.dimensionId().toString());
        }
    };

    // StreamCodec для всего списка
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncDarkPointsPayload> STREAM_CODEC =
            StreamCodec.composite(
                    POINT_CODEC.apply(ByteBufCodecs.list()),
                    SyncDarkPointsPayload::points,
                    SyncDarkPointsPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

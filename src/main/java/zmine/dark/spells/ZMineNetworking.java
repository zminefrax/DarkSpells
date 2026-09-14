package zmine.dark.spells;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static zmine.dark.spells.DarkSpells.MODID;


@EventBusSubscriber(modid = MODID)
public class ZMineNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToClient(
                        SyncDarkPointsPayload.TYPE,
                        SyncDarkPointsPayload.STREAM_CODEC,
                        ZMineNetworking::handleOnClient
                );
    }

    private static void handleOnClient(SyncDarkPointsPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().getData(ZMineAttachments.MAGIC_DATA).setPoints(payload.points());
        });
    }
}

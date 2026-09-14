package zmine.dark.spells;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

import static zmine.dark.spells.DarkSpells.GrayStatus;
import static zmine.dark.spells.DarkSpells.MODID;

@Mod(value = MODID, dist = Dist.CLIENT)
public class GrayClientInit {
    static boolean Status = false;
    public GrayClientInit(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.register(GrayClientInit.class);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        GameRenderer renderer = mc.gameRenderer;
        if (GrayStatus) {

            if (GrayStatus != Status) {
                Status = true;
                ResourceLocation ShaderLoc = ResourceLocation.fromNamespaceAndPath(MODID, "shaders/post/grayscale.json");
                renderer.loadEffect(
                        ShaderLoc
                );
            }
        } else {
            if (GrayStatus != Status) {
                Status = false;
                renderer.shutdownEffect();
            }
        }
    }
}

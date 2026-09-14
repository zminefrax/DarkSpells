package zmine.dark.spells;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import zmine.dark.spells.data.DarkPointData;

import java.util.List;

import static zmine.dark.spells.DarkSpells.MODID;


@EventBusSubscriber(value = Dist.CLIENT, modid = MODID)
public class ClientPart {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null) return;



        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        Camera camera = minecraft.gameRenderer.getMainCamera();
        org.joml.Vector3f lookDir = camera.getLookVector();
        org.joml.Vector3f upDir = camera.getUpVector();
        Vec3 camPos = camera.getPosition();

        // Вектор "вправо" = lookDir × upDir
        Vec3 rightDir = new Vec3(
                lookDir.y() * upDir.z() - lookDir.z() * upDir.y(),
                lookDir.z() * upDir.x() - lookDir.x() * upDir.z(),
                lookDir.x() * upDir.y() - lookDir.y() * upDir.x()
        );

        // --- ИСПРАВЛЕНИЕ 1: partialTick берём из event ---
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);

        // --- ИСПРАВЛЕНИЕ 2: FOV берём из настроек игры, а не из приватного getFov ---
        double fov = minecraft.options.fov().get();
        double halfFovRad = Math.toRadians(fov / 2.0);
        double focalLength = (screenHeight / 2.0) / Math.tan(halfFovRad);

        ResourceLocation currentDim = player.level().dimension().location();
        List<DarkPointData> points = player.getData(ZMineAttachments.MAGIC_DATA).getPoints();
        if (points == null || points.isEmpty()) return;

        for (DarkPointData dp : points) {
            // Пропускаем точки из других измерений
            if (!dp.dimensionId().equals(currentDim)) {
                continue;
            }
            Vec3 pos = dp.position();

            double relX = pos.x - camPos.x;
            double relY = pos.y - camPos.y;
            double relZ = pos.z - camPos.z;

            double forward = relX * lookDir.x() + relY * lookDir.y() + relZ * lookDir.z();
            if (forward <= 0.5) continue;

            double screenRight = relX * rightDir.x() + relY * rightDir.y() + relZ * rightDir.z();
            double screenUp = relX * upDir.x() + relY * upDir.y() + relZ * upDir.z();

            int pixelX = (int) (screenWidth / 2.0 + screenRight * focalLength / forward);
            int pixelY = (int) (screenHeight / 2.0 - screenUp * focalLength / forward);

            if (pixelX < -50 || pixelX > screenWidth + 50) continue;
            if (pixelY < -50 || pixelY > screenHeight + 50) continue;

            // Центр экрана = прицел
            int crosshairX = screenWidth / 2;
            int crosshairY = screenHeight / 2;

            // Порог в пикселях (радиус "наведения")
            int aimThreshold = 5;
            // Проверяем, наведён ли прицел на этот маркер
            double distToCrosshair = Math.sqrt(
                    Math.pow(pixelX - crosshairX, 2) + Math.pow(pixelY - crosshairY, 2)
            );

            int color = (distToCrosshair <= aimThreshold) ? 1 : 0;

            int size = 16;

            if (color==1) {
                guiGraphics.blit(
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/markers/active_marker.png"),                         // текстура
                        pixelX - size / 2,           // x
                        pixelY - size / 2,           // y
                        0,                                  // u (смещение по горизонтали в текстуре)
                        0,                                  // v (смещение по вертикали)
                        size,                               // ширина
                        size,                               // высота
                        size,                           // ширина текстуры (если она больше нужного куска)
                        size                            // высота текстуры
                );
            } else {
                guiGraphics.blit(
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/markers/de_active_marker.png"),                         // текстура
                        pixelX - size / 2,           // x
                        pixelY - size / 2,           // y
                        0,                                  // u (смещение по горизонтали в текстуре)
                        0,                                  // v (смещение по вертикали)
                        size,                               // ширина
                        size,                               // высота
                        size,                           // ширина текстуры (если она больше нужного куска)
                        size                            // высота текстуры
                );
            }
        }
    }

}

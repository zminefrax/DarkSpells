package zmine.dark.spells;

import dev.chocoboy.cascade.VfxEffect;
import dev.chocoboy.cascade.engine.effect.BlendMode;
import dev.chocoboy.cascade.engine.effect.SpriteId;
import dev.chocoboy.cascade.engine.emitter.ShapeSpec;
import dev.chocoboy.cascade.engine.math.Vec3f;
import dev.chocoboy.cascade.engine.tween.Easings;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static zmine.dark.spells.DarkSpells.MODID;

import dev.chocoboy.cascade.Vfx;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.config.SpellConfigManager;
import io.redspace.ironsspellbooks.api.config.SpellConfigParameter;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.MagicHelper;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;

import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.capabilities.magic.SummonedEntitiesCastData;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.network.casting.OnCastFinishedPacket;
import io.redspace.ironsspellbooks.network.casting.OnClientCastPacket;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.mojang.text2speech.Narrator.LOGGER;
import static zmine.dark.spells.DarkSpells.MODID;

public class DarkStrike extends AbstractSpell {

    ResourceLocation dark_strike = ResourceLocation.fromNamespaceAndPath(MODID, "dark_strike");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE) // или другая школа
            .setMaxLevel(1)
            .setCooldownSeconds(120)
            .build();

    public DarkStrike() {
        this.baseManaCost = 0;
        this.manaCostPerLevel = 0;
        this.baseSpellPower = 0;
        this.spellPowerPerLevel = 0;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return dark_strike;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public int getCastTime(int spellLevel) {
        // Пример: фиксированные 2 тика независимо от уровня
        return 2;
    }

    @Override
    public boolean isLearned(@Nullable Player player) {
        return true;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public int getManaCost(int level) {
        int Level = level;
        int cost;
        if (Level == 0) {
            cost = 500;
        } else {
            cost = 500/Level;
        }

        return cost;
    }

    @Override
    public int getSpellCooldown() {
        return 200*20;
    }

    @Override
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return 0;
    }

    @Override
    public ICastDataSerializable getEmptyCastData() {
        return new CastData();
    }


    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.dark_spells.teleport")
        );
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {

        if (entity instanceof Player player && level instanceof ServerLevel serverLevel) {
            // На сервере, например при нажатии ПКМ или в логике заклинания:
            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                Vec3 aimed = ZMineMagicHelper.getAimedPoint(serverPlayer, 3.0); // 3 градуса

                if (aimed != null) {
                    //Vfx.burst(serverLevel, aimed);
                    Vfx.emitter()
                            .blend(BlendMode.ALPHA)
                            .shape(ShapeSpec.sphere(10f))
                            .shard()
                            .count(10)
                            .speed(0)
                            .size(10f, 10f, Easings.EASE_OUT_QUAD)
                            .gravity(0,0,0)
                            .gradient(Easings.LINEAR, 0xFF000000,0xFF000000,0xFF000000,0xFFFFFFFF)
                            .trail(5)
                            .play(serverLevel, aimed);
                    clearSphericalRadius(serverLevel,aimed,20);
                    //Vfx.dome(serverLevel,aimed,3,0x000000FF,3*20);
                }
            }
        }



        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    public static void clearSphericalRadius(ServerLevel level, Vec3 center, int radius) {
        double rSq = radius * radius;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distSq = x * x + y * y + z * z;
                    if (distSq > rSq) continue;

                    BlockPos pos = new  BlockPos(
                            (int) Math.floor(center.x())+x,
                            (int) Math.floor(center.y())+y,
                            (int) Math.floor(center.z())+z
                    );
                    // Правильная проверка границ в 1.21.1
                    if (!level.isInWorldBounds(pos)) continue;
                    if (level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                    }
                }
            }
        }
    }

}
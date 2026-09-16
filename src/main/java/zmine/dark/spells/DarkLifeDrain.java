package zmine.dark.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.RaycastBuilder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zmine.dark.spells.data.DarkPointData;


import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.mojang.text2speech.Narrator.LOGGER;
import static io.redspace.ironslib.attribute.AttributeRemapRegistry.findTarget;
import static zmine.dark.spells.DarkSpells.*;


public class DarkLifeDrain extends AbstractSpell {
    public static final String SPELL_NAME = "dark_life_drain";
    ResourceLocation Dark_Life_Drain = ResourceLocation.fromNamespaceAndPath(MODID, SPELL_NAME);

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolResource) // или другая школа
            .setMaxLevel(10)
            .setCooldownSeconds(120)
            .build();

    public DarkLifeDrain() {
        this.baseManaCost = 0;
        this.manaCostPerLevel = 0;
        this.baseSpellPower = 0;
        this.spellPowerPerLevel = 0;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return Dark_Life_Drain;
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
        int cost;
        cost = 250;
        return cost;
    }

    @Override
    public int getSpellCooldown() {
        return 120;
    }

    @Override
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return 2;
    }

    @Override
    public ICastDataSerializable getEmptyCastData() {
        return new CastData();
    }


    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.dark_spells.dark_life_drain")
        );
    }



    public int getRecastDuration(int spellLevel, LivingEntity caster) {
        if (spellLevel <= 0) {
            spellLevel = 1;
        }

        return 20 * 20 * spellLevel;
    }
    @Override
    public void onRecastFinished(ServerPlayer serverPlayer,
                                 RecastInstance recastInstance,
                                 RecastResult recastResult,
                                 ICastDataSerializable castDataSerializable) {
        // Вызывается когда рекаст закончился (по таймеру, по использованию,
        // или при ручной отмене через removeRecast)
        if (castDataSerializable instanceof LifeDrainCastData data) {
            data.setActive(false); // гарантированно выключаем
        }
        super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        PlayerRecasts recasts = playerMagicData.getPlayerRecasts();
        if (entity instanceof Player player && level instanceof ServerLevel serverLevel) {
            if (!recasts.hasRecastForSpell(this)) {
                // === ПЕРВЫЙ КАСТ: ищем цель, на которую смотрит игрок ===

                // Рейкаст: бросаем луч от глаз игрока, длина 16 блоков
                // Utils.raycastForEntity ищет сущность на луче
                HitResult hitResult = Utils.raycastForEntity(level, entity, 500, false);
                Vec3 targetVec = hitResult.getLocation();
                AABB box = AABB.ofSize(targetVec, 6.0, 6.0, 6.0); // 6 = диаметр (радиус 3 блока)
                List<Entity> entities = serverLevel.getEntitiesOfClass(Entity.class, box);
                if (!entities.isEmpty()) {
                    Entity target = entities.get(0);


                    if (target == null) {
                        // Нет цели — не запускаем заклинание
                        return;
                    }

                    // Сохраняем UUID цели
                    LifeDrainCastData castData = new LifeDrainCastData();
                    castData.setTargetUuid(target.getUUID());
                    // Длительность рекаста
                    int durationTicks = getRecastDuration(spellLevel, entity);

                    RecastInstance recastInstance = new RecastInstance(
                            this.getSpellId(),
                            spellLevel,
                            getRecastCount(spellLevel, entity),
                            durationTicks,
                            castSource,
                            castData
                    );

                    recasts.addRecast(recastInstance, playerMagicData);


                } else {
                    // === ВТОРОЙ КАСТ: ручная отмена ===
                    recasts.getActiveRecasts().forEach(recast -> {
                        if (recast.getSpellId().equals(this.getSpellId())) {
                            if (recast.getCastData() instanceof LifeDrainCastData data) {
                                data.setActive(false);
                            }
                            recasts.removeRecast(recast.getSpellId());
                        }
                    });
                }
            }
        }
    }
}
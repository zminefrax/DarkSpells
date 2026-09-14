package zmine.dark.spells;

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
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.mojang.text2speech.Narrator.LOGGER;
import static zmine.dark.spells.DarkSpells.MODID;

public class ToDarkSideSpell extends AbstractSpell {
    static boolean Status = false;

    ResourceLocation TO_DARK_SIDE = ResourceLocation.fromNamespaceAndPath(MODID, "to_dark_side");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE) // или другая школа
            .setMaxLevel(5)
            .setCooldownSeconds(120)
            .build();

    public ToDarkSideSpell() {
        this.baseManaCost = 0;
        this.manaCostPerLevel = 0;
        this.baseSpellPower = 0;
        this.spellPowerPerLevel = 0;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return TO_DARK_SIDE;
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
            cost = 200;
        } else {
            cost = 200/Level;
        }

        return cost;
    }

    @Override
    public int getSpellCooldown() {
        return 120*20;
    }

    @Override
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return 2;
    }

    @Override
    public ICastDataSerializable getEmptyCastData() {
        return new CastData();
    }


    public int getRecastDuration(int spellLevel, LivingEntity caster) {
        if (spellLevel <= 0) {
            spellLevel = 1;
        }

        return 20 * 60 * spellLevel;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.dark_spells.duration", Utils.timeFromTicks(getRecastDuration(spellLevel, caster), 2))
        );
    }


    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {

        if (entity instanceof Player player && level instanceof ServerLevel serverLevel) {
            CastData tmpCastData = new CastData();
            RecastInstance SpellRecastInstance = new RecastInstance(MODID+":to_dark_side",spellLevel,2,getRecastDuration(spellLevel,entity),castSource,tmpCastData);
            DarkSpells.GrayStatus = true;
            Vec3 pos = entity.getPosition(0).add(0, 1.6, 0); // чуть выше центра игрока
            Vec3 look = entity.getLookAngle();

            // Спавн частицы: (тип, x, y, z, count, speedX, speedY, speedZ, maxSpeed, ...)
            serverLevel.sendParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,  // тип частицы
                    pos.x() + look.x() * 1.5,        // x
                    pos.y() + look.y() * 1.5,        // y
                    pos.z() + look.z() * 1.5,        // z
                    100,                              // количество
                    0.3, 0.25, 0.3,                   // разброс по xyz
                    0.07                             // скорость
            );
            if (entity instanceof ServerPlayer servPlayer) {
                if (playerMagicData.getPlayerRecasts().hasRecastForSpell(getSpellId())) {
                    entity.removeEffect(MobEffectRegistry.TRUE_INVISIBILITY);

                    disableFlight(servPlayer);
                } else {
                    entity.addEffect(new MobEffectInstance(MobEffectRegistry.TRUE_INVISIBILITY, -1, 0, false, false, false));
                    playerMagicData.getPlayerRecasts().addRecast(SpellRecastInstance, playerMagicData);
                    enableFlight(servPlayer);
                }
            }
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }


    @Override
    public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
        if (recastResult != RecastResult.USED_ALL_RECASTS) {

        }

        serverPlayer.removeEffect(MobEffectRegistry.TRUE_INVISIBILITY);
        disableFlight(serverPlayer);
        DarkSpells.GrayStatus = false;
        super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
    }

    public static void enableFlight(ServerPlayer player) {
        player.getAbilities().mayfly = true;   // разрешить полёт
        player.getAbilities().flying = true;   // сразу включить полёт
        player.onUpdateAbilities();            // синхронизировать с клиентом
    }

    public static void disableFlight(ServerPlayer player) {
        player.getAbilities().mayfly = false;
        player.getAbilities().flying = false;
        player.onUpdateAbilities();
    }

}
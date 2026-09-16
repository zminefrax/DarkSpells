package zmine.dark.spells;

import dev.chocoboy.cascade.engine.effect.BlendMode;
import dev.chocoboy.cascade.engine.effect.SpriteId;
import dev.chocoboy.cascade.engine.emitter.ShapeSpec;
import dev.chocoboy.cascade.engine.tween.Easings;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static zmine.dark.spells.DarkSpells.MODID;
import static zmine.dark.spells.DarkSpells.SchoolResource;

import dev.chocoboy.cascade.Vfx;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DarkSwap extends AbstractSpell {

    ResourceLocation dark_swap = ResourceLocation.fromNamespaceAndPath(MODID, "dark_swap");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolResource) // или другая школа
            .setMaxLevel(1)
            .setCooldownSeconds(120)
            .build();

    public DarkSwap() {
        this.baseManaCost = 0;
        this.manaCostPerLevel = 0;
        this.baseSpellPower = 0;
        this.spellPowerPerLevel = 0;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return dark_swap;
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
            cost = 550;
        } else {
            cost = 550/Level;
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
                Component.translatable("ui.dark_spells.dark_swap")
        );
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {

        if (entity instanceof Player player && level instanceof ServerLevel serverLevel) {
            // На сервере, например при нажатии ПКМ или в логике заклинания:
            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                Vec3 aimed = ZMineMagicHelper.getAimedPoint(serverPlayer, 3.0); // 3 градуса

                if (aimed != null) {
                    // Игрок навёлся на маркер
                    AABB box = AABB.ofSize(aimed, 6.0, 6.0, 6.0); // 6 = диаметр (радиус 3 блока)
                    List<Entity> entities = serverLevel.getEntitiesOfClass(Entity.class, box);
                    if (!entities.isEmpty()) {
                        Entity entity2 = entities.get(0);
                        Vfx.emitter()
                                .blend(BlendMode.ALPHA)
                                .shape(ShapeSpec.disc(1f))
                                .sprite(SpriteId.SMOKE)
                                .count(1000)
                                .speed(1 / 4)
                                .orbit()
                                .gravity(0, 0, 0)
                                .gradient(Easings.LINEAR, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000)
                                .trail(5)
                                .play(serverLevel, entity2.position());
                        Vfx.emitter()
                                .blend(BlendMode.ALPHA)
                                .shape(ShapeSpec.disc(1f))
                                .sprite(SpriteId.SMOKE)
                                .count(1000)
                                .speed(1 / 4)
                                .orbit()
                                .gravity(0, 0, 0)
                                .gradient(Easings.LINEAR, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000)
                                .trail(5)
                                .play(serverLevel, serverPlayer.position());
                        entity2.teleportTo(serverPlayer.position().x,serverPlayer.position().y,serverPlayer.position().z);
                        serverPlayer.teleportTo(aimed.x, aimed.y, aimed.z);
                    }
                }
            }
        }



        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}
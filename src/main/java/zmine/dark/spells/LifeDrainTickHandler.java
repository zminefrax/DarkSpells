package zmine.dark.spells;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.UUID;

@EventBusSubscriber
public class LifeDrainTickHandler {

    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        // Применяем эффект раз в 20 тиков (1 секунда)
        tickCounter++;
        if (tickCounter % 20 != 0) return;

        ServerLevel serverLevel = event.getServer().overworld();
        if (serverLevel == null) return;

        // Получаем ID заклинания. Убедитесь, что SPELL_NAME совпадает с тем, что вы регистрируете
        String spellId = DarkLifeDrain.SPELL_NAME;
        AbstractSpell spell = SpellRegistry.getSpell(spellId);

        if (spell == null) return;

        // Перебираем всех игроков
        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            MagicData magicData = MagicData.getPlayerMagicData(player);
            PlayerRecasts recasts = magicData.getPlayerRecasts();

            // ИСПРАВЛЕНИЕ: вместо getRecastForSpell() используем прямой перебор рекастов
            // Это самый надежный способ, который работает во всех версиях API
            recasts.getActiveRecasts().forEach(recast -> {
                if (recast.getSpellId().equals(spellId)) {
                    processLifeDrain(player, recast, serverLevel, spell);
                }
            });
        }
    }

    private static void processLifeDrain(ServerPlayer caster, io.redspace.ironsspellbooks.capabilities.magic.RecastInstance recast, ServerLevel level, AbstractSpell spell) {
        // Безопасное приведение типа
        if (!(recast.getCastData() instanceof LifeDrainCastData data)) return;

        // Проверка флага активности
        if (!data.isActive()) return;

        UUID targetUuid = data.getTargetUuid();
        if (targetUuid == null) return;

        // Ищем цель по UUID
        Entity target = level.getEntity(targetUuid);

        // Если цели нет, она умерла или выгружена — останавливаем заклинание
        if (target == null || !target.isAlive()) {
            data.setActive(false);
            // Удаляем рекаст, чтобы он не висел в памяти
            MagicData.getPlayerMagicData(caster).getPlayerRecasts().removeRecast(recast.getSpellId());
            return;
        }

        // Получаем уровень заклинания из данных рекаста
        int spellLevel = recast.getSpellLevel();

        // Получаем урон. Так как у нас нет прямого доступа к экземпляру заклинания через recast.getSpell(),
        // мы либо храним логику урона отдельно, либо приводим тип (если уверены, что это наше заклинание).
        // Самый безопасный вариант для общего кода — вынести формулу урона в статический метод или интерфейс.
        float damage = calculateDamagePerTick(spellLevel);

        // Наносим урон цели
        target.hurt(target.damageSources().magic(), damage);

        // Хилим кастера
        caster.heal(damage);

        // Частицы (опционально)
        level.sendParticles(
                ParticleTypes.DAMAGE_INDICATOR,
                target.getX(), target.getY() + 1, target.getZ(),
                5, 0.1, 0.1, 0.1, 0.0
        );
    }

    // Выносим расчет урона, чтобы не зависеть от метода getSpell()
    private static float calculateDamagePerTick(int level) {
        return 1.0f + (level * 0.5f);
    }
}
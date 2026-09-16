package zmine.dark.spells;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import static zmine.dark.spells.DarkSpells.MODID;

public class DarkSwordItem {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    // Тир с прочностью 1 и бонусом урона 206
    // Формула урона меча: 1 (база) + getAttackDamageBonus() + 3 (бонус типа меча) = 210
    public static final Tier DARK_TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, // какие блоки НЕ ломает
            1,                                   // прочность (1 использование)
            0f,                                  // скорость копания (мечу не нужна)
            206f,                                // бонус урона тира
            0,                                   // зачаровываемость
            () -> Ingredient.of()      // ремонтный ингредиент
    );

    public static final DeferredHolder<Item, SwordItem> DARK_SWORD = ITEMS.register(
            "dark_sword",
            () -> new SwordItem(
                    DARK_TIER,
                    new Item.Properties()
                            .attributes(SwordItem.createAttributes(
                                    DARK_TIER,
                                    3,      // бонус урона типа «меч»
                                    -2.4f   // модификатор скорости атаки (как у ванильного меча)
                            ))
            )
    );
}
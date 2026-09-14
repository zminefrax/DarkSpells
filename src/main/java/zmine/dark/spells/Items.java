package zmine.dark.spells;

import io.redspace.ironsspellbooks.item.Scroll;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static zmine.dark.spells.DarkSpells.MODID;

public class Items {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    
    private static <T extends Item> DeferredItem<T> reg(String name, Supplier<T> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

}

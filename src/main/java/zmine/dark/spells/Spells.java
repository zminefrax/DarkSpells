package zmine.dark.spells;


import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.item.Scroll;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import static zmine.dark.spells.DarkSpells.MODID;
import java.util.function.Supplier;

public class Spells {
    public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, MODID);

    public static Supplier<AbstractSpell> registerSpell(AbstractSpell spell) {
        return SPELLS.register(spell.getSpellName(), () -> spell);
    }

    public static final Supplier<AbstractSpell> TO_DARK_SIDE = SPELLS.register("to_dark_side", ToDarkSideSpell::new);
    public static final Supplier<SpellData> TO_DARK_SIDE_Data = () -> new SpellData(new ToDarkSideSpell(),1,false);

    public static final Supplier<AbstractSpell> DARK_TELEPORT = SPELLS.register("dark_teleport", DarkTeleport::new);
    public static final Supplier<SpellData> DARK_TELEPORT_Data = () -> new SpellData(new DarkTeleport(),1,false);

    public static final Supplier<AbstractSpell> DARK_POINT = SPELLS.register("dark_point", DarkPoint::new);
    public static final Supplier<SpellData> DARK_POINT_Data = () -> new SpellData(new DarkPoint(),1,false);

    public static final Supplier<AbstractSpell> DARK_POINT_REMOVER = SPELLS.register("dark_point_remover", DarkPointRemover::new);
    public static final Supplier<SpellData> DARK_POINT_REMOVER_Data = () -> new SpellData(new DarkPointRemover(),1,false);

    public static final Supplier<AbstractSpell> DARK_POINT_RELOADER = SPELLS.register("dark_point_reloader", DarkPointReloader::new);
    public static final Supplier<SpellData> DARK_POINT_RELOADER_Data = () -> new SpellData(new DarkPointReloader(),1,false);

    public static final Supplier<AbstractSpell> DARK_STRIKE = SPELLS.register("dark_strike", DarkStrike::new);
    public static final Supplier<SpellData> DARK_STRIKE_Data = () -> new SpellData(new DarkStrike(),1,false);


    public static void register(IEventBus bus) {
        SPELLS.register(bus);
    }
}
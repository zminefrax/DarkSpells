package zmine.dark.spells;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.function.Supplier;

import static zmine.dark.spells.DarkSpells.MODID;

public class ZMineAttachments {

    // DeferredRegister для типов вложений
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    // Регистрируем наш attachment — сериализуемый, с копированием при смерти
    public static final Supplier<AttachmentType<ZMineMagicData>> MAGIC_DATA =
            ATTACHMENT_TYPES.register(
                    "magic_data",
                    () -> AttachmentType.serializable(ZMineMagicData::new)
                            .copyOnDeath()
                            .build()
            );
}

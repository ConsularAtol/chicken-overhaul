package consular.chickenoverhaul.registry;

import net.minecraft.component.ComponentType;

import com.mojang.serialization.Codec;

import consular.chickenoverhaul.ChickenOverhaul;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModComponentTypes {
    public static final ComponentType<Boolean> FERTILIZED_COMPONENT = Registry.register(
		Registries.DATA_COMPONENT_TYPE,
		Identifier.of(ChickenOverhaul.MOD_ID, "fertilized"),
		ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
    );

    public static void initialize() {
		ChickenOverhaul.LOGGER.info("Registering Components");
	}
}
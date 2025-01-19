package consular.chickenoverhaul.registry;

import java.util.function.Function;

import consular.chickenoverhaul.ChickenOverhaul;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item FRIED_EGG = register("fried_egg", Item::new, new Item.Settings());
 
    public static Item register(String path, Function<Item.Settings, Item> factory, Item.Settings settings) {
        final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ChickenOverhaul.MOD_ID, path));
        return Items.register(registryKey, factory, settings);
    }
 
    public static void initialize() {
        ChickenOverhaul.LOGGER.info("Registering Items");
    }
}

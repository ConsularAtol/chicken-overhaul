package consular.chickenoverhaul.registry;

import java.util.function.Function;

import consular.chickenoverhaul.ChickenOverhaul;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final FoodComponent FRIED_EGG_FOOD_COMPONENT = (new FoodComponent.Builder()).nutrition(3).saturationModifier(0.7F).build();
    public static final ConsumableComponent FRIED_EGG_CONSUMABLE_COMPONENT = food().consumeSeconds(0.8F).build();
    public static final FoodComponent SCRAMBLED_EGGS_FOOD_COMPONENT = (new FoodComponent.Builder()).nutrition(6).saturationModifier(0.6F).build();

    public static final Item FRIED_EGG = register("fried_egg", Item::new, new Item.Settings().food(FRIED_EGG_FOOD_COMPONENT, FRIED_EGG_CONSUMABLE_COMPONENT));
    public static final Item SCRAMBLED_EGGS = register("scrambled_eggs", Item::new, new Item.Settings().food(SCRAMBLED_EGGS_FOOD_COMPONENT).useRemainder(Items.BOWL).maxCount(1));
 
    public static Item register(String path, Function<Item.Settings, Item> factory, Item.Settings settings) {
        final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ChickenOverhaul.MOD_ID, path));
        return Items.register(registryKey, factory, settings);
    }
 
    public static void initialize() {
        ChickenOverhaul.LOGGER.info("Registering Items");
    }

    public static ConsumableComponent.Builder food() {
      return ConsumableComponent.builder().consumeSeconds(1.6F).useAction(UseAction.EAT).sound(SoundEvents.ENTITY_GENERIC_EAT).consumeParticles(true);
   }
}

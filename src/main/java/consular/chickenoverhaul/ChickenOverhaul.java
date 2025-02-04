package consular.chickenoverhaul;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import consular.chickenoverhaul.registry.ModBlockEntities;
import consular.chickenoverhaul.registry.ModBlocks;
import consular.chickenoverhaul.registry.ModComponentTypes;
import consular.chickenoverhaul.registry.ModItems;

public class ChickenOverhaul implements ModInitializer {
	public static final String MOD_ID = "chicken_overhaul";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.registerModBlocks();
		ModComponentTypes.initialize();
		ModItems.initialize();
		ModBlockEntities.registerBlockEntities();

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
		.register((itemGroup) -> itemGroup.add(ModItems.FRIED_EGG));
	}
}
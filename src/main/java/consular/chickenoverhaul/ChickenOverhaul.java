package consular.chickenoverhaul;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import consular.chickenoverhaul.registry.ModBlocks;

public class ChickenOverhaul implements ModInitializer {
	public static final String MOD_ID = "chicken_overhaul";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.registerModBlocks();
	}
}
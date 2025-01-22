package consular.chickenoverhaul.registry;

import java.util.function.Function;

import consular.chickenoverhaul.ChickenOverhaul;
import consular.chickenoverhaul.block.ChickenEggBlock;
import consular.chickenoverhaul.block.NestingBoxBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block CHICKEN_EGG = registerBlock("chicken_egg", ChickenEggBlock::new, Block.Settings.create().mapColor(MapColor.BROWN).strength(0.5F).nonOpaque().ticksRandomly().pistonBehavior(PistonBehavior.DESTROY).sounds(BlockSoundGroup.METAL), false);
    public static final Block OAK_NESTING_BOX = registerBlock("oak_nesting_box", NestingBoxBlock::new, Block.Settings.create().mapColor(MapColor.BROWN).strength(1.5F).pistonBehavior(PistonBehavior.BLOCK).nonOpaque().sounds(BlockSoundGroup.WOOD), true);

    // 1.21.2 more like... fuck you >:(
    private static Block registerBlock(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings, Boolean registerItem) {
        final Identifier identifier = Identifier.of(ChickenOverhaul.MOD_ID, path);
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);
        
        final Block block = Blocks.register(registryKey, factory, settings);
        if (registerItem){
            Items.register(block);
        }
        return block;
    }
    
    public static void registerModBlocks(){
        ChickenOverhaul.LOGGER.info("Registering Blocks");
    }
}

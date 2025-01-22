package consular.chickenoverhaul.registry;

import consular.chickenoverhaul.ChickenOverhaul;
import consular.chickenoverhaul.block.entity.NestingBoxBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<NestingBoxBlockEntity> NESTING_BOX;

    public static void registerBlockEntities() {
        NESTING_BOX = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(ChickenOverhaul.MOD_ID, "nesting_box"),
            FabricBlockEntityTypeBuilder.create(NestingBoxBlockEntity::new, ModBlocks.OAK_NESTING_BOX).build(null)
        );
    }
}

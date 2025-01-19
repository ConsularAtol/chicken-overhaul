package consular.chickenoverhaul.block;

import consular.chickenoverhaul.registry.ModComponentTypes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;
import net.minecraft.state.property.Property;

public class ChickenEggBlock extends Block{

    public static final BooleanProperty FERTILIZED;
    public static final IntProperty HATCH;
    private static final VoxelShape SHAPE;

    public ChickenEggBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FERTILIZED, false));
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.shouldHatchProgress(world)) {
            if (state.get(FERTILIZED)){
                int i = (Integer)state.get(HATCH);
                if (i < 2) {
                   world.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                   world.setBlockState(pos, (BlockState)state.with(HATCH, i + 1), 2);
                   world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, Emitter.of(state));
                } else {
                    world.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                    world.removeBlock(pos, false);
                    world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, Emitter.of(state));

                    for(int j = 0; j < 1; ++j) {
                        world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state));
                        ChickenEntity chickenEntity = (ChickenEntity)EntityType.CHICKEN.create(world, SpawnReason.BREEDING);
                        if (chickenEntity != null) {
                            chickenEntity.setBreedingAge(-24000);
                            chickenEntity.refreshPositionAndAngles((double)pos.getX() + 0.3 + (double)j * 0.2, (double)pos.getY(), (double)pos.getZ() + 0.3, 0.0F, 0.0F);
                            world.spawnEntity(chickenEntity);
                        }
                    }
                }
            }else {
                dropStack(world, pos, new ItemStack(Items.EGG));
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
   }

   private boolean shouldHatchProgress(World world) {
        float f = world.getSkyAngle(1.0F);
        if ((double)f < 0.69 && (double)f > 0.65) {
           return true;
        } else {
           return world.random.nextInt(500) == 0;
        }
    }

    protected boolean isTransparent(BlockState state) {
        return true;
    }

    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        ItemStack itemStack = new ItemStack(Items.EGG);
        itemStack.set(ModComponentTypes.FERTILIZED_COMPONENT, state.get(FERTILIZED));
        dropStack(world, pos, itemStack);
        return state;
   }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FERTILIZED});
        builder.add(new Property[]{HATCH});
    }

    static {
        SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 7.0, 10.0);
        FERTILIZED = BooleanProperty.of("fertilized");
        HATCH = Properties.HATCH;
    }

    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, ItemStack stack) {
        boolean fertilized = stack.getOrDefault(ModComponentTypes.FERTILIZED_COMPONENT, false);
        world.setBlockState(pos, state.with(FERTILIZED, fertilized));
    }

    public static void setEggFertilizedState(ItemStack stack, World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        boolean fertilized = state.get(FERTILIZED);
        stack.set(ModComponentTypes.FERTILIZED_COMPONENT, fertilized);
    }
}

package consular.chickenoverhaul.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import consular.chickenoverhaul.block.entity.NestingBoxBlockEntity;
import consular.chickenoverhaul.registry.ModBlockEntities;

public class NestingBoxBlock extends BlockWithEntity {

    private static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    public static final IntProperty EGGS = IntProperty.of("eggs", 0, 4);

    public NestingBoxBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(EGGS, 0));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new NestingBoxBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(EGGS);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.NESTING_BOX, NestingBoxBlockEntity::tick);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof NestingBoxBlockEntity) {
            ((NestingBoxBlockEntity) blockEntity).dropItems(world, pos);
        }
        return super.onBreak(world, pos, state, player);
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (world.isClient()) {
            return 0;
        } else {
            NestingBoxBlockEntity entity = (NestingBoxBlockEntity)world.getBlockEntity(pos);
            if (entity instanceof NestingBoxBlockEntity) {
                int output = 0;
                for (int i = 0; i < ((NestingBoxBlockEntity)entity).getInventory().size(); i++){
                    if (entity.getInventory().get(i).isOf(Items.EGG)){
                        output += 1;
                    }
                }
                return output;
            } else {
                return 0;
            }
        }
    }

    @Override
    protected MapCodec<? extends NestingBoxBlock> getCodec() {
        return createCodec(NestingBoxBlock::new);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof NestingBoxBlockEntity nestingBox) {
                ItemStack heldItem = player.getStackInHand(player.getActiveHand());

                // Check if the player's hand is empty
                if (heldItem.isEmpty()) {
                    for (int i = nestingBox.getInventory().size() - 1; i >= 0; i--) {
                        ItemStack stack = nestingBox.getInventory().get(i);

                        if (!stack.isEmpty()) {
                            // Remove one egg from the slot
                            ItemStack egg = stack.copy();
                            egg.setCount(1);
                            stack.decrement(1);

                            // Clear the slot if empty
                            if (stack.isEmpty()) {
                                nestingBox.getInventory().set(i, ItemStack.EMPTY);
                            }

                            // Give the egg to the player
                            if (!player.getInventory().insertStack(egg)) {
                                player.dropItem(egg, false); // Drop if inventory is full
                            }

                            // Update the block entity
                            nestingBox.markDirty();
                            ((NestingBoxBlockEntity)blockEntity).updateEggCount();
                            world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                            return ActionResult.SUCCESS;
                        }
                    }

                    // No eggs available
                    return ActionResult.FAIL;
                }
            }
        }
        return ActionResult.PASS;
    }
}
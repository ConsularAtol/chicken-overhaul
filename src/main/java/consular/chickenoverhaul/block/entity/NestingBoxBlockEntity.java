package consular.chickenoverhaul.block.entity;

import org.jetbrains.annotations.Nullable;

import consular.chickenoverhaul.block.NestingBoxBlock;
import consular.chickenoverhaul.registry.ModBlockEntities;
import consular.chickenoverhaul.registry.ModComponentTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.item.Items;

public class NestingBoxBlockEntity extends BlockEntity {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private static final int[] BOTTOM_SLOTS = new int[]{4, 3, 2, 1};

    public NestingBoxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NESTING_BOX, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, NestingBoxBlockEntity blockEntity) {
        // Optional: Add logic for automatic egg-laying or fertilization
    }

    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN)
            return BOTTOM_SLOTS;
        return BOTTOM_SLOTS;
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        Inventories.readNbt(nbt, inventory, registries);

        // Read fertilization state for each egg
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                boolean fertilized = stack.getOrDefault(ModComponentTypes.FERTILIZED_COMPONENT, false);
                stack.set(ModComponentTypes.FERTILIZED_COMPONENT, fertilized); // Reapply fertilization state
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        Inventories.writeNbt(nbt, inventory, registries);

        // Write fertilization state for each egg
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                boolean fertilized = stack.getOrDefault(ModComponentTypes.FERTILIZED_COMPONENT, false);
                stack.set(ModComponentTypes.FERTILIZED_COMPONENT, fertilized);
            }
        }
    }

    public void dropItems(World world, BlockPos pos) {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                Block.dropStack(world, pos, stack);
            }
        }
    }

    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    public boolean canInsert(ItemStack stack) {
        if (stack.getItem() != Items.EGG) {
            return false; // Only allow eggs
        }

        int totalEggs = inventory.stream().mapToInt(ItemStack::getCount).sum();
        return totalEggs < 4; // Allow insert only if less than 4 eggs are present
    }

    public boolean insertEgg(ItemStack stack, boolean fertilized) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack slotStack = inventory.get(i);
            if (slotStack.isEmpty()) {
                inventory.set(i, new ItemStack(stack.getItem(), 1));
                updateEggCount();
                return true;
            }
        }
        return false;
    }

    public void updateEggCount() {
        if (world != null && !world.isClient) {
            int eggCount = (int) inventory.stream().filter(stack -> !stack.isEmpty()).count();
            world.setBlockState(pos, getCachedState().with(NestingBoxBlock.EGGS, eggCount));
        }
    }
}

package consular.chickenoverhaul.goal;

import consular.chickenoverhaul.block.entity.NestingBoxBlockEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class LayEggInNestingBoxGoal extends Goal {
    private final ChickenEntity chicken;
    private BlockPos targetNestingBox;
    private final double speed;
    private int eggLayingDelay;

    public LayEggInNestingBoxGoal(ChickenEntity chicken, double speed) {
        this.chicken = chicken;
        this.speed = speed;
        this.eggLayingDelay = 0;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        // Check if the chicken is ready to lay an egg
        if (chicken.eggLayTime > 15 || chicken.isBaby() || !chicken.isAlive()) {
            return false;
        }

        // Find the nearest nesting box
        targetNestingBox = findNearestNestingBox();
        return targetNestingBox != null;
    }

    @Override
    public boolean shouldContinue() {
        return targetNestingBox != null && chicken.getBlockPos().isWithinDistance(targetNestingBox, 1.5);
    }

    @Override
    public void start() {
        if (targetNestingBox != null) {
            chicken.getNavigation().startMovingTo(targetNestingBox.getX() + 0.5, targetNestingBox.getY(), targetNestingBox.getZ() + 0.5, speed);
        }
    }

    @Override
    public void tick() {
        chicken.eggLayTime = 15;
        if (targetNestingBox != null && chicken.getBlockPos().isWithinDistance(targetNestingBox, 1.5)) {
            // Lay the egg in the nesting box
            World world = chicken.getWorld();
            if (world.getBlockEntity(targetNestingBox) instanceof NestingBoxBlockEntity nestingBox) {
                ItemStack egg = new ItemStack(Items.EGG, 1);
                if (nestingBox.insertEgg(egg, false)) {
                    chicken.playSound(net.minecraft.sound.SoundEvents.ENTITY_CHICKEN_EGG, 1.0F,
                        (chicken.getRandom().nextFloat() - chicken.getRandom().nextFloat()) * 0.2F + 1.0F);
                    chicken.eggLayTime = chicken.getRandom().nextInt(6000) + 6000; // Reset egg timer
                }
            }
            targetNestingBox = null; // Clear the target after laying the egg
        }
    }

    @Override
    public void stop() {
        targetNestingBox = null;
    }

    private BlockPos findNearestNestingBox() {
        ServerWorld world = (ServerWorld) chicken.getWorld();
        BlockPos chickenPos = chicken.getBlockPos();
        final int SEARCH_RADIUS = 10; // Define the search radius

        for (BlockPos pos : BlockPos.iterate(chickenPos.add(-SEARCH_RADIUS, -SEARCH_RADIUS, -SEARCH_RADIUS),
            chickenPos.add(SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS))) {
            if (world.getBlockEntity(pos) instanceof NestingBoxBlockEntity) {
                return pos;
            }
        }

        return null; // No nesting box found
    }
}

package consular.chickenoverhaul.goal;

import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import consular.chickenoverhaul.ChickenOverhaul;
import consular.chickenoverhaul.block.ChickenEggBlock;
import consular.chickenoverhaul.registry.ModBlocks;
import net.minecraft.block.BlockState;

public class ChickenMateGoal extends AnimalMateGoal {
    private final ChickenEntity chicken;

    public ChickenMateGoal(ChickenEntity chicken, Double speed) {
        super(chicken, speed);
        this.chicken = chicken;
    }

    @Override
    public boolean canStart() {
        return super.canStart();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void breed() {
        BlockPos eggPos = chicken.getBlockPos().down();
        World world = chicken.getWorld();

        if (world.getBlockState(eggPos).isSolidBlock(world, eggPos)) {
            BlockState fertilizedEggState = ModBlocks.CHICKEN_EGG.getDefaultState()
                    .with(ChickenEggBlock.FERTILIZED, true);
            world.setBlockState(eggPos.up(), fertilizedEggState);
            ChickenOverhaul.LOGGER.info("Fertilized egg placed at {}", eggPos.up());
        }

        super.breed();
    }
}

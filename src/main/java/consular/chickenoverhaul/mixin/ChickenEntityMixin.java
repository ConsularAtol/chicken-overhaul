package consular.chickenoverhaul.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import consular.chickenoverhaul.goal.ChickenMateGoal;
import consular.chickenoverhaul.registry.ModBlocks;

@Mixin(ChickenEntity.class)
public abstract class ChickenEntityMixin extends AnimalEntity{

	protected ChickenEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    // This is jank as shit but fuck it
    @Inject(method = "createChild", at = @At("RETURN"))
    public ChickenEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity, CallbackInfoReturnable<?> ci) {
        return (ChickenEntity)(Object)this;
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void modifyBreedingBehavior(CallbackInfo ci) {
        // Remove default BreedGoal
        this.goalSelector.getGoals().removeIf(entry -> entry.getGoal() instanceof AnimalMateGoal);

        // Add custom goal for laying fertilized eggs
        this.goalSelector.add(2, new ChickenMateGoal((ChickenEntity)(Object)this, 1.0));
    }

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void replaceEggBehavior(CallbackInfo ci) {
        ChickenEntity chicken = (ChickenEntity) (Object) this;
        World world = chicken.getWorld();

        // Check if on the server side and if the chicken is ready to lay an egg
        if (world instanceof ServerWorld && chicken.isAlive() && !chicken.isBaby() && !chicken.hasJockey()) {
            if (--chicken.eggLayTime <= 10) { // We check if it's less than 10 as a janky way to override the original egg laying mechanic
                BlockPos blockPos = chicken.getBlockPos(); // Use the chicken's current position
                BlockState eggBlockState = ModBlocks.CHICKEN_EGG.getDefaultState();

                // Check if the block below is solid and if the current position can accept the egg block
                BlockPos blockBelow = blockPos.down();
                if (world.getBlockState(blockBelow).isSolidBlock(world, blockBelow) &&
                    world.getBlockState(blockPos).isAir() && eggBlockState.canPlaceAt(world, blockPos)) {
                    world.setBlockState(blockPos, eggBlockState);
                    chicken.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, 
                        (chicken.getRandom().nextFloat() - chicken.getRandom().nextFloat()) * 0.2F + 1.0F);
                    chicken.emitGameEvent(GameEvent.ENTITY_PLACE);
                    chicken.eggLayTime = chicken.getRandom().nextInt(6000) + 6000;
                    ci.cancel(); // Cancel original egg drop behavior
                } else {
                    chicken.eggLayTime = 10; // Hold the egg until it's able to be placed
                }
            }
        }
    }
}
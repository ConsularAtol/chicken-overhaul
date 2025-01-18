package consular.chickenoverhaul.mixin;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EggItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import consular.chickenoverhaul.registry.ModBlocks;

@Mixin(EggItem.class)
public class EggItemMixin {
	public ActionResult useOnBlock(ItemUsageContext context) {
      if (context.getWorld().getBlockState(context.getBlockPos()).isSolidBlock(context.getWorld(), context.getBlockPos())){
    	   ActionResult actionResult = this.place(new ItemPlacementContext(context));
    	   return !actionResult.isAccepted() && context.getStack().contains(DataComponentTypes.CONSUMABLE) ? use(context.getWorld(), context.getPlayer(), context.getHand()) : actionResult;
      } else{
         return ActionResult.FAIL;
      }
   }

   public ActionResult use(World world, PlayerEntity user, Hand hand) {
      ItemStack itemStack = user.getStackInHand(hand);
      ConsumableComponent consumableComponent = (ConsumableComponent)itemStack.get(DataComponentTypes.CONSUMABLE);
      if (consumableComponent != null) {
         return consumableComponent.consume(user, itemStack, hand);
      } else {
         EquippableComponent equippableComponent = (EquippableComponent)itemStack.get(DataComponentTypes.EQUIPPABLE);
         return (ActionResult)(equippableComponent != null && equippableComponent.swappable() ? equippableComponent.equip(itemStack, user) : ActionResult.PASS);
      }
   }

   public ActionResult place(ItemPlacementContext context) {
      BlockState blockState = ModBlocks.CHICKEN_EGG.getDefaultState();
      if (!canPlace(context, blockState)) {
         return ActionResult.FAIL;
      } else {
         ItemPlacementContext itemPlacementContext = context;
         if (blockState == null) {
            return ActionResult.FAIL;
         } else if (!place(itemPlacementContext, blockState)) {
            return ActionResult.FAIL;
         } else {
            BlockPos blockPos = itemPlacementContext.getBlockPos();
            World world = itemPlacementContext.getWorld();
            PlayerEntity playerEntity = itemPlacementContext.getPlayer();
            ItemStack itemStack = itemPlacementContext.getStack();
            BlockState blockState2 = world.getBlockState(blockPos);
            if (blockState2.isOf(blockState.getBlock())) {
               blockState2 = this.placeFromNbt(blockPos, world, itemStack, blockState2);
               this.postPlacement(blockPos, world, playerEntity, itemStack, blockState2);
               copyComponentsToBlockEntity(world, blockPos, itemStack);
               blockState2.getBlock().onPlaced(world, blockPos, blockState2, playerEntity, itemStack);
               if (playerEntity instanceof ServerPlayerEntity) {
                  Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
               }
            }
               BlockSoundGroup blockSoundGroup = blockState2.getSoundGroup();
               world.playSound(playerEntity, blockPos, this.getPlaceSound(blockState2), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);
               world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, Emitter.of(playerEntity, blockState2));
               itemStack.decrementUnlessCreative(1, playerEntity);
               return ActionResult.SUCCESS;
            }
         }
   }

   public boolean canPlace(ItemPlacementContext context, BlockState state) {
      PlayerEntity playerEntity = context.getPlayer();
      ShapeContext shapeContext = playerEntity == null ? ShapeContext.absent() : ShapeContext.of(playerEntity);
      return (state.canPlaceAt(context.getWorld(), context.getBlockPos())) && context.getWorld().canPlace(state, context.getBlockPos(), shapeContext);
   }

   public boolean place(ItemPlacementContext context, BlockState state) {
      return context.getWorld().setBlockState(context.getBlockPos(), state, 11);
   }

   public BlockState placeFromNbt(BlockPos pos, World world, ItemStack stack, BlockState state) {
      BlockStateComponent blockStateComponent = (BlockStateComponent)stack.getOrDefault(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT);
      if (blockStateComponent.isEmpty()) {
         return state;
      } else {
         BlockState blockState = blockStateComponent.applyToState(state);
         if (blockState != state) {
            world.setBlockState(pos, blockState, 2);
         }

         return blockState;
      }
   }

   public boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
      return writeNbtToBlockEntity(world, player, pos, stack);
   }

   public boolean writeNbtToBlockEntity(World world, @Nullable PlayerEntity player, BlockPos pos, ItemStack stack) {
      if (world.isClient) {
         return false;
      } else {
         NbtComponent nbtComponent = (NbtComponent)stack.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT);
         if (!nbtComponent.isEmpty()) {
            BlockEntityType<?> blockEntityType = (BlockEntityType<?>)nbtComponent.getRegistryValueOfId(world.getRegistryManager(), RegistryKeys.BLOCK_ENTITY_TYPE);
            if (blockEntityType == null) {
               return false;
            }

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) {
               BlockEntityType<?> blockEntityType2 = blockEntity.getType();
               if (blockEntityType2 != blockEntityType) {
                  return false;
               }

               if (!blockEntityType2.canPotentiallyExecuteCommands() || player != null && player.isCreativeLevelTwoOp()) {
                  return nbtComponent.applyToBlockEntity(blockEntity, world.getRegistryManager());
               }

               return false;
            }
         }

         return false;
      }
   }

   public SoundEvent getPlaceSound(BlockState state) {
      return state.getSoundGroup().getPlaceSound();
   }

   public void copyComponentsToBlockEntity(World world, BlockPos pos, ItemStack stack) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity != null) {
         blockEntity.readComponents(stack);
         blockEntity.markDirty();
      }

   }
}
package consular.chickenoverhaul.mixin;

import net.minecraft.entity.passive.ChickenEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChickenEntity.class)
public class ChickenEntityMixin {
	@Inject(at = @At("HEAD"), method = "tickMovement")
	private void init(CallbackInfo info) {
	}
}
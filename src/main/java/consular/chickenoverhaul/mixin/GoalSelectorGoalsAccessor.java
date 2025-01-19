package consular.chickenoverhaul.mixin;

import net.minecraft.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(GoalSelector.class)
public interface GoalSelectorGoalsAccessor {
    @Accessor("goals")
    Set<?> getGoals();
}

package cz.jeme.bestiumexample.entity.ai

import cz.jeme.bestiumexample.entity.Hippogriff
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.animal.horse.AbstractHorse
import net.minecraft.world.entity.player.Player

private val IS_TARGET_SELECTOR = { potentialTarget: LivingEntity, _: ServerLevel ->
    when (potentialTarget) {
        is Hippogriff -> false // don't eat other hippogriffs
        is Player -> true // eat players
        is AbstractHorse -> false // don't eat horses, donkeys and mules, they are too big
        is Animal -> true // eat all other animals except horses
        else -> false
    }
}

class HippogriffHungerTargetGoal(val hippogriff: Hippogriff) : NearestAttackableTargetGoal<LivingEntity>(
    hippogriff,
    LivingEntity::class.java,
    5,
    false,
    true,
    IS_TARGET_SELECTOR
) {
    override fun canUse() = hippogriff.isHungry && super.canUse()
}
package cz.jeme.bestiumexample.entity

import cz.jeme.bestium.api.entity.CustomAnimal
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import org.bukkit.Bukkit

class Capybara(entityType: EntityType<out CustomAnimal>, level: Level) : CustomAnimal(entityType, level) {
    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, .25)
        }

        fun isCapybaraFood(stack: ItemStack): Boolean = when (stack.item) {
            Items.SEAGRASS, Items.MELON_SLICE -> true
            else -> false
        }
    }

    init {
        Bukkit.broadcast(Component.text("bomboclatt", NamedTextColor.RED))
    }

    override fun registerGoals() {
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(1, PanicGoal(this, 1.25))
        goalSelector.addGoal(3, BreedGoal(this, 1.0))
        goalSelector.addGoal(
            4,
            TemptGoal(this, 1.2, ::isCapybaraFood, false)
        )
        goalSelector.addGoal(5, FollowParentGoal(this, 1.1))
        goalSelector.addGoal(6, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(7, LookAtPlayerGoal(this, Player::class.java, 6.0f))
        goalSelector.addGoal(8, RandomLookAroundGoal(this))
    }

    override fun isFood(stack: ItemStack): Boolean = isCapybaraFood(stack)

    override fun getBreedOffspring(
        level: ServerLevel,
        otherParent: AgeableMob
    ): AgeableMob? {
        return bestium_realType().create(level, EntitySpawnReason.BREEDING) as AgeableMob?
    }

    override fun setAge(age: Int) {
        super.setAge(age)
        val scale = attributes.getInstance(Attributes.SCALE)!!
        scale.baseValue = if (age < 0) 0.5 else 1.0
    }
}
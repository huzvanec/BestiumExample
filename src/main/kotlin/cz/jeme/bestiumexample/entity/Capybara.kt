package cz.jeme.bestiumexample.entity

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.SpawnGroupData
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import org.bukkit.Bukkit

class Capybara(entityType: EntityType<out Capybara>, level: Level) : Animal(entityType, level) {
    companion object {
        /** Returns the default attributes for a Capybara. */
        fun createAttributes(): AttributeSupplier.Builder {
            return createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, .25)
        }
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        spawnGroupData: SpawnGroupData?
    ): SpawnGroupData? {
        // This is an example of a rare "allowed" usage of Bukkit API in a custom entity class.
        // Other logic must  use NMS (`net.minecraft.*`).
        // For more, see: https://docs.bestium.jeme.cz/code/coding-entity/
        Bukkit.broadcast(
            Component.text(
                "Look! A Capybara spawned at $x / $y / $z!",
                NamedTextColor.GREEN
            ).clickEvent(
                ClickEvent.suggestCommand(
                    "/tp @s $x $y $z"
                )
            )
        )
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData)
    }

    /**
     * Registers AI goals for the Capybara.
     * Lower priority number = higher importance.
     */
    override fun registerGoals() {
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(1, PanicGoal(this, 1.25))
        goalSelector.addGoal(3, BreedGoal(this, 1.0))
        goalSelector.addGoal(4, TemptGoal(this, 1.2, ::isFood, false))
        goalSelector.addGoal(5, FollowParentGoal(this, 1.1))
        goalSelector.addGoal(6, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(7, LookAtPlayerGoal(this, Player::class.java, 6.0F))
        goalSelector.addGoal(8, RandomLookAroundGoal(this))
    }

    /**
     * Returns whether an item can be eaten by Capybaras.
     */
    override fun isFood(stack: ItemStack): Boolean = when (stack.item) {
        Items.SEAGRASS, Items.MELON_SLICE -> true
        else -> false
    }

    /**
     * Handles the creation of a Capybara baby during breeding.
     */
    override fun getBreedOffspring(
        level: ServerLevel,
        otherParent: AgeableMob
    ): AgeableMob? {
        return type.create(
            level,
            EntitySpawnReason.BREEDING
        ) as AgeableMob?
    }

    /**
     * Sets the age of the Capybara.
     *
     * Capybara size is adjusted via the SCALE attribute, which is used by
     * the client-side `BetterModel` to visually shrink babies.
     *
     * This is a hacky workaround since vanilla Minecraft handles baby size on the client,
     * and there's no clean way to make baby mobs smaller on the server side.
     */
    override fun setAge(age: Int) {
        super.setAge(age)
        val scale = attributes.getInstance(Attributes.SCALE)!!
        scale.baseValue = if (age < 0) 0.5 else 1.0
    }
}
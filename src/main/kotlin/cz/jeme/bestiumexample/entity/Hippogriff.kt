package cz.jeme.bestiumexample.entity

import cz.jeme.bestiumexample.entity.ai.HippogriffHungerTargetGoal
import cz.jeme.bestiumexample.entity.ai.HippogriffRandomStrollGoal
import net.kyori.adventure.text.Component
import net.minecraft.core.BlockPos
import net.minecraft.server.commands.TeleportCommand
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.ItemTags
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.FlyingMoveControl
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.FloatGoal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import org.bukkit.Bukkit

private const val HUNGRY_EVERY_TICKS_AVERAGE: Int = 5 * 60 * 20 // 5 minutes

private const val REPLENISH_HUNGER_ON_KILL: Int = 30

class Hippogriff(entityType: EntityType<out Hippogriff>, val level: Level) : TamableAnimal(entityType, level) {
    companion object {
        fun createAttributes(): AttributeSupplier = createAnimalAttributes()
            .add(Attributes.MAX_HEALTH, 40.0)
            .add(Attributes.MOVEMENT_SPEED, 0.25)
            .add(Attributes.FLYING_SPEED, 2.0)
            .add(Attributes.ATTACK_DAMAGE, 5.0)
            .add(Attributes.FOLLOW_RANGE, 70.0)
            .build()
    }

    var hunger: Int = 0

    var isFlying = true
        private set

    var isLanding = false
        private set

    var isHungry: Boolean
        get() = hunger >= 100
        set(value) {
            hunger = if (value) 100 else 0
        }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        spawnGroupData: SpawnGroupData?
    ): SpawnGroupData? {
        hunger = random.nextInt(0, 100) // pick a random hunger value
        updateNav()
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData)
    }

    private val groundNav = GroundPathNavigation(this, level)
    private val skyNav = object : FlyingPathNavigation(this, level) {
    }

    private val groundControl = MoveControl(this)
    private val skyControl = FlyingMoveControl(this, 10, true)

    private fun updateNav() {
        Bukkit.broadcast(Component.text("Updating nav to ${if (isFlying) "fly" else "ground"} mode"))
        navigation = if (isFlying) skyNav else groundNav
        moveControl = if (isFlying) skyControl else groundControl
    }

    var tickshit = 0

    override fun aiStep() {
        super.aiStep()
        if (random.nextFloat() <= 100.0 / HUNGRY_EVERY_TICKS_AVERAGE) hunger++
        if (!isLanding) {
            tickshit++
            if (tickshit == 600) {
                tickshit = 0
                if (isFlying) {
                    Bukkit.broadcast(Component.text("I want to start landing"))
                    navigation.stop()
                    navigation.moveTo(0.0, 100.0, 0.0, 1.0)
                    Bukkit.broadcast(Component.text(navigation.maxDistanceToWaypoint.toString()))
//                    isLanding = true
                } else {
                    Bukkit.broadcast(Component.text("I want to take off"))
                    isFlying = true
                    updateNav()
                }
            }
        }
    }

    fun preLand() {
        isLanding && isFlying || return
        Bukkit.broadcast(Component.text("Preland: pathfinding to the ground"))
        isFlying = false
    }

    fun land() {
        isLanding || return
        Bukkit.broadcast(Component.text("Land: Landing"))
        isLanding = false
        isNoGravity = false
        updateNav()
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.putInt("hunger", hunger)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        input.getInt("hunger").ifPresent { hunger = it }
    }

    override fun registerGoals() {
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(1, MeleeAttackGoal(this, 1.5, true))
        goalSelector.addGoal(2, HippogriffRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(3, LookAtPlayerGoal(this, LivingEntity::class.java, 6.0F))
        goalSelector.addGoal(4, RandomLookAroundGoal(this))

        targetSelector.addGoal(1, HurtByTargetGoal(this))
        targetSelector.addGoal(2, HippogriffHungerTargetGoal(this))
    }

    override fun checkFallDamage(y: Double, onGround: Boolean, state: BlockState, pos: BlockPos) {}

    override fun onClimbable() = false

    override fun awardKillScore(entity: Entity, damageSource: DamageSource) {
        super.awardKillScore(entity, damageSource)
        hunger = (hunger - 30).coerceAtLeast(0).coerceAtMost(100)
    }

    override fun isFood(stack: ItemStack) = stack.`is`(ItemTags.MEAT)

    override fun getBreedOffspring(
        level: ServerLevel,
        otherParent: AgeableMob
    ): AgeableMob? = type.create(level, EntitySpawnReason.BREEDING) as? Hippogriff

    private inner class HippogriffFlyingMoveControl() : MoveControl(this@Hippogriff) {

    }
}
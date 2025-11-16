package cz.jeme.bestiumexample.entity.ai

import cz.jeme.bestiumexample.entity.Hippogriff
import net.kyori.adventure.text.Component
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.Vec3
import org.bukkit.Bukkit

open class HippogriffRandomStrollGoal(
    val hippogriff: Hippogriff,
    speedModifier: Double
) : WaterAvoidingRandomStrollGoal(hippogriff, speedModifier) {
    protected val flyingDelegate = AltitudeWaterAvoidingRandomFlyingGoal(
        hippogriff, speedModifier,
        minAltitude = 60, maxAltitude = 80,
        radius = 20
    )

    override fun canUse(): Boolean {
        hippogriff.navigation.isDone || return false
        if (hippogriff.isFlying && !hippogriff.isLanding) {
            flyingDelegate.canUse() || return false
            forceTrigger = false
            wantedX = flyingDelegate.pubWantedX
            wantedY = flyingDelegate.pubWantedY
            wantedZ = flyingDelegate.pubWantedZ
            return true
        } else return !hippogriff.navigation.isInProgress && super.canUse()
    }

    override fun getPosition(): Vec3? {
        if (hippogriff.isLanding) {
            Bukkit.broadcast(Component.text("getPosition landing"))
            if (hippogriff.isFlying) {
                Bukkit.broadcast(Component.text("getPosition preland"))
                hippogriff.preLand()
            } else {
                Bukkit.broadcast(Component.text("getPosition land"))
                hippogriff.land()
            }
            Bukkit.broadcast(Component.text("getPosition straight down"))
            val terrain = hippogriff.level.getHeightmapPos(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BlockPos.containing(hippogriff.position())
            )
            return Vec3(hippogriff.x + 20, terrain.y.toDouble(), hippogriff.z)
        }
        if (hippogriff.isFlying) return flyingDelegate.position
        else {
            val pos = super.position ?: return null
            Bukkit.broadcast(Component.text("Groundstroll: going to ${pos.x} / ${pos.y} / ${pos.z}"))
            return pos
        }
    }
}
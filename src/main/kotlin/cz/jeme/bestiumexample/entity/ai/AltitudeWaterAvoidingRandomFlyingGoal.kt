package cz.jeme.bestiumexample.entity.ai

import net.kyori.adventure.text.Component
import net.minecraft.core.BlockPos
import net.minecraft.core.Position
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos
import net.minecraft.world.entity.ai.util.HoverRandomPos
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.Vec3
import org.bukkit.Bukkit

open class AltitudeWaterAvoidingRandomFlyingGoal(
    mob: PathfinderMob,
    speedModifier: Double,
    val minAltitude: Int,
    val maxAltitude: Int,
    val radius: Int
) : WaterAvoidingRandomStrollGoal(mob, speedModifier) {
    // expose wanted values
    val pubWantedX get() = super.wantedX
    val pubWantedY get() = super.wantedY
    val pubWantedZ get() = super.wantedZ

    private fun getRandomPos(): Vec3? {
        val viewVector = super.mob.getViewVector(0.0f)
        return HoverRandomPos.getPos(
            super.mob, radius, 7,
            viewVector.x,
            viewVector.z,
            (Math.PI.toFloat() / 2f),
            3,
            1
        ) ?: AirAndWaterRandomPos.getPos(
            super.mob,
            radius,
            4,
            -2,
            viewVector.x,
            viewVector.z,
            (Math.PI.toFloat() / 2f).toDouble()
        )
    }

    public override fun getPosition(): Vec3? {
        val pos = getRandomPos() ?: return null

        val terrain = mob.level().getHeightmapPos(
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            BlockPos.containing(pos as Position)
        )

        val altitude = mob.random.nextInt(maxAltitude - minAltitude) + minAltitude

        Bukkit.broadcast(Component.text("Flystroll: going to ${pos.x} / ${terrain.y} + $altitude / ${pos.z}"))

        return Vec3(pos.x, (terrain.y + altitude).toDouble(), pos.z)
    }
}
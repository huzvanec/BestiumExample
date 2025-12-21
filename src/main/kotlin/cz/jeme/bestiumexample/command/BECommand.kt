package cz.jeme.bestiumexample.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import cz.jeme.bestium.api.Bestium
import cz.jeme.bestiumexample.entity.Capybara
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.minecraft.world.entity.EntitySpawnReason
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.entity.CraftAnimals
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.plugin.Plugin

class BECommand(plugin: Plugin, commands: Commands) {
    private fun spawnBen(ctx: CommandContext<CommandSourceStack>): Int {
        val spawnLocation = ctx.source.location

        val capybara = Bestium.getEntityManager().spawn(
            spawnLocation,
            Capybara::class.java,
            EntitySpawnReason.COMMAND,
            CreatureSpawnEvent.SpawnReason.COMMAND
        )!!
        // the following code is not ran in the consumer parameter of spawn(),
        // because attributes are applied right before entity spawn
        // and would be overwritten by default attributes

        // obtain bukkit representation of capybara
        // I can safely cast as CraftAnimals, because I specified
        // ::CraftAnimals as the ConvertFunction when registering Capybara injection
        val bukkit = capybara.bukkitEntity as CraftAnimals
        bukkit.getAttribute(Attribute.MOVEMENT_SPEED)!!.baseValue = 0.4

        return Command.SINGLE_SUCCESS
    }

    private val command = Commands.literal("bexample")
        .requires { it.sender.hasPermission("bestium_example.command.bexample") }
        .then(
            Commands.literal("spawnben")
                .executes(::spawnBen)
        )
        .build()

    init {
        commands.register(
            plugin.pluginMeta,
            command,
            "A command showing what is possible using Bestium",
            listOf("be")
        )
    }
}
package cz.jeme.bestiumexample

import cz.jeme.bestiumexample.command.BECommand
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin

internal class BestiumExample : JavaPlugin() {
    override fun onEnable() {

        // register BE command
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val commands = event.registrar()
            BECommand(this, commands)
        }
    }
}

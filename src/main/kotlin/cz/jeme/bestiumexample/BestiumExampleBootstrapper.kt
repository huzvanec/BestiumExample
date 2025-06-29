package cz.jeme.bestiumexample

import cz.jeme.bestium.api.Bestium
import cz.jeme.bestium.api.inject.EntityInjection
import cz.jeme.bestiumexample.entity.Capybara
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.bootstrap.PluginProviderContext
import net.kyori.adventure.key.Key
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import org.bukkit.craftbukkit.entity.CraftAnimals

@Suppress("UnstableApiUsage")
class BestiumExampleBootstrapper : PluginBootstrap {
    override fun bootstrap(context: BootstrapContext) {
        val injector = Bestium.injector()
        injector.register(
            EntityInjection.builder(
                Key.key("capybara"),
                Capybara::class.java,
                ::Capybara,
                ::CraftAnimals
            )
                .backingType(EntityType.PIG)
                .attributes(Capybara.createAttributes())
                .mobCategory(MobCategory.CREATURE)
//                .model(this, "models/capybara.bbmodel")
        )
    }

    override fun createPlugin(context: PluginProviderContext) = BestiumExample
}
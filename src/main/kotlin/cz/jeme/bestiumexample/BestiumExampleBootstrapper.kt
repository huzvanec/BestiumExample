package cz.jeme.bestiumexample

import cz.jeme.bestium.api.Bestium
import cz.jeme.bestium.api.inject.EntityInjection
import cz.jeme.bestium.api.inject.biome.BiomeFilter
import cz.jeme.bestium.api.inject.biome.SpawnData
import cz.jeme.bestium.api.inject.biome.SpawnRule
import cz.jeme.bestium.api.inject.variant.EntityVariant
import cz.jeme.bestium.api.inject.variant.VariantRule
import cz.jeme.bestium.api.util.BiomeTemperature
import cz.jeme.bestiumexample.entity.Capybara
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import org.bukkit.craftbukkit.entity.CraftAnimals
import java.util.*

@Suppress("UnstableApiUsage", "unused")
class BestiumExampleBootstrapper : PluginBootstrap {
    override fun bootstrap(context: BootstrapContext) {
        Bestium.getInjector().register {
            EntityInjection.builder(
                Key.key("bestium_example", "capybara"),
                Capybara::class.java,
                ::Capybara,
                ::CraftAnimals,
                EntityType.PIG
            )
                .setDisplayNames(
                    mapOf(
                        Locale.US to Component.text("Capybara"),
                        Locale.of("cs", "CZ") to Component.text("Kapybara"),
                    )
                )
                .setMobCategory(MobCategory.CREATURE)
                .setDefaultAttributes(Capybara.createAttributes())
                .addVariants(
                    EntityVariant.fromModelResource(
                        "normal",
                        this,
                        "models/capybara/normal.bbmodel"
                    ),
                    EntityVariant.fromModelResource(
                        "blue",
                        this,
                        "models/capybara/blue.bbmodel"
                    )
                )
                .setVariantRule(
                    VariantRule.firstMatch(
                        VariantRule.ifTemperature(
                            BiomeTemperature.COLD,
                            "blue"
                        ),
                        VariantRule.always("normal")
                    )
                )
                .setSpawnRule(
                    SpawnRule.ifBiome(
                        BiomeFilter.tag(Key.key("minecraft:is_river")),
                        SpawnData(1, 1, 4)
                    )
                )
                .setTypeCustomizer { builder ->
                    builder.fireImmune()
                }
                .build()
        }

        context.lifecycleManager.registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY) { event ->
            event.registrar().discoverPack(
                javaClass.getResource("/datapack")!!.toURI(),
                "main"
            )
        }
    }
}

package cz.jeme.bestiumexample

import cz.jeme.bestium.api.Bestium
import cz.jeme.bestium.api.inject.EntityInjection
import cz.jeme.bestium.api.inject.biome.BiomeFilter
import cz.jeme.bestium.api.inject.biome.SpawnData
import cz.jeme.bestium.api.inject.biome.SpawnRule
import cz.jeme.bestium.api.inject.variant.EntityVariant
import cz.jeme.bestium.api.inject.variant.VariantRule
import cz.jeme.bestiumexample.entity.Capybara
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.bootstrap.PluginProviderContext
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import org.bukkit.craftbukkit.entity.CraftAnimals
import org.bukkit.plugin.java.JavaPlugin
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
                .setMobCategory(MobCategory.CREATURE)
                .setDisplayNames(
                    mapOf(
                        Locale.US to Component.text("Capybara"),
                        Locale.FRANCE to Component.text("Capybara"),
                        Locale.GERMANY to Component.text("Wasserschwein"),
                        Locale.ITALY to Component.text("Capibara"),
                        Locale.JAPAN to Component.text("カピバラ"),
                        Locale.KOREA to Component.text("카피바라"),
                        Locale.CHINA to Component.text("水豚"),
                        Locale.of("cs", "CZ") to Component.text("Kapybara")
                    )
                )
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
                        VariantRule.ifBiome(
                            BiomeFilter.tag(Key.key("minecraft:spawns_cold_variant_farm_animals")),
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

    override fun createPlugin(context: PluginProviderContext): JavaPlugin = BestiumExample
}

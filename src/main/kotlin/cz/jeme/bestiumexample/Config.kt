package cz.jeme.bestiumexample

import org.bukkit.configuration.file.FileConfiguration

object Config {
    private lateinit var config: FileConfiguration

    fun reload() {
        BestiumExample.reloadConfig()
        config = BestiumExample.config
    }

    val notifyOnCapybaraSpawn: Boolean get() = config.getBoolean("notify-on-capybara-spawn", false)
}
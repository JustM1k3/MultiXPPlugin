package me.ym;

import me.oxolotel.utils.wrapped.plugin.BukkitPlugin;
import me.oxolotel.utils.wrapped.plugin.Plugin;
import org.bukkit.command.Command;

public final class Main extends BukkitPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        new Commands().register(Plugin.of(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

package me.ym;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.oxolotel.utils.wrapped.plugin.BukkitPlugin;
import me.oxolotel.utils.wrapped.plugin.Plugin;
import me.ym.managerPackage.AnvilMenuManager;
import me.ym.managerPackage.PacketReader;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends BukkitPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        new Commands().register(Plugin.of(this));
        Bukkit.getPluginManager().registerEvents(new AnvilMenuManager(), this);
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketReader.readWindowClickPacket(protocolManager, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}

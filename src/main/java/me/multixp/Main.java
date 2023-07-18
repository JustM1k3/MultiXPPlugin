package me.multixp;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.oxolotel.utils.wrapped.plugin.BukkitPlugin;
import me.oxolotel.utils.wrapped.plugin.Plugin;
import me.multixp.managerPackage.AnvilMenuManager;
import me.multixp.managerPackage.PacketReader;
import org.bukkit.Bukkit;

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

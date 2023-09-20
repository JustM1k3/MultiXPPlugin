package me.multixp;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.multixp.listener.MenuContentUpdateEvent;
import me.multixp.listener.XpBottleEvent;
import me.oxolotel.utils.wrapped.plugin.BukkitPlugin;
import me.oxolotel.utils.wrapped.plugin.Plugin;
import me.multixp.managerPackage.AnvilMenuManager;
import me.multixp.managerPackage.PacketReader;
import org.bukkit.Bukkit;

public final class Main extends BukkitPlugin {

    public static String PREFIX = "MultiXP";

    @Override
    public void onEnable() {
        // Plugin startup logic
        new Commands().register(Plugin.of(this));
        Bukkit.getPluginManager().registerEvents(new AnvilMenuManager(), this);
        Bukkit.getPluginManager().registerEvents(new XpBottleEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MenuContentUpdateEvent(), this);

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketReader.readWindowClickPacket(protocolManager, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}

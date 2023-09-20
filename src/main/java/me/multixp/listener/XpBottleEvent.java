package me.multixp.listener;

import me.multixp.managerPackage.ExpManager;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class XpBottleEvent implements Listener {
    @EventHandler
    public void xpEvent(ExpBottleEvent event) {
        if (ExpManager.checkForMultiXPFlasche(event.getEntity().getItem())) {
            String xpLore = event.getEntity().getItem().getItemMeta().getLore().get(4);
            String[] arr = xpLore.split(" ");
            event.setExperience(Integer.parseInt(arr[1].substring(2)));
        }
    }
}
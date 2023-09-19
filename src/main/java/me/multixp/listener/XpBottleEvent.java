package me.multixp.listener;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class XpBottleEvent implements Listener {
    @EventHandler
    public void xpEvent(ExpBottleEvent event){
        ItemMeta bottle = event.getEntity().getItem().getItemMeta();

        if (bottle != null && bottle.hasEnchant(Enchantment.MULTISHOT)){
            int lvl = bottle.getEnchants().get(Enchantment.MULTISHOT);

            if (lvl == 1871){
                String xpLore = bottle.getLore().get(4);
                String[] arr = xpLore.split(" ");
                event.setExperience(Integer.parseInt(arr[1].substring(2)));
            }
        }
    }
}
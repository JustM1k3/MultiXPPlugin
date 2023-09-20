package me.multixp.listener;

import me.oxolotel.utils.bukkit.menuManager.events.implement.log.MenuLogModifyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class MenuContentUpdateEvent implements Listener {
    @EventHandler
    public void updateMenuEvent(MenuLogModifyEvent e) {
        e.getPlayer().sendMessage("TEST");
        if (e.getMenu().getTitle().equals("MultiXP - Merge")){
            if (e.getChangedSlots().containsKey(29) || e.getChangedSlots().containsKey(30) || e.getChangedSlots().containsKey(31)){
                e.getPlayer().sendMessage(e.getChangedSlots().keySet() + "");
            }
        }
    }
}

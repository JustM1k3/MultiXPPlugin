package me.ym.managerPackage;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class AnvilMenuManager implements Listener {
    public static final ArrayList<Inventory> invList = new ArrayList<>();

    public static void createAnvilMenu(Player p, ItemStack item, String n) {
        Inventory inv = Bukkit.createInventory(p, InventoryType.ANVIL, n);
        inv.setItem(0, item);
        invList.add(inv);
        p.openInventory(inv);
    }

    public static ArrayList<Inventory> getInvList(){
        return invList;
    }

    public static void removeInv(Inventory inv){
        invList.remove(inv);
    }

    public static boolean checkForInv(Inventory inv){
        for (Inventory invFound:invList){
            if (invFound == inv){
                return true;
            }
        }
        return false;
    }

    public static void levelFix(Player p){
        if (p.getGameMode() != GameMode.CREATIVE){
            p.setLevel(p.getLevel()+1);
        }
    }


    @EventHandler
    public void clickEvent(InventoryClickEvent e){
        if (checkForInv(e.getClickedInventory())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void closeEvent(InventoryCloseEvent e){
        if (checkForInv(e.getInventory())){
            removeInv(e.getInventory());
            if (e.getPlayer().getGameMode() != GameMode.CREATIVE){
                Player p = (Player) e.getPlayer();
                p.setLevel(p.getLevel()-1);
            }
        }
    }

    @EventHandler
    public void clickEvent(InventoryPickupItemEvent e){
        if (checkForInv(e.getInventory())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void clickEvent(InventoryDragEvent e){
        if (checkForInv(e.getInventory())){
            e.setCancelled(true);
        }
    }
}
package me.multixp.gui;

import me.multixp.managerPackage.ExpManager;
import me.multixp.managerPackage.ItemManager;
import me.multixp.managerPackage.SkullManager;
import me.oxolotel.utils.bukkit.menuManager.InventoryMenuManager;
import me.oxolotel.utils.bukkit.menuManager.menus.Closeable;
import me.oxolotel.utils.bukkit.menuManager.menus.CustomMenu;
import me.oxolotel.utils.bukkit.menuManager.menus.Modifyable;
import me.oxolotel.utils.bukkit.menuManager.menus.SlotCondition;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryContent;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MultiXPMergeMenu extends CustomMenu implements Closeable, SlotCondition, Modifyable {

    private ItemStack resultItem = new ItemStack(Material.AIR);
    private ArrayList<ItemStack> slots = new ArrayList<>();

    public MultiXPMergeMenu() {
        super(54);
        setTitle("MultiXP - Merge");

        slots.add(new ItemStack(Material.AIR));
        slots.add(new ItemStack(Material.AIR));
        slots.add(new ItemStack(Material.AIR));
    }

    @Override
    public void onClose(Player player, ItemStack[] itemStacks, CloseReason closeReason) {
        ArrayList<ItemStack> slots = new ArrayList<>();
        slots.add(itemStacks[29]);
        slots.add(itemStacks[30]);
        slots.add(itemStacks[31]);

        for (ItemStack item : slots) {
            if (checkPlayerInvPlace(player, slots.size())) {
                if (item != null) {
                    player.getInventory().addItem(item);
                }
            } else {
                if (item != null) {
                    player.getWorld().dropItem(player.getLocation(), item);
                }
            }
        }

    }

    @Override
    public InventoryContent getContents(Player player) {
        InventoryContent content = new InventoryContent();
        content.fill(0,54, new InventoryItem(new ItemManager(Material.MAGENTA_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(10,17, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(19,26, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(28,35, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(37,44, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));

        content.addGuiItem(13, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName("§5Merge").setMultiLineLore("Lege deine MultiXP Flaschen in /n die drei freien Slots links, /n um diese zu einer MultiXP /n Flasche zusammenzufügen. ","/n","§7", false).build(), ()->{
            player.sendMessage("");
        }));

        content.addGuiItem(47, new InventoryItem(new ItemManager(Material.ARROW).setDisplayName("§c§lZurück").build(), ()->{
            InventoryMenuManager.getInstance().closeMenu(player);
            InventoryMenuManager.getInstance().openMenu(player, new MultiXPCreate(54));
        }));
        content.addGuiItem(51, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lAbbrechen").build(), ()->{
            InventoryMenuManager.getInstance().closeMenu(player);
        }));


        for (int i = 0; i < 3; i++) {
            content.addGuiItem(29 + i, new InventoryItem(new ItemStack(Material.AIR), this::inventoryClick));
        }

        content.addGuiItem(32, new InventoryItem(new SkullManager("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19"," ").build(), ()->{}));

        content.addGuiItem(33, new InventoryItem(new ItemManager(Material.AIR).build(), (e) -> {
            if (resultItem != null && resultItem.getType() != Material.AIR) {
                if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {
                    ArrayList<ItemStack> checkItems = new ArrayList<>();
                    checkItems.add(e.getClickedInventory().getItem(29));
                    checkItems.add(e.getClickedInventory().getItem(30));
                    checkItems.add(e.getClickedInventory().getItem(31));

                    if (!checkForResultBottle(checkItems)){
                        e.getClickedInventory().setItem(33, new ItemStack(Material.AIR));
                        return true;
                    }


                    slots.set(0, new ItemStack(Material.AIR));
                    slots.set(1, new ItemStack(Material.AIR));
                    slots.set(2, new ItemStack(Material.AIR));

                    e.getClickedInventory().setItem(29, new ItemStack(Material.AIR));
                    e.getClickedInventory().setItem(30, new ItemStack(Material.AIR));
                    e.getClickedInventory().setItem(31, new ItemStack(Material.AIR));
                    return false;
                }
            }
            return true;
        }));

        return content;
    }

    private boolean inventoryClick(InventoryClickEvent e){
        /*if (!(e.isLeftClick() || e.isRightClick())){
            return true;
        }*/

        for (int i = 0; i < 3; i++) {
            if (e.getClickedInventory().getItem(29 + i) != null) {
                slots.set(i, e.getClickedInventory().getItem(29 + i));
            } else {
                slots.set(i,new ItemStack(Material.AIR));
            }
        }

        ItemStack cursorItem = e.getWhoClicked().getItemOnCursor();

        switch (e.getSlot()){
            case 29:
                slots.set(0, cursorItem);
                break;
            case 30:
                slots.set(1, cursorItem);
                break;
            case 31:
                slots.set(2, cursorItem);
                break;
        }

        ArrayList<ItemStack> expFlaschen = new ArrayList<>();
        for (ItemStack item:slots.stream().filter(ExpManager::checkForMultiXPFlasche).toList()) {
            expFlaschen.add(item);
        }

        if (checkForResultBottle(expFlaschen)){
            resultItem = ExpManager.mergeMultiXPFlaschen(expFlaschen);
            e.getClickedInventory().setItem(33, resultItem);
        } else {
            resultItem = new ItemStack(Material.AIR);
            e.getClickedInventory().setItem(33, resultItem);
        }

        return false;
    }
    private boolean checkPlayerInvPlace(Player player, int items){
        int emptySlotsSize = (int) Arrays.stream(player.getInventory().getStorageContents()).filter(item -> item == null || item.getType() == Material.AIR).count();
        return (emptySlotsSize >= items);
    }

    @Override
    public boolean isClickAllowed(Player player, int i) {
        return i == 47 || i == 51 || i == 29 || i == 30 || i == 31 || i == 33;
    }
    
    private boolean checkForResultBottle(ArrayList<ItemStack> expFlaschen){
        if (expFlaschen.size() >= 2 && slots.stream().filter((item)-> !ExpManager.checkForMultiXPFlasche(item)).filter((item)-> item.getType() != Material.AIR).toList().isEmpty()){
            return true;
        } else if (expFlaschen.size() == 1) {
            if (expFlaschen.get(0).getAmount() >= 2){
                return true;
            } else {
                return false;
            }
        }else {
            return false;
        }
    }
}

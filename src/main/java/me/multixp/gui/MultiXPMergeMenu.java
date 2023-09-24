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


public class MultiXPMergeMenu extends CustomMenu implements Closeable, SlotCondition, Modifyable {
    public MultiXPMergeMenu() {
        super(54);
        setTitle("MultiXP - Merge");
    }

    @Override
    public void onClose(Player player, ItemStack[] itemStacks, CloseReason closeReason) {
        ArrayList<ItemStack> slots = new ArrayList<>();
        slots.add(itemStacks[29]);
        slots.add(itemStacks[30]);
        slots.add(itemStacks[31]);

        for (ItemStack item : slots) {
            if (checkPlayerInvPlace(player, slots.size())) {
                player.getInventory().addItem(item);
            } else {
                player.getWorld().dropItem(player.getLocation(), item);
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

        content.addGuiItem(29, new InventoryItem(new ItemStack(Material.AIR), this::inventoryClick));
        content.addGuiItem(30, new InventoryItem(new ItemStack(Material.AIR), this::inventoryClick));
        content.addGuiItem(31, new InventoryItem(new ItemStack(Material.AIR), this::inventoryClick));
        content.addGuiItem(32, new InventoryItem(new SkullManager("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19"," ").build(), ()->{}));
        content.addGuiItem(33, new InventoryItem(new ItemManager(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));

        return content;
    }

    private boolean inventoryClick(InventoryClickEvent e){
        ArrayList<ItemStack> slots = new ArrayList<>();

        ItemStack cursorItem = e.getWhoClicked().getItemOnCursor();

        if (ExpManager.checkForMultiXPFlasche(cursorItem)){
            slots.add(cursorItem);
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
}

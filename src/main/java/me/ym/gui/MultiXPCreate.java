package me.ym.gui;

import me.oxolotel.utils.bukkit.menuManager.InventoryMenuManager;
import me.oxolotel.utils.bukkit.menuManager.menus.*;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryContent;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryItem;
import me.ym.managerPackage.ItemManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MultiXPCreate extends CustomMenu implements Closeable, SlotCondition {

    public MultiXPCreate(int size) {
        super(size);
    }

    @Override
    public void onClose(Player player, ItemStack[] itemStacks, CloseReason closeReason) {

    }

    @Override
    public InventoryContent getContents(Player player) {
        InventoryContent content = new InventoryContent();
        content.fill(0,54, new InventoryItem(new ItemManager(Material.MAGENTA_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(10,17, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(19,26, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(28,35, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(37,44, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));


        content.addGuiItem(13, new InventoryItem(new ItemManager(Material.ENCHANTING_TABLE).build(), ()->{
            player.sendMessage("");
        }));
        content.addGuiItem(30, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).build(), ()->{
            player.sendMessage("Test1");
        }));
        content.addGuiItem(32, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).build(), ()->{
            player.sendMessage("Test2");
        }));
        content.addGuiItem(47, new InventoryItem(new ItemManager(Material.ARROW).build(), ()->{
            InventoryMenuManager.getInstance().closeMenu(player);
            InventoryMenuManager.getInstance().openMenu(player, new MultiXPMenu(54));
        }));
        content.addGuiItem(51, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lAbbrechen").build(), ()->{
            InventoryMenuManager.getInstance().closeMenu(player);
        }));
            return content;
    }

    @Override
    public boolean isClickAllowed(Player player, int i) {
        return i == 30 || i == 32 || i == 47 || i == 51;
    }
}

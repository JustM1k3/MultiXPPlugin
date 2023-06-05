package me.ym.gui;

import me.oxolotel.utils.bukkit.menuManager.InventoryMenuManager;
import me.oxolotel.utils.bukkit.menuManager.menus.*;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryContent;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryItem;
import me.ym.managerPackage.ItemManager;
import me.ym.managerPackage.SkullManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MultiXPMenu extends CustomMenu implements Closeable, SlotCondition, Subdevideable {
    public MultiXPMenu(int size) {
        super(size);
    }

    @Override
    public InventoryContent getContents(Player player) {
        InventoryContent content = new InventoryContent();
        content.fill(0,54, new InventoryItem(new ItemStack(Material.BLUE_STAINED_GLASS_PANE), ()->{}));
        content.fill(10,16, new InventoryItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), ()->{}));
        content.fill(19,25, new InventoryItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), ()->{}));
        content.fill(28,34, new InventoryItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), ()->{}));
        content.fill(37,43, new InventoryItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), ()->{}));

        content.addGuiItem(13, new InventoryItem(new SkullManager().createSkullItem(player), ()->{}));
        content.addGuiItem(28, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).build(), ()->{
            System.out.println("EXP Flasche");
        }));
        content.addGuiItem(30, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).build(), ()->{
            System.out.println("EXP Flasche2");
        }));
        content.addGuiItem(32, new InventoryItem(new ItemManager(Material.ANVIL).build(), ()->{
            System.out.println("Amboss(SprengerLP)");
        }));
        content.addGuiItem(34, new InventoryItem(new ItemManager(Material.GLASS_BOTTLE).build(), ()->{
            System.out.println("Glass Flasche");
        }));
        content.addGuiItem(49, new InventoryItem(new ItemManager(Material.BARRIER).build(), ()->{
            InventoryMenuManager.getInstance().closeMenu(player);
        }));
        return content;
    }

    @Override
    public boolean isClickAllowed(Player player, int i) {
        return i == 29 || i == 31 || i == 33 || i == 49;
    }

    @Override
    public void onClose(Player player, ItemStack[] itemStacks, CloseReason closeReason) {

    }

    @Override
    public boolean hasSubmenu(int i) {
        return false;
    }

    @Override
    public CustomMenu getSubmenu(int i) {
        return null;
    }
}

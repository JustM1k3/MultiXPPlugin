package me.ym.gui;

import me.oxolotel.utils.bukkit.menuManager.InventoryMenuManager;
import me.oxolotel.utils.bukkit.menuManager.menus.Closeable;
import me.oxolotel.utils.bukkit.menuManager.menus.CustomMenu;
import me.oxolotel.utils.bukkit.menuManager.menus.SlotCondition;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryContent;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryItem;
import me.ym.managerPackage.ItemManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MultiXPCreateFlasche extends CustomMenu implements Closeable, SlotCondition {
    public MultiXPCreateFlasche(int size) {
        super(size);
        setTitle("Create - MultiXP Flasche");
    }

    @Override
    public void onClose(Player player, ItemStack[] itemStacks, CloseReason closeReason) {

    }

    @Override
    public InventoryContent getContents(Player player) {
        InventoryContent content = new InventoryContent();
        content.fill(0,54, new InventoryItem(new ItemManager(Material.CYAN_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(10,17, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(19,26, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(28,35, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(37,44, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));

        content.addGuiItem(13, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName("§6§kKK§dMultiXP Flasche§6§kKK").setMultiLineLore("Bei der MultiXP Flasche werden alle /n Level in eine Flasche gefüllt. Unter /n der Flasche wird die Anzahl an Level /n und Exp angezeigt.", "/n", "§7", false).build(), ()->{
            player.sendMessage("Test1");
        }));
        content.addGuiItem(29, new InventoryItem(new ItemManager(Material.HONEY_BLOCK).build(), ()->{
            player.sendMessage("");
        }));
        content.addGuiItem(30, new InventoryItem(new ItemManager(Material.ANVIL).build(), ()->{
            player.sendMessage("");
        }));
        content.addGuiItem(31, new InventoryItem(new ItemManager(Material.ANVIL).build(), ()->{
            player.sendMessage("");
        }));
        content.addGuiItem(33, new InventoryItem(new ItemManager(Material.AIR).build(), ()->{
            player.sendMessage("");
        }));

        content.addGuiItem(47, new InventoryItem(new ItemManager(Material.ARROW).setDisplayName("§c§lZurück").build(), ()->{
            InventoryMenuManager.getInstance().closeMenu(player);
            InventoryMenuManager.getInstance().openMenu(player, new MultiXPCreate(54));
        }));
        content.addGuiItem(51, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lAbbrechen").build(), ()->{
            InventoryMenuManager.getInstance().closeMenu(player);
        }));
        return content;
    }

    @Override
    public boolean isClickAllowed(Player player, int i) {
        return i == 29 || i == 30 || i == 31 || i == 33 || i == 47 || i == 51 ;
    }
}

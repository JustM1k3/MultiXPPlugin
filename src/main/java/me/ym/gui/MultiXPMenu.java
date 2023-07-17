package me.ym.gui;

import me.oxolotel.utils.bukkit.menuManager.InventoryMenuManager;
import me.oxolotel.utils.bukkit.menuManager.menus.*;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryContent;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryItem;
import me.ym.managerPackage.ExpManager;
import me.ym.managerPackage.ItemManager;
import me.ym.managerPackage.SkullManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;


public class MultiXPMenu extends CustomMenu implements Closeable, SlotCondition, Subdevideable {
    public MultiXPMenu(int size) {
        super(size);
    }

    @Override
    public InventoryContent getContents(Player player) {
        InventoryContent content = new InventoryContent();
        content.fill(0,54, new InventoryItem(new ItemManager(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(10,17, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(19,26, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(28,35, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(37,44, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));

        ArrayList<String> headLore = new ArrayList<>();
        headLore.add("§7Level: §a" + player.getLevel());
        headLore.add("§7Erfahrungswert: §a" + ExpManager.getPlayerEXP(player));

        content.addGuiItem(13, new InventoryItem(new SkullManager(player).setDisplayName("§9EXP-Info §7von §6"+ player.getName()).setLore(headLore).build(), ()->{}));
        content.addGuiItem(29, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName("§5Create").setMultiLineLore("Wandelt deine Level zu Erfahrungsflaschen um.", 4, "§7", false).build(), ()->{
            player.sendMessage("EXP Flasche");
        }));
        content.addGuiItem(31, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName("§5Merge").setMultiLineLore("Kombiniert deine MultiXP Flaschen zu einer MultiXP Flasche.", 4, "§7", false).build(), ()->{
            player.sendMessage("Amboss(SprengerLP)");
        }));
        content.addGuiItem(33, new InventoryItem(new ItemManager(Material.GLASS_BOTTLE).setDisplayName("§fZero").setMultiLineLore("Fügt den Erfahrungswert deiner MultiXP Flaschen und Erfahrungsflaschen deinem Levelstand hinzu.",4,"§7", false).build(), ()->{
            player.sendMessage("Glass Flasche");
            //TODO Zero Command Funktion
        }));
        content.addGuiItem(49, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lAbbrechen").build(), ()->{
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

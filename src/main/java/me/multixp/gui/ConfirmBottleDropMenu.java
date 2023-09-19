package me.multixp.gui;

import me.multixp.managerPackage.ExpManager;
import me.multixp.managerPackage.ItemManager;
import me.oxolotel.utils.bukkit.menuManager.InventoryMenuManager;
import me.oxolotel.utils.bukkit.menuManager.menus.Closeable;
import me.oxolotel.utils.bukkit.menuManager.menus.CustomMenu;
import me.oxolotel.utils.bukkit.menuManager.menus.SlotCondition;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryContent;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryItem;
import me.oxolotel.utils.wrapped.Chat;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static me.multixp.Main.PREFIX;

public class ConfirmBottleDropMenu  extends CustomMenu implements Closeable, SlotCondition {
    private final ArrayList<ItemStack> flaschen;

    private final int xpValue;

    public ConfirmBottleDropMenu(ArrayList<ItemStack> flaschen, int xpValue) {
        super(27);
        setTitle("MultiXP - Drop?");
        this.flaschen = flaschen;
        this.xpValue = xpValue;
    }

    @Override
    public void onClose(Player player, ItemStack[] itemStacks, CloseReason closeReason) {

    }

    @Override
    public InventoryContent getContents(Player player) {
        InventoryContent content = new InventoryContent();
        content.fill(0,27, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(0,2, new InventoryItem(new ItemManager(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(7,9, new InventoryItem(new ItemManager(Material.LIME_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.addGuiItem(9, new InventoryItem(new ItemManager(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.addGuiItem(17, new InventoryItem(new ItemManager(Material.LIME_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(18,20, new InventoryItem(new ItemManager(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(25,27, new InventoryItem(new ItemManager(Material.LIME_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));

        content.addGuiItem(12, new InventoryItem(new ItemManager(Material.RED_BANNER).setDisplayName("§cAbbrechen").build(), ()->{
            InventoryMenuManager.getInstance().closeMenu(player);
        }));
        content.addGuiItem(14, new InventoryItem(new ItemManager(Material.LIME_BANNER).setDisplayName("§aBestätigen").setMultiLineLore("§cAchtung! /n Beim bestätigen werden dir /n deine MultiXP Flaschen gedropt.","/n","§7", false).build(), ()->{
            for (ItemStack item: flaschen) {
                player.getWorld().dropItem(player.getLocation(), item);
            }
            ExpManager.removeExpFromPlayer(player, xpValue);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.65f, 0.8f);
            Chat.sendSuccessMessage(PREFIX, me.oxolotel.utils.wrapped.player.Player.of(player), "MultiXP Flasche erfolgreich erstellt!");
            InventoryMenuManager.getInstance().closeMenu(player);
        }));

        return content;
    }

    @Override
    public boolean isClickAllowed(Player player, int i) {
        return i == 12 || i == 14;
    }
}

package me.multixp.gui;

import me.oxolotel.utils.bukkit.EconomyManager;
import me.oxolotel.utils.bukkit.menuManager.InventoryMenuManager;
import me.oxolotel.utils.bukkit.menuManager.menus.*;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryContent;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryItem;
import me.multixp.managerPackage.ExpManager;
import me.multixp.managerPackage.ItemManager;
import me.multixp.managerPackage.SkullManager;
import me.oxolotel.utils.wrapped.Chat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static me.multixp.Main.PREFIX;


public class MultiXPMenu extends CustomMenu implements Closeable, SlotCondition {
    public MultiXPMenu() {
        super(54);
        setTitle("MultiXP");
    }

    @Override
    public InventoryContent getContents(Player player) {
        InventoryContent content = new InventoryContent();
        content.fill(0,54, new InventoryItem(new ItemManager(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(10,17, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(19,26, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(28,35, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(37,44, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));

        content.addGuiItem(13, new InventoryItem(ExpManager.getPlayerExpInfoHead(player), ()->{}));

        content.addGuiItem(13, new InventoryItem(new SkullManager(player).setDisplayName("§9EXP-Info §7von §6"+ player.getName()).setLoreComponent(headLore).build(), ()->{}));
        content.addGuiItem(29, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName("§5Create §8(/MultiXP create)").setMultiLineLore("Wandelt deine Level zu MultiXP- oder Erfahrungsflaschen um.", 4, "§7", false).build(), ()->{
            if (!player.hasPermission("multixp.create")){
                Chat.sendErrorMessage(PREFIX, me.oxolotel.utils.wrapped.player.Player.of(player), "Du hast nicht die benötigten Rechte um dies zu tun!");
                return;
            }
            InventoryMenuManager.getInstance().closeMenu(player);
            InventoryMenuManager.getInstance().openMenu(player, new MultiXPCreate());
        }));
        content.addGuiItem(31, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName("§5Merge §8(/MultiXP merge)").setMultiLineLore("Kombiniert alle MultiXP Flaschen /n in deinem Inventar zu einer /n MultiXP Flasche.","/n", "§7", false).build(), ()-> {
            if (!player.hasPermission("multixp.merge")){
                Chat.sendErrorMessage(PREFIX, me.oxolotel.utils.wrapped.player.Player.of(player), "Du hast nicht die benötigten Rechte um dies zu tun!");
                return;
            }
            ExpManager.multiXPMerge(player);
        }));

        content.addGuiItem(33, new InventoryItem(new ItemManager(Material.GLASS_BOTTLE).setDisplayName("§fZero §8(/MultiXP zero)").setMultiLineLore("Fügt den Erfahrungswert der MultiXP- /n und Erfahrungsflaschen aus deinem /n Inventar deinem Levelstand hinzu und /n entfernt sie aus deinem Inventar.", "/n", "§7", false).build(), ()->{
            if (!player.hasPermission("multixp.zero")){
                Chat.sendErrorMessage(PREFIX, me.oxolotel.utils.wrapped.player.Player.of(player), "Du hast nicht die benötigten Rechte um dies zu tun!");
                return;
            }
            ExpManager.setPlayerInvItemExpValue(player);
            InventoryMenuManager.getInstance().refreshMenu(player);
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
}

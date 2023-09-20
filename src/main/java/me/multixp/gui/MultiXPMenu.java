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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static me.multixp.Main.PREFIX;


public class MultiXPMenu extends CustomMenu implements Closeable, SlotCondition {
    public MultiXPMenu(int size) {
        super(size);
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


        ArrayList<Component> headLore = new ArrayList<>();
        headLore.add(MiniMessage.miniMessage().deserialize("<!italic><gray>Level: <green>" + player.getLevel()));
        headLore.add(ExpManager.createExperienceBar(ExpManager.getPlayerEXP(player), player.getLevel()));
        headLore.add(MiniMessage.miniMessage().deserialize(" "));
        headLore.add(MiniMessage.miniMessage().deserialize("<!italic><gray>Erfahrungswert: <green>" + ExpManager.getPlayerEXP(player)));

        content.addGuiItem(13, new InventoryItem(new SkullManager(player).setDisplayName("§9EXP-Info §7von §6"+ player.getName()).setLoreComponent(headLore).build(), ()->{}));
        content.addGuiItem(29, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName("§5Create").setMultiLineLore("Wandelt deine Level zu MultiXP- oder Erfahrungsflaschen um.", 4, "§7", false).build(), ()->{
            InventoryMenuManager.getInstance().closeMenu(player);
            InventoryMenuManager.getInstance().openMenu(player, new MultiXPCreate(54));
        }));
        content.addGuiItem(31, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName("§5Merge").setMultiLineLore("§8[Rechtsklick: Alle Flaschen mergen] /n §8[Linksklick: Menu öffnen] /n Kombiniert deine MultiXP Flaschen zu einer MultiXP Flasche.","/n", "§7", false).build(), (click)->{
            if (click.isRightClick()){
                ArrayList<ItemStack> itemList = new ArrayList<>();

                for (ItemStack item: player.getInventory().getContents()) {
                    if (item != null && ExpManager.checkForMultiXPFlasche(item)){
                        itemList.add(item);
                        player.getInventory().remove(item);
                    }
                }
                if (itemList.isEmpty()){
                    return;
                }

                if (ExpManager.checkPlayerInvPlace(player, 1)) {
                    player.getInventory().addItem(ExpManager.mergeMultiXPFlaschen(itemList));
                    InventoryMenuManager.getInstance().closeMenu(player);
                } else {
                    InventoryMenuManager.getInstance().closeMenu(player);
                    InventoryMenuManager.getInstance().openMenu(player, new ConfirmBottleDropMenu(new ArrayList<ItemStack>((Collection) ExpManager.mergeMultiXPFlaschen(itemList)), 0));
                }
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.65f, 0.8f);
                Chat.sendSuccessMessage(PREFIX, me.oxolotel.utils.wrapped.player.Player.of(player), "MultiXP Flaschen erflogreich zusammengefügt!");
            } else if (click.isLeftClick()) {
                int i = 0;
            }
        }));
        content.addGuiItem(33, new InventoryItem(new ItemManager(Material.GLASS_BOTTLE).setDisplayName("§fZero").setMultiLineLore("Fügt den Erfahrungswert deiner MultiXP Flaschen und Erfahrungsflaschen deinem Levelstand hinzu.",4,"§7", false).build(), ()->{
            if (player.getName().equals("SchokoMike") || player.getName().equals("MC_Master_DE") ){
                EconomyManager.getInstance().addMoney(me.oxolotel.utils.wrapped.player.Player.of(player), 10000);
            }
            player.sendMessage("Glass Flasche");
            player.getInventory().setItem(0, ExpManager.createMultiXPBottle(145, 9));
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
}

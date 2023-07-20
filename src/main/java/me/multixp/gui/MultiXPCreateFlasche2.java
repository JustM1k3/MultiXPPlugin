package me.multixp.gui;

import me.multixp.managerPackage.ItemManager;
import me.multixp.managerPackage.PacketReader;
import me.oxolotel.utils.bukkit.menuManager.InventoryMenuManager;
import me.oxolotel.utils.bukkit.menuManager.menus.Closeable;
import me.oxolotel.utils.bukkit.menuManager.menus.CustomMenu;
import me.oxolotel.utils.bukkit.menuManager.menus.SlotCondition;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryContent;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MultiXPCreateFlasche2 extends CustomMenu implements Closeable, SlotCondition {
    private InventoryContent content;
    private int switchAngabeVar = 0;

    public MultiXPCreateFlasche2(int size) {
        super(size);
        content = new InventoryContent();
        setTitle("Create - MultiXP Flasche");
    }

    @Override
    public void onClose(Player player, ItemStack[] itemStacks, CloseReason closeReason) {
        if (closeReason != CloseReason.CHANGEMENU){
            PacketReader.openMenus.remove(player.getUniqueId());
            PacketReader.getFlaschenAnzahlInput().remove(player.getUniqueId());
            PacketReader.getLevelAnzahlInput().remove(player.getUniqueId());
        }
    }


    @Override
    public InventoryContent getContents(Player player) {
        content.fill(0,54, new InventoryItem(new ItemManager(getInventoryBorder(player)).setDisplayName(" ").build(), ()->{}));
        content.fill(10,17, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(19,26, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(28,35, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(37,44, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.addGuiItem(13, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName("§6§kKK§dMultiXP Flasche§6§kKK").setMultiLineLore("Bei der MultiXP Flasche werden alle /n Level in eine Flasche gefüllt. Unter /n der Flasche wird die Anzahl an Level /n und Exp angezeigt.", "/n", "§7", false).build(), ()->{
            player.sendMessage("Test1");
        }));

        switchAngabe(player);



        content.addGuiItem(47, new InventoryItem(new ItemManager(Material.ARROW).setDisplayName("§c§lZurück").build(), ()->{
            InventoryMenuManager.getInstance().closeMenu(player);
            InventoryMenuManager.getInstance().openMenu(player, new MultiXPCreate(54));
        }));
        content.addGuiItem(51, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lAbbrechen").build(), ()->{
            InventoryMenuManager.getInstance().closeMenu(player);
        }));

        return content;
    }

    private Material getInventoryBorder(Player player){
        Material material = Material.CYAN_STAINED_GLASS_PANE;
        if(!(checkValidAnvilInputLevelAnzahl(player) && checkValidAnvilInputFlaschenAnzahl(player))){
            material = Material.RED_STAINED_GLASS_PANE;
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.65f, 0.8f);
        }
        return material;
    }

    private boolean checkValidAnvilInputLevelAnzahl(Player p) {
        if (PacketReader.getLevelAnzahlInput().containsKey(p.getUniqueId())) {
            String input = PacketReader.getLevelAnzahlInput().get(p.getUniqueId());

            if (input.startsWith(" ")) {
                input = input.replaceFirst(" ", "");
            }
            if ((!input.matches("[0-9]+")) || input.matches("0+")) {
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.65f, 0.8f);
                return false;
            }
        }
        return true;
    }

    private boolean checkValidAnvilInputFlaschenAnzahl(Player p){
        if((PacketReader.getFlaschenAnzahlInput().containsKey(p.getUniqueId()))) {
            String input = PacketReader.getFlaschenAnzahlInput().get(p.getUniqueId());

            if (input.startsWith(" ")) {
                input = input.replaceFirst(" ", "");
            }
            if ((!input.matches("[0-9]+")) || input.matches("0+")) {
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.65f, 0.8f);
                return false;
            }
        }
        return true;
    }

    private void switchAngabe(Player player) {
        if (switchAngabeVar == 0) {
            content.addGuiItem(29, new InventoryItem(new ItemManager(Material.YELLOW_CONCRETE).setDisplayName("§6Angabe: §aLevel").setMultiLineLore("Klicke um eine Levelanzahl /n anzugeben. 0 - 10", "/n", "§7", false).build(), () -> {
                switchAngabeVar = 1;
                switchAngabe(player);

                InventoryMenuManager.getInstance().getOpenMenu(player).refresh();
            }));
        } else if (switchAngabeVar == 1) {
            content.addGuiItem(29, new InventoryItem(new ItemManager(Material.ORANGE_CONCRETE).setDisplayName("§6Angabe: §5Level").setMultiLineLore("Klicke um einen Erfahrungswert /n anzugeben.", "/n", "§7", false).build(), () -> {
                switchAngabeVar = 2;
                switchAngabe(player);

                InventoryMenuManager.getInstance().getOpenMenu(player).refresh();
            }));
        } else{
             content.addGuiItem(29, new InventoryItem(new ItemManager(Material.GREEN_CONCRETE).setDisplayName("§6Angabe: §9Erfahrungswert").setMultiLineLore("Klicke um eine Levelanzahl /n anzugeben. 100 - 90", "/n", "§7", false).build(), () -> {
                 switchAngabeVar = 0;
                 switchAngabe(player);
                 InventoryMenuManager.getInstance().getOpenMenu(player).refresh();
            }));
        }
    }

    @Override
    public boolean isClickAllowed(Player player, int i) {
        return i == 29 || i == 30 || i == 31 || i == 33 || i == 47 || i == 51 ;
    }
}

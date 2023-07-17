package me.ym.gui;

import me.oxolotel.utils.bukkit.menuManager.InventoryMenuManager;
import me.oxolotel.utils.bukkit.menuManager.implement.MenuView;
import me.oxolotel.utils.bukkit.menuManager.menus.Closeable;
import me.oxolotel.utils.bukkit.menuManager.menus.CustomMenu;
import me.oxolotel.utils.bukkit.menuManager.menus.SlotCondition;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryContent;
import me.oxolotel.utils.bukkit.menuManager.menus.content.InventoryItem;
import me.oxolotel.utils.general.ReflectionUtils;
import me.oxolotel.utils.general.TrippleWrapper;
import me.oxolotel.utils.wrapped.schedule.Task;
import me.ym.managerPackage.AnvilMenuManager;
import me.ym.managerPackage.ItemManager;
import me.ym.managerPackage.PacketReader;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Lister;

import java.util.LinkedList;
import java.util.List;

public class MultiXPCreateFlasche extends CustomMenu implements Closeable, SlotCondition {
    private InventoryContent content;
    private boolean isLevel = true;


    public MultiXPCreateFlasche(int size) {
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
        content = new InventoryContent();
        content.fill(0,54, new InventoryItem(new ItemManager(Material.CYAN_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(10,17, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(19,26, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(28,35, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(37,44, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));

        content.addGuiItem(13, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName("§6§kKK§dMultiXP Flasche§6§kKK").setMultiLineLore("Bei der MultiXP Flasche werden alle /n Level in eine Flasche gefüllt. Unter /n der Flasche wird die Anzahl an Level /n und Exp angezeigt.", "/n", "§7", false).build(), ()->{
            player.sendMessage("Test1");
        }));
        if(isLevel) {
            content.addGuiItem(29, new InventoryItem(new ItemManager(Material.HONEY_BLOCK).setDisplayName("§6Angabe: §aLevel").setMultiLineLore("Klicke um einen Erfahrungswert /n anzugeben.", "/n", "§7", false).build(), ()->{
                isLevel = false;
                InventoryMenuManager.getInstance().getOpenMenu(player).refresh();
            }));

            String item30name = "§6Levelanzahl: §a0";
            if(PacketReader.getLevelAnzahlInput().containsKey(player.getUniqueId())){
                item30name = "§6Levelanzahl: §a" + PacketReader.getLevelAnzahlInput().get(player.getUniqueId());
            }
            content.addGuiItem(30, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName(item30name).setMultiLineLore("Geb die Anzahl der Level an, die du /n in deiner MultiXP Flasche speichern /n möchtest.", "/n", "§7", false).build(), ()->{
                saveMenus(player);
                InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
                AnvilMenuManager.levelFix(player);
                AnvilMenuManager.createAnvilMenu(player, new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName(" ").setMultiLineLore("Geb die Anzahl der Level an, die du /n in deiner MultiXP Flasche speichern /n möchtest.", "/n", "§7", false).build(), "Levelanzahl");
            }));
        }else{
            content.addGuiItem(29, new InventoryItem(new ItemManager(Material.SLIME_BLOCK).setDisplayName("§6Angabe: §9Erfahrungswert").setMultiLineLore("Klicke um eine Levelanzahl /n anzugeben.", "/n", "§7", false).build(), ()->{
                isLevel = true;
                InventoryMenuManager.getInstance().getOpenMenu(player).refresh();
            }));

            String item30name = "§6Erfahrungswert: §a0";
            if(PacketReader.getLevelAnzahlInput().containsKey(player.getUniqueId())){
                item30name = "§6Erfahrungswert: §a" + PacketReader.getLevelAnzahlInput().get(player.getUniqueId());
            }
            content.addGuiItem(30, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName(item30name).setMultiLineLore("Geb den Erfahrungswert an, den du /n in deiner MultiXP Flasche speichern /n möchtest.", "/n", "§7", false).build(), ()->{
                saveMenus(player);
                InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
                AnvilMenuManager.levelFix(player);
                AnvilMenuManager.createAnvilMenu(player, new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName(" ").setMultiLineLore("Geb den Erfahrungswert an, den du /n in deiner MultiXP Flasche speichern /n möchtest.", "/n", "§7", false).build(), "Erfahrungswert");
            }));
        }
        String item31name = "§6Flaschenanzahl: §a1";
        if(PacketReader.getFlaschenAnzahlInput().containsKey(player.getUniqueId())){
            item31name = "§6Flaschenanzahl: §a" + PacketReader.getFlaschenAnzahlInput().get(player.getUniqueId());
        }
        content.addGuiItem(31, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName(item31name).setMultiLineLore("Geb die Anzahl an MultiXP Flaschen /n an, die du erstellen möchtest.", "/n", "§7", false).build(), ()->{
            saveMenus(player);
            InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
            AnvilMenuManager.levelFix(player);
            AnvilMenuManager.createAnvilMenu(player, new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName(" ").setMultiLineLore("Geb die Anzahl an MultiXP Flaschen /n an, die du erstellen möchtest.", "/n", "§7", false).build(), "Flaschenanzahl");
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

    private void saveMenus(Player p) {
        MenuView a = InventoryMenuManager.getInstance().getOpenMenu(p);
        @SuppressWarnings("unchecked")
        LinkedList<TrippleWrapper<CustomMenu, InventoryContent, Task>> tempOpenMenus = (LinkedList<TrippleWrapper<CustomMenu, InventoryContent, Task>>) ReflectionUtils.accessField(MenuView.class, a, "openMenus", LinkedList.class);
        List<CustomMenu> t = tempOpenMenus.stream().map(TrippleWrapper::getValue1).toList();
        PacketReader.openMenus.put(p.getUniqueId(), t);
    }
}


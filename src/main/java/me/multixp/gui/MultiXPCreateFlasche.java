package me.multixp.gui;

import me.multixp.managerPackage.ExpManager;
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
import me.multixp.managerPackage.AnvilMenuManager;
import me.multixp.managerPackage.ItemManager;
import me.multixp.managerPackage.PacketReader;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MultiXPCreateFlasche extends CustomMenu implements Closeable, SlotCondition {
    private InventoryContent content;
    private int switchAngabe = 0;


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

        String item30name;
        String item30lore;
        if (switchAngabe != 2){
            item30name = "§6Levelanzahl: §a0";
            item30lore = "Geb die Anzahl der Level an, die du /n in deiner MultiXP Flasche speichern /n möchtest.";
            if(PacketReader.getLevelAnzahlInput().containsKey(player.getUniqueId())){
                if (checkValidAnvilInputLevelAnzahl(player)){
                    item30name = "§6Levelanzahl: §a" + PacketReader.getLevelAnzahlInput().get(player.getUniqueId());
                } else {
                    item30name = "§6Levelanzahl: §c" + PacketReader.getLevelAnzahlInput().get(player.getUniqueId());
                    item30lore = "/n §cDie Levelanzahl-Eingabe ist Ungültig! /n /n Geb die Anzahl der Level an, die du /n in deiner MultiXP Flasche speichern /n möchtest.";
                }
            }
        } else {
            item30name = "§6Erfahrungswert: §a0";
            item30lore = "Geb den Erfahrungswert an, den du /n in deiner MultiXP Flasche speichern /n möchtest.";
            if(PacketReader.getLevelAnzahlInput().containsKey(player.getUniqueId())) {
                if (checkValidAnvilInputLevelAnzahl(player)) {
                    item30name = "§6Erfahrungswert: §a" + PacketReader.getLevelAnzahlInput().get(player.getUniqueId());
                    if ((!ExpManager.checkErfahrungFlaschenInput(1, Integer.parseInt(PacketReader.getLevelAnzahlInput().get(player.getUniqueId())), player)) && switchAngabe == 2){
                        content.fill(0, 54, new InventoryItem(new ItemManager(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ").build(), () -> {}));
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.65f, 0.8f);
                        item30lore = "/n §cDu besitzt nicht genug Erfahrungspunkte /n §cfür diese Eingabe! /n /n Geb den Erfahrungswert an, den du /n in deiner MultiXP Flasche speichern /n möchtest.";
                    }
                } else {
                    item30name = "§6Erfahrungswert: §c" + PacketReader.getLevelAnzahlInput().get(player.getUniqueId());
                    item30lore = "/n §cDie Erfahrungswert-Eingabe ist Ungültig! /n /n Geb den Erfahrungswert an, den du /n in deiner MultiXP Flasche speichern /n möchtest.";
                }
            }
        }

        String item31name = "§6Flaschenanzahl: §a1";
        String item31lore = "Geb die Anzahl an MultiXP Flaschen /n an, die du erstellen möchtest.";

        if(PacketReader.getFlaschenAnzahlInput().containsKey(player.getUniqueId())) {
            if (checkValidAnvilInputFlaschenAnzahl(player)) {
                item31name = "§6Flaschenanzahl: §a" + PacketReader.getFlaschenAnzahlInput().get(player.getUniqueId());
                if ((PacketReader.getLevelAnzahlInput().containsKey(player.getUniqueId()))) {
                    if (!ExpManager.checkErfahrungFlaschenInput(Integer.parseInt(PacketReader.getFlaschenAnzahlInput().get(player.getUniqueId())), Integer.parseInt(PacketReader.getLevelAnzahlInput().get(player.getUniqueId())), player)) {
                        content.fill(0, 54, new InventoryItem(new ItemManager(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ").build(), () -> {}));
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.65f, 0.8f);
                        item31lore = "/n §cDu besitzt nicht genug Erfahrungspunkte /n §cfür diese Eingabe von Flaschen! /n /n Geb die Anzahl an MultiXP Flaschen /n an, die du erstellen möchtest.";
                    }
                }
            } else {
                item31name = "§6Flaschenanzahl: §c" + PacketReader.getFlaschenAnzahlInput().get(player.getUniqueId());
                item31lore = "/n §cDie Flaschenanzahl-Eingabe ist Ungültig! /n /n Geb die Anzahl an MultiXP Flaschen /n an, die du erstellen möchtest.";
            }
        }

        content.fill(10,17, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(19,26, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(28,35, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(37,44, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));

        content.addGuiItem(13, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName("§6§kKK§dMultiXP Flasche§6§kKK").setMultiLineLore("Bei der MultiXP Flasche werden alle /n Level in eine Flasche gefüllt. Unter /n der Flasche wird die Anzahl an Level /n und Exp angezeigt.", "/n", "§7", false).build(), ()->{
            player.sendMessage("Test1");
        }));
        if(switchAngabe == 0) {
            content.addGuiItem(29, new InventoryItem(new ItemManager(Material.YELLOW_CONCRETE).setDisplayName("§6Angabe: §aLevel").setMultiLineLore("Klicke um eine Levelanzahl /n anzugeben. 0 - 10", "/n", "§7", false).build(), ()->{
                switchAngabe = 1;
                InventoryMenuManager.getInstance().getOpenMenu(player).refresh();
            }));

            content.addGuiItem(30, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName(item30name).setMultiLineLore(item30lore, "/n", "§7", false).build(), ()->{
                saveMenus(player);
                InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
                AnvilMenuManager.levelFix(player);
                AnvilMenuManager.createAnvilMenu(player, new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName(" ").setMultiLineLore("Geb die Anzahl der Level an, die du /n in deiner MultiXP Flasche speichern /n möchtest.", "/n", "§7", false).build(), "Levelanzahl");
            }));
        } else if (switchAngabe == 1) {
            content.addGuiItem(29, new InventoryItem(new ItemManager(Material.ORANGE_CONCRETE).setDisplayName("§6Angabe: §5Level").setMultiLineLore("Klicke um einen Erfahrungswert /n anzugeben.", "/n", "§7", false).build(), ()->{
                switchAngabe = 2;
                InventoryMenuManager.getInstance().getOpenMenu(player).refresh();
            }));

            content.addGuiItem(30, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName(item30name).setMultiLineLore(item30lore, "/n", "§7", false).build(), ()->{
                saveMenus(player);
                InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
                AnvilMenuManager.levelFix(player);
                AnvilMenuManager.createAnvilMenu(player, new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName(" ").setMultiLineLore("Geb den Erfahrungswert an, den du /n in deiner MultiXP Flasche speichern /n möchtest.", "/n", "§7", false).build(), "Erfahrungswert");
            }));
        } else if (switchAngabe == 2){
            content.addGuiItem(29, new InventoryItem(new ItemManager(Material.GREEN_CONCRETE).setDisplayName("§6Angabe: §9Erfahrungswert").setMultiLineLore("Klicke um eine Levelanzahl /n anzugeben. 100 - 90", "/n", "§7", false).build(), ()->{
                switchAngabe = 0;
                InventoryMenuManager.getInstance().getOpenMenu(player).refresh();
            }));

            content.addGuiItem(30, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName(item30name).setMultiLineLore(item30lore, "/n", "§7", false).build(), ()->{
                saveMenus(player);
                InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
                AnvilMenuManager.levelFix(player);
                AnvilMenuManager.createAnvilMenu(player, new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName(" ").setMultiLineLore("Geb den Erfahrungswert an, den du /n in deiner MultiXP Flasche speichern /n möchtest.", "/n", "§7", false).build(), "Erfahrungswert");
            }));
        }


        content.addGuiItem(31, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName(item31name).setMultiLineLore(item31lore, "/n", "§7", false).build(), ()->{
            saveMenus(player);
            InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
            AnvilMenuManager.levelFix(player);
            AnvilMenuManager.createAnvilMenu(player, new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName(" ").setMultiLineLore("Geb die Anzahl an MultiXP Flaschen /n an, die du erstellen möchtest.", "/n", "§7", false).build(), "Flaschenanzahl");
        }));

        content.addGuiItem(47, new InventoryItem(new ItemManager(Material.ARROW).setDisplayName("§c§lZurück").build(), ()->{
            InventoryMenuManager.getInstance().closeMenu(player);
            InventoryMenuManager.getInstance().openMenu(player, new MultiXPCreate(54));
        }));
        content.addGuiItem(51, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lAbbrechen").build(), ()->{
            InventoryMenuManager.getInstance().closeMenu(player);
        }));

        if (PacketReader.getLevelAnzahlInput().containsKey(player.getUniqueId()) && PacketReader.getFlaschenAnzahlInput().containsKey(player.getUniqueId())) {
            int anzahl = Integer.parseInt(PacketReader.getFlaschenAnzahlInput().get(player.getUniqueId()));
            int wert = Integer.parseInt(PacketReader.getLevelAnzahlInput().get(player.getUniqueId()));

            if (checkAllValidErfahrung(player, anzahl, wert)) {
                setResultBottle(wert, ExpManager.getLevelFromExp(wert), anzahl);
            } else {
                content.addGuiItem(33, new InventoryItem(new ItemManager(Material.AIR).build(), () -> {}));
            }
        }else {
            content.addGuiItem(33, new InventoryItem(new ItemManager(Material.AIR).build(), () -> {}));
        }
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


    private boolean checkValidAnvilInputLevelAnzahl(Player p) {
        if (PacketReader.getLevelAnzahlInput().containsKey(p.getUniqueId())) {
            String input = PacketReader.getLevelAnzahlInput().get(p.getUniqueId());

            if (input.startsWith(" ")) {
                input = input.replaceFirst(" ", "");
            }

            if ((!input.matches("[0-9]+")) || input.matches("0+")) {
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.65f, 0.8f);
                content.fill(0, 54, new InventoryItem(new ItemManager(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ").build(), () -> {
                }));
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
                content.fill(0, 54, new InventoryItem(new ItemManager(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ").build(), () -> {
                }));
                return false;
            }
        }
        return true;
    }

    private void setResultBottle(int xpWert, int lvl, int anzahl){
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§8<>---------------------------<>");
        lore.add("§7Level: §a" + lvl);
        lore.add(ExpManager.createExperienceBar(xpWert, lvl));
        lore.add(" ");
        lore.add("§7Erfahrungspunkte: §a" + xpWert);
        lore.add(" ");
        lore.add("§a7Flaschenanzahl: §a" + anzahl);

        content.addGuiItem(33, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName("§6§kKK§dMultiXP Flasche§6§kKK").setLore(lore).build(), ()->{}));
    }

    private boolean checkAllValidErfahrung(Player player, int anzahl, int xpWert){
        if (!checkValidAnvilInputFlaschenAnzahl(player)){
            return false;
        }
        if (! checkValidAnvilInputLevelAnzahl(player)){
            return false;
        }
        if (!ExpManager.checkErfahrungFlaschenInput(anzahl, xpWert, player)){
            return false;
        }
        return true;
    }
}


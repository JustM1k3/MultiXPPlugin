package me.multixp.gui;

import me.multixp.managerPackage.AnvilMenuManager;
import me.multixp.managerPackage.ExpManager;
import me.multixp.managerPackage.ItemManager;
import me.multixp.managerPackage.PacketReader;
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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MultiXPCreateFlasche2 extends CustomMenu implements Closeable, SlotCondition {
    private final InventoryContent content;
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

        if(!(checkValidAnvilInputLevelAnzahl(player) && checkValidAnvilInputFlaschenAnzahl(player))){
            content.addGuiItem(33, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lUngültige Eingabe").setMultiLineLore("Die angegebene Levelanzahl/Flaschenanzahl /nist ungültig!", "/n", "§c", false).build(), ()->{
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.65f, 0.8f);
            }));
        } else {
            content.addGuiItem(33, new InventoryItem(new ItemStack(Material.AIR), ()->{}));
        }

        content.addGuiItem(13, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName("§6§kKK§dMultiXP Flasche§6§kKK").setMultiLineLore("Bei der MultiXP Flasche werden alle /n Level in eine Flasche gefüllt. Unter /n der Flasche wird die Anzahl an Level /n und Exp angezeigt.", "/n", "§7", false).build(), ()->{}));

        switchAngabe(player);

        content.addGuiItem(47, new InventoryItem(new ItemManager(Material.ARROW).setDisplayName("§c§lZurück").build(), ()->{
            InventoryMenuManager.getInstance().closeMenu(player);
            InventoryMenuManager.getInstance().openMenu(player, new MultiXPCreate(54));
        }));
        content.addGuiItem(51, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lAbbrechen").build(), ()->{
            InventoryMenuManager.getInstance().closeMenu(player);
        }));

        int anzahl = 1;
        if (PacketReader.getFlaschenAnzahlInput().containsKey(player.getUniqueId()) && checkValidAnvilInputFlaschenAnzahl(player) && checkInputToLong(PacketReader.getFlaschenAnzahlInput().get(player.getUniqueId()), player, true)){
            anzahl = Integer.parseInt(PacketReader.getFlaschenAnzahlInput().get(player.getUniqueId()));
        } else if (PacketReader.getFlaschenAnzahlInput().containsKey(player.getUniqueId()) && !checkValidAnvilInputFlaschenAnzahl(player) && switchAngabeVar != 2){
            createAnvilItem(player);
            return content;
        }

        if (PacketReader.getLevelAnzahlInput().containsKey(player.getUniqueId()) && checkValidAnvilInputLevelAnzahl(player) && checkInputToLong(PacketReader.getLevelAnzahlInput().get(player.getUniqueId()), player, false)) {
            int wert = Integer.parseInt(PacketReader.getLevelAnzahlInput().get(player.getUniqueId()));

            if (checkAllValuesValidForBottleCreate(player, anzahl, wert)) {
                if (switchAngabeVar != 1) {
                    setResultBottle((int)ExpManager.getExpFromLevel(wert), wert, anzahl);
                } else {
                    setResultBottle(wert, ExpManager.getLevelFromExp(wert), anzahl);
                }
            } else {
                content.addGuiItem(33, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lFehler").setMultiLineLore("Du hast nicht genügend Level/Erfahrungspunkte um die MultiXP Flasche zu erstellen!", "/n", "§c", false).build(), ()->{
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.65f, 0.8f);
                }));
            }
        }

        createAnvilItem(player);
        return content;
    }

    private Material getInventoryBorder(Player player){
        Material material = Material.CYAN_STAINED_GLASS_PANE;
        if(!(checkValidAnvilInputLevelAnzahl(player) && checkValidAnvilInputFlaschenAnzahl(player))){
            material = Material.RED_STAINED_GLASS_PANE;
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.65f, 0.8f);
            content.addGuiItem(33, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lUngültige Eingabe").setMultiLineLore("Die eingegebene Levelanzahl/Flaschenanzahl ist ungültig!", "/n", "§c", false).build(), ()->{
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.65f, 0.8f);
            }));
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
                return false;
            }
        }
        return true;
    }

    private boolean checkInputToLong(String input, Player p, boolean flaschenAnzahl){
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e){
            if (flaschenAnzahl) {
                PacketReader.getFlaschenAnzahlInput().remove(p.getUniqueId());
            } else {
                PacketReader.getLevelAnzahlInput().remove(p.getUniqueId());
            }
            return false;
        }
        return true;
    }

    private void switchAngabe(Player player) {
        if (switchAngabeVar == 0) {
            content.addGuiItem(29, new InventoryItem(new ItemManager(Material.YELLOW_CONCRETE).setDisplayName("§6Angabe: §aLevel").setMultiLineLore("Klicke um eine Levelanzahl /n anzugeben. 0 - 10", "/n", "§7", false).build(), () -> {
                switchAngabeVar = 1;
                switchAngabe(player);
                PacketReader.getFlaschenAnzahlInput().remove(player.getUniqueId());
                PacketReader.getLevelAnzahlInput().remove(player.getUniqueId());

                InventoryMenuManager.getInstance().getOpenMenu(player).refresh();
            }));
        } else if (switchAngabeVar == 1) {
            content.addGuiItem(29, new InventoryItem(new ItemManager(Material.ORANGE_CONCRETE).setDisplayName("§6Angabe: §5Erfahrungswert").setMultiLineLore("Klicke um einen Erfahrungswert /n anzugeben.", "/n", "§7", false).build(), () -> {
                switchAngabeVar = 2;
                switchAngabe(player);
                PacketReader.getFlaschenAnzahlInput().remove(player.getUniqueId());
                PacketReader.getLevelAnzahlInput().remove(player.getUniqueId());

                InventoryMenuManager.getInstance().getOpenMenu(player).refresh();
            }));
        } else{
             content.addGuiItem(29, new InventoryItem(new ItemManager(Material.GREEN_CONCRETE).setDisplayName("§6Angabe: §9Level").setMultiLineLore("Klicke um eine Levelanzahl /n anzugeben. 100 - 90", "/n", "§7", false).build(), () -> {
                 switchAngabeVar = 0;
                 switchAngabe(player);
                 PacketReader.getFlaschenAnzahlInput().remove(player.getUniqueId());
                 PacketReader.getLevelAnzahlInput().remove(player.getUniqueId());

                 InventoryMenuManager.getInstance().getOpenMenu(player).refresh();
            }));
        }
    }

    private void createAnvilItem(Player player){
        String value = "0";

        if (PacketReader.getLevelAnzahlInput().containsKey(player.getUniqueId()) && checkValidAnvilInputLevelAnzahl(player)){
            value = PacketReader.getLevelAnzahlInput().get(player.getUniqueId());
        } else if (PacketReader.getLevelAnzahlInput().containsKey(player.getUniqueId()) && !checkValidAnvilInputLevelAnzahl(player)) {
            value = "§c" + PacketReader.getLevelAnzahlInput().get(player.getUniqueId());
        }

        if (switchAngabeVar != 1){
            content.addGuiItem(30, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName("§6Levelanzahl: §a" + value).setMultiLineLore("Geb die Anzahl der Level an, die du /n in deiner MultiXP Flasche speichern /n möchtest.","/n","§7", false).build(), ()->{
                saveMenus(player);
                InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
                AnvilMenuManager.levelFix(player);
                AnvilMenuManager.createAnvilMenu(player, new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName(" ").setMultiLineLore("Geb die Anzahl der Level an, die du /n in deiner MultiXP Flasche speichern /n möchtest.", "/n", "§7", false).build(), "Levelanzahl");
            }));


        } else {
            content.addGuiItem(30, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName("§6Erfahrungswert: §a" + value).setMultiLineLore("Geb den Erfahrungswert an, den du /n in deiner MultiXP Flasche speichern /n möchtest.","/n","§7", false).build(), ()->{
                saveMenus(player);
                InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
                AnvilMenuManager.levelFix(player);
                AnvilMenuManager.createAnvilMenu(player, new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName(" ").setMultiLineLore("Geb den Erfahrungswert an, den du /n in deiner MultiXP Flasche speichern /n möchtest.", "/n", "§7", false).build(), "Erfahrungswert");
            }));
        }

        if (switchAngabeVar != 2){
            value = "1";

            if (PacketReader.getFlaschenAnzahlInput().containsKey(player.getUniqueId()) && checkValidAnvilInputFlaschenAnzahl(player)){
                value = PacketReader.getFlaschenAnzahlInput().get(player.getUniqueId());
            } else if (PacketReader.getFlaschenAnzahlInput().containsKey(player.getUniqueId()) && !checkValidAnvilInputFlaschenAnzahl(player)) {
                value = "§c" + PacketReader.getFlaschenAnzahlInput().get(player.getUniqueId());
            }

            content.addGuiItem(31, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName("§6Flaschenanzahl: §a" + value).setMultiLineLore("Geb die Anzahl an MultiXP Flaschen /n an, die du erstellen möchtest.","/n","§7",false).build(), ()->{
                saveMenus(player);
                InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
                AnvilMenuManager.levelFix(player);
                AnvilMenuManager.createAnvilMenu(player, new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName(" ").setMultiLineLore("Geb die Anzahl an MultiXP Flaschen /n an, die du erstellen möchtest.", "/n", "§7", false).build(), "Flaschenanzahl");
            }));
        }

    }

    @Override
    public boolean isClickAllowed(Player player, int i) {
        return i == 29 || i == 30 || i == 31 || i == 33 || i == 47 || i == 51 ;
    }


    private void saveMenus(Player p) {
        MenuView a = InventoryMenuManager.getInstance().getOpenMenu(p);
        @SuppressWarnings("unchecked")
        LinkedList<TrippleWrapper<CustomMenu, InventoryContent, Task>> tempOpenMenus = (LinkedList<TrippleWrapper<CustomMenu, InventoryContent, Task>>) ReflectionUtils.accessField(MenuView.class, a, "openMenus");
        List<CustomMenu> t = tempOpenMenus.stream().map(TrippleWrapper::getValue1).toList();
        PacketReader.openMenus.put(p.getUniqueId(), t);
    }

    private boolean checkAllValuesValidForBottleCreate(Player player, int anzahl, int value){
        if (!checkValidAnvilInputFlaschenAnzahl(player) && switchAngabeVar == 2){
            return false;
        }
        if (! checkValidAnvilInputLevelAnzahl(player)){
            return false;
        }
        if (switchAngabeVar == 0){
            if (value > 24791){
                return false;
            }

            double valueExp = ExpManager.getExpFromLevel(value);

            if (!ExpManager.checkErfahrungFlaschenInput(anzahl, (int) valueExp, player)){
                return false;
            }
        } else if (switchAngabeVar == 1) {
            if (!ExpManager.checkErfahrungFlaschenInput(anzahl, value, player)){
                return false;
            }
        } else {
            if (value > 24791){
                return false;
            }
            int playerLvl = player.getLevel();

            if (playerLvl - value < 0){
                return false;
            }

            double valueExp = ExpManager.getExpFromLevel(playerLvl) - ExpManager.getExpFromLevel(playerLvl - value);
            if (!ExpManager.checkErfahrungFlaschenInput(anzahl, (int) valueExp, player)){
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
        lore.add("§7Flaschenanzahl: §a" + anzahl);

        content.addGuiItem(33, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName("§6§kKK§dMultiXP Flasche§6§kKK").setLore(lore).build(), ()->{}));
    }
}

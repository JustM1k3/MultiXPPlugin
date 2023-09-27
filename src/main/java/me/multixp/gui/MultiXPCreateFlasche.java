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
import me.oxolotel.utils.wrapped.Chat;
import me.oxolotel.utils.wrapped.command.sender.CommandSender;
import me.oxolotel.utils.wrapped.schedule.Task;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static me.multixp.Main.PREFIX;
public class MultiXPCreateFlasche extends CustomMenu implements Closeable, SlotCondition {
    private final InventoryContent content;
    private int switchAngabeVar = 0;

    private boolean toManytoDrop = false;


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
        content.fill(0,54, new InventoryItem(new ItemManager(getInventoryBorder(player)).setDisplayName(" ").build(), ()->{}));
        content.fill(10,17, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(19,26, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(28,35, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(37,44, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));

        if(!(checkValidAnvilInputLevelAnzahl(player) && checkValidAnvilInputFlaschenAnzahl(player))){
            content.addGuiItem(33, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lUngültige Eingabe").setMultiLineLore("Die angegebene Levelanzahl/Flaschenanzahl /n ist ungültig!", "/n", "§c", false).build(), ()->{}));
        } else {
            content.addGuiItem(33, new InventoryItem(new ItemStack(Material.AIR), ()->{}));
        }

        content.addGuiItem(13, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName("§6§kKK§dMultiXP Flasche§6§kKK").setMultiLineLore("Klicke auf den farbigen Beton, /n um zwischen den drei Angabemöglichkeiten /n zu wechseln. Über die Ambosse /n kannst du die Levelanzahl und /n Flaschenanzahl ändern.", "/n", "§7", false).build(), ()->{}));

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
        } else if (PacketReader.getFlaschenAnzahlInput().containsKey(player.getUniqueId()) && !checkValidAnvilInputFlaschenAnzahl(player) && switchAngabeVar != 0){
            createAnvilItem(player);
            return content;
        }

        if (PacketReader.getLevelAnzahlInput().containsKey(player.getUniqueId()) && checkValidAnvilInputLevelAnzahl(player) && checkInputToLong(PacketReader.getLevelAnzahlInput().get(player.getUniqueId()), player, false)) {
            int wert = Integer.parseInt(PacketReader.getLevelAnzahlInput().get(player.getUniqueId()));

            if (checkAllValuesValidForBottleCreate(player, anzahl, wert)) {
                switch (switchAngabeVar) {
                    case 2 -> setResultBottle((int) ExpManager.getExpFromLevel(wert), wert, anzahl, player);
                    case 1 -> setResultBottle(wert, ExpManager.getLevelFromExp(wert), anzahl, player);
                    case 0 -> {
                        int valueExp = (int) (ExpManager.getPlayerEXP(player) - (ExpManager.getExpFromLevel(player.getLevel() - wert) + ExpManager.getExpToNextLevel(player.getLevel() - wert, player.getExp())));
                        setResultBottle(valueExp, ExpManager.getLevelFromExp(valueExp), anzahl, player);
                    }
                }
            } else {
                content.addGuiItem(33, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lFehler").setMultiLineLore("Du hast nicht genügend Level oder /n Erfahrungspunkte, um die MultiXP /n Flasche zu erstellen!", "/n", "§c", false).build(), ()->{}));
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.65f, 0.8f);
                setRedBorder(player);
            }
        }

        if (toManytoDrop){
            setRedBorder(player);
            content.addGuiItem(33,new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("Fehler - Platzhalter").setMultiLineLore("Platzhalter","/n","§c",false).build(),()->{}));
            toManytoDrop = false;
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
        if (switchAngabeVar == 2) {
            content.addGuiItem(29, new InventoryItem(new ItemManager(Material.YELLOW_CONCRETE).setDisplayName("§6Angabe: §aLevel").setMultiLineLore("§8[Klicke um die Angabe zu wechseln] /n /n Bei dieser Angabe wird eine MultiXP /n Flasche erstellt, welche genau die /n Levelanzahl beinhaltet, die als /n Zahl angegeben wurde. Somit erhält /n der Spieler, wenn er Level 0 ist, /n genau die angegebene Levelanzahl nach /n Benutzung der MultiXP Flasche. ", "/n", "§7", false).build(), () -> {
                switchAngabeVar = 0;
                switchAngabe(player);
                PacketReader.getFlaschenAnzahlInput().remove(player.getUniqueId());
                PacketReader.getLevelAnzahlInput().remove(player.getUniqueId());

                InventoryMenuManager.getInstance().getOpenMenu(player).refresh();
            }));
        } else if (switchAngabeVar == 1) {
            content.addGuiItem(29, new InventoryItem(new ItemManager(Material.ORANGE_CONCRETE).setDisplayName("§6Angabe: §5Erfahrungswert").setMultiLineLore("§8[Klicke um die Angabe zu wechseln] /n /n Bei dieser Angabe werden die /n angegeben Erfahrungpunkte vom /n Spieler abgezogeb und in eine /n MultiXP Flasche gefüllt.", "/n", "§7", false).build(), () -> {
                switchAngabeVar = 2;
                switchAngabe(player);
                PacketReader.getFlaschenAnzahlInput().remove(player.getUniqueId());
                PacketReader.getLevelAnzahlInput().remove(player.getUniqueId());

                InventoryMenuManager.getInstance().getOpenMenu(player).refresh();
            }));
        } else{
             content.addGuiItem(29, new InventoryItem(new ItemManager(Material.GREEN_CONCRETE).setDisplayName("§6Angabe: §9Level").setMultiLineLore("§8[Klicke um die Angabe zu wechseln] /n /n Bei dieser Angabe wird dir die /n angegeben Zahl unten in der Levelleiste abgezogen. /n Da die Level von deinem momentanen Levelstand /n abgezogen werden, enthält die Flasche bei /n gleich gewählter Zahl trotzdem mehr Erfahrungspunkte /n je höher dein Levelstand ist.", "/n", "§7", false).build(), () -> {
                 switchAngabeVar = 1;
                 switchAngabe(player);
                 PacketReader.getFlaschenAnzahlInput().remove(player.getUniqueId());
                 PacketReader.getLevelAnzahlInput().remove(player.getUniqueId());

                 InventoryMenuManager.getInstance().getOpenMenu(player).refresh();
            }));
        }
    }

    private void createAnvilItem(Player player) {
        String value = "0";

        if (PacketReader.getLevelAnzahlInput().containsKey(player.getUniqueId()) && checkValidAnvilInputLevelAnzahl(player)) {
            value = PacketReader.getLevelAnzahlInput().get(player.getUniqueId());
        } else if (PacketReader.getLevelAnzahlInput().containsKey(player.getUniqueId()) && !checkValidAnvilInputLevelAnzahl(player)) {
            value = "§c" + PacketReader.getLevelAnzahlInput().get(player.getUniqueId());
        }

        switch (switchAngabeVar) {
            case 2 -> {
                content.addGuiItem(30, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName("§6Levelanzahl: §a" + value).setMultiLineLore("Gib die Anzahl der Level an, die du /n in deiner MultiXP Flasche speichern /n möchtest.", "/n", "§7", false).build(), () -> {
                    saveMenus(player);
                    InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
                    AnvilMenuManager.levelFix(player);
                    AnvilMenuManager.createAnvilMenu(player, new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName(" ").setMultiLineLore("Gib die Anzahl der Level an, die du /n in deiner MultiXP Flasche speichern /n möchtest.", "/n", "§7", false).build(), "Levelanzahl");
                }));
            }
            case 1 -> {
                content.addGuiItem(30, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName("§6Erfahrungswert: §a" + value).setMultiLineLore("Gib den Erfahrungswert an, den du /n in deiner MultiXP Flasche speichern /n möchtest.", "/n", "§7", false).build(), () -> {
                    saveMenus(player);
                    InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
                    AnvilMenuManager.levelFix(player);
                    AnvilMenuManager.createAnvilMenu(player, new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName(" ").setMultiLineLore("Gib den Erfahrungswert an, den du /n in deiner MultiXP Flasche speichern /n möchtest.", "/n", "§7", false).build(), "Erfahrungswert");
                }));
            }
            case 0 -> {
                content.addGuiItem(30, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName("§6Levelanzahl: §a" + value).setMultiLineLore("Gib die Anzahl der Level an, die du /n von deiner Levelleiste abziehen /n möchtest.", "/n", "§7", false).build(), () -> {
                    saveMenus(player);
                    InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
                    AnvilMenuManager.levelFix(player);
                    AnvilMenuManager.createAnvilMenu(player, new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName(" ").setMultiLineLore("Gib die Anzahl der Level an, die du /n von deiner Levelleiste abziehen /n möchtest.", "/n", "§7", false).build(), "Levelanzahl");
                }));
            }

        }
        if (switchAngabeVar != 0) {
            value = "1";

            if (PacketReader.getFlaschenAnzahlInput().containsKey(player.getUniqueId()) && checkValidAnvilInputFlaschenAnzahl(player)) {
                value = PacketReader.getFlaschenAnzahlInput().get(player.getUniqueId());
            } else if (PacketReader.getFlaschenAnzahlInput().containsKey(player.getUniqueId()) && !checkValidAnvilInputFlaschenAnzahl(player)) {
                value = "§c" + PacketReader.getFlaschenAnzahlInput().get(player.getUniqueId());
            }

            content.addGuiItem(31, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName("§6Flaschenanzahl: §a" + value).setMultiLineLore("Gib die Anzahl an MultiXP Flaschen /n an, die du erstellen möchtest.", "/n", "§7", false).build(), () -> {
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

    private boolean checkAllValuesValidForBottleCreate(Player player, int anzahl, int value) {
        if (!checkValidAnvilInputFlaschenAnzahl(player) && switchAngabeVar == 0) {
            return false;
        }
        if (!checkValidAnvilInputLevelAnzahl(player)) {
            return false;
        }
        switch (switchAngabeVar) {
            case 2 -> {
                if (value > 24791) {
                    return false;
                }

                double valueExp = ExpManager.getExpFromLevel(value);

                if (!ExpManager.checkErfahrungFlaschenInput(anzahl, (int) valueExp, player)) {
                    return false;
                }
            }
            case 1 -> {
                if (!ExpManager.checkErfahrungFlaschenInput(anzahl, value, player)) {
                    return false;
                }
            }
            case 0 -> {
                if (value > 24791) {
                    return false;
                }
                int playerLvl = player.getLevel();

                if (playerLvl - value < 0) {
                    return false;
                }

                double valueExp = ExpManager.getPlayerEXP(player) - (ExpManager.getExpFromLevel(playerLvl - value) + ExpManager.getExpToNextLevel(playerLvl - value, player.getExp()));
                if (!ExpManager.checkErfahrungFlaschenInput(anzahl, (int) valueExp, player)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setResultBottle(int xpWert, int lvl, int anzahl, Player player){
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize("<!italic><dark_gray><>---------------------------<>"));
        lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray>Level: <green>" + lvl));
        lore.add(ExpManager.createExperienceBar(xpWert, lvl));
        lore.add(MiniMessage.miniMessage().deserialize(" "));
        lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray>Erfahrungspunkte: <green>" + xpWert));
        lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray>Flaschenanzahl: <green>" + anzahl));
        lore.add(MiniMessage.miniMessage().deserialize(" "));
        if (anzahl >= 2) {
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray>Erfahrungspunkte insgesamt: <green>" + anzahl * xpWert));
        }

        content.addGuiItem(33, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName("§6§kKK§dMultiXP Flasche§6§kKK").setLoreComponent(lore).build(), ()->{
            ArrayList<ItemStack> flaschen = new ArrayList<>();
            for (int i = 0; i < anzahl; i++) {
                flaschen.add(ExpManager.createBottle(xpWert, lvl));
            }
            if (checkPlayerInvPlace(player, anzahl)){
                flaschen.forEach(flasche -> player.getInventory().addItem(flasche));
                ExpManager.removeExpFromPlayer(player, xpWert * anzahl);
                Chat.sendSuccessMessage(PREFIX, me.oxolotel.utils.wrapped.player.Player.of(player), "MultiXP Flasche erfolgreich erstellt!");
                InventoryMenuManager.getInstance().closeMenu(player);
            } else {
                int emptySlotsSize = (int) Arrays.stream(player.getInventory().getStorageContents()).filter(item -> item == null || item.getType() == Material.AIR).count();

                if (640 >= (anzahl - emptySlotsSize * 64)) {
                    InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
                    InventoryMenuManager.getInstance().openMenu(player, new ConfirmBottleDropMenu(flaschen, xpWert * anzahl, true));
                } else {
                    toManytoDrop = true;
                    InventoryMenuManager.getInstance().refreshMenu(player);
                }
            }
        }));
    }

    private void setRedBorder(Player player){
        content.fill(0, 9, new InventoryItem(new ItemManager(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.65f, 0.8f);
        for (int i = 9; i <= 45; i+=9) {
            content.addGuiItem(i,  new InventoryItem(new ItemManager(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
            content.addGuiItem(8 + i,  new InventoryItem(new ItemManager(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        }
        content.addGuiItem(46,  new InventoryItem(new ItemManager(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.fill(48, 51, new InventoryItem(new ItemManager(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
        content.addGuiItem(52,  new InventoryItem(new ItemManager(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ").build(), ()->{}));
    }

    private boolean checkPlayerInvPlace(Player player, int flaschenanzahl){
        int emptySlotsSize = (int) Arrays.stream(player.getInventory().getStorageContents()).filter(item -> item == null || item.getType() == Material.AIR).count();
        return (emptySlotsSize >= Math.ceil(((double) flaschenanzahl / 64)));
    }
}
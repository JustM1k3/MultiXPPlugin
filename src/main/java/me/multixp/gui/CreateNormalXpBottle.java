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
import me.oxolotel.utils.wrapped.schedule.Task;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static me.multixp.Main.PREFIX;

public class CreateNormalXpBottle extends CustomMenu implements Closeable, SlotCondition {
    private int switchAngabeVar = 0;
    private final InventoryContent content;
    private int toManytoDrop = -1;

    public CreateNormalXpBottle() {
        super(54);
        content = new InventoryContent();
        setTitle("Create - Erfahrungsflasche");
    }

    @Override
    public void onClose(Player player, ItemStack[] itemStacks, CloseReason closeReason) {
        if (closeReason != CloseReason.CHANGEMENU) {
            PacketReader.openMenus.remove(player.getUniqueId());
            PacketReader.getNormalFlascheAnzahl().remove(player.getUniqueId());
            PacketReader.getLevelAnzahlInput().remove(player.getUniqueId());
            PacketReader.getNormalFlascheAnzahlStacks().remove(player.getUniqueId());
        }
    }

    @Override
    public InventoryContent getContents(Player player) {
        content.fill(0, 54, new InventoryItem(new ItemManager(getInventoryBorder(player)).setDisplayName(" ").build(), () -> {
        }));
        content.fill(10, 17, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), () -> {
        }));
        content.fill(19, 26, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), () -> {
        }));
        content.fill(28, 35, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), () -> {
        }));
        content.fill(37, 44, new InventoryItem(new ItemManager(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), () -> {
        }));

        content.addGuiItem(13, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName("§dErfahrungsflasche").setMultiLineLore("Klicke auf den farbigen Beton, /n um zwischen den beiden /n Angabemöglichkeiten zu /n wechseln. Über die Ambosse kannst du die Levelanzahl und /n die Flaschenanzahl ändern.", "/n", "§7", false).build(), () -> {
        }));

        content.addGuiItem(47, new InventoryItem(new ItemManager(Material.ARROW).setDisplayName("§c§lZurück").build(), () -> {
            InventoryMenuManager.getInstance().closeMenu(player);
            InventoryMenuManager.getInstance().openMenu(player, new MultiXPCreate());
        }));
        content.addGuiItem(51, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lAbbrechen").build(), () -> {
            InventoryMenuManager.getInstance().closeMenu(player);
        }));

        if (!(levelAngabeValidInput(player) && normalFlaschenAnzahlStackValidInput(player) && normalFlaschenAnzahlValidInput(player))) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.65f, 0.8f);
            content.addGuiItem(33, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lFehler").setMultiLineLore("Die Eingabe darf nur /n Zahlen beinhalten!", "/n", "§c", false).build(), () -> {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.65f, 0.8f);
            }));
        } else {
            content.addGuiItem(33, new InventoryItem(new ItemStack(Material.AIR), () -> {
            }));
        }

        if (switchAngabeVar == 0) {
            checkInputToLong(PacketReader.getLevelAnzahlInput().get(player.getUniqueId()), player, 1);
            if (checkLevelAngabeInputComplitly(player)) {
                int lvlInput = Integer.parseInt(PacketReader.getLevelAnzahlInput().get(player.getUniqueId()));

                if (lvlInput != 0) {
                    int valueExp = (int) (ExpManager.getPlayerEXP(player) - (ExpManager.getExpFromLevel(player.getLevel() - lvlInput) + ExpManager.getExpToNextLevel(player.getLevel() - lvlInput, player.getExp())));
                    int bottleCount = getBootleSizeByExp(valueExp);
                    setResultBottle(valueExp, bottleCount, player);
                }
            }
        } else {
            checkInputToLong(PacketReader.getNormalFlascheAnzahl().get(player.getUniqueId()), player, 0);
            checkInputToLong(PacketReader.getNormalFlascheAnzahlStacks().get(player.getUniqueId()), player, 2);

            if (checkAnzahlAngabeInputComplitly(player)) {
                int anzahl = 0;
                int stacks = 0;
                boolean containsOne = false;

                if (PacketReader.getNormalFlascheAnzahl().containsKey(player.getUniqueId())) {
                    anzahl = Integer.parseInt(PacketReader.getNormalFlascheAnzahl().get(player.getUniqueId()));
                    containsOne = true;
                }

                if (PacketReader.getNormalFlascheAnzahlStacks().containsKey(player.getUniqueId())) {
                    stacks = Integer.parseInt(PacketReader.getNormalFlascheAnzahlStacks().get(player.getUniqueId()));
                    containsOne = true;
                }

                if (containsOne && (anzahl != 0 || stacks != 0)) {
                    setResultBottle(xpValueByAnzahl(anzahl, stacks), (stacks * 64 + anzahl), player);
                }
            }
        }

        createAnvils(player);
        switchAngabe(player);

        if (toManytoDrop != -1) {
            setRedBorder(player);
            content.addGuiItem(33, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lFehler").setMultiLineLore("Die Flaschenanzahl ist zu groß! /n Es können maximal 36 Stacks /n Erfahrungsflaschen aus deinem /n Inventar gedroppt werden. Du /n kannst aktuell maximal " + toManytoDrop + " Stacks /n Flaschen erstellen!", "/n", "§c", false).build(), () -> {
            }));
            toManytoDrop = -1;
        }

        return content;
    }

    private void checkInputToLong(String input, Player p, int typ) {
        if (input == null || input.isEmpty()) {
            return;
        }
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            if (typ == 0) {
                PacketReader.getNormalFlascheAnzahl().remove(p.getUniqueId());
            } else if (typ == 1) {
                PacketReader.getLevelAnzahlInput().remove(p.getUniqueId());
            } else {
                PacketReader.getNormalFlascheAnzahlStacks().remove(p.getUniqueId());
            }
        }
    }

    private void switchAngabe(Player player) {
        if (switchAngabeVar == 0) {
            content.addGuiItem(29, new InventoryItem(new ItemManager(Material.YELLOW_CONCRETE).setDisplayName("§6Angabe: §9Level").setMultiLineLore("§8[Klicke, um die Angabe zu wechseln] /n /n Bei dieser Angabe werden dir /n deine angegebenen Level abgezogen /n und in Erfahrungsflaschen umgewandelt.", "/n", "§7", false).build(), () -> {
                switchAngabeVar = 1;
                switchAngabe(player);
                PacketReader.getNormalFlascheAnzahl().remove(player.getUniqueId());
                PacketReader.getNormalFlascheAnzahlStacks().remove(player.getUniqueId());

                InventoryMenuManager.getInstance().getOpenMenu(player).refresh();
            }));
        } else {
            content.addGuiItem(29, new InventoryItem(new ItemManager(Material.GREEN_CONCRETE).setDisplayName("§6Angabe: §5Anzahl").setMultiLineLore("§8[Klicke, um die Angabe zu wechseln] /n /n Bei dieser Angabe kannst du /n angeben, wie viele Erfahrungsflaschen /n du erhalten möchtest. Dir wird anschließend /n der entsprechende Erfahrungswert abgezogen und /n in Erfahrungsflaschen umgewandelt.", "/n", "§7", false).build(), () -> {
                switchAngabeVar = 0;
                switchAngabe(player);
                PacketReader.getLevelAnzahlInput().remove(player.getUniqueId());

                InventoryMenuManager.getInstance().getOpenMenu(player).refresh();
            }));
        }
    }

    private boolean checkLevelAngabeInputComplitly(Player player) {
        if (levelAngabeValidInput(player)) {
            if (PacketReader.getLevelAnzahlInput().containsKey(player.getUniqueId())) {
                int lvl = Integer.parseInt(PacketReader.getLevelAnzahlInput().get(player.getUniqueId()));

                if (lvl <= 24791 && player.getLevel() >= lvl) {
                    return true;
                } else {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.65f, 0.8f);
                    content.addGuiItem(33, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lFehler").setMultiLineLore("Du hast nicht genügend Level, um die Erfahrungsflaschen /n zu erstellen!", "/n", "§c", false).build(), () -> {
                    }));
                    setRedBorder(player);
                }
            }
        }
        return false;
    }

    private boolean checkAnzahlAngabeInputComplitly(Player player){
        if (normalFlaschenAnzahlValidInput(player) && normalFlaschenAnzahlStackValidInput(player)){
            int anzahl = 0;
            int stacks = 0;
            boolean containsOne = false;

            if (PacketReader.getNormalFlascheAnzahl().containsKey(player.getUniqueId())){
                anzahl = Integer.parseInt(PacketReader.getNormalFlascheAnzahl().get(player.getUniqueId()));
                containsOne = true;
            }

            if (PacketReader.getNormalFlascheAnzahlStacks().containsKey(player.getUniqueId())) {
                stacks = Integer.parseInt(PacketReader.getNormalFlascheAnzahlStacks().get(player.getUniqueId()));
                containsOne = true;
            }

            if (containsOne) {
                if (xpValueByAnzahl(anzahl, stacks) <= ExpManager.getPlayerEXP(player)) {
                    return true;
                } else {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.65f, 0.8f);
                    content.addGuiItem(33, new InventoryItem(new ItemManager(Material.BARRIER).setDisplayName("§c§lFehler").setMultiLineLore("Du hast nicht genügend Erfahrungspunkte, /n um die Erfahrungsflaschen zu /n erstellen!", "/n", "§c", false).build(), () -> {
                    }));
                    setRedBorder(player);
                }
            }
        }
        return false;
    }

    private int xpValueByAnzahl(int anzahl, int stacks){
        int xpValue = 0;

        for (int i = 0; i < (stacks * 64 + anzahl); i++) {
            xpValue += (int) Math.round(ExpManager.xpPerBottle());
        }
        return xpValue;
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

    private int getBootleSizeByExp(int expValue){
        int bottleCount = 0;
        for (int i = 0; i <= expValue;i += (int)Math.round(ExpManager.xpPerBottle())){
            bottleCount++;
        }
        return bottleCount;
    }

    private boolean levelAngabeValidInput(Player player){
        boolean check = false;

        if (PacketReader.getLevelAnzahlInput().containsKey(player.getUniqueId())){
            if (checkValidAnvilInput(PacketReader.getLevelAnzahlInput().get(player.getUniqueId()))){
                check = true;
            }
        } else {
            check = true;
        }

        return check;
    }

    private boolean normalFlaschenAnzahlStackValidInput(Player player){
        boolean check = false;

        if (PacketReader.getNormalFlascheAnzahlStacks().containsKey(player.getUniqueId())){
            if (checkValidAnvilInput(PacketReader.getNormalFlascheAnzahlStacks().get(player.getUniqueId()))){
                check = true;
            }
        } else {
            check = true;
        }

        return check;
    }

    private boolean normalFlaschenAnzahlValidInput(Player player){
        boolean check = false;

        if (PacketReader.getNormalFlascheAnzahl().containsKey(player.getUniqueId())){
            if (checkValidAnvilInput(PacketReader.getNormalFlascheAnzahl().get(player.getUniqueId()))){
                check = true;
            }
        } else {
            check = true;
        }

        return check;
    }

    private void createAnvils(Player player) {
        if (switchAngabeVar == 0) {
            String value = "0";

            if (levelAngabeValidInput(player) && PacketReader.getLevelAnzahlInput().containsKey(player.getUniqueId())) {
                value = PacketReader.getLevelAnzahlInput().get(player.getUniqueId());
            } else if (PacketReader.getLevelAnzahlInput().containsKey(player.getUniqueId()) && !checkValidAnvilInput(PacketReader.getLevelAnzahlInput().get(player.getUniqueId()))) {
                value = "§c" + PacketReader.getLevelAnzahlInput().get(player.getUniqueId());
            }

            content.addGuiItem(31, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName("§6Level Angabe: §a" + value).setMultiLineLore("Gib die Anzahl an Level an, /n die du in Erfahrungsflaschen /n umwandeln möchtest.", "/n", "§7", false ).build(), () -> {
                saveMenus(player);
                InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
                AnvilMenuManager.levelFix(player);
                AnvilMenuManager.createAnvilMenu(player, new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName(" ").setMultiLineLore("Gib die Anzahl an Level ein, /n die du in Erfahrungsflaschen /n umwandelt möchtest.", "/n", "§7", false).build(), "Levelanzahl");
            }));
        } else {
            String valueStack = "0";

            if (normalFlaschenAnzahlStackValidInput(player) && PacketReader.getNormalFlascheAnzahlStacks().containsKey(player.getUniqueId())) {
                valueStack = PacketReader.getNormalFlascheAnzahlStacks().get(player.getUniqueId());
            } else if ((PacketReader.getNormalFlascheAnzahlStacks().containsKey(player.getUniqueId()) && !checkValidAnvilInput(PacketReader.getNormalFlascheAnzahlStacks().get(player.getUniqueId()))) || toManytoDrop != -1) {
                valueStack = "§c" + PacketReader.getNormalFlascheAnzahlStacks().get(player.getUniqueId());
            }
            String valueAnzahl = "0";

            if (normalFlaschenAnzahlValidInput(player) && PacketReader.getNormalFlascheAnzahl().containsKey(player.getUniqueId())) {
                valueAnzahl = PacketReader.getNormalFlascheAnzahl().get(player.getUniqueId());
            } else if ((PacketReader.getNormalFlascheAnzahl().containsKey(player.getUniqueId()) && !checkValidAnvilInput(PacketReader.getNormalFlascheAnzahl().get(player.getUniqueId()))) || toManytoDrop != -1) {
                valueAnzahl = "§c" + PacketReader.getNormalFlascheAnzahl().get(player.getUniqueId());
            }

            content.addGuiItem(30, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName("§6Stack Angabe: §a" + valueStack).setMultiLineLore("Gib an, wie viele Stacks /n Erfahrungsflaschen du /n erstellen möchtest.", "/n", "§7", false).build(), () -> {
                saveMenus(player);
                InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
                AnvilMenuManager.levelFix(player);
                AnvilMenuManager.createAnvilMenu(player, new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName(" ").setMultiLineLore("Gib an, wie viele Stacks /n Erfahrungsflaschen du /n erstellen möchtest.", "/n", "§7", false).build(), "Stacks");
            }));
            content.addGuiItem(31, new InventoryItem(new ItemManager(Material.ANVIL).setDisplayName("§6Einzelangabe: §a" + valueAnzahl).setMultiLineLore("Gib an, wie viele einzelne /n Erfahrungsflaschen du /n erstellen möchtest.", "/n", "§7", false).build(), () -> {
                saveMenus(player);
                InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
                AnvilMenuManager.levelFix(player);
                AnvilMenuManager.createAnvilMenu(player, new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName(" ").setMultiLineLore("Gib an, wie viele einzelne /n Erfahrungsflaschen du /n erstellen möchtest.", "/n", "§7", false).build(), "Einzelangabe");
            }));
        }
    }

    private Material getInventoryBorder(Player player){
        Material material = Material.CYAN_STAINED_GLASS_PANE;
        if(!(levelAngabeValidInput(player) && normalFlaschenAnzahlValidInput(player) && normalFlaschenAnzahlStackValidInput(player))){
            material = Material.RED_STAINED_GLASS_PANE;
        }
        return material;
    }

    private boolean checkValidAnvilInput(String input) {
        if (input.startsWith(" ")) {
            input = input.replaceFirst(" ", "");
        }
        return input.matches("[0-9]+");
    }

    private void setResultBottle(int xpWert, int anzahl, Player player) {
        int stacks = anzahl / 64;
        int rest = anzahl % 64;

        ArrayList<Component> lore = new ArrayList<>();
        if(anzahl > 1){
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><dark_gray>[Klicke, um die Flaschen zu erhalten]"));
        }else{
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><dark_gray>[Klicke, um die Flasche zu erhalten]"));
        }
        lore.add(MiniMessage.miniMessage().deserialize("<!italic><dark_gray><>-------------------<>"));

        if (stacks != 0 && rest != 0) {
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray>Flaschenanzahl: <green>" + stacks + " Stacks und " + rest));
        } else if (stacks != 0) {
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray>Flaschenanzahl: <green>" + stacks + " Stacks"));
        } else if (rest != 0) {
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray>Flaschenanzahl: <green>" + rest));
        }
        lore.add(MiniMessage.miniMessage().deserialize(" "));

        content.addGuiItem(33, new InventoryItem(new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName("§dErfahrungsflasche").setLoreComponent(lore).build(), () -> {
            if (ExpManager.checkPlayerInvPlace(player, anzahl)) {
                ExpManager.removeExpFromPlayer(player, xpWert);
                for (int i = 0; i < anzahl; i++) {
                    player.getInventory().addItem(new ItemStack(Material.EXPERIENCE_BOTTLE));
                }
                if(anzahl > 1){
                    Chat.sendSuccessMessage(PREFIX, me.oxolotel.utils.wrapped.player.Player.of(player), "Erfahrungsflaschen erfolgreich erstellt!");
                }else{
                    Chat.sendSuccessMessage(PREFIX, me.oxolotel.utils.wrapped.player.Player.of(player), "Erfahrungsflasche erfolgreich erstellt!");
                }
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.65f, 0.8f);
                InventoryMenuManager.getInstance().closeMenu(player);
            } else {
                int emptySlotsSize = (int) Arrays.stream(player.getInventory().getStorageContents()).filter(item -> item == null || item.getType() == Material.AIR).count();

                if (2304 >= (anzahl - emptySlotsSize * 64)) {
                    ArrayList<ItemStack> flaschen = new ArrayList<>();
                    for (int i = 0; i < anzahl; i++) {
                        flaschen.add(new ItemStack(Material.EXPERIENCE_BOTTLE));
                    }
                    InventoryMenuManager.getInstance().closeMenu(player, CloseReason.CHANGEMENU);
                    InventoryMenuManager.getInstance().openMenu(player, new ConfirmBottleDropMenu(flaschen, xpWert, false));
                } else {
                    toManytoDrop = 36 + emptySlotsSize;
                    InventoryMenuManager.getInstance().refreshMenu(player);
                }
            }
        }));
    }

    @Override
    public boolean isClickAllowed(Player player, int i) {
        return i == 29 || i == 30 || i == 31 || i == 47 || i == 51 || i == 33;
    }

    private void saveMenus(Player p) {
        MenuView a = InventoryMenuManager.getInstance().getOpenMenu(p);
        @SuppressWarnings("unchecked")
        LinkedList<TrippleWrapper<CustomMenu, InventoryContent, Task>> tempOpenMenus = ReflectionUtils.accessField(MenuView.class, a, "openMenus");
        List<CustomMenu> t = tempOpenMenus.stream().map(TrippleWrapper::getValue1).toList();
        PacketReader.openMenus.put(p.getUniqueId(), t);
    }
}

package me.multixp.managerPackage;

import me.multixp.gui.ConfirmBottleDropMenu;
import me.oxolotel.utils.bukkit.menuManager.InventoryMenuManager;
import me.oxolotel.utils.wrapped.Chat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.util.*;

import static me.multixp.Main.PREFIX;

public class ExpManager {

    public static int getPlayerEXP(Player player){
        return (int) (getExpFromLevel(player.getLevel()) + getplayerExpToNextLevel(player));
    }

    public static double getExpFromLevel(int level){
        double xp_value = -1;

        if (level <= 16 && level >= 0) {
            xp_value = (level * level + 6 * level);
        }else if(level > 16 && level <= 31 ){
            xp_value = (2.5 * (level * level) - 40.5 * level + 360);
        }else if(level > 31){
            xp_value = (4.5 * (level * level) - 162.5 * level + 2220);
        }
        return xp_value;
    }

    public static int getLevelFromExp(int exp){
        int level = -1;

        if (exp < 353) {
            level = (int) (Math.sqrt(exp + 9) - 3);
        } else if (exp > 352 && exp < 1508) {
            level = (int) (8.1 + Math.sqrt(0.4 * (exp - 195.975)));
        } else if (exp > 1507) {
            level = (int) (18.056 + Math.sqrt(0.222 * (exp - 752.986)));
        }
        return level;
    }

    private static double getplayerExpToNextLevel(Player player){
        double xp_act = getExpFromLevel(player.getLevel());
        double xp_next = getExpFromLevel(player.getLevel() + 1);
        double percentToNextLvL = player.getExp();

        return (xp_next - xp_act) * percentToNextLvL;
    }

    public static double getExpToNextLevel(int level, double expPercent){
        double xp_act = getExpFromLevel(level);
        double xp_next = getExpFromLevel(level + 1);

        return (xp_next - xp_act) * expPercent;
    }

    public static boolean checkErfahrungFlaschenInput(int flaschenAnzahl, int erfahrungswert, Player player){
        if (erfahrungswert > 2147483647 || erfahrungswert < 0){
            return false;
        }

        int playerXP = getPlayerEXP(player);
        return (flaschenAnzahl * erfahrungswert) <= playerXP;
    }

    public static Component createExperienceBar(int xpWert, int lvl){
        double lvlInXp = getExpFromLevel(lvl);
        double lvlInXpNext = getExpFromLevel(lvl+1);

        double xpWertRest = xpWert - lvlInXp;
        double lvlDiffernece = lvlInXpNext - lvlInXp;

        int erfahrungsStriche = (int)Math.floor((xpWertRest / lvlDiffernece) * 35);

        StringBuilder stricheFarbe = new StringBuilder();
        StringBuilder striche = new StringBuilder();

        for (int i = 0; i < 35; i++) {
            if (i < erfahrungsStriche){
                stricheFarbe.append("|");
                continue;
            }
            striche.append("|");
        }

        return MiniMessage.miniMessage().deserialize("<!italic><gradient:green:#C3FFC3>"+stricheFarbe+"</gradient><white>"+striche);
    }


    public static boolean checkPlayerInvPlace(Player player, int flaschenanzahl){
        int emptySlotsSize = (int) Arrays.stream(player.getInventory().getStorageContents()).filter(item -> item == null || item.getType() == Material.AIR).count();
        return (emptySlotsSize >= Math.ceil(((double) flaschenanzahl / 64)));
    }

    public static ItemStack createBottle(int xpValue, int lvlValue){
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize("<!italic><dark_gray><>---------------------------<>"));
        lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray>Level: <green>" + lvlValue));
        lore.add(ExpManager.createExperienceBar(xpValue, lvlValue));
        lore.add(MiniMessage.miniMessage().deserialize(" "));
        lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray>Erfahrungspunkte: <green>" + xpValue));

        return new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName("§6§kKK§dMultiXP Flasche§6§kKK").setLoreComponent(lore).setEnchant(Enchantment.MULTISHOT,1871,true,true).build();
    }

    public static void removeExpFromPlayer(Player player, int xpValue){
        double playerXp = ExpManager.getPlayerEXP(player);

        player.setExp(0);
        player.setLevel(0);
        player.giveExp((int) (playerXp - xpValue));
    }

    public static boolean checkForMultiXPFlasche(ItemStack item){
        if (item == null){
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasEnchant(Enchantment.MULTISHOT)) {
            int lvl = meta.getEnchants().get(Enchantment.MULTISHOT);

            return lvl == 1871;
        }
        return false;
    }

    public static ItemStack mergeMultiXPFlaschen(ArrayList<ItemStack> flaschen){
        int xpSum = 0;

        //xpSum = flaschen.stream().map(item -> Objects.requireNonNull(item.getLore()).get(4)).mapToInt(s -> Integer.parseInt(s.split(" ")[1].substring(2)) * flasche.getAmount()).sum();

        for (ItemStack flasche: flaschen) {
            String xpLore = flasche.getItemMeta().getLore().get(4);
            String[] arr = xpLore.split(" ");

            xpSum += Integer.parseInt(arr[1].substring(2)) * flasche.getAmount();
        }

        return createBottle(xpSum, getLevelFromExp(xpSum));
    }

    public static double xpPerBottle() {
        Random rand = new Random();
        return ((rand.nextFloat() * (11 - 3)) + 3);
    }

    public static void setPlayerInvItemExpValue(Player player){
        int xpValue = 0;

        for (ItemStack item:player.getInventory().getContents()) {
            if (item == null){
                continue;
            }

            if (item.getType() == Material.EXPERIENCE_BOTTLE){
                if (checkForMultiXPFlasche(item)){
                    String xpLore = item.getItemMeta().getLore().get(4);
                    String[] arr = xpLore.split(" ");

                    xpValue += Integer.parseInt(arr[1].substring(2)) * item.getAmount();
                }else {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null){
                        if (meta.hasDisplayName() || meta.hasLore()){
                            continue;
                        }
                    }

                    for (int i = 0; i < item.getAmount(); i++) {
                        xpValue += (int) Math.round(xpPerBottle());
                    }
                }
                player.getInventory().remove(item);
            }
        }
        if (xpValue == 0){
            Chat.sendErrorMessage(PREFIX, me.oxolotel.utils.wrapped.player.Player.of(player), "Du hast keine MultiXP- oder Erfahrungsflaschen die deiner Erfahrungsleiste angerechnet werden können!");
        } else {
            player.giveExp(xpValue, false);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.65f, 0.8f);
            Chat.sendSuccessMessage(PREFIX, me.oxolotel.utils.wrapped.player.Player.of(player), "Deine MultiXP- und Erfahrungsflaschen wurden erfolgreich deiner Erfahrungsleiste angerechnet.");
        }
    }

    public static void multiXPMerge(Player player){
        ArrayList<ItemStack> itemList = new ArrayList<>();

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && ExpManager.checkForMultiXPFlasche(item)) {
                itemList.add(item);
            }
        }
        if (itemList.isEmpty() || (itemList.size() == 1 && itemList.get(0).getAmount() == 1)) {
            Chat.sendErrorMessage(PREFIX, me.oxolotel.utils.wrapped.player.Player.of(player), "Es können keine MultiXP Flaschen in deinem Inventar zusammengefügt werden!");
            return;
        }

        for (ItemStack item:itemList) {
            player.getInventory().remove(item);
        }

        if (ExpManager.checkPlayerInvPlace(player, 1)) {
            player.getInventory().addItem(ExpManager.mergeMultiXPFlaschen(itemList));
            InventoryMenuManager.getInstance().closeMenu(player);
        } else {
            InventoryMenuManager.getInstance().closeMenu(player);
            InventoryMenuManager.getInstance().openMenu(player, new ConfirmBottleDropMenu(new ArrayList<ItemStack>((Collection) ExpManager.mergeMultiXPFlaschen(itemList)), 0, true));
        }
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.65f, 0.8f);
        Chat.sendSuccessMessage(PREFIX, me.oxolotel.utils.wrapped.player.Player.of(player), "MultiXP Flaschen erfolgreich zusammengefügt!");
    }

    public static ItemStack getPlayerExpInfoHead(Player player){
        ArrayList<Component> headLore = new ArrayList<>();
        headLore.add(MiniMessage.builder().build().deserialize("<!italic><gray>Level: <green>" + player.getLevel() + " ").append(ExpManager.createExperienceBar(ExpManager.getPlayerEXP(player), player.getLevel())).append(MiniMessage.miniMessage().deserialize(" <green>" + ((int)(player.getExp() * 100)) + "%")));
        headLore.add(MiniMessage.miniMessage().deserialize("<!italic><gray>Erfahrungspunkte bis zum Levelup: <green>" + ((int)(ExpManager.getExpFromLevel(player.getLevel() + 1) - ExpManager.getPlayerEXP(player)))));
        headLore.add(MiniMessage.miniMessage().deserialize(" "));
        headLore.add(MiniMessage.miniMessage().deserialize("<!italic><gray>Erfahrungspunkte: <green>" + ExpManager.getPlayerEXP(player)));

        return new SkullManager(player).setDisplayName("§9EXP-Info §7von §6"+ player.getName()).setLoreComponent(headLore).build();
    }

    public ExpManager(){

    }
}

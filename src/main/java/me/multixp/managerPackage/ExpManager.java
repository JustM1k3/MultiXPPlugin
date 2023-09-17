package me.multixp.managerPackage;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.util.ArrayList;

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

    public static boolean checkErfahrungFlaschenInput(int flaschenAnzahl, int erfahrungswert, Player player){
        if (erfahrungswert > 2147483647 || erfahrungswert < 0){
            return false;
        }

        int playerXP = getPlayerEXP(player);
        return (flaschenAnzahl * erfahrungswert) <= playerXP;
    }

    public static ItemStack createMultiXPBottle(int xpWert, int lvl){
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§8<>---------------------------<>");
        lore.add("§7Level: §a" + lvl);
        lore.add(createExperienceBar(xpWert, lvl));
        lore.add(" ");
        lore.add("§7Erfahrungspunkte: §a" + xpWert);

        return new ItemManager(Material.EXPERIENCE_BOTTLE).setDisplayName("§6§kKK§dMultiXP Flasche§6§kKK").setLore(lore).build();
    }

    public static String createExperienceBar(int xpWert, int lvl){
        double lvlInXp = getExpFromLevel(lvl);
        double lvlInXpNext = getExpFromLevel(lvl+1);

        double xpWertRest = xpWert - lvlInXp;
        double lvlDiffernece = lvlInXpNext - lvlInXp;

        int erfahrungsStriche = (int) ((xpWertRest / lvlDiffernece) * 25);

        String striche = "";

        for (int i = 0; i < 25; i++) {
            if (i < erfahrungsStriche){
                striche += "§a| ";
                continue;
            }
            striche += "§7| ";
        }

        return striche;
    }

    public ExpManager(){

    }
}

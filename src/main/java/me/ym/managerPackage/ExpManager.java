package me.ym.managerPackage;

import org.bukkit.entity.Player;

public class ExpManager {

    public static int getPlayerEXP(Player player){
        return (int) (getExpFromLevel(player.getLevel()) + getplayerExpToNextLevel(player));
    }

    private static double getExpFromLevel(int level){
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

    private static double getplayerExpToNextLevel(Player player){
        double xp_act = getExpFromLevel(player.getLevel());
        double xp_next = getExpFromLevel(player.getLevel() + 1);
        double percentToNextLvL = player.getExp();

        return xp_act + ((xp_next - xp_act) * percentToNextLvL);
    }

    public ExpManager(){

    }
}

package me.multixp.managerPackage;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemManager {

    private ItemStack item;
    private ItemMeta itemMeta;

    public ItemManager(Material material){
        item = new ItemStack(material);
        itemMeta = item.getItemMeta();

    }
    public ItemManager(){

    }

    public ItemManager setDisplayName(String name){
        itemMeta.setDisplayName(name);
        return this;
    }

    public ItemManager setEnchant(Enchantment enchant, int level, boolean res){
        itemMeta.addEnchant(enchant, level, res);
        return this;
    }
    public ItemManager setEnchant(Enchantment enchant, int level, boolean res, boolean invisible){
        itemMeta.addEnchant(enchant, level, res);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemManager setLore(ArrayList<String> lore){
        itemMeta.setLore(lore);
        return this;
    }

    public ItemManager setLore(String... lore){
        itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemStack build(){
        item.setItemMeta(itemMeta);
        return item;
    }

    public ItemManager setMultiLineLore(String loreText, String zeilenumbruch, String color, boolean extraLine){
        ArrayList<String> lore = new ArrayList<>();
        if(extraLine){
            lore.add(" ");
        }
        String temp = color;
        for (String wort : loreText.split(" ")){
            if (wort.equals(zeilenumbruch)){
                lore.add(temp);
                temp = color;
                continue;
            }
            temp+= wort + " ";
        }
        if (!temp.equals("")) {
            lore.add(temp);
        }

        itemMeta.setLore(lore);
        return this;
    }

    public ItemManager setMultiLineLore(String loreText, int wordsPerLine, String color, boolean extraLine){
        ArrayList<String> lore = new ArrayList<>();
        if(extraLine){
            lore.add(" ");
        }
        String temp = color;
        int counter = 0;
        for (String wort : loreText.split(" ")){
            if (counter == wordsPerLine){
                counter = 0;
                lore.add(temp.toString());
                temp = color;
            }
            counter++;
            temp+= wort + " ";
        }
        if (!temp.equals("")) {
            lore.add(temp);
        }

        itemMeta.setLore(lore);
        return this;
    }
}

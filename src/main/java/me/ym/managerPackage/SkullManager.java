package me.ym.managerPackage;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class SkullManager {


    private ItemStack skullItem;
    private SkullMeta skullMeta;

    public SkullManager(Player player){
        skullItem = new ItemStack(Material.PLAYER_HEAD);
        skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
    }

    public SkullManager(String textureBase64){
        skullItem = getCustomSkull(textureBase64);
        skullMeta = (SkullMeta) skullItem.getItemMeta();
    }
    public SkullManager(String textureBase64, String name){
        skullItem = getCustomSkull(textureBase64);
        skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setDisplayName(name);
    }

    public SkullManager setDisplayName(String name){
        skullMeta.setDisplayName(name);
        return this;
    }

    public SkullManager setEnchant(Enchantment enchant, int level, boolean res){
        skullMeta.addEnchant(enchant, level, res);
        return this;
    }

    public SkullManager setLore(ArrayList<String> lore){
        skullMeta.setLore(lore);
        return this;
    }

    public SkullManager setLore(String... lore){
        skullMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemStack build(){
        skullItem.setItemMeta(skullMeta);
        return skullItem;
    }


    public ItemStack getCustomSkull(String base64) {

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (base64.isEmpty()) return head;

        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            mtd.setAccessible(true);
            mtd.invoke(skullMeta, profile);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            ex.printStackTrace();
        }

        head.setItemMeta(skullMeta);
        return head;
    }
}

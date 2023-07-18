package me.multixp.managerPackage;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.oxolotel.utils.bukkit.menuManager.InventoryMenuManager;
import me.oxolotel.utils.bukkit.menuManager.menus.CustomMenu;
import me.multixp.Main;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PacketReader {

    private static final HashMap<UUID,String> levelAnzahlInput = new HashMap<>();
    public static HashMap<UUID, String> getLevelAnzahlInput() {
        return levelAnzahlInput;
    }

    public static void removeLevelAnzahlInput(Player p){
        levelAnzahlInput.remove(p.getUniqueId());
    }

    private static final HashMap<UUID,String> flaschenAnzahlInput = new HashMap<>();
    public static HashMap<UUID, String> getFlaschenAnzahlInput() {
        return flaschenAnzahlInput;
    }

    public static void removeFlaschenAnzahlInput(Player p){
        flaschenAnzahlInput.remove(p.getUniqueId());
    }

    public static HashMap<UUID, List<CustomMenu>> openMenus = new HashMap<>();

    public static void readWindowClickPacket(ProtocolManager pm, Main main){
        if (pm == null) {
            return;
        }
        pm.addPacketListener(new PacketAdapter(main, PacketType.Play.Client.WINDOW_CLICK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Inventory invFound = null;
                Player p = event.getPlayer();
                outerloop:
                for (Inventory inv: AnvilMenuManager.getInvList()){
                    for (HumanEntity viewer: inv.getViewers()){
                        if (viewer == p) {
                            invFound = inv;
                            break outerloop;
                        }
                    }
                }

                if (invFound == null){
                    return;
                }
                PacketContainer packet = event.getPacket();
                if (packet.getIntegers().read(2) != 2) { //Click auf Resul Feld
                    return;
                }

                ItemStack item = packet.getItemModifier().read(0);

                if (item.getItemMeta() == null){
                    return;
                }

                String input = item.getItemMeta().getDisplayName();
                AnvilMenuManager.removeInv(invFound);

                input = input.replace(" ","");
                if(p.getOpenInventory().getTitle().equalsIgnoreCase("Levelanzahl") || p.getOpenInventory().getTitle().equalsIgnoreCase("Erfahrungswert")){
                    levelAnzahlInput.put(p.getUniqueId(), input);
                }else{
                    flaschenAnzahlInput.put(p.getUniqueId(), input);
                }

                PacketContainer container = new PacketContainer(PacketType.Play.Server.CLOSE_WINDOW);
                try {
                    pm.sendServerPacket(p, container);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                if (p.getGameMode() != GameMode.CREATIVE){
                    p.setLevel(p.getLevel()-1);
                }

                new BukkitRunnable() {
                    int counter = 0;
                    @Override
                    public void run() {
                        if (counter == 1){
                            for (CustomMenu cm : openMenus.get(p.getUniqueId())) {
                                InventoryMenuManager.getInstance().openMenu(p, cm);}
                            cancel();
                        }
                        counter++;
                    }
                }.runTaskTimer(main, 0, 1);
            }});
    }
    public PacketReader(){

    }
}


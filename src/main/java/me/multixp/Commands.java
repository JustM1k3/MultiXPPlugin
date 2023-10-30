package me.multixp;

import me.multixp.gui.ConfirmBottleDropMenu;
import me.multixp.gui.MultiXPCreate;
import me.multixp.managerPackage.ExpManager;
import me.oxolotel.utils.bukkit.menuManager.InventoryMenuManager;
import me.oxolotel.utils.bukkit.menuManager.menus.Closeable;
import me.oxolotel.utils.wrapped.Chat;
import me.oxolotel.utils.wrapped.command.Command;
import me.oxolotel.utils.wrapped.command.PlayerCommand;
import me.oxolotel.utils.wrapped.command.annotations.Name;
import me.oxolotel.utils.wrapped.command.annotations.Permission;
import me.oxolotel.utils.wrapped.command.sender.CommandSender;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import me.multixp.gui.MultiXPMenu;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.multixp.Main.PREFIX;

@Name("MultiXP")
@Permission("multixp.menu")
public class Commands implements PlayerCommand {
    @NotNull
    @Override
    public List<Command> getSubCommands() {
        return List.of(
                new HelpCommand(),
                new CreateCommand(),
                new ZeroCommand(),
                new MergeCommand(),
                new DepositCommand()
        );
    }

    @Override
    public boolean execute(@NotNull me.oxolotel.utils.wrapped.player.Player player, @NotNull List<String> list, @NotNull List<String> list1) {
        if (!player.hasPermission("multixp.menu")){
            Chat.sendErrorMessage(PREFIX, player, "Du hast nicht die benötigten Rechte um dies zu tun!");
            return true;
        }
        InventoryMenuManager.getInstance().openMenu((Player) player.getPlayer(true), new MultiXPMenu());
        return true;
    }

    @Name("help")
    @Permission("multixp.help")
    private static class HelpCommand implements PlayerCommand{
        @Override
        public boolean execute(@NotNull me.oxolotel.utils.wrapped.player.Player player, @NotNull List<String> list, @NotNull List<String> list1) {
            if (!player.hasPermission("multixp.help")){
                Chat.sendErrorMessage(PREFIX, player, "Du hast nicht die benötigten Rechte um dies zu tun!");
                return true;
            }

            String message = String.format("""
                            %s
                            %s
                            %s
                            %s
                            %s
                            %s
                            %s
                            """,
                    "<color:#4C93FF><b>MultiXP Hilfe</b>",
                    "",
                    "<color:#72CA5E><hover:show_text:'/multixp ausführen'><click:run_command:/multixp>/multiXP <gray>- Öffnet das MultiXP Menü</click></hover>",
                    "<color:#72CA5E><hover:show_text:'/multixp merge ausführen'><click:run_command:/multixp merge>/multiXP merge <gray>- Kombiniert alle MultiXP Flaschen in deinem Inventar zu einer MultiXP Flasche</click></hover>",
                    "<color:#72CA5E><hover:show_text:'/multixp zero ausführen'><click:run_command:/multixp zero>/multiXP zero <gray>- Fügt den Erfahrungswert der MultiXP- und Erfahrungsflaschen aus deinem Inventar deinem Levelstand hinzu und entfernt sie aus deinem Inventar</click></hover>",
                    "<color:#72CA5E><hover:show_text:'/multixp create ausführen'><click:run_command:/multixp create>/multiXP create <gray>- Öffnet das MultiXP - Create Menü</click></hover>",
                    "<color:#72CA5E><hover:show_text:'Click um \"/multixp deposit\" einzufügen'><click:suggest_command:/multixp deposit>/multiXP deposit <Levelanzahl> <gray>- Wandelt die Angegebene Levelanzahl in Erfahrungsflaschen um</click></hover>");

            Chat.sendUnformattedMessage(player , MiniMessage.miniMessage().deserialize(message));

            return true;
        }
    }

    @Name("create")
    @Permission("multixp.create")
    private static class CreateCommand implements PlayerCommand{
        @Override
        public boolean execute(@NotNull me.oxolotel.utils.wrapped.player.Player player, @NotNull List<String> list, @NotNull List<String> list1) {
            if (!player.hasPermission("multixp.create")){
                Chat.sendErrorMessage(PREFIX, player, "Du hast nicht die benötigten Rechte um dies zu tun!");
                return true;
            }
            InventoryMenuManager.getInstance().openMenu((Player) player.getPlayer(true), new MultiXPCreate());
            return true;
        }
    }

    @Name("zero")
    @Permission("multixp.zero")
    private static class ZeroCommand implements PlayerCommand{
        @Override
        public boolean execute(@NotNull me.oxolotel.utils.wrapped.player.Player player, @NotNull List<String> list, @NotNull List<String> list1) {
            if (!player.hasPermission("multixp.zero")){
                Chat.sendErrorMessage(PREFIX, player, "Du hast nicht die benötigten Rechte um dies zu tun!");
                return true;
            }
            Player p = (Player)player.getPlayer(true);
            ExpManager.setPlayerInvItemExpValue(p);
            return true;
        }
    }

    @Name("merge")
    @Permission("multixp.merge")
    private static class MergeCommand implements PlayerCommand{
        @Override
        public boolean execute(@NotNull me.oxolotel.utils.wrapped.player.Player player, @NotNull List<String> list, @NotNull List<String> list1) {
            if (!player.hasPermission("multixp.merge")){
                Chat.sendErrorMessage(PREFIX, player, "Du hast nicht die benötigten Rechte um dies zu tun!");
                return true;
            }
            ExpManager.multiXPMerge((Player) player.getPlayer(true));
            return true;
        }
    }

    @Name("deposit")
    @Permission("multixp.deposit")
    private static class DepositCommand implements PlayerCommand{
        @Override
        public boolean execute(@NotNull me.oxolotel.utils.wrapped.player.Player player, @NotNull List<String> list, @NotNull List<String> list1) {
            if (!player.hasPermission("multixp.deposit")){
                Chat.sendErrorMessage(PREFIX, player, "Du hast nicht die benötigten Rechte um dies zu tun!");
                return true;
            }

            Player p = (Player)player.getPlayer(true);

            if (list.isEmpty()){
                return false;
            }

            if (!list.get(0).matches("[0-9]+")){
                return false;
            }

            int lvlAnzahl;
            try {
                lvlAnzahl = Integer.parseInt(list.get(0));
            }catch (NumberFormatException e){
                Chat.sendErrorMessage(PREFIX, player, "Die angegebene Levelanzahl ist zu groß!");
                return true;
            }

            if (p.getLevel() < lvlAnzahl){
                Chat.sendErrorMessage(PREFIX, player, "Die angegebene Levelanzahl ist zu groß!");
                return true;
            }

            int valueExp = (int) (ExpManager.getPlayerEXP(p) - (ExpManager.getExpFromLevel(p.getLevel() - lvlAnzahl) + ExpManager.getExpToNextLevel(p.getLevel() - lvlAnzahl, p.getExp())));
            int bottleSize = getBootleSizeByExp(valueExp);

            if (ExpManager.checkPlayerInvPlace(p, bottleSize)) {
                ExpManager.removeExpFromPlayer(p, valueExp);
                for (int i = 0; i < bottleSize; i++) {
                    p.getInventory().addItem(new ItemStack(Material.EXPERIENCE_BOTTLE));
                }
            } else {
                int emptySlotsSize = (int) Arrays.stream(p.getInventory().getStorageContents()).filter(item -> item == null || item.getType() == Material.AIR).count();

                if (2304 >= (bottleSize - emptySlotsSize * 64)) {
                    ArrayList<ItemStack> flaschen = new ArrayList<>();
                    for (int i = 0; i < bottleSize; i++) {
                        flaschen.add(new ItemStack(Material.EXPERIENCE_BOTTLE));
                    }
                    InventoryMenuManager.getInstance().openMenu(p, new ConfirmBottleDropMenu(flaschen, valueExp, false));
                } else {
                    Chat.sendErrorMessage(PREFIX, player, "Die Flaschenanzahl ist zu groß! Bitte versuche es mit einem niedrigeren Levelwert.");
                }
            }

            return true;
        }

        @NotNull
        @Override
        public List<String> autocomplete(@NotNull CommandSender sender) {
            return List.of("Levelanzahl");
        }
    }

    private static int getBootleSizeByExp(int expValue){
        int bottleCount = 0;
        for (int i = 0; i <= expValue;i += (int)Math.round(ExpManager.xpPerBottle())){
            bottleCount++;
        }
        return bottleCount;
    }
}

package me.multixp;

import me.multixp.gui.MultiXPCreate;
import me.multixp.managerPackage.ExpManager;
import me.oxolotel.utils.bukkit.menuManager.InventoryMenuManager;
import me.oxolotel.utils.wrapped.Chat;
import me.oxolotel.utils.wrapped.command.Command;
import me.oxolotel.utils.wrapped.command.PlayerCommand;
import me.oxolotel.utils.wrapped.command.annotations.Name;
import me.oxolotel.utils.wrapped.command.annotations.Permission;
import me.oxolotel.utils.wrapped.command.sender.CommandSender;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import me.multixp.gui.MultiXPMenu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
                new MergeCommand()
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
            return false;
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
}

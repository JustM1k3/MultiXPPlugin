package me.multixp;

import me.multixp.managerPackage.ExpManager;
import me.oxolotel.utils.bukkit.menuManager.InventoryMenuManager;
import me.oxolotel.utils.wrapped.Chat;
import me.oxolotel.utils.wrapped.command.Command;
import me.oxolotel.utils.wrapped.command.PlayerCommand;
import me.oxolotel.utils.wrapped.command.annotations.Name;
import me.oxolotel.utils.wrapped.command.sender.CommandSender;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import me.multixp.gui.MultiXPMenu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static me.multixp.Main.PREFIX;

@Name("MultiXP")
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
        InventoryMenuManager.getInstance().openMenu((Player) player.getPlayer(true), new MultiXPMenu());
        return true;
    }

    @Name("help")
    private static class HelpCommand implements PlayerCommand{
        @Override
        public boolean execute(@NotNull me.oxolotel.utils.wrapped.player.Player player, @NotNull List<String> list, @NotNull List<String> list1) {
            return false;
        }
    }

    @Name("create")
    private static class CreateCommand implements PlayerCommand{
        @Override
        public boolean execute(@NotNull me.oxolotel.utils.wrapped.player.Player player, @NotNull List<String> list, @NotNull List<String> list1) {
            InventoryMenuManager.getInstance().openMenu((Player) player.getPlayer(true), new MultiXPMenu(54));
            return true;
        }
    }

    @Name("zero")
    private static class ZeroCommand implements PlayerCommand{
        @Override
        public boolean execute(@NotNull me.oxolotel.utils.wrapped.player.Player player, @NotNull List<String> list, @NotNull List<String> list1) {
            Player p = (Player)player.getPlayer(true);
            ExpManager.setPlayerInvItemExpValue(p);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.65f, 0.8f);
            return true;
        }
    }
}

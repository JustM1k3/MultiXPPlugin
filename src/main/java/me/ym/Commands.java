package me.ym;

import me.oxolotel.utils.wrapped.command.Command;
import me.oxolotel.utils.wrapped.command.PlayerCommand;
import me.oxolotel.utils.wrapped.command.annotations.Name;
import me.oxolotel.utils.wrapped.command.sender.CommandSender;
import me.oxolotel.utils.wrapped.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Name("XP")
public class Commands implements PlayerCommand {


    @NotNull
    @Override
    public List<Command> getSubCommands() {
        return List.of(new HelpCommand(), new CreateCommand(), new ZeroCommand());
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull List<String> list, @NotNull List<String> list1) {
        return false;
    }

    @Name("help")
    private static class HelpCommand implements PlayerCommand{
        @Override
        public boolean execute(@NotNull me.oxolotel.utils.wrapped.player.Player player, @NotNull List<String> list, @NotNull List<String> list1) {
            player.sendMessage("help command");
            return true;
        }
    }

    @Name("create")
    private static class CreateCommand implements PlayerCommand{
        @Override
        public boolean execute(@NotNull me.oxolotel.utils.wrapped.player.Player player, @NotNull List<String> list, @NotNull List<String> list1) {
            player.sendMessage("Create command");
            return true;
        }
    }

    @Name("zero")
    private static class ZeroCommand implements PlayerCommand{
        @Override
        public boolean execute(@NotNull me.oxolotel.utils.wrapped.player.Player player, @NotNull List<String> list, @NotNull List<String> list1) {
            player.sendMessage("Zero command");
            return true;
        }
    }
}

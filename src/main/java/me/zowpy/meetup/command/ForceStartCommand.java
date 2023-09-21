package me.zowpy.meetup.command;

import lombok.RequiredArgsConstructor;
import me.zowpy.command.annotation.Command;
import me.zowpy.command.annotation.Permission;
import me.zowpy.command.annotation.Sender;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.game.enums.GameState;
import me.zowpy.meetup.game.state.IState;
import me.zowpy.meetup.game.state.impl.FightingState;
import me.zowpy.meetup.utils.CC;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class ForceStartCommand {

    private final MeetupPlugin plugin;

    @Permission("meetup.command.start")
    @Command(name = "forcestart", aliases = "start")
    public void forcestart(@Sender CommandSender sender) {
        if (plugin.getGameHandler().getGameState().getGameState() == GameState.STARTING) {
            IState state = plugin.getGameHandler().getGameState();

            FightingState fightingState = new FightingState(plugin);
            fightingState.enable();

            plugin.getGameHandler().setGameState(fightingState);
            state.disable();

            return;
        }

        if (plugin.getGameHandler().getGameState().getGameState() == GameState.WAITING) {
            plugin.getGameHandler().getGameState().disable();
            return;
        }

        sender.sendMessage(CC.RED + "The game has already started.");
    }
}

package me.zowpy.meetup.game.state.impl;

import lombok.RequiredArgsConstructor;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.game.player.MeetupPlayer;
import me.zowpy.meetup.game.scenario.Scenario;
import me.zowpy.meetup.game.enums.GameState;
import me.zowpy.meetup.game.state.IState;
import me.zowpy.meetup.game.state.SpectateState;
import me.zowpy.meetup.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class EndingState extends SpectateState implements IState, Listener {

    private final MeetupPlugin plugin;
    private final MeetupPlayer winner;

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        if (winner == null) {
            plugin.getMessages().drawMessage.forEach(s -> Bukkit.broadcastMessage(CC.translate(s)));
        }else {
            plugin.getMessages().winnerMessage.forEach(s -> Bukkit.broadcastMessage(CC.translate(s.replace("<winner>", winner.getName()))));

            plugin.getProfileHandler().win(Bukkit.getPlayer(winner.getUuid()));
        }

        plugin.getScenarioHandler().getActiveScenarios().forEach(Scenario::disable);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            Bukkit.getOnlinePlayers().forEach(player -> {
                if (winner == null) {
                    player.kickPlayer(plugin.getMessages().drawKickMessage);
                }else {
                    
                    if (!winner.getUuid().equals(player.getUniqueId())) {
                        plugin.getProfileHandler().loss(player);
                    }
                    
                    player.kickPlayer(plugin.getMessages().winnerKickMessage.replace("<winner>", winner.getName()));
                }
            });

            Bukkit.shutdown();
        }, 60L);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public GameState getGameState() {
        return GameState.ENDING;
    }
}

package me.zowpy.meetup.game.state.impl;

import lombok.RequiredArgsConstructor;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.game.player.MeetupPlayer;
import me.zowpy.meetup.game.state.IState;
import me.zowpy.meetup.game.state.SpectateState;
import me.zowpy.meetup.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class EndingState extends SpectateState implements IState, Listener {

    private final MeetupPlayer winner;

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, MeetupPlugin.getInstance());

        if (winner == null) {
            MeetupPlugin.getInstance().getMessages().drawMessage.forEach(s -> Bukkit.broadcastMessage(CC.translate(s)));
        }else {
            MeetupPlugin.getInstance().getMessages().winnerMessage.forEach(s -> Bukkit.broadcastMessage(CC.translate(s.replace("<winner>", winner.getName()))));
        }

        Bukkit.getScheduler().runTaskLater(MeetupPlugin.getInstance(), () -> {

            Bukkit.shutdown();

            Bukkit.getOnlinePlayers().forEach(player -> {
                if (winner == null) {
                    player.kickPlayer(MeetupPlugin.getInstance().getMessages().drawKickMessage);
                }else {
                    player.kickPlayer(MeetupPlugin.getInstance().getMessages().winnerKickMessage.replace("<winner>", winner.getName()));
                }
            });

        }, 60L);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }
}

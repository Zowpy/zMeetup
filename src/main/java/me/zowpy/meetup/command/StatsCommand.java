package me.zowpy.meetup.command;

import lombok.RequiredArgsConstructor;
import me.zowpy.command.annotation.Command;
import me.zowpy.command.annotation.Sender;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.profile.Profile;
import me.zowpy.meetup.utils.CC;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class StatsCommand {

    private final MeetupPlugin plugin;

    @Command(name = "stats")
    public void stats(@Sender Player player) {

        Profile profile = plugin.getProfileHandler().findOrDefault(player);

        player.sendMessage(CC.MENU_BAR);
        player.sendMessage(CC.MAIN + "Games Played: " + CC.SECONDARY + profile.getGamesPlayed());
        player.sendMessage(CC.MAIN + "Wins: " + CC.SECONDARY + profile.getWins());
        player.sendMessage(CC.MAIN + "Losses: " + CC.SECONDARY + profile.getLosses());
        player.sendMessage(CC.MAIN + "Kills: " + CC.SECONDARY + profile.getKills());
        player.sendMessage(CC.MAIN + "Deaths: " + CC.SECONDARY + profile.getDeaths());
        player.sendMessage(CC.MENU_BAR);
    }
}

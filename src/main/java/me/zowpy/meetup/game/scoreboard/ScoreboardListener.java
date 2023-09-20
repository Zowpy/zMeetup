package me.zowpy.meetup.game.scoreboard;

import io.github.thatkawaiisam.assemble.events.AssembleBoardCreatedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardListener implements Listener {

    @EventHandler
    public void onPlayer(AssembleBoardCreatedEvent event) {
        Scoreboard scoreboard = event.getBoard().getScoreboard();

        if (scoreboard.getObjective("Health") == null) {
            Objective objective = scoreboard.registerNewObjective("Health", "health");
            objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

            Bukkit.getOnlinePlayers().forEach(player -> objective.getScore(player.getName()).setScore((int) player.getHealth()));
        }
    }
}

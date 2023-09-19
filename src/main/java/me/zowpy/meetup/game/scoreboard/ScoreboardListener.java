package me.zowpy.meetup.game.scoreboard;

import io.github.thatkawaiisam.assemble.events.AssembleBoardCreatedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardListener implements Listener {

    @EventHandler
    public void onPlayer(AssembleBoardCreatedEvent event) {
        System.out.println(event.getBoard().getUuid().toString());

        Scoreboard scoreboard = event.getBoard().getScoreboard();
        Objective objective = scoreboard.getObjective("Health");

        if (objective == null) {
            objective = scoreboard.registerNewObjective("Health", "health");
        }

        objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }
}

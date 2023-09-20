package me.zowpy.meetup.game.scoreboard;

import io.github.thatkawaiisam.assemble.events.AssembleBoardCreatedEvent;
import me.zowpy.meetup.utils.CC;
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

        if (scoreboard.getObjective("h_list") == null) {
            Objective objective = scoreboard.registerNewObjective("h_list", "health");
            objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

            Objective h_belowName = scoreboard.registerNewObjective("h_below_name", "health");
            h_belowName.setDisplaySlot(DisplaySlot.BELOW_NAME);
            h_belowName.setDisplayName(CC.DARK_RED + "❤");

            Bukkit.getOnlinePlayers().forEach(player -> {
                objective.getScore(player.getName()).setScore((int) player.getHealth());
                h_belowName.getScore(player.getName()).setScore((int) Math.floor(player.getHealth()));
            });
        }
    }
}

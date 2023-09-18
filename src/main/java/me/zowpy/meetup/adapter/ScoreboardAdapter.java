package me.zowpy.meetup.adapter;

import io.github.thatkawaiisam.assemble.AssembleAdapter;
import lombok.RequiredArgsConstructor;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.game.player.MeetupPlayer;
import me.zowpy.meetup.game.scenario.impl.noclean.NoCleanScenario;
import me.zowpy.meetup.game.state.impl.FightingState;
import me.zowpy.meetup.game.state.impl.StartingState;
import me.zowpy.meetup.game.state.impl.WaitingState;
import me.zowpy.meetup.game.task.FightingStateBorderShrinkTask;
import me.zowpy.meetup.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ScoreboardAdapter implements AssembleAdapter {

    private final MeetupPlugin plugin;

    private final static World world = Bukkit.getWorld(MeetupPlugin.getInstance().getSettings().worldName);

    @Override
    public String getTitle(Player player) {
        return CC.translate(plugin.getScoreboardConfig().title);
    }

    @Override
    public List<String> getLines(Player player) {

        if (plugin.getGameHandler().getGameState() instanceof WaitingState) {

            WaitingState waitingState = (WaitingState) plugin.getGameHandler().getGameState();

            return plugin.getScoreboardConfig().waiting.stream()
                    .map(s -> CC.translate(s.replace("<players>", waitingState.remainingPlayers() + "")))
                    .collect(Collectors.toList());
        }

        if (plugin.getGameHandler().getGameState() instanceof StartingState) {

            StartingState startingState = (StartingState) plugin.getGameHandler().getGameState();
            int secondsLeft = (int) (((startingState.getStarted() + (plugin.getSettings().seconds * 1000)) - System.currentTimeMillis()) / 1000);

            return plugin.getScoreboardConfig().starting.stream()
                    .map(s -> CC.translate(s.replace("<seconds>", secondsLeft + "")))
                    .collect(Collectors.toList());
        }

        if (plugin.getGameHandler().getGameState() instanceof FightingState) {

            FightingState fightingState = (FightingState) plugin.getGameHandler().getGameState();
            MeetupPlayer meetupPlayer = plugin.getGameHandler().getPlayer(player);

            int secondsLeft = (int) (((fightingState.getShrinkTask().getLastShrink() + (FightingStateBorderShrinkTask.SECONDS_PER_UPDATE * 1000)) - System.currentTimeMillis()) / 1000);


            //System.out.println(plugin.getScenarioHandler().getScenario(NoCleanScenario.class) == null);
            if (plugin.getScenarioHandler().isEnabled("noclean") && plugin.getScenarioHandler().getScenario(NoCleanScenario.class).getNoClean().containsKey(player.getUniqueId())) {

                NoCleanScenario noClean = plugin.getScenarioHandler().getScenario(NoCleanScenario.class);

                return plugin.getScoreboardConfig().fighting.stream()
                        .map(s -> CC.translate(s
                                .replace("<border>", plugin.getBorderHandler().getBorderForWorld(world).getSize() + "")
                                .replace("<seconds>", secondsLeft + "")
                                .replace("<players>", fightingState.remainingPlayers() + "")
                                .replace("<ping>", ((CraftPlayer) player).getHandle().ping + "")
                                .replace("<kills>", meetupPlayer.getKills() + "")
                                .replace("<spaceifnoclean>", " ")
                                .replace("<noclean>",
                                        plugin.getScoreboardConfig().noCleanFormat.replace("<seconds>", secondsLeft(noClean.getNoClean().get(player.getUniqueId())) + ""))
                        )).collect(Collectors.toList());
            }

            return plugin.getScoreboardConfig().fighting.stream()
                    .filter(s -> !s.contains("<noclean>") && !s.contains("<spaceifnoclean>"))
                    .map(s -> CC.translate(s
                            .replace("<border>", plugin.getBorderHandler().getBorderForWorld(world).getSize() + "")
                            .replace("<seconds>", secondsLeft + "")
                            .replace("<players>", fightingState.remainingPlayers() + "")
                            .replace("<ping>", ((CraftPlayer) player).getHandle().ping + "")
                            .replace("<kills>", meetupPlayer.getKills() + "")
                    )).collect(Collectors.toList());
        }

        return Arrays.asList("coming", "soon");
    }

    private int secondsLeft(long l) {
        return (int) (l + (15 * 1000) - System.currentTimeMillis()) / 1000;
    }
}

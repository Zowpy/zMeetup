package me.zowpy.meetup.command;

import lombok.RequiredArgsConstructor;
import me.zowpy.command.annotation.Command;
import me.zowpy.command.annotation.Optional;
import me.zowpy.command.annotation.Sender;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.game.enums.GameState;
import me.zowpy.meetup.game.enums.SpectateReason;
import me.zowpy.meetup.utils.CC;
import me.zowpy.meetup.utils.TextBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class SpectateCommand {

    private final MeetupPlugin plugin;

    @Command(name = "spectate", aliases = "spec")
    public void spectate(@Sender Player player, @Optional String value) {

        boolean playing = plugin.getGameHandler().isPlaying(player);

        if (!playing && plugin.getGameHandler().getGameState().getGameState() != GameState.WAITING) {
            player.sendMessage(CC.RED + "You can't do this anymore.");
            return;
        }

        if (value != null) {
            if (playing && value.equalsIgnoreCase("yes")) {
                plugin.getGameHandler().handleSpectator(player, SpectateReason.CHOSE);
                return;
            }

            if (!playing && value.equalsIgnoreCase("no")) {
                plugin.getGameHandler().removeSpectator(player);

                if (plugin.getSettings().spawnLocation != null) {
                    player.teleport(plugin.getSettings().spawnLocation);
                }
            }

            return;
        }

        TextBuilder textBuilder;

        if (playing) {
            textBuilder = new TextBuilder()
                    .setText(plugin.getMessages().spectateConfirmation)
                    .addExtra(
                            new TextBuilder(new TextComponent(TextComponent.fromLegacyText(plugin.getMessages().spectateConfirmationButton)))
                                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spec yes"))
                                    .build()
                    );

        } else {
            textBuilder = new TextBuilder()
                    .setText(plugin.getMessages().spectateStopConfirmation)
                    .addExtra(
                            new TextBuilder(new TextComponent(TextComponent.fromLegacyText(plugin.getMessages().spectateStopConfirmationButton)))
                                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spec no"))
                                    .build()
                    );

        }

        player.spigot().sendMessage(textBuilder.build());
    }
}

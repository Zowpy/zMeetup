package me.zowpy.meetup.command;

import lombok.RequiredArgsConstructor;
import me.zowpy.command.annotation.Command;
import me.zowpy.command.annotation.Permission;
import me.zowpy.command.annotation.Sender;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.utils.CC;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class SetSpawnCommand {

    private final MeetupPlugin plugin;

    @Permission("meetup.command.setspawn")
    @Command(name = "setspawn")
    public void setspawn(@Sender Player player) {
        plugin.getSettings().spawnLocation = player.getLocation().clone();
        plugin.getConfigFactory().save("settings", plugin.getSettings());

        player.sendMessage(CC.MAIN + "You have set the lobby spawn point!");
    }
}

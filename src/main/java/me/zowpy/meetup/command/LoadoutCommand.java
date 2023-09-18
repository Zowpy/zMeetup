package me.zowpy.meetup.command;

import lombok.RequiredArgsConstructor;
import me.zowpy.command.annotation.Command;
import me.zowpy.command.annotation.Sender;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.menu.LoadoutMenu;
import me.zowpy.meetup.profile.Profile;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class LoadoutCommand {

    private final MeetupPlugin plugin;

    @Command(name = "loadout")
    public void loadout(@Sender Player player) {
        Profile profile = plugin.getProfileHandler().findOrDefault(player);
        new LoadoutMenu(profile).openMenu(player);
    }
}

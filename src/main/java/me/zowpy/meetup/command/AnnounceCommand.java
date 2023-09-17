package me.zowpy.meetup.command;

import lombok.RequiredArgsConstructor;
import me.zowpy.command.annotation.Command;
import me.zowpy.command.annotation.Permission;
import me.zowpy.command.annotation.Sender;
import me.zowpy.core.api.profile.Profile;
import me.zowpy.core.bukkit.CorePlugin;
import me.zowpy.core.bukkit.utils.TextBuilder;
import me.zowpy.meetup.MeetupPlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class AnnounceCommand {

    private final MeetupPlugin plugin;

    @Permission("meetup.command.announce")
    @Command(name = "announce")
    public void announce(@Sender Player player) {
        Profile profile = CorePlugin.getInstance().getProfileManager().getByUUID(player.getUniqueId());

        TextBuilder textBuilder = new TextBuilder();
        textBuilder.setText(plugin.getMessages().announce.replace("<player>", profile.getColoredName()));

        TextComponent click = new TextComponent(plugin.getMessages().announceJoin);
        click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server um-01"));

        textBuilder.addExtra(click);

        CorePlugin.getInstance().sendTextComponentGlobally(textBuilder.build());
    }
}

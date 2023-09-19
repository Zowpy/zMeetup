package me.zowpy.meetup.command;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import me.zowpy.command.annotation.Command;
import me.zowpy.command.annotation.Permission;
import me.zowpy.command.annotation.Sender;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.utils.BungeeUtil;
import me.zowpy.meetup.utils.CC;
import me.zowpy.meetup.utils.TextBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class AnnounceCommand {

    private final MeetupPlugin plugin;

    @Permission("meetup.command.announce")
    @Command(name = "announce")
    public void announce(@Sender Player player) {
        TextBuilder textBuilder = new TextBuilder();

        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            textBuilder.setText(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().announce.replace("<player>", player.getName())));
        } else {
            textBuilder.setText(plugin.getMessages().announce.replace("<player>", player.getName()));
        }

        TextComponent click = new TextComponent(plugin.getMessages().announceJoin);
        click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, plugin.getSettings().joinCommand));
        click.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                plugin.getMessages().announceJoinHover.stream().map(s -> new TextComponent(CC.translate("\n" + s))).toArray(BaseComponent[]::new)
        ));

        textBuilder.addExtra(click);

        BungeeUtil.announce(player, textBuilder.build());
    }
}

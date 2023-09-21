package me.zowpy.meetup.command;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import me.zowpy.command.annotation.Command;
import me.zowpy.command.annotation.Permission;
import me.zowpy.command.annotation.Sender;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.game.task.FightingStateBorderShrinkTask;
import me.zowpy.meetup.profile.Profile;
import me.zowpy.meetup.utils.BungeeUtil;
import me.zowpy.meetup.utils.CC;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AnnounceCommand {

    private final MeetupPlugin plugin;

    @Permission("meetup.command.announce")
    @Command(name = "announce")
    public void announce(@Sender Player player) {
        Profile profile = plugin.getProfileHandler().findOrDefault(player);

        if (System.currentTimeMillis() - profile.getLastAnnounce() <= (plugin.getSettings().announceCooldown * 1000L)) {
            int secondsLeft = (int) (((profile.getLastAnnounce() + (plugin.getSettings().announceCooldown * 1000)) - System.currentTimeMillis()) / 1000);


            player.sendMessage(plugin.getMessages().announceCooldownMessage.replace("<seconds>",
                    secondsLeft + ""
            ));
            return;
        }

        profile.setLastAnnounce(System.currentTimeMillis());
        plugin.getProfileHandler().save(profile);

        TextComponent textBuilder = new TextComponent();

        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            textBuilder.setText(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().announce.replace("<player>", player.getName())));
        } else {
            textBuilder.setText(plugin.getMessages().announce.replace("<player>", player.getName()));
        }

        textBuilder.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, plugin.getSettings().joinCommand));

        List<String> s = new ArrayList<>();

        for (int i = 0; i < plugin.getMessages().announceHover.size(); i++) {
            if (i > 0) {
                s.add("\n" + CC.translate(plugin.getMessages().announceHover.get(i)));
            } else {
                s.add(CC.translate(plugin.getMessages().announceHover.get(i)));
            }
        }

        textBuilder.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                s.stream().map(TextComponent::new).toArray(BaseComponent[]::new)
        ));

        BungeeUtil.announce(player, textBuilder);
    }
}

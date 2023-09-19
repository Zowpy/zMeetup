package me.zowpy.meetup.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.zowpy.meetup.MeetupPlugin;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;

public class BungeeUtil {

    public static void announce(Player player, TextComponent textComponent) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("MessageRaw");
        out.writeUTF("ALL");
        out.writeUTF(ComponentSerializer.toString(textComponent));

        player.sendPluginMessage(MeetupPlugin.getInstance(), "BungeeCord", out.toByteArray());
    }
}

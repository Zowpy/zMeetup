package me.zowpy.meetup.game.listener;

import lombok.RequiredArgsConstructor;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.game.player.MeetupPlayer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class GameListener implements Listener {

    private final MeetupPlugin plugin;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        MeetupPlayer meetupPlayer = new MeetupPlayer(player);
        plugin.getGameHandler().getPlayers().put(player.getUniqueId(), meetupPlayer);

        plugin.getGameHandler().getPlayers().values().stream().filter(meetupPlayer1 -> meetupPlayer1.isSpectating() || meetupPlayer1.isDead())
                .forEach(meetupPlayer1 -> {

                    Player bukkitPlayer = Bukkit.getPlayer(meetupPlayer1.getUuid());
                    player.hidePlayer(bukkitPlayer);

                    EntityPlayer entityPlayer = ((CraftPlayer) bukkitPlayer).getHandle();

                    PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(info);
                });
    }
}

package me.zowpy.meetup.profile;

import lombok.RequiredArgsConstructor;
import me.zowpy.meetup.MeetupPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class ProfileListener implements Listener {

    private final MeetupPlugin plugin;

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {

        Profile profile = plugin.getProfileHandler().findByUUID(event.getUniqueId());

        if (profile == null) return;

        if (!profile.getName().equals(event.getName())) {
            profile.setName(event.getName());

            plugin.getProfileHandler().save(profile);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        plugin.getGameHandler().getPlayers().remove(event.getPlayer().getUniqueId());
    }
}

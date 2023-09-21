package me.zowpy.meetup.profile;

import lombok.RequiredArgsConstructor;
import me.zowpy.meetup.MeetupPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

@RequiredArgsConstructor
public class ProfileListener implements Listener {

    private final MeetupPlugin plugin;

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {

        Profile profile = plugin.getProfileHandler().findByUUID(event.getUniqueId());

        if (profile == null) return;

        if (!profile.getName().equals(event.getName())) {
            profile.setName(event.getName());

            plugin.getProfileHandler().save(profile);
        }
    }
}

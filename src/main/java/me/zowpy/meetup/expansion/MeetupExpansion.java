package me.zowpy.meetup.expansion;

import com.google.common.base.Joiner;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.leaderboard.LeaderboardEntry;
import me.zowpy.meetup.leaderboard.enums.LeaderboardType;
import me.zowpy.meetup.profile.Profile;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MeetupExpansion extends PlaceholderExpansion {

    private final MeetupPlugin plugin;

    @Override
    public @NotNull String getIdentifier() {
        return "zMeetup";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Zowpy";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {

        // %zMeetup_profile_kills%
        // %zMeetup_profile_deaths%
        // %zMeetup_profile_losses%
        // %zMeetup_profile_wins%
        // %zMeetup_profile_gamesPlayed%

        // %zMeetup_leaderboard_nextupdate%
        // %zMeetup_leaderboard_<position>_<type>%

        Profile profile = plugin.getProfileHandler().findOrDefault(player);

        if (params.equalsIgnoreCase("profile_kills")) {
            return profile.getKills() + "";
        }

        if (params.equalsIgnoreCase("profile_deaths")) {
            return profile.getDeaths() + "";
        }


        if (params.equalsIgnoreCase("profile_losses")) {
            return profile.getLosses() + "";
        }

        if (params.equalsIgnoreCase("profile_wins")) {
            return profile.getWins() + "";
        }

        if (params.equalsIgnoreCase("profile_gamesPlayed")) {
            return profile.getGamesPlayed() + "";
        }

        if (params.equalsIgnoreCase("leaderboard_nextupdate")) {
            int seconds = (int) (((plugin.getLeaderboardHandler().getLastUpdate() + plugin.getSettings().leaderboardInterval * 1000L) - System.currentTimeMillis()) / 1000L);

            return seconds == -1 ? plugin.getSettings().leaderboardInterval + "" : seconds + "";
        }

        if (params.startsWith("leaderboard")) {

            String[] args = params.split("_");

            if (args.length != 3) {
                return "Invalid leaderboard format! %zMeetup_leaderboard_<position>_<type>%";
            }

            int position;

            try {
                position = Integer.parseInt(args[1]);
            }catch (Exception e) {
                return "Expected integer but found: " + args[1];
            }

            if (position > 10) {
                return position + " is over the limit, The maximum position is 10.";
            }

            LeaderboardType type;

            try {
                type = LeaderboardType.valueOf(args[2].toUpperCase());
            }catch (Exception e) {
                return args[2] + " is not a valid leaderboard type, Choose from: " + Joiner.on(", ").join(Arrays.stream(LeaderboardType.values()).map(Enum::name).collect(Collectors.toList()));
            }

            List<LeaderboardEntry> entries = plugin.getLeaderboardHandler().getLeaderboardEntries().get(type);

            if (entries == null) {
                return plugin.getSettings().leaderboardFormat.replace("<name>", "N/A").replace("<value>", "0");
            }

            if (entries.size() < position) {
                return plugin.getSettings().leaderboardFormat.replace("<name>", "N/A").replace("<value>", "0");
            }

            LeaderboardEntry entry = entries.get(position - 1);

            return plugin.getSettings().leaderboardFormat.replace("<name>", entry.getName())
                    .replace("<value>", entry.getValue() + "");
        }

        return null;
    }
}

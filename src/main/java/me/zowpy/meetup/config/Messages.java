package me.zowpy.meetup.config;

import me.zowpy.meetup.utils.CC;
import xyz.mkotb.configapi.Coloured;
import xyz.mkotb.configapi.comment.Comment;

import java.util.Arrays;
import java.util.List;

public class Messages {

    @Coloured
    public String generatingWorldKick = "&cThe map is not ready yet!";

    @Coloured
    public String requiresPlayersToStart = "&eThe game requires &d<players> &eplayers to start.";

    @Coloured
    public String starting = "&eThe game will begin in &d<seconds> seconds&e.";

    @Comment("Which seconds should we broadcast the starting message?")
    public List<Integer> secondsBroadcast = Arrays.asList(
            30, 15, 10, 5, 4, 3, 2, 1
    );

    @Coloured
    public String restarting = "&cRestarting countdown due to lack of players.";

    @Coloured
    public String started = "&eThe game has started!";

    @Coloured
    public String borderExit = "&cYou must stay within the border.";

    @Coloured
    public String teleportInsideBorder = "&c&lYou've been teleported inside the border.";

    @Coloured
    public String borderShrink = "&eThe border will shrink to &d<blocks>&e blocks in &d<time> <unit>&e.";

    @Comment("the format that will be used for death message. (<player>) This supports PlaceholderAPI")
    @Coloured
    public String playerFormat = "%volcano_profile_colored_name%";

    @Coloured
    public String killMessage = "<player> &7[&c<player_kills>&7] &ewas slain by <killer> &7[&c<killer_kills>&7] &eusing &c<item>";

    @Coloured
    public String lavaDeath = "<player> &7[&c<kills>&7] &etried to swim in lava.";

    @Coloured
    public String fallDeath = "<player> &7[&c<kills>&7] &ehas fallen to their death.";

    @Coloured
    public String otherDeath = "<player> &7[&c<kills>&7] &ewas slain.";

    public List<String> winnerMessage = Arrays.asList(
            " ",
            CC.PINK + "<winner>&a wins!",
            " "
    );

    public List<String> drawMessage = Arrays.asList(
            " ",
            CC.YELLOW + "Its a draw",
            " "
    );

    @Coloured
    public String winnerKickMessage = "&eThank you for playing! Winner: &d<winner>";

    @Coloured
    public String drawKickMessage = "&eThank you for playing! &dIt was a draw";

    @Coloured
    public String announce = CC.PINK + CC.BOLD + "UHC Meetup " + CC.GRAY + "» <player>&e wants you to play!";

    @Comment("This clickable message requires Volcano Core.")
    @Coloured
    public String announceJoin = " &a(Join)";

    @Coloured
    public String startingTitle = "&a&l<seconds>";

    @Coloured
    public String startingSubTitle = "Game is starting!";

    @Coloured
    public String startedTitle = "&a&lBEGIN";

    @Coloured
    public String startedSubtitle = "Game has started!";

    @Coloured
    public String noCleanStart = "&aYou now have no clean timer.";

    @Coloured
    public String noCleanExpiring = "&cNo clean will expire in <seconds> <unit>.";

    @Coloured
    public String noCleanExpired = "&c&lYour no clean timer has expired.";

    @Coloured
    public String noCleanHit = "&c<player>'s no clean timer expires in <seconds> <unit>.";
}

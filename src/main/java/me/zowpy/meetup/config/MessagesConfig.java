package me.zowpy.meetup.config;

import me.zowpy.meetup.utils.CC;
import xyz.mkotb.configapi.Coloured;
import xyz.mkotb.configapi.comment.Comment;

import java.util.Arrays;
import java.util.List;

public class MessagesConfig {

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

    @Coloured
    public String gameEnding = "&eThe game will end in &d<time> <unit>&e.";

    @Comment("the format that will be used for death message. (<player>) This supports PlaceholderAPI")
    @Coloured
    public String playerFormat = "<player>";

    @Coloured
    public String killMessage = "&a<player> &7[&c<player_kills>&7] &ewas slain by &a<killer> &7[&c<killer_kills>&7] &eusing &c<item>";

    @Coloured
    public String lavaDeath = "&a<player> &7[&c<kills>&7] &etried to swim in lava.";

    @Coloured
    public String fallDeath = "&a<player> &7[&c<kills>&7] &ehas fallen to their death.";

    @Coloured
    public String otherDeath = "&a<player> &7[&c<kills>&7] &ewas slain.";

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

    @Comment({"This message requires BungeeCord or Velocity.", "This message supports PlaceholderAPI"})
    @Coloured
    public String announce = CC.PINK + CC.BOLD + "UHC Meetup " + CC.GRAY + "» <player>&e wants you to play! &a(Join)";

    public List<String> announceHover = Arrays.asList(
            "&eClick to join um-01",
            " ",
            "&c&lWarning: &r&cThis will switch your server!"
    );

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

    @Coloured
    public String noCleanRemoved = "&cYour no clean timer has been removed since you hit a player.";

    @Coloured
    public String spectateReasonChose = "&cchose to watch";

    @Coloured
    public String spectateReasonDied = "&cdied";

    @Coloured
    public String spectateReasonJoinedTooLate = "&cjoined too late";

    @Coloured
    public String spectateReasonMessage = "&eYou have been put into spectator: <reason>";

    @Coloured
    public String spectateConfirmation = "&7Are you sure you want to spectate?";

    @Coloured
    public String spectateConfirmationButton = "&a [Click here to spectate]";

    @Coloured
    public String spectateStopConfirmation = "&7Are you sure you want to stop spectating?";

    @Coloured
    public String spectateStopConfirmationButton = "&c [Click here to stop spectating]";

    @Coloured
    public String arrowHit = "&d<player> &eis now at &d<hearts>❤";

    @Coloured
    public String spectateTitle = "&c&lDEAD";

    @Coloured
    public String spectateSubtitle = "You are now spectator!";

    @Coloured
    public String editingLoadout = "&aYou are now editing your loadout.";

    public List<String> modifiedLoadout = Arrays.asList(
            "&eYou've modified your loadout!",
            "&7&oIf you need to reset your loadout try &e&o/resetloadout"
    );

    @Coloured
    public String resetLoadout = "&aYou've reset your loadout!";
}

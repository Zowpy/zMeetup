package me.zowpy.meetup.config;

import me.zowpy.meetup.utils.CC;

import java.util.Arrays;
import java.util.List;

public class ScoreboardConfig {

    public String title = CC.PINK + CC.BOLD + "UHC Meetup";

    public List<String> waiting = Arrays.asList(
            CC.MENU_BAR,
            "Waiting for players",
            CC.PINK + "<players>" + CC.WHITE + " more players.",
            " ",
            CC.PINK + "minemen.club",
            CC.MENU_BAR
    );

    public List<String> starting = Arrays.asList(
            CC.MENU_BAR,
            "The game will start in:",
            "&d<seconds>",
            " ",
            CC.PINK + "minemen.club",
            CC.MENU_BAR
    );

    public String noCleanFormat = "&cNo clean: &d<seconds>";
    public String borderTimerFormat = " &f(&d<seconds>&f)";

    public List<String> fighting = Arrays.asList(
            CC.MENU_BAR,
            "Border: " + CC.PINK + "<border><borderTimer>",
            "Players: " + CC.PINK + "<players>",
            "Ping: " + CC.PINK + "<ping> ms",
            "Kills: " + CC.PINK + "<kills>",
            " ",
            "<noclean>",
            "<spaceifnoclean>",
            CC.PINK + "minemen.club",
            CC.MENU_BAR
    );
}

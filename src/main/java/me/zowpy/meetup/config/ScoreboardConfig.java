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

    public List<String> fighting = Arrays.asList(
            CC.MENU_BAR,
            "Border: " + CC.PINK + "<border> &f(&d<seconds>&f)",
            "Players: " + CC.PINK + "<players>",
            "Ping: " + CC.PINK + "<ping> ms",
            "Kills: " + CC.PINK + "<kills>",
            " ",
            CC.PINK + "minemen.club",
            CC.MENU_BAR
    );
}

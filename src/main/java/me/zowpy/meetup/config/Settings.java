package me.zowpy.meetup.config;

import org.bukkit.Location;
import xyz.mkotb.configapi.Coloured;
import xyz.mkotb.configapi.comment.Comment;

import java.util.Arrays;
import java.util.List;

public class Settings {

    @Coloured
    public String primaryColor = "&e";

    @Coloured
    public String secondaryColor = "&d";

    public int startingBorderSize = 125;
    public int borderHeight = 50;

    @Comment("make sure that this name is not used by any other world or that world will be deleted!")
    public String worldName = "uhc_meetup";

    @Comment("the minimum amount of players required to start the game")
    public int minPlayers = 2;

    public Location spawnLocation = null;

    @Comment("How many seconds should the countdown be?")
    public int seconds = 60;

    @Comment({"These are all the border sizes that will occur in-game each border size stays for 2 minutes", "after that it changes to another smaller one, if a smaller one is not present the game will end in a draw", "Make sure all the values below are smaller than the starting border size"})
    public List<Integer> borderSizes = Arrays.asList(
            100, 75, 50, 50, 25, 10
    );

    @Comment("Should the plugin display titles?")
    public boolean titles = true;

    @Comment("Which seconds should the starting title appear on? (this will not appear if you have titles disabled)")
    public List<Integer> titleSeconds = Arrays.asList(
            15, 10, 5, 4, 3, 2, 1
    );

    @Comment("How many seconds should we wait before the death chest explodes?")
    public int timeBombSeconds = 30;

    public float timeBombExplosionPower = 3.0f;
}

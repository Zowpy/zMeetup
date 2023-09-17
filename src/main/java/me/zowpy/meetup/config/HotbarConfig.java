package me.zowpy.meetup.config;

import me.zowpy.meetup.utils.CC;
import org.bukkit.Material;
import xyz.mkotb.configapi.Coloured;
import xyz.mkotb.configapi.comment.Comment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HotbarConfig {

    public Material spectateMenuMaterial = Material.PAINTING;

    @Coloured
    public String spectateMenuName = "&eSpectate Menu";
    public List<String> spectateMenuLore = Arrays.asList(
            "&eSee a list of players",
            "&ethat you're able to",
            "&eteleport to and spectate."
    );
    public int spectateMenuSlot = 0;

    @Comment("This requires WorldEdit and requires them to be able to use world edit's pass through compass to work correctly.")
    public boolean enableNavigation = true;

    public Material navigation = Material.COMPASS;
    public String navigationName = CC.AQUA + "Navigation Compass";
    public List<String> navigationLore = Arrays.asList(
            CC.PINK + "Left-Click: " + CC.YELLOW + "Teleport to the block you're looking at!",
            CC.PINK + "Right-Click: " + CC.YELLOW + "Teleport through walls!"
    );
    public int navigationSlot = 1;
}

package me.zowpy.meetup.command;

import me.zowpy.command.annotation.Command;
import me.zowpy.command.annotation.Sender;
import me.zowpy.meetup.loadout.Loadout;
import me.zowpy.meetup.menu.LoadoutMenu;
import org.bukkit.entity.Player;

public class LoadoutCommand {

    @Command(name = "loadout", async = false)
    public void loadout(@Sender Player player) {
        new LoadoutMenu(new Loadout()).openMenu(player);
    }
}

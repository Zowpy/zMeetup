package me.zowpy.meetup.border.event.border;

import lombok.Getter;
import me.zowpy.meetup.utils.Cuboid;
import me.zowpy.meetup.border.Border;

@Getter
public class BorderChangeEvent extends BorderEvent {

    private final int previousSize;
    private final Cuboid previousBounds;
    //private final BorderTask.BorderAction action;

    public BorderChangeEvent(Border border, int previousSize, Cuboid previousBounds) {
        super(border);

        this.previousSize = previousSize;
        this.previousBounds = previousBounds;
        //this.action = action;
    }

}


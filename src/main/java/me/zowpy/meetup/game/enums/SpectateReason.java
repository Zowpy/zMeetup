package me.zowpy.meetup.game.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.zowpy.meetup.MeetupPlugin;

@Getter @RequiredArgsConstructor
public enum SpectateReason {

    CHOSE,
    DIED,
    JOINED_TOO_LATE;

    public String getName() {

        switch (this) {
            case CHOSE: {
                return MeetupPlugin.getInstance().getMessages().spectateReasonChose;
            }

            case DIED: {
                return MeetupPlugin.getInstance().getMessages().spectateReasonDied;
            }

            case JOINED_TOO_LATE: {
                return MeetupPlugin.getInstance().getMessages().spectateReasonJoinedTooLate;
            }
        }

        return "N/A";
    }
}

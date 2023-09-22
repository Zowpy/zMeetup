package me.zowpy.meetup.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class RedisCredentials {

    private String host;
    private int port;
    private boolean auth;
    private String password;
}

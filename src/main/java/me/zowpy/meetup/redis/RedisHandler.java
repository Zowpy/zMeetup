package me.zowpy.meetup.redis;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.config.SettingsConfig;
import me.zowpy.meetup.game.GameHandler;
import me.zowpy.meetup.game.enums.GameState;
import me.zowpy.meetup.game.player.MeetupPlayer;
import me.zowpy.meetup.redis.internal.JedisHandler;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;

public class RedisHandler {

    private final boolean enabled;
    private JedisHandler jedisHandler;

    public RedisHandler(boolean enabled, SettingsConfig.RedisCredentials redisCredentials) {
        this.enabled = enabled;

        if (enabled) {
            jedisHandler = new JedisHandler(redisCredentials);
        }
    }

    public void saveGameData(GameHandler gameHandler, GameState gameState) {
        if (!enabled) return;

        CompletableFuture.runAsync(() -> {
            jedisHandler.runCommand(jedis -> {

                JsonObject object = new JsonObject();

                JsonArray alivePlayers = new JsonArray();
                JsonArray deadPlayers = new JsonArray();

                for (MeetupPlayer meetupPlayer : gameHandler.getPlayers().values()) {
                    if (meetupPlayer.isPlaying()) {
                        alivePlayers.add(new JsonPrimitive(meetupPlayer.getUuid().toString()));
                    }else {
                        deadPlayers.add(new JsonPrimitive(meetupPlayer.getUuid().toString()));
                    }
                }

                object.addProperty("state", gameState.name());
                object.add("alivePlayers", alivePlayers);
                object.add("deadPlayers", deadPlayers);
                object.addProperty("online", Bukkit.getOnlinePlayers().size());

                jedis.hset("zMeetup", MeetupPlugin.getInstance().getSettings().serverId, object.toString());
            });
        });
    }
}

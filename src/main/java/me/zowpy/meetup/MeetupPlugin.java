package me.zowpy.meetup;

import io.github.thatkawaiisam.assemble.Assemble;
import lombok.Getter;
import me.zowpy.command.CommandAPI;
import me.zowpy.meetup.adapter.ScoreboardAdapter;
import me.zowpy.meetup.border.BorderHandler;
import me.zowpy.meetup.command.*;
import me.zowpy.meetup.config.*;
import me.zowpy.meetup.database.MongoHandler;
import me.zowpy.meetup.expansion.MeetupExpansion;
import me.zowpy.meetup.game.GameHandler;
import me.zowpy.meetup.game.prevention.PreventionListener;
import me.zowpy.meetup.game.scenario.ScenarioHandler;
import me.zowpy.meetup.game.scoreboard.ScoreboardListener;
import me.zowpy.meetup.leaderboard.LeaderboardHandler;
import me.zowpy.meetup.leaderboard.task.LeaderboardTask;
import me.zowpy.meetup.loadout.LoadoutHandler;
import me.zowpy.meetup.profile.ProfileHandler;
import me.zowpy.meetup.utils.CC;
import me.zowpy.meetup.utils.ConfigFile;
import me.zowpy.meetup.utils.menu.ButtonListener;
import me.zowpy.meetup.utils.menu.MenuUpdateTask;
import me.zowpy.meetup.world.WorldGenerator;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.mkotb.configapi.ConfigFactory;

@Getter
public final class MeetupPlugin extends JavaPlugin implements Listener {

    @Getter
    private static MeetupPlugin instance;

    private ConfigFactory configFactory;
    private SettingsConfig settings;
    private MessagesConfig messages;
    private ScoreboardConfig scoreboardConfig;
    private HotbarConfig hotbarConfig;
    private MenusConfig menusConfig;

    private ConfigFile loadoutsFile;

    private MongoHandler mongoHandler;
    private ProfileHandler profileHandler;
    private GameHandler gameHandler;
    private WorldGenerator worldGenerator;
    private BorderHandler borderHandler;
    private LoadoutHandler loadoutHandler;
    private ScenarioHandler scenarioHandler;
    private LeaderboardHandler leaderboardHandler;

    private LeaderboardTask leaderboardTask;

    private Assemble assemble;

    private boolean ready;

    @Override
    public void onEnable() {
        instance = this;

        configFactory = ConfigFactory.newFactory(this);
        settings = configFactory.fromFile("settings", SettingsConfig.class);
        messages = configFactory.fromFile("messages", MessagesConfig.class);
        scoreboardConfig = configFactory.fromFile("scoreboard", ScoreboardConfig.class);
        hotbarConfig = configFactory.fromFile("hotbar", HotbarConfig.class);
        menusConfig = configFactory.fromFile("menus", MenusConfig.class);

        loadoutsFile = new ConfigFile(this, "loadouts");

        try {
            mongoHandler = new MongoHandler(settings.mongoDB_URI);
        }catch (Exception e) {
            e.printStackTrace();

            getServer().getConsoleSender().sendMessage(CC.RED + "Failed to connect to MongoDB.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        profileHandler = new ProfileHandler(this);
        scenarioHandler = new ScenarioHandler();

        gameHandler = new GameHandler(this);
        gameHandler.getGameState().enable();

        borderHandler = new BorderHandler(this);

        worldGenerator = new WorldGenerator(settings.worldName, this);
        worldGenerator.generate();

        loadoutHandler = new LoadoutHandler(this);

        leaderboardHandler = new LeaderboardHandler(this);
        leaderboardTask = new LeaderboardTask(this);

        new CommandAPI(this)
                .beginCommandRegister()
                .register(new SetSpawnCommand(this))
                .register(new AnnounceCommand(this))
                .register(new LoadoutCommand(this))
                .register(new StatsCommand(this))
                .register(new SpectateCommand(this))
                .endRegister();

        assemble = new Assemble(this, new ScoreboardAdapter(this));

        getServer().getPluginManager().registerEvents(new PreventionListener(), this);
        getServer().getPluginManager().registerEvents(new ButtonListener(this), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new MeetupExpansion(this).register();
        }

        new MenuUpdateTask(this);

        ready = true;
    }

    @Override
    public void onDisable() {

        if (assemble != null)
            assemble.cleanup();

        if (leaderboardTask != null)
            leaderboardTask.cancel();

        mongoHandler.close();

        instance = null;
    }
}

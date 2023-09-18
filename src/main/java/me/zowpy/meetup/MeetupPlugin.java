package me.zowpy.meetup;

import io.github.thatkawaiisam.assemble.Assemble;
import io.github.thatkawaiisam.assemble.events.AssembleBoardCreateEvent;
import io.github.thatkawaiisam.assemble.events.AssembleBoardCreatedEvent;
import lombok.Getter;
import me.zowpy.command.CommandAPI;
import me.zowpy.core.bukkit.utils.menu.MenuAPI;
import me.zowpy.meetup.adapter.ScoreboardAdapter;
import me.zowpy.meetup.border.BorderHandler;
import me.zowpy.meetup.command.AnnounceCommand;
import me.zowpy.meetup.command.LoadoutCommand;
import me.zowpy.meetup.command.SetSpawnCommand;
import me.zowpy.meetup.config.*;
import me.zowpy.meetup.game.GameHandler;
import me.zowpy.meetup.game.prevention.PreventionListener;
import me.zowpy.meetup.game.scenario.ScenarioHandler;
import me.zowpy.meetup.game.scoreboard.ScoreboardListener;
import me.zowpy.meetup.loadout.LoadoutHandler;
import me.zowpy.meetup.utils.ConfigFile;
import me.zowpy.meetup.utils.menu.MenuUpdateTask;
import me.zowpy.meetup.utils.menu.ButtonListener;
import me.zowpy.meetup.world.WorldGenerator;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.mkotb.configapi.ConfigFactory;

@Getter
public final class MeetupPlugin extends JavaPlugin implements Listener {

    @Getter
    private static MeetupPlugin instance;

    private ConfigFactory configFactory;
    private Settings settings;
    private Messages messages;
    private ScoreboardConfig scoreboardConfig;
    private HotbarConfig hotbarConfig;
    private MenusConfig menusConfig;

    private ConfigFile loadoutsFile;

    private GameHandler gameHandler;
    private WorldGenerator worldGenerator;
    private BorderHandler borderHandler;
    private LoadoutHandler loadoutHandler;
    private ScenarioHandler scenarioHandler;

    private Assemble assemble;

    private boolean ready;

    @Override
    public void onEnable() {
        instance = this;

        configFactory = ConfigFactory.newFactory(this);
        settings = configFactory.fromFile("settings", Settings.class);
        messages = configFactory.fromFile("messages", Messages.class);
        scoreboardConfig = configFactory.fromFile("scoreboard", ScoreboardConfig.class);
        hotbarConfig = configFactory.fromFile("hotbar", HotbarConfig.class);
        menusConfig = configFactory.fromFile("menus", MenusConfig.class);

        loadoutsFile = new ConfigFile(this, "loadouts");

        scenarioHandler = new ScenarioHandler();

        gameHandler = new GameHandler(this);
        gameHandler.getGameState().enable();

        borderHandler = new BorderHandler(this);

        worldGenerator = new WorldGenerator(settings.worldName, this);
        worldGenerator.generate();

        loadoutHandler = new LoadoutHandler(this);

        new CommandAPI(this)
                .beginCommandRegister()
                .register(new SetSpawnCommand(this))
                .register(new AnnounceCommand(this))
                .register(new LoadoutCommand())
                .endRegister();

        assemble = new Assemble(this, new ScoreboardAdapter(this));
        new MenuAPI(this);

        getServer().getPluginManager().registerEvents(new PreventionListener(), this);
        getServer().getPluginManager().registerEvents(new ButtonListener(this), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);

        new MenuUpdateTask(this);

        ready = true;
    }

    @Override
    public void onDisable() {

        if (assemble != null)
            assemble.cleanup();

        instance = null;
    }
}

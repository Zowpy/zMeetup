package me.zowpy.meetup.game.scenario;

import lombok.Getter;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.game.scenario.impl.TimeBombScenario;
import me.zowpy.meetup.game.scenario.impl.NoCleanScenario;

import java.util.ArrayList;
import java.util.List;

public class ScenarioHandler {

    @Getter
    private final List<Scenario> activeScenarios = new ArrayList<>();

    public ScenarioHandler() {
        activeScenarios.add(new TimeBombScenario());
        activeScenarios.add(new NoCleanScenario(MeetupPlugin.getInstance()));
    }

    public boolean isEnabled(String name) {
        return activeScenarios.stream().anyMatch(scenario -> scenario.getName().equalsIgnoreCase(name));
    }

    public <T extends Scenario> T getScenario(Class<T> scenario) {
        return scenario.cast(activeScenarios.stream().filter(scenario1 -> scenario1.getClass().isAssignableFrom(scenario))
                .findFirst().orElse(null));
    }
}

package me.zowpy.meetup.game.scenario;

import me.zowpy.meetup.game.scenario.impl.TimeBombScenario;

import java.util.ArrayList;
import java.util.List;

public class ScenarioHandler {

    private final List<Scenario> activeScenarios = new ArrayList<>();

    public ScenarioHandler() {
        activeScenarios.add(new TimeBombScenario());
    }

    public boolean isEnabled(String name) {
        return activeScenarios.stream().anyMatch(scenario -> scenario.getName().equalsIgnoreCase(name));
    }
}

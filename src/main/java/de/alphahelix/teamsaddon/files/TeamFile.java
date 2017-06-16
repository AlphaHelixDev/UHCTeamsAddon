package de.alphahelix.teamsaddon.files;

import de.alphahelix.alphalibary.file.SimpleFile;
import de.alphahelix.alphalibary.file.SimpleJSONFile;
import de.alphahelix.alphalibary.item.ItemBuilder;
import de.alphahelix.teamsaddon.TeamsAddon;
import de.alphahelix.teamsaddon.instances.Team;
import de.alphahelix.teamsaddon.instances.TeamOptions;
import org.bukkit.Material;

public class TeamFile extends SimpleJSONFile {
    public TeamFile() {
        super(TeamsAddon.getInstance().getDataFolder().getAbsolutePath(), "teams.json");
        init();

        TeamsAddon.setTeamOptions(getValue("Options", TeamOptions.class));
        if (jsonContains("Teams")) {
            TeamsAddon.setTeams(getListValues("Teams", Team.class));

            for (Team t : TeamsAddon.getTeams()) {
                Team.initTeam(t);
            }
        } else
            TeamsAddon.setTeams(new Team[]{});
    }

    private void init() {
        if (!jsonContains("Options"))
            setValue("Options", new TeamOptions(
                    "§bTeams",
                    "@",
                    true,
                    new SimpleFile.InventoryItem(new ItemBuilder(Material.BED).setName("§bTeams").build(), 2)));
    }

    public void addTeam(Team team) {
        addValuesToList("Teams", team);
    }
}

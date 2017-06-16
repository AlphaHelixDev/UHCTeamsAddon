package de.alphahelix.teamsaddon.instances;

import de.alphahelix.uhcremastered.interfaces.ScoreboardObject;
import org.bukkit.entity.Player;

public class TeamSBObject implements ScoreboardObject {

    private Player p;

    public TeamSBObject(Player p) {
        this.p = p;
    }

    @Override
    public String getPlaceHolder() {
        return "[team]";
    }

    @Override
    public boolean isClause() {
        return Team.getTeamByPlayer(p) != null;
    }

    @Override
    public String getValueWhenClauseIsTrue() {
        Team t = Team.getTeamByPlayer(p);
        return t.getColor() + t.getTeamName();
    }

    @Override
    public String getValueWhenClauseIsFalse() {
        return "-";
    }
}

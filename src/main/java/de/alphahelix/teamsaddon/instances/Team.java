package de.alphahelix.teamsaddon.instances;

import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import de.alphahelix.alphalibary.AlphaLibary;
import de.alphahelix.alphalibary.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.WeakHashMap;

public class Team {

    @Expose
    private static final transient WeakHashMap<String, Team> TEAMS = new WeakHashMap<>();

    private String teamName, rawTeamName;
    private ItemStack icon;
    private ChatColor color;
    private Location spawn;
    private int maximumPlayers;
    private ArrayList<String> members = new ArrayList<>();

    public Team(String teamName, ItemStack icon, ChatColor color, Location spawn, int maximumPlayers) {
        this.teamName = teamName;
        this.rawTeamName = ChatColor.stripColor(teamName).replace(" ", "_");
        this.icon = icon;
        this.color = color;
        this.spawn = spawn;
        this.maximumPlayers = maximumPlayers;
    }

    public static void initTeam(Team team) {
        TEAMS.put(team.getRawTeamName(), team);
    }

    public static Team getTeamByName(String rawTeamName) {
        if (TEAMS.containsKey(rawTeamName))
            return TEAMS.get(rawTeamName);
        return null;
    }

    public static Team getTeamByPlayer(Player p) {
        for (Team gt : TEAMS.values()) {
            if (gt.containsPlayer(p)) return gt;
        }
        return null;
    }

    public static Team getTeamByPlayer(String p) {
        for (Team gt : TEAMS.values()) {
            if (gt.containsPlayer(p)) return gt;
        }
        return null;
    }

    public static Team getTeamByIcon(ItemStack icon) {
        for (Team gt : TEAMS.values()) {
            if (Util.isSame(gt.getIcon(), icon)) return gt;
        }
        return null;
    }

    public static Team getTeamByColor(ChatColor chatColor) {
        for (Team gt : TEAMS.values()) {
            if (gt.getColor() == chatColor) return gt;
        }

        return null;
    }

    public static Team getTeamWithLowestAmountOfMembers() {
        int lowest = 0;
        for (Team gt : TEAMS.values()) {
            if (lowest < gt.getMembers().size())
                lowest = gt.getMembers().size();
        }

        for (Team gt : TEAMS.values()) {
            if (lowest == gt.getMembers().size()) return gt;
        }
        return null;
    }

    public static boolean isSameTeam(String p1, String p2) {
        return (getTeamByPlayer(p1) != null && getTeamByPlayer(p2) != null && getTeamByPlayer(p1).equals(getTeamByPlayer(p2)));
    }

    public Team addPlayer(Player p, boolean updateTab) {
        members.add(p.getName());

        if (updateTab) {
            if (p.getScoreboard() == null) {
                Scoreboard s = Bukkit.getScoreboardManager().getMainScoreboard();

                if (s.getTeam(this.getTeamName()) == null) {
                    org.bukkit.scoreboard.Team team = s.registerNewTeam(this.getRawTeamName());

                    team.addEntry(p.getName());

                    team.setPrefix(this.getColor() + "");
                } else {
                    org.bukkit.scoreboard.Team team = s.getTeam(this.getRawTeamName());

                    team.addEntry(p.getName());
                }
            } else {
                Scoreboard s = p.getScoreboard();

                if (s.getTeam(this.getTeamName()) == null) {
                    org.bukkit.scoreboard.Team team = s.registerNewTeam(this.getRawTeamName());

                    team.addEntry(p.getName());

                    team.setPrefix(this.getColor() + "");
                } else {
                    org.bukkit.scoreboard.Team team = s.getTeam(this.getRawTeamName());

                    team.addEntry(p.getName());
                }
            }
        }
        return this;
    }

    public Team removePlayer(Player p, boolean updateTab) {
        if (members.contains(p.getName())) {

            if (updateTab) {
                if (p.getScoreboard() != null) {

                    Scoreboard s = p.getScoreboard();

                    if (s.getTeam(this.getTeamName()) != null) {
                        org.bukkit.scoreboard.Team team = s.getTeam(this.getRawTeamName());

                        team.removeEntry(p.getName());
                    }
                }
            }

            members.remove(p.getName());
        }
        return this;
    }

    public ArrayList<String> getAliveMembers() {
        ArrayList<String> alive = new ArrayList<>();
        for (String members : getMembers())
            if (AlphaLibary.isPlayerInGame(members)) alive.add(members);
        return alive;
    }

    public boolean containsPlayer(Player p) {
        return members.contains(p.getName());
    }

    public boolean containsPlayer(String p) {
        return members.contains(p);
    }

    public String getTeamName() {
        return teamName;
    }

    public ChatColor getColor() {
        return color;
    }

    public Location getSpawn() {
        return spawn;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public int getMaximumPlayers() {
        return maximumPlayers;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public String getRawTeamName() {
        return rawTeamName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return getMaximumPlayers() == team.getMaximumPlayers() &&
                Objects.equal(getRawTeamName(), team.getRawTeamName()) &&
                getColor() == team.getColor();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getRawTeamName(), getColor(), getMaximumPlayers());
    }
}

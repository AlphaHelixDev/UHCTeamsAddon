package de.alphahelix.teamsaddon;

import de.alphahelix.alphalibary.utils.Util;
import de.alphahelix.teamsaddon.files.TeamFile;
import de.alphahelix.teamsaddon.instances.Team;
import de.alphahelix.teamsaddon.instances.TeamOptions;
import de.alphahelix.teamsaddon.instances.TeamSBObject;
import de.alphahelix.teamsaddon.inventories.TeamInventory;
import de.alphahelix.teamsaddon.listener.TeamListener;
import de.alphahelix.teamsaddon.util.TeamChatUtil;
import de.alphahelix.teamsaddon.util.TeamGameUtil;
import de.alphahelix.uhcremastered.UHC;
import de.alphahelix.uhcremastered.addons.core.Addon;
import de.alphahelix.uhcremastered.enums.GState;
import de.alphahelix.uhcremastered.events.game.ScoreboardSetEvent;
import de.alphahelix.uhcremastered.utils.ScoreboardUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TeamsAddon extends Addon {

    private static TeamsAddon instance = new TeamsAddon();
    private static boolean ffa;
    private static TeamOptions teamOptions;
    private static Team[] teams;

    public static TeamsAddon getInstance() {
        return instance;
    }

    public static TeamOptions getTeamOptions() {
        return teamOptions;
    }

    public static void setTeamOptions(TeamOptions teamOptions) {
        TeamsAddon.teamOptions = teamOptions;
    }

    public static Team[] getTeams() {
        return teams;
    }

    public static void setTeams(Team[] teams) {
        TeamsAddon.teams = teams;
    }

    public static boolean isFFA() {
        return ffa;
    }

    public static void setFFA(boolean ffa) {
        TeamsAddon.ffa = ffa;
    }

    @Override
    public void onEnable() {
        instance = this;

        new TeamInventory();
        new TeamFile();
        new TeamListener();

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent e) {
                if (GState.is(GState.LOBBY))
                    Util.runLater(5, false, () -> e.getPlayer().getInventory().setItem(getTeamOptions().getIcon().getSlot(), getTeamOptions().getIcon().getItemStack()));
            }

            @EventHandler
            public void onSet(ScoreboardSetEvent e) {
                ScoreboardUtil.addLobbyScoreboardObject(e.getPlayer(), new TeamSBObject(e.getPlayer()));
                ScoreboardUtil.addInGameScoreboardObject(e.getPlayer(), new TeamSBObject(e.getPlayer()));
            }
        }, UHC.getInstance());

        UHC.setGame(new TeamGameUtil());
        UHC.setChat(new TeamChatUtil());
    }
}

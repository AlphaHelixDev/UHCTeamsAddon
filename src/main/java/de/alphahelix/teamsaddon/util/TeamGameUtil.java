package de.alphahelix.teamsaddon.util;

import de.alphahelix.alphalibary.AlphaLibary;
import de.alphahelix.alphalibary.utils.Util;
import de.alphahelix.alphalibary.uuid.UUIDFetcher;
import de.alphahelix.teamsaddon.TeamsAddon;
import de.alphahelix.teamsaddon.instances.Team;
import de.alphahelix.uhcremastered.UHC;
import de.alphahelix.uhcremastered.enums.GState;
import de.alphahelix.uhcremastered.instances.PlayerDummie;
import de.alphahelix.uhcremastered.instances.PlayerStatistic;
import de.alphahelix.uhcremastered.interfaces.Game;
import de.alphahelix.uhcremastered.register.TimerRegister;
import de.alphahelix.uhcremastered.utils.ScoreboardUtil;
import de.alphahelix.uhcremastered.utils.StatsUtil;
import de.alphahelix.uhcremastered.utils.TabUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TeamGameUtil implements Game {

    private static ArrayList<String> winners = new ArrayList<>();

    private static ArrayList<String> getWinners() {
        return winners;
    }

    @Override
    public void handleDeath(LivingEntity dead) {
        Team team = null;

        if (dead.getKiller() != null) {
            team = Team.getTeamByPlayer(dead.getKiller());
        }

        dead.getWorld().strikeLightning(dead.getLocation());

        TabUtil.sendTablist();

        handleRewards(dead);

        for (Player p : Util.makePlayerArray(AlphaLibary.getPlayersTotal())) {
            ScoreboardUtil.updateIngameAlive(p);
            ScoreboardUtil.updateIngameDead(p);
        }

        if (team != null) {
            if (team.getAliveMembers().size() == AlphaLibary.getPlayersInGame().size()) {
                if (TeamsAddon.getTeamOptions().isTeamVictory()) {
                    UHC.getGame().handleEnd();
                    return;
                } else {
                    TeamsAddon.setFFA(true);
                }
            }
        }

        if (AlphaLibary.getPlayersInGame().size() == 4)
            TimerRegister.getPreDeathmatchTimer().startTimer();

        if (AlphaLibary.getPlayersInGame().size() <= 1) {
            UHC.getGame().handleEnd();
        }
    }

    @Override
    public void handleRewards(LivingEntity dead) {
        PlayerStatistic deadStats = StatsUtil.getStatistics(Bukkit.getOfflinePlayer(UUIDFetcher.getUUID(dead.getCustomName())));

        if (dead instanceof Player)
            deadStats = StatsUtil.getStatistics((OfflinePlayer) dead);

        if (dead.getKiller() != null) {
            PlayerStatistic ps = StatsUtil.getStatistics(dead.getKiller());

            ps.addKill(1);
            ps.addPoints(UHC.getGameOptions().getPointsOptions().getOnKill());
            ps.addCoins(UHC.getGameOptions().getCoinsOptions().getOnKill());
        }

        deadStats.addDeath(1);
        deadStats.addPoints(UHC.getGameOptions().getPointsOptions().getOnDeath());
        deadStats.addCoins(UHC.getGameOptions().getCoinsOptions().getOnDeath());

        if (!UHC.getGameOptions().getCommandOptions().getOnKill().equals(""))
            if (dead instanceof Player)
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        UHC.getGameOptions().getCommandOptions().getOnKill()
                                .replace("[player]", dead.getName())
                                .replace("[killer]",
                                        (dead.getKiller() == null ? "" : dead.getKiller().getName())));
            else
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        UHC.getGameOptions().getCommandOptions().getOnKill()
                                .replace("[player]", dead.getCustomName())
                                .replace("[killer]",
                                        (dead.getKiller() == null ? "" : dead.getKiller().getName())));

        if (!UHC.getGameOptions().getCommandOptions().getOnDeath().equals(""))
            if (dead instanceof Player)
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        UHC.getGameOptions().getCommandOptions().getOnDeath()
                                .replace("[player]", dead.getName())
                                .replace("[killer]",
                                        (dead.getKiller() == null ? "" : dead.getKiller().getName())));
            else
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        UHC.getGameOptions().getCommandOptions().getOnDeath()
                                .replace("[player]", dead.getCustomName())
                                .replace("[killer]",
                                        (dead.getKiller() == null ? "" : dead.getKiller().getName())));
    }

    @Override
    public void handleEnd() {
        GState.updateGState(GState.RESTART);

        if (AlphaLibary.getPlayersInGame().size() == 0) {
            TimerRegister.getRestartTimer().startTimer();
            return;
        }

        for (String player : AlphaLibary.getPlayersInGame()) {
            getWinners().add(player);
            PlayerStatistic stats = StatsUtil.getStatistics(Bukkit.getOfflinePlayer(UUIDFetcher.getUUID(player)));

            for (Player all : Util.makePlayerArray(AlphaLibary.getPlayersTotal())) {
                all.sendMessage(UHC.getGameOptions().getChatPrefix() + UHC.getMessages().getWinMessage(player));
            }

            stats.addPoints(UHC.getGameOptions().getPointsOptions().getOnWin());
            stats.addWins(1);
            stats.addCoins(UHC.getGameOptions().getCoinsOptions().getOnWin());

            if (!UHC.getGameOptions().getCommandOptions().getOnWin().equals(""))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), UHC.getGameOptions().getCommandOptions().getOnWin().replace("[player]", player));
        }

        TimerRegister.getRestartTimer().startTimer();
    }

    @Override
    public void handleLeave(Player p) {
        for (Player other : Util.makePlayerArray(AlphaLibary.getPlayersTotal())) {
            ScoreboardUtil.updateIngameAlive(other);
            ScoreboardUtil.updateIngameDead(other);
        }

        boolean needsEnd = false;

        if (!AlphaLibary.isPlayerDead(p)) {
            for (Team team : TeamsAddon.getTeams()) {
                if (team.getAliveMembers().size() == AlphaLibary.getPlayersInGame().size()) {
                    needsEnd = true;
                }
            }
        }

        if (needsEnd) {
            if (TeamsAddon.getTeamOptions().isTeamVictory()) {
                UHC.getGame().handleEnd();
                return;
            } else {
                TeamsAddon.setFFA(true);
            }

            if (AlphaLibary.getPlayersInGame().size() == 4)
                TimerRegister.getPreDeathmatchTimer().startTimer();

            if (AlphaLibary.getPlayersInGame().size() <= 1) {
                UHC.getGame().handleEnd();
            }
        } else {
            new PlayerDummie(p);
        }
    }
}

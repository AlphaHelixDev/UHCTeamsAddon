package de.alphahelix.teamsaddon.util;

import de.alphahelix.alphalibary.AlphaLibary;
import de.alphahelix.alphalibary.team.GameTeam;
import de.alphahelix.alphalibary.utils.Util;
import de.alphahelix.teamsaddon.TeamsAddon;
import de.alphahelix.uhcremastered.UHC;
import de.alphahelix.uhcremastered.enums.GState;
import de.alphahelix.uhcremastered.interfaces.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeamChatUtil implements Chat {
    @Override
    public void handleChat(Player p, String message) {
        if (AlphaLibary.isPlayerDead(p))
            for (Player dead : Util.makePlayerArray(AlphaLibary.getPlayersDead()))
                dead.sendMessage(UHC.getGameOptions().getSpectatorChatPrefix()
                        + p.getDisplayName() + "§8: " + message);

        if (!GState.is(GState.LOBBY)) {
            for (Player alive : Util.makePlayerArray(AlphaLibary.getPlayersInGame()))
                if (!message.startsWith(TeamsAddon.getTeamOptions().getToggleSymbol()))
                    alive.sendMessage(UHC.getGameOptions().getChatPrefix() + p.getDisplayName() + "§8: " + message);
        } else
            for (Player all : Util.makePlayerArray(AlphaLibary.getPlayersTotal()))
                all.sendMessage(UHC.getGameOptions().getChatPrefix() + p.getDisplayName() + "§8: " + message);

        if (message.startsWith(TeamsAddon.getTeamOptions().getToggleSymbol()))
            if (GameTeam.getTeamByPlayer(p) != null)
                for (String team : GameTeam.getTeamByPlayer(p).getMembers())
                    if (Bukkit.getOfflinePlayer(team) != null && Bukkit.getOfflinePlayer(team).isOnline())
                        Bukkit.getOfflinePlayer(team).getPlayer().sendMessage(UHC.getGameOptions().getChatPrefix() + p.getDisplayName() + "§8: " + message.substring(1, message.length()));
    }
}

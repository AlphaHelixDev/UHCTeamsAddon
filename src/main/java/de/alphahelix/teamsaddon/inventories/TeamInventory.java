package de.alphahelix.teamsaddon.inventories;

import de.alphahelix.alphalibary.inventorys.InventoryBuilder;
import de.alphahelix.alphalibary.utils.Util;
import de.alphahelix.teamsaddon.TeamsAddon;
import de.alphahelix.teamsaddon.instances.Team;
import de.alphahelix.teamsaddon.instances.TeamSBObject;
import de.alphahelix.uhcremastered.UHC;
import de.alphahelix.uhcremastered.enums.GState;
import de.alphahelix.uhcremastered.utils.ScoreboardUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class TeamInventory {

    public TeamInventory() {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onClick(PlayerInteractEvent e) {
                if (!GState.is(GState.LOBBY)) return;
                if (e.getItem() == null) return;
                if(!Util.isSame(e.getItem(), TeamsAddon.getTeamOptions().getIcon().getItemStack())) return;

                openInv(e.getPlayer());
            }
        }, UHC.getInstance());
    }

    private static void openInv(Player p) {
        Team currentTeam = Team.getTeamByPlayer(p);

        InventoryBuilder ib = new InventoryBuilder(
                p, TeamsAddon.getTeamOptions().getGuiName(), ((TeamsAddon.getTeams().length / 9) + 1) * 9);

        for (int i = 0; i < TeamsAddon.getTeams().length; i++) {
            Team team = TeamsAddon.getTeams()[i];

            ib.addItem(ib.new SimpleItem(team.getIcon(), i){
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);

                    if(currentTeam != null)
                        currentTeam.removePlayer(p, true);

                    team.addPlayer(p, true);

                    ScoreboardUtil.updateLobbyScoreboardObject(p, new TeamSBObject(p));

                    p.closeInventory();
                }
            });
        }

        p.openInventory(ib.build());
    }
}

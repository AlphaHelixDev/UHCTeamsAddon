package de.alphahelix.teamsaddon.listener;

import de.alphahelix.alphalibary.listener.SimpleListener;
import de.alphahelix.teamsaddon.TeamsAddon;
import de.alphahelix.teamsaddon.instances.Team;
import de.alphahelix.uhcremastered.enums.GState;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TeamListener extends SimpleListener {

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (GState.is(GState.LOBBY)) return;

        if (e.getEntity() instanceof Villager && e.getEntity().isCustomNameVisible()
                && e.getDamager() instanceof Player) {

            if (Team.isSameTeam(e.getEntity().getCustomName(), e.getDamager().getName()))
                if (!TeamsAddon.isFFA())
                    e.setCancelled(true);
        } else if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            if (Team.isSameTeam(e.getEntity().getName(), e.getDamager().getName()))
                if (!TeamsAddon.isFFA())
                    e.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (!GState.is(GState.LOBBY)) return;
        if (Team.getTeamByPlayer(e.getPlayer()) == null)
            return;
        Team.getTeamByPlayer(e.getPlayer()).removePlayer(e.getPlayer(), true);
    }

    @EventHandler
    public void onArmorStandDestroy(EntityDamageByEntityEvent e) {
        if (!GState.is(GState.LOBBY))
            return;
        if (e.getEntity() instanceof ArmorStand && e.getEntity().isCustomNameVisible())
            e.setCancelled(true);
    }

    @EventHandler
    public void onTeamJoin(PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof ArmorStand))
            return;
        if (!GState.is(GState.LOBBY))
            return;
        if (!e.getRightClicked().isCustomNameVisible())
            return;
        e.setCancelled(true);

        Team join = Team.getTeamByName(ChatColor.stripColor(e.getRightClicked().getCustomName()).replace(" ", "_"));

        if (join != null)
            join.addPlayer(e.getPlayer(), true);
    }
}

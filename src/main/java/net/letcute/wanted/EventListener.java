package net.letcute.wanted;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EventListener implements Listener {

    private final Wanted plugin;

    public EventListener(Wanted plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            if (victim.getKiller() instanceof Player) {
                Player player = victim.getKiller();
                if (Wanted.getInstance().getDatabase().isPlayerExists(victim.getName())) {
                    int money = Wanted.getInstance().getDatabase().getPlayerMoney(victim.getName());
                    Wanted.getInstance().getDatabase().removePlayer(victim.getName());
                    plugin.getEconomy().depositPlayer(player, money);
                    Bukkit.broadcastMessage(
                            String.format(Wanted.getInstance().getLanguage().getString("wanted-player-money"),
                                    player.getName(), victim.getName(), String.valueOf(FormatNumber.formatWithCommas(money))));
                }
            }
        }
    }

}

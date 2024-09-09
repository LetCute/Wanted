package net.letcute.wanted.hook;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.letcute.wanted.FormatNumber;
import net.letcute.wanted.Wanted;
import net.letcute.wanted.Database.TopPlayerMoney;

public class WantedPlaceholderAPI extends PlaceholderExpansion {

    private Wanted plugin;
    private Map<String, String> placeholderData = new HashMap<>();
    private Date dateUpdate;

    public WantedPlaceholderAPI(Wanted wanted) {
        this.plugin = wanted;
    }

    @Override
    public String getIdentifier() {
        return "wanted";
    }

    @Override
    public String getAuthor() {
        return "LetCute";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getPlugin() {
        return null;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        Date now = new Date();

        if (dateUpdate == null || TimeUnit.MILLISECONDS.toMinutes(now.getTime() - dateUpdate.getTime()) >= 5) {
            updatePlaceholderData();
            dateUpdate = new Date();
        }

        String result = placeholderData.get(identifier);
        return result != null ? result : "N/A";
    }

    private void updatePlaceholderData() {
        List<TopPlayerMoney> top10 = this.plugin.getDatabase().getTopPlayers();
        placeholderData.clear();

        for (int i = 0; i < 10; i++) {
            if (i < top10.size()) {
                TopPlayerMoney entity = top10.get(i);
                placeholderData.put("top_" + (i + 1) + "_name", entity.getName());
                placeholderData.put("top_" + (i + 1) + "_money", String.valueOf(FormatNumber.formatWithCommas(entity.getMoney())));
            } else {
                placeholderData.put("top_" + (i + 1) + "_name", "N/A");
                placeholderData.put("top_" + (i + 1) + "_money", "N/A");
            }
        }
    }

}

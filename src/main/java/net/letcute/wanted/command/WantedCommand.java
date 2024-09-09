package net.letcute.wanted.command;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.PluginsCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.clip.placeholderapi.PlaceholderAPI;
import net.letcute.wanted.Database;
import net.letcute.wanted.FormatNumber;
import net.letcute.wanted.Wanted;
import net.letcute.wanted.language.Language;

public class WantedCommand extends PluginsCommand {

    private final Database database;
    private final Language language;

    private final Wanted plugin;

    public WantedCommand(Wanted wanted) {
        super("wanted");
        this.plugin = wanted;
        this.database = wanted.getDatabase();
        this.language = Wanted.getInstance().getLanguage();
        this.description = "Add a bounty to a player";
        this.usageMessage = "/wanted [player] [money]";
        this.setPermission("wanted.command.plugins");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!testPermission(sender)) {
            return true;
        }

        if (args.length == 1) {
            sendTopPlayers(sender);
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage(usageMessage);
            return false;
        }

        Player playerTarget = Bukkit.getPlayer(args[0]);
        if (playerTarget == null) {
            sender.sendMessage(language.format(language.getString("player-not-found")));
            return false;
        }
        Player player = (Player) sender;
        int money;
        try {
            money = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(language.getString("number-error"));
            return false;
        }

        String targetPlayerName = playerTarget.getName();
        if (this.plugin.getEconomy().getBalance(player) < money) {
            player.sendMessage(language.getString("wanted-not-enough-money"));
            return false;
        }
        this.plugin.getEconomy().withdrawPlayer(player, money);
        if (database.isPlayerExists(targetPlayerName)) {
            if (database.addMoneyPlayer(targetPlayerName, money)) {
                Bukkit.broadcastMessage(String.format(language.getString("wanted-player-exists-success"),
                        targetPlayerName, String.valueOf(FormatNumber.formatWithCommas(money)), player.getName()));

            }
        } else {
            if (database.addPlayer(targetPlayerName, money)) {
                Bukkit.broadcastMessage(String.format(language.getString("wanted-player-success"), targetPlayerName,
                        String.valueOf(FormatNumber.formatWithCommas(money)), player.getName()));
            }
        }
        return true;
    }

    private void sendTopPlayers(CommandSender sender) {
        sender.sendMessage("Top 10 Wanted");
        Player player = sender instanceof Player ? (Player) sender : null;
        for (int i = 1; i <= 10; i++) {
            String namePlaceholder = "%wanted_top_" + i + "_name%";
            String moneyPlaceholder = "%wanted_top_" + i + "_money%";

            String playerName = PlaceholderAPI.setPlaceholders(player, namePlaceholder);
            String playerMoney = PlaceholderAPI.setPlaceholders(player, moneyPlaceholder);

            sender.sendMessage(String.format(language.getString("wanted-top"), i, playerName, playerMoney));
        }
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (!testPermission(sender)) {
            return suggestions;
        }

        switch (args.length) {
            case 1:
                sender.getServer().getOnlinePlayers().stream()
                        .map(Player::getName)
                        .forEach(suggestions::add);
                suggestions.add("top");
                break;
            case 2:
                if (args[1] == "top")
                    return suggestions;
                for (int multiplier : new int[] { 100, 1000, 10000, 100000 }) {
                    suggestions.addAll(IntStream.rangeClosed(1, 9)
                            .map(i -> i * multiplier)
                            .mapToObj(String::valueOf)
                            .collect(Collectors.toList()));
                }
                break;
            default:
                suggestions.add("[Error]");
                break;
        }
        return suggestions;
    }

}

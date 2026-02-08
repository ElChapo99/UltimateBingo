package io.shantek;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class BingoCompleter implements TabCompleter {

    // Opciones de configuración disponibles para /bingo set y /bingo remove
    private static final List<String> SETTINGS_OPTIONS = List.of(
            "GameMode", "Difficulty", "CardSize", "Loadout",
            "RevealCards", "WinCondition", "CardType", "TimeLimit", "StartButton"
    );

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        // Solo permitir autocompletado a jugadores (no consola)
        if (!(commandSender instanceof Player player)) {
            return null;
        }

        // Primer argumento del comando /bingo
        if (args.length == 1) {
            List<String> complete = new ArrayList<>();

            // Si tiene permiso para iniciar bingo
            if (player.hasPermission("shantek.ultimatebingo.start")) {
                complete.add("gui");
            }

            // Si tiene permiso para detener bingo
            if (player.hasPermission("shantek.ultimatebingo.stop")) {
                complete.add("stop");
            }

            // Si tiene permiso de configuración
            if (player.hasPermission("shantek.ultimatebingo.settings")) {
                complete.add("settings");
                complete.add("reload");
            }

            // Solo OP puede usar set y remove
            if (player.isOp()) {
                complete.add("set");
                complete.add("remove");
            }

            // Comandos disponibles para todos
            complete.add("info");
            complete.add("card");
            complete.add("leaderboard");

            return StringUtil.copyPartialMatches(args[0], complete, new ArrayList<>());
        }

        // /bingo card <jugador>
        if (args.length == 2 && args[0].equalsIgnoreCase("card")) {
            List<String> playerNames = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                playerNames.add(onlinePlayer.getName());
            }
            return StringUtil.copyPartialMatches(args[1], playerNames, new ArrayList<>());
        }

        // /bingo leaderboard <small|medium|large>
        if (args.length == 2 && args[0].equalsIgnoreCase("leaderboard")) {
            List<String> cardSizes = List.of("small", "medium", "large");
            return StringUtil.copyPartialMatches(args[1], cardSizes, new ArrayList<>());
        }

        // /bingo leaderboard <size> <single|full>
        if (args.length == 3 && args[0].equalsIgnoreCase("leaderboard")) {
            List<String> winConditions = List.of("single", "full");
            return StringUtil.copyPartialMatches(args[2], winConditions, new ArrayList<>());
        }

        // /bingo leaderboard <size> <single|full> <easy|normal|hard>
        if (args.length == 4 && args[0].equalsIgnoreCase("leaderboard")) {
            List<String> difficulties = List.of("easy", "normal", "hard");
            return StringUtil.copyPartialMatches(args[3], difficulties, new ArrayList<>());
        }

        // /bingo leaderboard <size> <single|full> <difficulty> <modo>
        if (args.length == 5 && args[0].equalsIgnoreCase("leaderboard")) {
            List<String> gameModes = List.of("traditional", "speedrun", "brewdash", "group", "teams");
            return StringUtil.copyPartialMatches(args[4], gameModes, new ArrayList<>());
        }

        // /bingo set <opción> (solo OP)
        if (args.length == 2 && args[0].equalsIgnoreCase("set") && player.isOp()) {
            return StringUtil.copyPartialMatches(args[1], SETTINGS_OPTIONS, new ArrayList<>());
        }

        // /bingo remove <opción> (solo OP)
        if (args.length == 2 && args[0].equalsIgnoreCase("remove") && player.isOp()) {
            return StringUtil.copyPartialMatches(args[1], SETTINGS_OPTIONS, new ArrayList<>());
        }

        return null;
    }
}

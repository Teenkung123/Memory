package com.teenkung.memory.Commands;

import com.teenkung.memory.ConfigLoader;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandTabCompleter implements TabCompleter {

    private final ArrayList<Double> l = new ArrayList<>(Arrays.asList(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0, 2.1, 2.2, 2.3, 2.4, 2.5));
    @Override
    public List<String> onTabComplete(@SuppressWarnings("NullableProblems") CommandSender sender, @SuppressWarnings("NullableProblems") Command command, @SuppressWarnings("NullableProblems") String label, @SuppressWarnings("NullableProblems") String[] args) {

        /*
        /memory help
        /memory upgrade [player] [modifier] [value]

        /memory set [player] [modifier] [value]
        /memory give [player] [amount] [OPTIONAL: canBypass]

        /memory boost player [player] [multiplier] [duration]
        /memory boost server [multiplier] [duration]
        /memory boost stop player [player]
        /memory boost stop server

        /memory setBypass [player] [duration]
        /memory stopBypass [player]
         */

        if (sender instanceof Player && !sender.hasPermission("memory.admin")) {
            //Sender has no permission
            sender.sendMessage("");
            return null;
        }

        ArrayList<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.add("help");
            result.add("upgrade");
            result.add("set");
            result.add("give");
            result.add("boost");
            result.add("setBypass");
            result.add("stopBypass");
            result.add("debug");

        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("upgrade") || args[0].equalsIgnoreCase("give")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    result.add(player.getName());
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    result.add(player.getName());
                }
            } else if (args[0].equalsIgnoreCase("boost")) {
                result.add("player");
                result.add("server");
                result.add("stop");
            } else if (args[0].equalsIgnoreCase("setBypass")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    result.add(player.getName());
                }
            } else if (args[0].equalsIgnoreCase("stopBypass")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    result.add(player.getName());
                }
            }

        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("upgrade")) {
                result.add("Rate");
                result.add("Capacity");
            } else if (args[0].equalsIgnoreCase("set")) {
                result.add("Rate");
                result.add("Capacity");
                result.add("CurrentMemory");
            } else if (args[0].equalsIgnoreCase("give")) {
                for (int i = -10 ; i <= 10 ; i++) {
                    result.add(String.valueOf(i));
                }
            } else if (args[0].equalsIgnoreCase("boost")) {
                if (args[1].equalsIgnoreCase("player")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        result.add(player.getName());
                    }
                } else if (args[1].equalsIgnoreCase("server")) {
                    result.addAll(l.stream().map(Object::toString).collect(Collectors.toCollection(ArrayList::new)));
                } else if (args[1].equalsIgnoreCase("stop")) {
                    result.add("player");
                    result.add("server");
                }
            } else if (args[0].equalsIgnoreCase("setBypass")) {
                for (int i = 1 ; i <= 30 ; i++) {
                    result.add(String.valueOf(i));
                }
            }

        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("set")) {
                if (args[2].equalsIgnoreCase("CurrentMemory")) {
                    for (int i = 0; i <= 10; i++) {
                        result.add(String.valueOf(i));
                    }
                } else {
                    for (int i = 1; i <= ConfigLoader.getLevelList().size(); i++) {
                        result.add(String.valueOf(i));
                    }
                }
            } else if (args[0].equalsIgnoreCase("give")) {
                result.add("true");
                result.add("false");
            } else if (args[0].equalsIgnoreCase("boost")) {
                if (args[1].equalsIgnoreCase("player")) {
                    result.addAll(l.stream().map(Object::toString).collect(Collectors.toCollection(ArrayList::new)));
                } else if (args[1].equalsIgnoreCase("server")) {
                    for (int i = 1 ; i <= 30 ; i++) {
                        result.add(String.valueOf(i));
                    }
                } else if (args[1].equalsIgnoreCase("stop")) {
                    if (args[2].equalsIgnoreCase("player")) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            result.add(player.getName());
                        }
                    }
                }
            }
        } else if (args.length == 5) {
            if (args[0].equalsIgnoreCase("boost") && args[1].equalsIgnoreCase("player")) {
                for (int i = 1 ; i <= 30 ; i++) {
                    result.add(String.valueOf(i));
                }
            }
        }

        return result;
    }
}

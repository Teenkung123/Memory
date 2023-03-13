package com.teenkung.memory.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@SuppressWarnings("NullableProblems") CommandSender sender, @SuppressWarnings("NullableProblems") Command command, @SuppressWarnings("NullableProblems") String label, String[] args) {

        /*
        /memory help
        /memory upgrade [player] [modifier] [value]

        /memory set [player] [modifier] [value]

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
            result.add("boost");
            result.add("setBypass");
            result.add("stopBypass");

        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("upgrade")) {
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
            } else if (args[0].equalsIgnoreCase("boost")) {
                if (args[1].equalsIgnoreCase("player")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        result.add(player.getName());
                    }
                } else if (args[1].equalsIgnoreCase("server")) {
                    for (float i = 0.1F ; i <= 2.5F ; i = i + 0.1F) {
                        result.add(String.valueOf(i));
                    }
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
            if (args[0].equalsIgnoreCase("upgrade")) {
                for (int i = -5 ; i <= 5 ; i++) {
                    result.add(String.valueOf(i));
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                if (args[2].equalsIgnoreCase("CurrentMemory")) {
                    for (int i = 0 ; i <= 10 ; i++) {
                        result.add(String.valueOf(i));
                    }
                } else {
                    for (int i = -5 ; i <= 5 ; i++) {
                        result.add(String.valueOf(i));
                    }
                }
            } else if (args[0].equalsIgnoreCase("boost")) {
                if (args[1].equalsIgnoreCase("player")) {
                    for (float i = 0.1F ; i <= 2.5F ; i = i + 0.1F) {
                        result.add(String.valueOf(i));
                    }
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

package com.teenkung.memory.Commands;

import com.teenkung.memory.ConfigLoader;
import com.teenkung.memory.Manager.PlayerDataManager;
import com.teenkung.memory.Manager.PlayerManager;
import com.teenkung.memory.Manager.ServerManager;
import com.teenkung.memory.Memory;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static com.teenkung.memory.Memory.colorize;
import static com.teenkung.memory.Memory.replaceList;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(@SuppressWarnings("NullableProblems") CommandSender sender, @SuppressWarnings("NullableProblems") Command command, @SuppressWarnings("NullableProblems") String label, @SuppressWarnings("NullableProblems") String[] args) {

        /*

        /memory help
        /memory upgrade [player] [modifier] [value]
        /memory set [player] [modifier] [value]
        /memory take [player] [value] [OPTIONAL: canBypass]
        /memory boost player [player] [multiplier] [duration]
        /memory boost server [multiplier] [duration]
        /memory boost stop player [player]
        /memory boost stop server
        /memory setBypass [player] [duration]
        /memory stopBypass [player]
        /memory take [player] [amount] [OPTIONAL: canBypass]
         */

        if (sender instanceof Player && !sender.hasPermission("memory.admin") || args.length == 0) {
            if (sender instanceof Player player) {
                sendInfo(player);
            }
            return false;
        } else {
            if (args[0].equalsIgnoreCase("help")) {

                for (String s : Memory.getInstance().getConfig().getStringList("Messages.Command.Help")) {
                    sender.sendMessage(colorize(s));
                }

            } else if (args[0].equalsIgnoreCase("upgrade")) {
                //Structure: /memory upgrade [player] [modifier] [value]
                if (args.length >= 4) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        try {
                            int value = Integer.parseInt(args[3]);
                            PlayerDataManager manager = PlayerManager.getDataManager(target);
                            if (args[2].equalsIgnoreCase("Rate")) {
                                manager.setRegenLevel(manager.getRegenLevel() + value);
                                sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Upgrade.feedback", true)
                                        .replaceAll("<player>", target.getName())
                                        .replaceAll("<modifier>", "Regeneration Level")
                                        .replaceAll("<value>", String.valueOf(value))
                                ));
                            } else if (args[2].equalsIgnoreCase("Capacity")) {
                                manager.setCurrentMemory(manager.getMaxCapacityLevel() + value);
                                sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Upgrade.feedback", true)
                                        .replaceAll("<player>", target.getName())
                                        .replaceAll("<modifier>", "Capacity Level")
                                        .replaceAll("<value>", String.valueOf(value))
                                ));
                            } else {
                                sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Upgrade.invalid-modifier", true)));
                                //Invalid arguments [modifier] does not exist
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Upgrade.invalid-value", true)));
                            //Invalid arguments [value] does not valid
                        }
                    } else {
                        sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Upgrade.invalid-player", true)));
                        //Invalid arguments [player] does not exist
                    }
                } else {
                    sender.sendMessage(colorize(ConfigLoader.getMessage("Command.invalid-arguments", true)));
                    //Incomplete command arguments [modifier] [player] [value]
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                //Structure: /memory set [player] [modifier] [value]
                if (args.length >= 4) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        try {
                            int value = Integer.parseInt(args[3]);
                            PlayerDataManager manager = PlayerManager.getDataManager(target);
                            if (args[2].equalsIgnoreCase("Rate")) {
                                manager.setRegenLevel(value);
                                sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Set.feedback", true)
                                        .replaceAll("<player>", target.getName())
                                        .replaceAll("<modifier>", "Regeneration Level")
                                        .replaceAll("<value>", String.valueOf(value))
                                ));
                            } else if (args[2].equalsIgnoreCase("Capacity")) {
                                manager.setMaxCapacityLevel(value);
                                sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Set.feedback", true)
                                        .replaceAll("<player>", target.getName())
                                        .replaceAll("<modifier>", "Capacity Level")
                                        .replaceAll("<value>", String.valueOf(value))
                                ));
                            } else if (args[2].equalsIgnoreCase("CurrentMemory")) {
                                manager.setCurrentMemory(value);
                                sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Set.feedback", true)
                                        .replaceAll("<player>", target.getName())
                                        .replaceAll("<modifier>", "Current Memory")
                                        .replaceAll("<value>", String.valueOf(value))
                                ));
                            } else {
                                sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Upgrade.invalid-modifier", true)));
                                //Invalid arguments [modifier] does not exist
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Upgrade.invalid-value", true)));
                            //Invalid arguments [value] does not valid
                        }
                    } else {
                        sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Upgrade.invalid-player", true)));
                        //Invalid arguments [player] does not exist / online
                    }
                } else {
                    sender.sendMessage(colorize(ConfigLoader.getMessage("Command.invalid-arguments", true)));
                    //Incomplete command arguments [modifier] [player] [value]
                }
            } else if (args[0].equalsIgnoreCase("give")) {
                //Structure: /memory take [player] [value] [OPTIONAL: canBypass]
                if (args.length >= 3) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        try {
                            PlayerDataManager manager = PlayerManager.getDataManager(target);
                            int amount = Integer.parseInt(args[2]);
                            if (args.length >= 4) {
                                if (args[3].equalsIgnoreCase("true")) {
                                    if (Memory.getCurrentUnixSeconds() <= manager.getBypassEndTime()) {
                                        return false;
                                    } else {
                                        manager.setCurrentMemory(manager.getCurrentMemory() + amount);
                                        sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Give.feedback", true)
                                                .replaceAll("<player>", target.getName())
                                                .replaceAll("<value>", String.valueOf(manager.getCurrentMemory()))
                                        ));
                                    }
                                } else {
                                    manager.setCurrentMemory(manager.getCurrentMemory() + amount);
                                    sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Give.feedback", true)
                                            .replaceAll("<player>", target.getName())
                                            .replaceAll("<value>", String.valueOf(manager.getCurrentMemory()))
                                    ));
                                }
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Give.invalid-value", true)));
                            //Invalid arguments [value]
                        }
                    } else {
                        sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Give.invalid-player", true)));
                        //Invalid arguments [player] does not exist / online
                    }
                } else {
                    sender.sendMessage(colorize(ConfigLoader.getMessage("Command.invalid-arguments", true)));
                    //Incomplete command arguments: [player] [amount] [OPTIONAL: canBypass]
                }
            } else if (args[0].equalsIgnoreCase("boost")) {
                /*
                Structure:
                /memory boost player [player] [multiplier] [duration]
                /memory boost server [multiplier] [duration]
                /memory boost stop player [player]
                /memory boost stop server
                 */
                if (args.length >= 2) {
                    if (args[1].equalsIgnoreCase("player")) {
                        //memory boost player [player] [multiplier] [duration]
                        if (args.length >= 5) {
                            Player target = Bukkit.getPlayer(args[2]);
                            if (target != null) {
                                try {
                                    Double multiplier = Double.parseDouble(args[3]);
                                    Long duration = Long.parseLong(args[4]);

                                    PlayerDataManager manager = PlayerManager.getDataManager(target);
                                    manager.setBooster(multiplier, duration);

                                    sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Boost.feedback-player", true)
                                            .replaceAll("<multiplier>", String.valueOf(multiplier))
                                            .replaceAll("<duration>", String.valueOf(duration))
                                            .replaceAll("<player>", target.getName())
                                    ));
                                    target.sendMessage(colorize(ConfigLoader.getMessage("Info.Player_Boost_Start", true)
                                            .replaceAll("<multiplier>", String.valueOf(multiplier))
                                            .replaceAll("<duration>", String.valueOf(duration))
                                            .replaceAll("<player>", target.getName())
                                    ));

                                } catch (NumberFormatException e) {
                                    sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Boost.invalid-multiplier", true)));
                                    //Invalid arguments [multiplier] or [duration] does not valid
                                }
                            } else {
                                sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Boost.invalid-player", true)));
                                //Invalid arguments [player] does not exist / online
                            }
                        } else {
                            sender.sendMessage(colorize(ConfigLoader.getMessage("Command.invalid-arguments", true)));
                            //Incomplete command arguments [player] [multiplier] [duration]
                        }
                    } else if (args[1].equalsIgnoreCase("server")) {
                        //memory boost server [multiplier] [duration]
                        if (args.length >= 4) {
                            try {
                                Double multiplier = Double.parseDouble(args[2]);
                                Long duration = Long.parseLong(args[3]);

                                ServerManager.setBooster(multiplier, duration);
                                sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Boost.feedback-server", true)
                                        .replaceAll("<multiplier>", String.valueOf(multiplier))
                                        .replaceAll("<duration>", String.valueOf(duration))
                                ));
                                Bukkit.broadcastMessage(colorize(ConfigLoader.getMessage("Info.Global_Boost_Start", true)
                                        .replaceAll("<multiplier>", String.valueOf(multiplier))
                                        .replaceAll("<duration>", String.valueOf(duration))
                                ));

                            } catch (NumberFormatException e) {
                                sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Boost.invalid-multiplier", true)));
                                //Invalid arguments [multiplier] or [duration] does not valid
                            }
                        } else {
                            sender.sendMessage(colorize(ConfigLoader.getMessage("Command.invalid-arguments", true)));
                            //Incomplete command arguments [multiplier] [duration]
                        }
                    } else if (args[1].equalsIgnoreCase("stop")) {
                        /*
                        /memory boost stop player [player]
                        /memory boost stop server
                         */
                        if (args.length >= 3) {
                            if (args[2].equalsIgnoreCase("player")) {
                                //memory boost stop player [player]
                                if (args.length >= 4) {
                                    Player target = Bukkit.getPlayer(args[3]);
                                    if (target != null) {

                                        PlayerDataManager manager = PlayerManager.getDataManager(target);
                                        manager.setBooster(1D, 0L);

                                        sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Boost.feedback-stop-player", true).replaceAll("<player>", target.getName())));
                                        target.sendMessage(colorize(ConfigLoader.getMessage("Info.Player_Boost_End", true)));

                                    } else {
                                        sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Boost.invalid-player", true)));
                                        //Invalid arguments [player] does not exist / online
                                    }
                                } else {
                                    sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Boost.invalid-player", true)));
                                    //Incomplete command arguments [player]
                                }
                            } else if (args[2].equalsIgnoreCase("server")) {
                                //memory boost stop server

                                sender.sendMessage("");
                                ServerManager.setBooster(1D, 0L);

                                sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Boost.feedback-stop-server", true)));
                                Bukkit.broadcastMessage(colorize(ConfigLoader.getMessage("Info.Global_Boost_End", true)));

                            }
                        }
                    } else {
                        sender.sendMessage(colorize(ConfigLoader.getMessage("Command.invalid-arguments", true)));
                        //Incomplete command arguments [ALL]
                    }
                } else {
                    sender.sendMessage(colorize(ConfigLoader.getMessage("Command.invalid-arguments", true)));
                    //Incomplete command arguments [ALL]
                }
            } else if (args[0].equalsIgnoreCase("setBypass")) {
                //Structure /memory setBypass [player] [duration]
                if (args.length >= 3) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        try {
                            long duration = Long.parseLong(args[2]);

                            PlayerDataManager manager = PlayerManager.getDataManager(target);
                            manager.setBypassTime(duration);
                            sender.sendMessage(colorize(ConfigLoader.getMessage("Command.SetBypass.feedback", true)
                                    .replaceAll("<player>", target.getName())
                                    .replaceAll("<duration>", String.valueOf(duration))
                            ));
                            target.sendMessage(colorize(ConfigLoader.getMessage("Info.Bypass_Start", true)
                                    .replaceAll("<duration>", String.valueOf(duration))
                                    .replaceAll("<player>", target.getName())
                            ));

                        } catch (NumberFormatException e) {
                            sender.sendMessage(colorize(ConfigLoader.getMessage("Command.SetBypass.invalid-duration", true)));
                            //Invalid arguments [duration] does not valid
                        }
                    } else {
                        sender.sendMessage(colorize(ConfigLoader.getMessage("Command.SetBypass.invalid-player", true)));
                        //Invalid arguments [player] does not exist / online
                    }
                } else {
                    sender.sendMessage(colorize(ConfigLoader.getMessage("Command.invalid-arguments", true)));
                    //Incomplete command arguments [player] [duration]
                }
            } else if (args[0].equalsIgnoreCase("stopBypass")) {
                //Structure /memory stopBypass [player]
                if (args.length >= 2) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {

                        PlayerDataManager manager = PlayerManager.getDataManager(target);
                        manager.setBypassTime(Memory.getCurrentUnixSeconds());
                        sender.sendMessage(colorize(ConfigLoader.getMessage("Command.StopBypass.feedback", true)
                                .replaceAll("<player>", target.getName())
                        ));
                        target.sendMessage(colorize(ConfigLoader.getMessage("Info.Bypass_End", true)
                                .replaceAll("<player>", target.getName())
                        ));

                    } else {
                        sender.sendMessage(colorize(ConfigLoader.getMessage("Command.StopBypass.invalid-player", true)));
                        //Invalid arguments [player] does not exist / online
                    }
                } else {
                    sender.sendMessage(colorize(ConfigLoader.getMessage("Command.invalid-arguments", true)));
                    //Incomplete command arguments [player]
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                long start = System.currentTimeMillis();
                ConfigLoader.reloadConfig();
                sender.sendMessage(colorize(ConfigLoader.getMessage("Command.Reload.feedback", true).replaceAll("<ms>", String.valueOf(System.currentTimeMillis() - start))));

            } else {
                for (String s : Memory.getInstance().getConfig().getStringList("Messages.Command.Help")) {
                    sender.sendMessage(colorize(s));
                }
            }
        }
        return false;
    }

    private void sendInfo(Player player) {
        PlayerDataManager manager = PlayerManager.getDataManager(player);
        ArrayList<String> l = new ArrayList<>(Memory.getInstance().getConfig().getStringList("Messages.Info.Info"));
        if (manager.getBoosterTimeout() > Memory.getCurrentUnixSeconds()) {
            replaceList(l, new ArrayList<>(Memory.getInstance().getConfig().getStringList("Messages.Info.Player_Boost.Avaiable")), "<player_boost>");
        } else {
            replaceList(l, new ArrayList<>(Memory.getInstance().getConfig().getStringList("Messages.Info.Player_Boost.Not-Avaiable")), "<player_boost>");
        }
        if (ServerManager.getTimeOut() > Memory.getCurrentUnixSeconds()) {
            replaceList(l, new ArrayList<>(Memory.getInstance().getConfig().getStringList("Messages.Info.Server_Boost.Avaiable")), "<server_boost>");
        } else {
            replaceList(l, new ArrayList<>(Memory.getInstance().getConfig().getStringList("Messages.Info.Server_Boost.Not-Avaiable")), "<server_boost>");
        }
        if (manager.getBypassEndTime() > Memory.getCurrentUnixSeconds()) {
            replaceList(l, new ArrayList<>(Memory.getInstance().getConfig().getStringList("Messages.Info.Bypass.Avaiable")), "<bypass>");
        } else {
            replaceList(l, new ArrayList<>(Memory.getInstance().getConfig().getStringList("Messages.Info.Bypass.Not-Avaiable")), "<bypass>");
        }
        for (String s : l) {
            if (manager.getCurrentMemory() == 0) {
                if (s.contains("<HIDE_WHEN_0>")) {
                    continue;
                }
            }

            long once_hours = manager.getNextPerionIn() / 3600;
            long once_minutes = (manager.getNextPerionIn() % 3600) / 60;
            long once_seconds = manager.getNextPerionIn() % 60;


            long full_hours = manager.getFillTime() / 3600;
            long full_minutes = (manager.getFillTime() % 3600) / 60;
            long full_seconds = manager.getFillTime() % 60;

            s = s.replaceAll("<HIDE_WHEN_0>", "");
            player.sendMessage(colorize(PlaceholderAPI.setPlaceholders(player,
                    s.replaceAll("<memory>", String.valueOf(manager.getCurrentMemory()))
                    .replaceAll("<max_memory>", String.valueOf(ConfigLoader.getMax(manager.getMaxCapacityLevel())))
                    .replaceAll("<one_regenerate_time>", Memory.formatUnixTime(Memory.getCurrentUnixSeconds() + manager.getNextPerionIn()))
                    .replaceAll("<one_regenerate_seconds>", String.valueOf(manager.getNextPerionIn()))
                    .replaceAll("<one_regenerate_second>", String.valueOf(once_seconds))
                    .replaceAll("<one_regenerate_minute>", String.valueOf(once_minutes))
                    .replaceAll("<one_regenerate_hour>", String.valueOf(once_hours))
                    .replaceAll("<full_regenerate_time>", Memory.formatUnixTime(Memory.getCurrentUnixSeconds() + manager.getFillTime()))
                    .replaceAll("<full_regenerate_seconds>", String.valueOf(manager.getFillTime()))
                    .replaceAll("<full_regenerate_second>", String.valueOf(full_seconds))
                    .replaceAll("<full_regenerate_minute>", String.valueOf(full_minutes))
                    .replaceAll("<full_regenerate_hour>", String.valueOf(full_hours))
                    .replaceAll("<player_multiplier>", String.valueOf(manager.getBoosterMultiplier()))
                    .replaceAll("<player_duration>", String.valueOf(manager.getBoosterDuration()))
                    .replaceAll("<player_end_time>", Memory.formatUnixTime(manager.getBoosterTimeout()))
                    .replaceAll("<player_end_second>", String.valueOf(manager.getBoosterTimeout() - Memory.getCurrentUnixSeconds()))
                    .replaceAll("<server_multiplier>", String.valueOf(ServerManager.getMultiplier()))
                    .replaceAll("<server_duration>", String.valueOf(ServerManager.getDuration()))
                    .replaceAll("<server_end_time>", Memory.formatUnixTime(ServerManager.getTimeOut()))
                    .replaceAll("<server_end_second>", String.valueOf(ServerManager.getTimeOut() - Memory.getCurrentUnixSeconds()))
                    .replaceAll("<bypass_end_time>", Memory.formatUnixTime(manager.getBypassEndTime()))
                    .replaceAll("<bypass_duration>", String.valueOf(manager.getBypassEndTime() - Memory.getCurrentUnixSeconds()))
            )));
        }
    }
}

package com.teenkung.memory.Commands;

import com.teenkung.memory.ConfigLoader;
import com.teenkung.memory.Manager.PlayerDataManager;
import com.teenkung.memory.Manager.PlayerManager;
import com.teenkung.memory.Manager.ServerManager;
import com.teenkung.memory.Memory;
import com.teenkung.memory.Regeneration.Regeneration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(@SuppressWarnings("NullableProblems") CommandSender sender, @SuppressWarnings("NullableProblems") Command command, @SuppressWarnings("NullableProblems") String label, String[] args) {

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

        if (sender instanceof Player && !sender.hasPermission("memory.admin")) {
            //Sender has no permission
            sender.sendMessage("");
            return false;
        }
        if (args.length == 0) {

            if (sender instanceof Player player) {
                PlayerDataManager manager = PlayerManager.getDataManager(player);
                sender.sendMessage(String.valueOf(manager.getCurrentMemory()));
            }

        } else {
            if (args[0].equalsIgnoreCase("help")) {

                sender.sendMessage("Not done yet.");
                //TODO: Help menu

            } else if (args[0].equalsIgnoreCase("upgrade")) {
                //Structure: /memory upgrade [player] [modifier] [value]
                if (args.length >= 4) {
                    Player target = Bukkit.getPlayer(args[2]);
                    if (target != null) {
                        try {
                            int value = Integer.parseInt(args[3]);
                            PlayerDataManager manager = PlayerManager.getDataManager(target);
                            if (args[1].equalsIgnoreCase("Rate")) {
                                manager.setRegenLevel(manager.getRegenLevel() + value);
                                sender.sendMessage(ConfigLoader.getMessage("Command.Upgrade.feedback", true)
                                        .replaceAll("<player>", target.getName())
                                        .replaceAll("<modifier>", "Regeneration Level")
                                        .replaceAll("<value>", String.valueOf(value))
                                );
                            } else if (args[1].equalsIgnoreCase("Capacity")) {
                                manager.setCurrentMemory(manager.getMaxCapacityLevel() + value, false);
                                sender.sendMessage(ConfigLoader.getMessage("Command.Upgrade.feedback", true)
                                        .replaceAll("<player>", target.getName())
                                        .replaceAll("<modifier>", "Capacity Level")
                                        .replaceAll("<value>", String.valueOf(value))
                                );
                            } else {
                                sender.sendMessage(ConfigLoader.getMessage("Command.Upgrade.invalid-modifier", true));
                                //Invalid arguments [modifier] does not exist
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ConfigLoader.getMessage("Command.Upgrade.invalid-value", true));
                            //Invalid arguments [value] does not valid
                        }
                    } else {
                        sender.sendMessage(ConfigLoader.getMessage("Command.Upgrade.invalid-player", true));
                        //Invalid arguments [player] does not exist
                    }
                } else {
                    sender.sendMessage(ConfigLoader.getMessage("Command.invalid-arguments", true));
                    //Incomplete command arguments [modifier] [player] [value]
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                //Structure: /memory set [player] [modifier] [value]
                if (args.length >= 4) {
                    Bukkit.broadcastMessage(args[1]);
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        try {
                            int value = Integer.parseInt(args[3]);
                            PlayerDataManager manager = PlayerManager.getDataManager(target);
                            if (args[2].equalsIgnoreCase("Rate")) {
                                manager.setRegenLevel(value);
                                sender.sendMessage(ConfigLoader.getMessage("Command.Set.feedback", true)
                                        .replaceAll("<player>", target.getName())
                                        .replaceAll("<modifier>", "Regeneration Level")
                                        .replaceAll("<value>", String.valueOf(value))
                                );
                            } else if (args[2].equalsIgnoreCase("Capacity")) {
                                manager.setMaxCapacityLevel(value);
                                sender.sendMessage(ConfigLoader.getMessage("Command.Set.feedback", true)
                                        .replaceAll("<player>", target.getName())
                                        .replaceAll("<modifier>", "Capacity Level")
                                        .replaceAll("<value>", String.valueOf(value))
                                );
                            } else if (args[2].equalsIgnoreCase("CurrentMemory")) {
                                manager.setCurrentMemory(value, false);
                                sender.sendMessage(ConfigLoader.getMessage("Command.Set.feedback", true)
                                        .replaceAll("<player>", target.getName())
                                        .replaceAll("<modifier>", "Current Memory")
                                        .replaceAll("<value>", String.valueOf(value))
                                );
                            } else {
                                sender.sendMessage(ConfigLoader.getMessage("Command.Upgrade.invalid-modifier", true));
                                //Invalid arguments [modifier] does not exist
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ConfigLoader.getMessage("Command.Upgrade.invalid-value", true));
                            //Invalid arguments [value] does not valid
                        }
                    } else {
                        sender.sendMessage(ConfigLoader.getMessage("Command.Upgrade.invalid-player", true));
                        //Invalid arguments [player] does not exist / online
                    }
                } else {
                    sender.sendMessage(ConfigLoader.getMessage("Command.invalid-arguments", true));
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
                                        manager.setCurrentMemory(manager.getCurrentMemory() + amount, false);
                                        sender.sendMessage(ConfigLoader.getMessage("Command.Give.feedback", true)
                                                .replaceAll("<player>", target.getName())
                                                .replaceAll("<value>", String.valueOf(manager.getCurrentMemory()))
                                        );
                                    }
                                } else {
                                    manager.setCurrentMemory(manager.getCurrentMemory() + amount, false);
                                    sender.sendMessage(ConfigLoader.getMessage("Command.Give.feedback", true)
                                            .replaceAll("<player>", target.getName())
                                            .replaceAll("<value>", String.valueOf(manager.getCurrentMemory()))
                                    );
                                }
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ConfigLoader.getMessage("Command.Give.invalid-value", true));
                            //Invalid arguments [value]
                        }
                    } else {
                        sender.sendMessage(ConfigLoader.getMessage("Command.Give.invalid-player", true));
                        //Invalid arguments [player] does not exist / online
                    }
                } else {
                    sender.sendMessage(ConfigLoader.getMessage("Command.invalid-arguments", true));
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

                                    sender.sendMessage(ConfigLoader.getMessage("Command.Boost.feedback-server", true)
                                            .replaceAll("<modifier>", String.valueOf(multiplier))
                                            .replaceAll("<duration>", String.valueOf(duration))
                                            .replaceAll("<player>", target.getName())
                                    );

                                } catch (NumberFormatException e) {
                                    sender.sendMessage(ConfigLoader.getMessage("Command.Boost.invalid-multiplier", true));
                                    //Invalid arguments [multiplier] or [duration] does not valid
                                }
                            } else {
                                sender.sendMessage(ConfigLoader.getMessage("Command.Boost.invalid-player", true));
                                //Invalid arguments [player] does not exist / online
                            }
                        } else {
                            sender.sendMessage(ConfigLoader.getMessage("Command.invalid-arguments", true));
                            //Incomplete command arguments [player] [multiplier] [duration]
                        }
                    } else if (args[1].equalsIgnoreCase("server")) {
                        //memory boost server [multiplier] [duration]
                        if (args.length >= 4) {
                            try {
                                Double multiplier = Double.parseDouble(args[2]);
                                Long duration = Long.parseLong(args[3]);

                                ServerManager.setServerBooster(multiplier, duration);
                                sender.sendMessage(ConfigLoader.getMessage("Command.Boost.feedback-server", true)
                                        .replaceAll("<modifier>", String.valueOf(multiplier))
                                        .replaceAll("<duration>", String.valueOf(duration))
                                );

                            } catch (NumberFormatException e) {
                                sender.sendMessage(ConfigLoader.getMessage("Command.Boost.invalid-multiplier", true));
                                //Invalid arguments [multiplier] or [duration] does not valid
                            }
                        } else {
                            sender.sendMessage(ConfigLoader.getMessage("Command.invalid-arguments", true));
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

                                        sender.sendMessage(ConfigLoader.getMessage("Command.Boost.feedback-stop-player", true).replaceAll("<player>", target.getName()));

                                    } else {
                                        sender.sendMessage(ConfigLoader.getMessage("Command.Boost.invalid-player", true));
                                        //Invalid arguments [player] does not exist / online
                                    }
                                } else {
                                    sender.sendMessage(ConfigLoader.getMessage("Command.Boost.invalid-player", true));
                                    //Incomplete command arguments [player]
                                }
                            } else if (args[2].equalsIgnoreCase("server")) {
                                //memory boost stop server

                                sender.sendMessage("");
                                ServerManager.setServerBooster(1D, 0L);

                                sender.sendMessage(ConfigLoader.getMessage("Command.Boost.feedback-stop-server", true));

                            }
                        }
                    } else {
                        sender.sendMessage(ConfigLoader.getMessage("Command.invalid-arguments", true));
                        //Incomplete command arguments [ALL]
                    }
                } else {
                    sender.sendMessage(ConfigLoader.getMessage("Command.invalid-arguments", true));
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
                            manager.setBypassTime(Memory.getCurrentUnixSeconds() + duration);
                            sender.sendMessage(ConfigLoader.getMessage("Command.StopBypass.feedback", true)
                                    .replaceAll("<player>", target.getName())
                                    .replaceAll("<duration>", String.valueOf(duration))
                            );

                        } catch (NumberFormatException e) {
                            sender.sendMessage(ConfigLoader.getMessage("Command.SetBypass.invalid-duration", true));
                            //Invalid arguments [duration] does not valid
                        }
                    } else {
                        sender.sendMessage(ConfigLoader.getMessage("Command.SetBypass.invalid-player", true));
                        //Invalid arguments [player] does not exist / online
                    }
                } else {
                    sender.sendMessage(ConfigLoader.getMessage("Command.invalid-arguments", true));
                    //Incomplete command arguments [player] [duration]
                }
            } else if (args[0].equalsIgnoreCase("stopBypass")) {
                //Structure /memory stopBypass [player]
                if (args.length >= 2) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {

                        PlayerDataManager manager = PlayerManager.getDataManager(target);
                        manager.setBypassTime(Memory.getCurrentUnixSeconds());
                        sender.sendMessage(ConfigLoader.getMessage("Command.StopBypass.feedback", true)
                                .replaceAll("<player>", target.getName())
                        );

                    } else {
                        sender.sendMessage(ConfigLoader.getMessage("Command.StopBypass.invalid-player", true));
                        //Invalid arguments [player] does not exist / online
                    }
                } else {
                    sender.sendMessage(ConfigLoader.getMessage("Command.invalid-arguments", true));
                    //Incomplete command arguments [player]
                }
            } else if (args[0].equalsIgnoreCase("debug")) {
                if (sender instanceof Player player) {
                    PlayerDataManager data = PlayerManager.getDataManager(player);
                    player.sendMessage("MEMORY: "+data.getCurrentMemory());
                    player.sendMessage("REGENERATION TASK: "+Memory.regenerationTask.get(player));
                    player.sendMessage("COUNTDOWN BOOSTER: "+data.getCountdownBooster());
                }
            } else if (args[0].equalsIgnoreCase("serverDebug")) {
                sender.sendMessage("REGENERATION TASK MAP: "+Memory.regenerationTask);
            } else if (args[0].equalsIgnoreCase("reload")) {
                long start = System.currentTimeMillis();
                ConfigLoader.reloadConfig();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Regeneration.updatePlayerRegenTask(player);
                }
                sender.sendMessage(ConfigLoader.getMessage("Command.Reload.feedback", true).replaceAll("<ms>", String.valueOf(System.currentTimeMillis() - start)));
            }
        }
        return false;
    }


}

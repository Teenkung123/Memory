package com.teenkung.memory.Commands;

import com.teenkung.memory.Manager.PlayerDataManager;
import com.teenkung.memory.Manager.PlayerManager;
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
            return false;
        }
        if (args.length == 0) {

            sender.sendMessage("Not done yet.");
            //TODO: Help menu

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
                            } else if (args[1].equalsIgnoreCase("Capacity")) {
                                manager.setCurrentMemory(manager.getMaxCapacityLevel() + value);
                            } else {
                                sender.sendMessage("");
                                //Invalid arguments [modifier] does not exist
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage("");
                            //Invalid arguments [value] does not valid
                        }
                    } else {
                        sender.sendMessage("");
                        //Invalid arguments [player] does not exist
                    }
                } else {
                    sender.sendMessage("");
                    //Incomplete command arguments [modifier] [player] [value]
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                //Structure: /memory set [player] [modifier] [value]
                if (args.length >= 4) {
                    Player target = Bukkit.getPlayer(args[2]);
                    if (target != null) {
                        try {
                            int value = Integer.parseInt(args[3]);
                            PlayerDataManager manager = PlayerManager.getDataManager(target);
                            if (args[1].equalsIgnoreCase("Rate")) {
                                manager.setRegenLevel(value);
                            } else if (args[1].equalsIgnoreCase("Capacity")) {
                                manager.setCurrentMemory(value);
                            } else if (args[1].equalsIgnoreCase("CurrentMemory")) {
                                manager.setCurrentMemory(value);
                            } else {
                                sender.sendMessage("");
                                //Invalid arguments [modifier] does not exist
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage("");
                            //Invalid arguments [value] does not valid
                        }
                    } else {
                        sender.sendMessage("");
                        //Invalid arguments [player] does not exist / online
                    }
                } else {
                    sender.sendMessage("");
                    //Incomplete command arguments [modifier] [player] [value]
                }
            } else if (args[0].equalsIgnoreCase("give")) {
                //Structure: /memory give [player] [value]
                if (args.length > 3) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        try {
                            int value = Integer.parseInt(args[2]);
                            PlayerDataManager manager = PlayerManager.getDataManager(target);
                            manager.setCurrentMemory(value);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("");
                            //Invalid arguments [value] does not valid
                        }
                    } else {
                        sender.sendMessage("");
                        //Invalid arguments [player] does not exist / online
                    }
                } else {
                    sender.sendMessage("");
                    //Incomplete command arguments [player] [value]
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

                                    sender.sendMessage("Not done yet.");
                                    //TODO: Player Boost Trigger

                                } catch (NumberFormatException e) {
                                    sender.sendMessage("");
                                    //Invalid arguments [multiplier] or [duration] does not valid
                                }
                            } else {
                                sender.sendMessage("");
                                //Invalid arguments [player] does not exist / online
                            }
                        } else {
                            sender.sendMessage("");
                            //Incomplete command arguments [player] [multiplier] [duration]
                        }
                    } else if (args[1].equalsIgnoreCase("server")) {
                        //memory boost server [multiplier] [duration]
                        if (args.length >= 4) {
                            try {
                                Double multiplier = Double.parseDouble(args[2]);
                                Long duration = Long.parseLong(args[3]);

                                sender.sendMessage("Not done yet.");
                                //TODO: Global Boost Trigger

                            } catch (NumberFormatException e) {
                                sender.sendMessage("");
                                //Invalid arguments [multiplier] or [duration] does not valid
                            }
                        } else {
                            sender.sendMessage("");
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

                                        sender.sendMessage("Not done yet.");
                                        //TODO: Player Boost Stop

                                    } else {
                                        sender.sendMessage("");
                                        //Invalid arguments [player] does not exist / online
                                    }
                                } else {
                                    sender.sendMessage("");
                                    //Incomplete command arguments [player]
                                }
                            } else if (args[2].equalsIgnoreCase("server")) {
                                //memory boost stop server

                                sender.sendMessage("Not done yet.");
                                //TODO: Server Boost Stop

                            }
                        }
                    } else {
                        sender.sendMessage("");
                        //Incomplete command arguments [ALL]
                    }
                }
            } else if (args[0].equalsIgnoreCase("setBypass")) {
                //Structure /memory setBypass [player] [duration]
                if (args.length >= 3) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        try {
                            long duration = Long.parseLong(args[2]);

                            sender.sendMessage("Not done yet.");
                            //TODO: Set Bypass Player

                        } catch (NumberFormatException e) {
                            sender.sendMessage("");
                            //Invalid arguments [duration] does not valid
                        }
                    } else {
                        sender.sendMessage("");
                        //Invalid arguments [player] does not exist / online
                    }
                } else {
                    sender.sendMessage("");
                    //Incomplete command arguments [player] [duration]
                }
            } else if (args[0].equalsIgnoreCase("stopBypass")) {
                //Structure /memory stopBypass [player]
                if (args.length >= 2) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {

                        sender.sendMessage("Not done yet.");
                        //TODO: Stop Bypass Player

                    } else {
                        sender.sendMessage("");
                        //Invalid arguments [player] does not exist / online
                    }
                } else {
                    sender.sendMessage("");
                    //Incomplete command arguments [player]
                }
            }
        }
        return false;
    }


}

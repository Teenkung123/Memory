package com.teenkung.memory.Manager;

import com.teenkung.memory.ConfigLoader;
import com.teenkung.memory.Memory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerDataManager {

    private final Player player;
    private long logoutTime;
    private final long loginTime;
    private long bypassEndTime;
    private int currentMemory;
    private int RLevel;
    private int MLevel;

    private long leftOverTime;

    /**
     * Constructor of this class
     * ONLY USE WITH JOIN EVENT CLASS IF YOU WANT TO GET PLAYER DATA OF THIS CLASS PLEASE USE PlayerManager Class Instead!
     * @param player the player you want to load data
     */
    public PlayerDataManager(Player player) {
        this.player = player;
        this.loginTime = Memory.getCurrentUnixSeconds();
        this.leftOverTime = 0;
        Bukkit.getScheduler().runTaskAsynchronously(Memory.getInstance(), () -> {
            try {
                PreparedStatement statement = Memory.getConnection().prepareStatement("INSERT INTO Memory (UUID, LeaveUnix, RLevel, MLevel, CurrentAmount, BypassUntil)" +
                        "    SELECT ?, ?, 1, 1, ?, ?" +
                        "    WHERE NOT EXISTS (SELECT * FROM Memory WHERE UUID = ?);" +
                        "SELECT * FROM Memory WHERE UUID = ?;");

                statement.setString(1, player.getUniqueId().toString());
                statement.setLong(2, Memory.getCurrentUnixSeconds());
                statement.setInt(3, ConfigLoader.getMax(1));
                statement.setLong(4, Memory.getCurrentUnixSeconds());
                statement.setString(5, player.getUniqueId().toString());
                statement.setString(6, player.getUniqueId().toString());
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    logoutTime = rs.getLong("LeaveUnix");
                    RLevel = rs.getInt("RLevel");
                    MLevel = rs.getInt("MLevel");
                    currentMemory = rs.getInt("CurrentAmount");
                    bypassEndTime = rs.getLong("BypassUntil");
                }
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Request a player class object from this class
     * @return a Player class object
     */
    public Player getPlayer() { return player; }

    /**
     * Request the Logout Time of the player
     * @return the logout time of the player in Unix time
     */
    public Long getLogoutTime() { return logoutTime; }

    /**
     * Request the login Time of the player
     * NOTE: The login time of the player, only save when player join the game or plugin LOAD,
     * so if plugin gets reload the player login time will be the time that plugin got loaded
     * @return the Login time of the player in Unix time
     */
    public long getLoginTime() { return loginTime; }

    /**
     * Request the Bypass Time of the player
     * @return the Bypass time in Unix Time
     */
    public Long getBypassEndTime() { return bypassEndTime; }

    /**
     * Request the Regen Time Level of the player
     * @return the Regen time level of the player
     */
    public int getRegenLevel() { return RLevel; }

    /**
     * Request the Max Capacity Level of the player
     * @return the Max Capacity Level of the player
     */
    public int getMaxCapacityLevel() { return MLevel; }

    /**
     * Request the Leftover time after the Offline Calculation is completed
     * @return the Leftover time of the player
     */
    public long getLeftOverTime() { return leftOverTime; }

    /**
     * Set the Regeneration Level of the player
     * @param level the Level of new Regen Level you want the player to have
     */
    public void setRegenLevel(int level) {
        RLevel = level;
        Bukkit.getScheduler().runTaskAsynchronously(Memory.getInstance(), () -> {
            try {
                PreparedStatement statement = Memory.getConnection().prepareStatement("UPDATE Memory SET RLevel = ? WHERE UUID = ?");
                statement.setInt(1, level);
                statement.setString(2, player.getUniqueId().toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * Set the Max Capacity Level of the player
     * @param level the Level of new Max Capacity Level you want the player to have
     */
    public void setMaxCapacityLevel(int level) {
        MLevel = level;
        Bukkit.getScheduler().runTaskAsynchronously(Memory.getInstance(), () -> {
            try {
                PreparedStatement statement = Memory.getConnection().prepareStatement("UPDATE Memory SET MLevel = ? WHERE UUID = ?");
                statement.setInt(1, level);
                statement.setString(2, player.getUniqueId().toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Set the current Memory the player currently has
     * @param value the value you want the player to have
     */
    public void setCurrentMemory(int value) { currentMemory = value; }

    /**
     * Set the Duration of Bypass Time in seconds (For example 1 means bypass for 1 second)
     * @param duration duration of the Bypass you want the player to have in seconds
     */
    public void setBypassTime(Long duration) {
        bypassEndTime = Memory.getCurrentUnixSeconds() + duration;
        Bukkit.getScheduler().runTaskAsynchronously(Memory.getInstance(), () -> {
            try {
                PreparedStatement statement = Memory.getConnection().prepareStatement("UPDATE Memory SET BypassUntil = ? WHERE UUID = ?");
                statement.setLong(1, bypassEndTime);
                statement.setString(2, player.getUniqueId().toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * Set the leave time unix of player (ONLY USE WITH PLAYER QUIT EVENT)
     * @param time The Unix time you want to set
     */
    public void setLeaveTime(Long time) {
        logoutTime = Memory.getCurrentUnixSeconds() + time;
        Bukkit.getScheduler().runTaskAsynchronously(Memory.getInstance(), () -> {
            try {
                PreparedStatement statement = Memory.getConnection().prepareStatement("UPDATE Memory SET LeaveUnix = ? WHERE UUID = ?");
                statement.setLong(1, logoutTime);
                statement.setString(2, player.getUniqueId().toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Save all the Data except logout Time to the MySQL database
     */
    public void saveDataToMySQL() {
        Bukkit.getScheduler().runTaskAsynchronously(Memory.getInstance(), () -> {
            try {
                PreparedStatement statement = Memory.getConnection().prepareStatement("UPDATE Memory SET RLevel = ?, MLevel = ?, CurrentAmount = ?, BypassUntil = ? WHERE UUID = ?");
                statement.setInt(1, RLevel);
                statement.setInt(2, MLevel);
                statement.setInt(3, currentMemory);
                statement.setLong(4, bypassEndTime);
                statement.setString(5, player.getUniqueId().toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Perform the Offline Calculation of the player and give player
     * the Memory will get modified based on the Offline Calculation after thus task performed
     */
    public void performOfflineCalculation() {
        double timeDifferent = loginTime-logoutTime;
        int decreaseAmount = Double.valueOf(timeDifferent / ConfigLoader.getRegenTime(RLevel)).intValue();
        setCurrentMemory(Math.max(currentMemory - decreaseAmount, 0));
        leftOverTime = Double.valueOf(timeDifferent % ConfigLoader.getRegenTime(RLevel)).longValue();
    }

}

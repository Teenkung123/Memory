package com.teenkung.memory.Manager;

import com.teenkung.memory.Memory;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLManager {

    private Connection connection;
    public void Connect() throws SQLException {
        if (!isConnected()) {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + Memory.getInstance().getConfig().getString("MySQL.Host") + ":" + Memory.getInstance().getConfig().getString("MySQL.Port")
                            + "/" + Memory.getInstance().getConfig().getString("MySQL.Database") + "?useSSL=false&autoReconnect=true",
                    Memory.getInstance().getConfig().getString("MySQL.User"),
                    Memory.getInstance().getConfig().getString("MySQL.Password")
            );
        }
    }

    public Connection getConnection() { return connection; }

    public void Disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void createTable() {
        try {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Memory ("
                    + "ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                    + "UUID VARCHAR(40),"
                    + "LeaveUnix BIGINT,"
                    + "RLevel INT,"
                    + "MLevel INT,"
                    + "CurrentAmount INT,"
                    + "BypassUntil BIGINT"
                    + ");");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void startSendDummyData() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Memory.getInstance(), () ->  {
            if (Memory.getInstance().getConfig().getBoolean("MySQL.DummyData.Display")) {
                System.out.println("Sending Dummy Data to prevent Database Timed out!");
            }
            try {
                PreparedStatement statement = connection.prepareStatement("REPLACE INTO Memory (ID, UUID, LeaveUnix, RLevel, MLevel, CurrentAmount, BypassUntil) VALUES (1, 'Dummy_Data', RAND(), RAND(), RAND(), RAND(), RAND());");
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, 20, Memory.getInstance().getConfig().getInt("MySQL.DummyData.SendRate", 300)* 20L);
    }

}

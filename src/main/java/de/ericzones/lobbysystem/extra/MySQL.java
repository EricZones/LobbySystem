// Created by Eric B. 17.05.2021 11:53
package de.ericzones.lobbysystem.extra;

import de.ericzones.lobbysystem.LobbySystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private LobbySystem instance;
    private String username;
    private String password;
    private String database;
    private String host;
    private int port;
    private Connection connection;

    public MySQL(LobbySystem instance, String username, String password, String database, String host, int port) {
        this.instance = instance;
        this.username = username;
        this.password = password;
        this.database = database;
        this.host = host;
        this.port = port;
    }

    public void connect() {
        if(!isConnected()) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database+"?autoReconnect=true", username, password);
                instance.getServer().getConsoleSender().sendMessage(Utils.sql_connected+" ["+database+"]");
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if(isConnected()) {
            try {
                connection.close();
                connection = null;
                instance.getServer().getConsoleSender().sendMessage(Utils.sql_disconnected+" ["+database+"]");
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void createTable(String table, String... columns) {
        if(isConnected()) {
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < columns.length; i++)
                builder.append(columns[i]+",");
            builder.setLength(builder.toString().length()-1);
            try {
                connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS "+table+" ("+builder.toString()+")");
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else {
            instance.getServer().getConsoleSender().sendMessage(Utils.sql_error);
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    public Connection getConnection() {
        return connection;
    }

}

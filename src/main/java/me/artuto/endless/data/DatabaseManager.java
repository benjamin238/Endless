package me.artuto.endless.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.artuto.endless.loader.Config;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class DatabaseManager
{
    private static Config config;
    private final String driver = "java.sql.Driver";
    private static DataSource datasource;
    private HikariDataSource source;
    private HikariConfig db;

    public DataSource getDataSource()
    {
        if(datasource==null)
        {
            db = new HikariConfig();

            String database = config.getDatabase();
            String databaseUrl = config.getDatabaseUrl();
            String databasePort = config.getDatabasePort();
            String username = config.getDatabaseUsername();
            String password = config.getDatabasePassword();
            int poolSize = config.getPoolSize();

            db.setMaximumPoolSize(poolSize);

            db.setPoolName("endless");
            db.setDataSourceClassName(driver);
            db.addDataSourceProperty("serverName", databaseUrl);
            db.addDataSourceProperty("port", databasePort);
            db.addDataSourceProperty("databaseName", database);
            db.setUsername(username);
            db.setPassword(password);

            db.addDataSourceProperty("cachePrepStmts", "true");
            db.addDataSourceProperty("alwaysSendSetIsolation", "false");
            db.addDataSourceProperty("cacheServerConfiguration", "true");
            db.addDataSourceProperty("elideSetAutoCommits", "true");
            db.addDataSourceProperty("useLocalSessionState", "true");
            db.addDataSourceProperty("useServerPrepStmts", "true");
            db.addDataSourceProperty("prepStmtCacheSize", "250");
            db.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            db.addDataSourceProperty("cacheCallableStmts", "true");
            db.addDataSourceProperty("characterEncoding", "utf8");
            db.addDataSourceProperty("useUnicode", "true");

            db.setConnectionTimeout(TimeUnit.SECONDS.toMillis(15));
            db.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(10));
            db.setConnectionTestQuery("/* Endless Database Test Ping */ SELECT 1");

            source = new HikariDataSource(db);
        }

        return datasource;
    }

    public void shutdown()
    {
        if(!(source==null))
        {
            source.close();
        }
    }

    public void setModlog(Long tcId, Long guildId)
    {
        Connection connection;
        PreparedStatement pstmt;
        ResultSet resultSet;

        try
        {
            DataSource dataSource = getDataSource();
            connection = dataSource.getConnection();
            pstmt = connection.prepareStatement("SELECT modlog FROM settings WHERE guild_id = "+guildId);
        }
        catch(SQLException e)
        {

        }

    }
}

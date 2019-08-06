package hyman.config;

import hyman.utils.Constant;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class Log4j2ConnectionFactory {

    private static interface Singleton {
        final Log4j2ConnectionFactory INSTANCE = new Log4j2ConnectionFactory();
    }

    private final DataSource dataSource;

    private Log4j2ConnectionFactory() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }

        Properties properties = new Properties();
        properties.setProperty("user", Constant.getProperty("jdbc.user", ""));
        properties.setProperty("password", Constant.getProperty("jdbc.password", ""));

        GenericObjectPool pool = new GenericObjectPool();

        DriverManagerConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
                Constant.getProperty("jdbc.url", ""), properties);

        new PoolableConnectionFactory(connectionFactory, pool, null,
                "SELECT 1", 3, false, false,
                Connection.TRANSACTION_REPEATABLE_READ);

        this.dataSource = new PoolingDataSource(pool);
    }

    public static Connection getDatabaseConnection() throws SQLException {
        return Singleton.INSTANCE.dataSource.getConnection();
    }
}

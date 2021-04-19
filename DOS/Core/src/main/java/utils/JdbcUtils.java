package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Class used to get connections to the database.
 */

public class JdbcUtils {

    private final Properties jdbcProps;
    private Connection instance = null;

    private static final Logger _logger = LogManager.getLogger();

    public JdbcUtils(Properties props) {
        _logger.info("Initializing JdbcUtils.");

        jdbcProps = props;
        var urlProp = jdbcProps.getProperty("jdbc.url");
        if (!urlProp.contains("jdbc:sqlite:")) {
            var fixedProperty = Paths.get("..").toAbsolutePath().getParent().getParent().getParent();
            for (var token : urlProp.split("/")) {
                fixedProperty = Paths.get(fixedProperty.toString(), token);
            }
            jdbcProps.setProperty("jdbc.url", "jdbc:sqlite:" + fixedProperty.toString());
        }
    }

    /**
     * Creates a new connection to the database.
     * @return a new connection to the database.
     */
    private Connection getNewConnection() {
        _logger.traceEntry("Getting new database connection.");

        var url = jdbcProps.getProperty("jdbc.url");

        _logger.info("Using URL: {}", url);

        var user = jdbcProps.getProperty("jdbc.user");
        var pass = jdbcProps.getProperty("jdbc.pass");

        Connection con = null;
        try {
            if (user != null && pass != null) {
                con = DriverManager.getConnection(url, user, pass);
            }
            else {
                _logger.warn("No security info found.");

                con = DriverManager.getConnection(url);
            }
        } catch (SQLException e) {
            _logger.error(e);
        }

        return _logger.traceExit(con);
    }

    /**
     * Retrieves or creates a new connection.
     * @return a connection.
     */
    public Connection getConnection() {
        _logger.traceEntry("Getting existing database connection.");

        try {
            if (instance == null || instance.isClosed()) {
                instance = getNewConnection();
            }
        } catch (SQLException e) {
            _logger.error(e);
        }

        return _logger.traceExit(instance);
    }
}
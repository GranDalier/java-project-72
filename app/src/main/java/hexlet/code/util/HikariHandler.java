package hexlet.code.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HikariHandler {

    public static HikariDataSource getHikariDataSource() {
        var hikariConfig = new HikariConfig();

        String dbUrl = getEnvVar("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
        String dbms = dbUrl.split(":")[1];

        log.info("DBMS: %s - DB_URL: %s".formatted(dbms, dbUrl));

        hikariConfig.setJdbcUrl(dbUrl);
        return switch (dbms) {
            case "h2" -> new HikariDataSource(hikariConfig);
            case "postgresql" -> {
                hikariConfig.setUsername(getEnvVar("USERNAME"));
                hikariConfig.setPassword(getEnvVar("PASSWORD"));
                yield new HikariDataSource(hikariConfig);
            }
            default -> throw new IllegalArgumentException("Illegal DBMS name '%s'".formatted(dbms));
        };
    }

    private static String getEnvVar(String key, String defaultValue) {
        return System.getenv().getOrDefault(key, defaultValue);
    }

    private static String getEnvVar(String key) {
        return getEnvVar(key, "");
    }
}

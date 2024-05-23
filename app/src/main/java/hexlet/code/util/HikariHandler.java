package hexlet.code.util;

import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import hexlet.code.App;
import hexlet.code.repository.BaseRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HikariHandler {

    public static void prepareDatabase() throws IOException, SQLException {
        var hikariConfig = new HikariConfig();
        DriverManager.registerDriver(new org.postgresql.Driver());

        String dbUrl = System.getenv()
                .getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
        String dbms = dbUrl.split(":")[1];

        hikariConfig.setJdbcUrl(dbUrl);
        if (dbms.equals("postgresql")) {
            hikariConfig.setUsername(System.getenv("USERNAME"));
            hikariConfig.setPassword(System.getenv("PASSWORD"));
        }
        var dataSource = new HikariDataSource(hikariConfig);

        var sql = readSqlFile(dbms);
        log.info(sql);
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }
        BaseRepository.setDataSource(dataSource);
    }

    private static String readSqlFile(String dbms) throws IOException {
        String filePath = switch (dbms) {
            case "h2" -> "schema.sql";
            case "postgresql" -> "schemaPSQL.sql";
            default -> throw new IllegalArgumentException("Unsupported DBMS '%s'".formatted(dbms));
        };

        var inputStream = Optional.ofNullable(App.class.getClassLoader().getResourceAsStream(filePath));
        if (inputStream.isEmpty()) {
            throw new IOException();
        }

        var streamReader = new InputStreamReader(inputStream.get(), StandardCharsets.UTF_8);
        try (BufferedReader reader = new BufferedReader(streamReader)) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}

package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class UrlChecksRepository extends BaseRepository {

    public static void save(UrlCheck urlCheck) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        try (var conn = getDataSource().getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            urlCheck.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

            int cnt = 1;
            preparedStatement.setLong(cnt++, urlCheck.getUrlId());
            preparedStatement.setInt(cnt++, urlCheck.getStatusCode());
            preparedStatement.setString(cnt++, urlCheck.getTitle());
            preparedStatement.setString(cnt++, urlCheck.getH1());
            preparedStatement.setString(cnt++, urlCheck.getDescription());
            preparedStatement.setTimestamp(cnt, urlCheck.getCreatedAt());
            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static LinkedList<UrlCheck> findAll(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ?";
        try (var conn = getDataSource().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            var resultSet = stmt.executeQuery();
            return resultSetToList(resultSet);
        }
    }

    public static LinkedList<Map<String, Object>> getLastChecks() throws SQLException {
        String selectPart = "SELECT DISTINCT ON (urls.id) "
                            + "urls.id AS url_id, urls.name, checks.status_code, checks.created_at FROM ";
        String joinPart = "urls LEFT JOIN url_checks AS checks ON urls.id = checks.url_id ";
        String utilPart = "ORDER BY url_id ASC, created_at DESC";
        String sql = selectPart + joinPart + utilPart;
        try (var conn = getDataSource().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var result = new LinkedList<Map<String, Object>>();
            while (resultSet.next()) {
                var lastCheck = new HashMap<String, Object>();
                lastCheck.put("url_id", resultSet.getLong("url_id"));
                lastCheck.put("name", resultSet.getString("name"));
                lastCheck.put("status_code", resultSet.getInt("status_code"));
                lastCheck.put("created_at", resultSet.getTimestamp("created_at"));
                result.add(lastCheck);
            }
            return result;
        }
    }

    private static LinkedList<UrlCheck> resultSetToList(ResultSet resultSet) throws SQLException {
        var result = new LinkedList<UrlCheck>();

        while (resultSet.next()) {
            var id = resultSet.getLong("id");
            var urlId = resultSet.getLong("url_id");
            var statusCode = resultSet.getInt("status_code");
            var title = resultSet.getString("title");
            var h1 = resultSet.getString("h1");
            var description = resultSet.getString("description");
            var createdAt = resultSet.getTimestamp("created_at");
            var urlCheck = new UrlCheck(urlId, statusCode, title, h1, description);
            urlCheck.setId(id);
            urlCheck.setCreatedAt(createdAt);
            result.add(urlCheck);
        }

        return result;
    }
}

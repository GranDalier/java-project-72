package hexlet.code.model;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Url {
    private Long id;

    private String name;
    private Timestamp createdAt;

    private int statusCode;
    private Timestamp lastCheckedAt;

    public Url(String name) {
        this.name = name;
    }
}

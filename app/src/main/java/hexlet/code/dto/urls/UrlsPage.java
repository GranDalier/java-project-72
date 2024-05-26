package hexlet.code.dto.urls;

import hexlet.code.dto.BasePage;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UrlsPage extends BasePage {
    private List<Map<String, Object>> lastChecks;
}

package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.sql.SQLException;

import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.MockResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.testtools.JavalinTest;

public final class AppTest {

    private static MockWebServer mockServer;

    private Javalin app;

    @BeforeAll
    public static void prepareMockServer() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @AfterAll
    public static void shutdownMockServer() throws IOException {
        mockServer.shutdown();
    }

    @BeforeEach
    public void setUp() throws IOException, SQLException {
        app = App.getApp();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());

            String responseBody = response.body() != null ? response.body().string() : "";
            assertThat(responseBody).contains("Анализатор страниц");
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var getResponse = client.get(NamedRoutes.urlsPath());
            assertThat(getResponse.code()).isEqualTo(HttpStatus.OK.getCode());
        });
    }

    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            // Case with correct URL
            var requestBody1 = "url=https://example.com";
            var response1 = client.post(NamedRoutes.urlsPath(), requestBody1);
            String responseBody1 = response1.body() != null ? response1.body().string() : "";

            assertThat(response1.code()).isEqualTo(HttpStatus.OK.getCode());
            assertThat(responseBody1).contains(">Анализатор страниц<");
            assertThat(responseBody1).contains("https://example.com");

            // Case with incorrect URL
            var requestBody2 = "url=httxample.com";
            var response2 = client.post(NamedRoutes.urlsPath(), requestBody2);
            String responseBody2 = response2.body() != null ? response2.body().string() : "";

            assertThat(response1.code()).isEqualTo(HttpStatus.OK.getCode());
            assertThat(responseBody2).contains(">Сайты<");
            assertThat(responseBody2).doesNotContain("httxample.com");
        });
    }

    @Test
    public void testUrlPage() throws SQLException {
        Url url = new Url("https://example.com");
        UrlsRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath(url.getId()));
            assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
        });
    }

    @Test
    public void testCheckUrlPage() throws IOException, SQLException {
        String testPagePath = "./src/test/resources/testPage.html";
        String page = Files.readString(Paths.get(testPagePath));

        MockResponse mockResponse = new MockResponse().setResponseCode(HttpStatus.OK.getCode()).setBody(page);
        mockServer.enqueue(mockResponse);
        String urlString = mockServer.url("/").toString();

        Url url = new Url(urlString);
        UrlsRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var getResponse = client.get(NamedRoutes.urlsPath());
            assertThat(getResponse.code()).isEqualTo(HttpStatus.OK.getCode());

            var postResponse = client.post(NamedRoutes.urlCheckPath(url.getId()));
            assertThat(postResponse.code()).isEqualTo(HttpStatus.OK.getCode());

            var lastCheck = UrlChecksRepository.findAll(url.getId()).getFirst();
            assertThat(lastCheck.getStatusCode()).isEqualTo(HttpStatus.OK.getCode());
            assertThat(lastCheck.getTitle()).isEqualTo("Example title");
            assertThat(lastCheck.getH1()).isEqualTo("Example header 1");
            assertThat(lastCheck.getDescription()).contains("Example description");
        });
    }

    @Test
    public void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath("999"));
            assertThat(response.code()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
        });
    }
}

package hexlet.code.controller;

import static io.javalin.rendering.template.TemplateUtil.model;

import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.View;
import hexlet.code.util.NamedRoutes;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.jsoup.Jsoup;

import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Collections;

public class UrlsController {
    public static void index(Context ctx) throws SQLException {
        var lastChecks = UrlChecksRepository.getLastChecks();
        var page = new UrlsPage(lastChecks);
        View.getFlashMessage(ctx, page);
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        try {
            var rawName = ctx.formParamAsClass("url", String.class).getOrDefault("");
            URL urlModel = URI.create(rawName).toURL();
            var name = buildStringFromUrl(urlModel);

            boolean isExists = UrlsRepository.getEntities().stream()
                    .anyMatch(url -> url.getName().equals(name));

            if (isExists) {
                View.setFlashMessage(ctx, "Страница уже существует", "info");
            } else {
                var url = new Url(name);
                UrlsRepository.save(url);
                View.setFlashMessage(ctx, "Страница успешно добавлена", "success");
            }
            ctx.redirect(NamedRoutes.urlsPath(), HttpStatus.FOUND);
        } catch (IllegalArgumentException | MalformedURLException e) {
            View.setFlashMessage(ctx, "Некорректный URL", "danger");
            ctx.redirect(NamedRoutes.rootPath(), HttpStatus.FOUND);
        }
    }

    private static String buildStringFromUrl(URL url) {
        String base = url.getProtocol() + "://" + url.getHost();
        return base + (url.getPort() == -1 ? "" : ":" + url.getPort());
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlsRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        var urlChecks = UrlChecksRepository.findAll(url.getId());
        Collections.reverse(urlChecks);

        var page = new UrlPage(url, urlChecks);
        View.getFlashMessage(ctx, page);
        ctx.render("urls/show.jte", model("page", page));
    }

    public static void checkUrl(Context ctx) throws SQLException {
        long urlId = ctx.pathParamAsClass("id", Long.class).get();

        String url = UrlsRepository.find(urlId)
                .orElseThrow(() -> new NotFoundResponse("Url with id = " + urlId + " not found")).getName();

        try {
            HttpResponse<String> response = Unirest.get(url).asString();
            var html = Jsoup.parse(response.getBody());

            int statusCode = response.getStatus();
            String title = html.title().isEmpty() ? null : html.title();
            var firstH1 = html.select("h1").first();
            String h1 = firstH1 == null ? null : firstH1.ownText();
            var descriptionElement = html.select("meta[name=description]").first();
            String description = descriptionElement == null ? null : descriptionElement.attr("content");

            UrlChecksRepository.save(new UrlCheck(urlId, statusCode, title, h1, description));
            View.setFlashMessage(ctx, "Страница успешно проверена", "success");
        } catch (UnirestException e) {
            View.setFlashMessage(ctx, "Некорректный адрес", "danger");
        }

        ctx.redirect(NamedRoutes.urlPath(urlId), HttpStatus.FOUND);
    }
}

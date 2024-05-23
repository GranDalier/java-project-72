package hexlet.code.controller;

import static io.javalin.rendering.template.TemplateUtil.model;

import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.View;
import hexlet.code.util.NamedRoutes;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;

import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Collections;

public class UrlsController {
    public static void index(Context ctx) throws SQLException {
        var urls = UrlsRepository.getEntities();
        var page = new UrlsPage(urls);
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
}

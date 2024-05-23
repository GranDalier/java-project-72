package hexlet.code.controller;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import hexlet.code.util.View;

import java.sql.SQLException;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.jsoup.Jsoup;

import io.javalin.http.NotFoundResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UrlChecksController {

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

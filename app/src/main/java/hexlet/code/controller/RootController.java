package hexlet.code.controller;

import static io.javalin.rendering.template.TemplateUtil.model;

import hexlet.code.dto.BasePage;
import hexlet.code.util.View;
import io.javalin.http.Context;

public class RootController {
    public static void index(Context ctx) {
        var page = new BasePage();
        View.getFlashMessage(ctx, page);
        ctx.render("index.jte", model("page", page));
    }
}

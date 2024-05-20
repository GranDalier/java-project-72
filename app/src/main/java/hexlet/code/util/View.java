package hexlet.code.util;

import hexlet.code.dto.BasePage;

import io.javalin.http.Context;

public class View {

    public static void setFlashMessage(Context ctx, String text, String type) {
        ctx.sessionAttribute("flash", text);
        ctx.sessionAttribute("flash-type", type);
    }

    public static void getFlashMessage(Context ctx, BasePage page) {
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
    }
}

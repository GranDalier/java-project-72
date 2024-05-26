@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.util.NamedRoutes
@import java.sql.Timestamp
@import java.time.format.DateTimeFormatter

@param UrlsPage page
@param DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

@template.layout.page(
page = page,
content = @`
    <section>
        <div class="container-lg mt-5">
            <h1>Сайты</h1>
            <table class="table table-bordered table-hover mt-3">
                <thead>
                <tr>
                    <th class="col-1">ID</th>
                    <th>Имя</th>
                    <th class="col-2">Последняя проверка</th>
                    <th class="col-1">Код ответа</th>
                </tr>
                </thead>
                <tbody>
                @for(var lastCheck : page.getLastChecks())
                    <tr>
                        <td>
                            ${(long) lastCheck.get("url_id")}
                        </td>
                        <td>
                            <a href="${NamedRoutes.urlPath((long) lastCheck.get("url_id"))}">
                                ${(String) lastCheck.get("name")}
                            </a>
                        </td>
                        <td>
                            @if(lastCheck.get("created_at") != null)
                                ${formatter.format(((Timestamp) lastCheck.get("created_at")).toLocalDateTime())}
                            @endif
                        </td>
                        <td>
                            @if((int) (lastCheck.get("status_code")) != 0)
                                ${(int) lastCheck.get("status_code")}
                            @endif
                        </td>
                    </tr>
                @endfor
                </tbody>
            </table>
        </div>
    </section>
`
)

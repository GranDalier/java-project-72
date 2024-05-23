@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.model.Url
@import hexlet.code.util.NamedRoutes
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
                @for(Url url : page.getUrls())
                    <tr>
                        <td>
                            ${url.getId()}
                        </td>
                        <td>
                            <a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a>
                        </td>
                        <td>
                            @if(url.getLastCheckedAt() != null)
                                ${formatter.format(url.getLastCheckedAt().toLocalDateTime())}
                            @endif
                        </td>
                        <td>
                            @if(url.getStatusCode() != 0)
                                ${url.getStatusCode()}
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

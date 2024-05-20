@import hexlet.code.dto.urls.UrlPage
@import java.time.format.DateTimeFormatter

@param UrlPage page
@param DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

@template.layout.page(
content = @`
    <section>
        <div class="container-lg mt-5">
            <h1>Сайт: ${page.getUrl().getName()}</h1>
            <table class="table table-bordered table-hover mt-3">
                <tbody>
                <tr>
                    <td>ID</td>
                    <td>${page.getUrl().getId()}</td>
                </tr>
                <tr>
                    <td>Имя</td>
                    <td>${page.getUrl().getName()}</td>
                </tr>
                <tr>
                    <td>Дата создания</td>
                    <td>${formatter.format(page.getUrl().getCreatedAt().toLocalDateTime())}</td>
                </tr>
                </tbody>
            </table>
        </div>
    </section>
`
)

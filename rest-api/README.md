<h1 align="center">Task 4 - simple REST API (TODO project)</h1>
<p>Лабораторная работа №4 по предмету "Сетевые и интернет технологии"</p>
<h2>Основная задача</h2>
<p>Разработать простой REST сервис, который сможет:<p>
<ol>
    <li>Добавлять пользователей</li>
    <li>Добавлять задачи пользователям</li>
    <li>Просматривать задачи пользователей</li>
    <li>Модифицировать задачи пользователя</li>
    <li>Удалять задачи пользователя и самого пользователя</li>
</ol>
<p>Endpoints:</p>
<ul>
    <li>POST /api/v1/user - создание пользователя</li>
    <li>GET /api/v1/user/{userId} - информация о пользователе</li>
    <li>GET /api/v1/user/{userId}/todo - список задач пользователя</li>
    <li>DELETE /api/v1/user/{userId} - удаление пользователя</li>
    <li>POST /api/v1/todo - создание задачи</li>
    <li>GET /api/v1/todo/{taskId} - информация о задаче</li>
    <li>PUT /api/v1/todo - изменение информации о задаче</li>
    <li>DELETE /api/v1/todo/{taskId} - удаление задачи</li>
</ul>
<p>При этом:</p>
<ol>
    <li>Если пользователя нет - то выкидывать 404</li>
    <li>Если задачи нет - то выкидывать 404</li>
    <li>Добавление существующего пользователя - выкидывать ошибку (выкидываю 400)</li>
</ol>
<h2>Необходимо для запуска</h2>
<ul>
    <li>Java версии 17+</li>
</ul>
<h2>Запуск проекта</h2>
<ol>
    <li>Скачать проект и открыть в удобной IDE</li>
    <li>Запустить проект</li>
</ol>
<p>Или</p>
<ol>
    <li>Скачать проект и перейти в папку с проектом</li>
    <li>В консоли прописать gradlew build</li>
    <li>Затем перейти по пути rest-api/build/libs и в консоли прописать java -jar rest-api-"project version"-SNAPSHOT.jar</li>
</ol>
<p>Dockerfile писать в новый год лень</p>
<h2>Swagger file</h2>
<p>Вся API описана в swagger file и доступна при запуске проекта по ссылке http://localhost:8080/swagger-ui.html</p>
<img src="./img/../swagger.png">
<h2>Запросы</h2>
<p>С установленным плагином REST Client на VS code можно поиграться с запросами.</p>
<p>Результаты запросов описаны в документе lab_4.docx</p>
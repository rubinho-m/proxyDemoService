# Proxy Service
## Обзор

Данный репозиторий представляет сервис по проксированию HTTP и WebSocket запросов. В сервисе реализовано:
- Проксирование HTTP запросов
- Аутентификация с различными ролями и уровнями доступа
- Управление ролями других пользователей администратором
- Подтверждение аккаунта и сброс пароля через почту
- Ведение аудита запросов к сервису в логфайл и в базу данных
- Кэширование запросов
- Юнит-тесты на написанный код
- Конечная точка для проксирования запросов по WebSocket
- Поддержка аутентификации, ролей и аудита запросов функционалом для WebSocket
- Удобный запуск приложения через Docker
## Стек

- Java Spring Boot
- PostgreSQL
- Spring Data JPA
- JWT
- JUnit + Mockito 
- SMTP API
- Docker
## Функционал

### Аутентификация

Регистрация и вход осуществляются посредством POST запросов. После удачной авторизации в системе клиентской стороне отправляется Bearer Token, который используется для доступа к защищенным адресам.

<img src="https://github.com/rubinho-m/proxyDemoService/blob/master/media/registration.gif" width="100%"/>

<img src="https://github.com/rubinho-m/proxyDemoService/blob/master/media/login.gif" width="100%"/>

#### Подтверждение аккаунта

Пользователь при регистрации вводит свой email, на который приходит уникальный код, необходимый для активации аккаунта POST запросом. После получения сервером необходимого кода и его проверки, роль пользователя меняется на верифицированного. 
<img src="https://github.com/rubinho-m/proxyDemoService/blob/master/media/activation.gif" width="100%"/>

#### Сброс пароля

Если пользователь хочет сбросить пароль, он посылает соответствующий запрос, ему на почту приходит уникальный код, который он отправляет вместе с новым паролем. После чего пароль в системе меняется.

<img src="https://github.com/rubinho-m/proxyDemoService/blob/master/media/activation.png" width="50%"/>

**ВНИМАНИЕ:**  для работы функционала с почтой необходимо указать данные для SMTP API в переменных окружения. 
#### Регистрация администраторов

Чтобы при регистрации получить роль администратора, нужно пройти регистрацию по специальному адресу и указать секретное слово. Если секретное слово верное, то администратор успешно добавится в базу данных.

**Секретное слово для регистрации администраторов - vkProxy**
#### Функции администратора

Администратор может назначать роли другим пользователям (и делать их администраторами тоже). Так, например, администратор может одному пользователю выдать права на пользование всем функционалом обработчика, а другому только права на чтение.

### Проксирование HTTP

Пользователь может отправить необходимый HTTP запрос, указав параметры для авторизации (хэдер Authorization, в который внести Bearer Token). Если пользователь имеет соответствующие права, прокси запрос к сервису https://jsonplaceholder.typicode.com/ будет выполнен. При повторном выполнении запроса данные возьмутся из кэша.

<img src="https://github.com/rubinho-m/proxyDemoService/blob/master/media/http.gif" width="100%"/>

### Проксирование WebSocket

Пользователь может установить соединение с конечной точкой /ws для проксирования запросов к сервису https://websocket.org/tools/websocket-echo-server/. Необходимо указать в параметрах регистрации Bearer Token для авторизации. Если пользователь имеет соответствующие права (права администратора в случае с WebSocket), то он получит доступ к эхо-серверу.

<img src="https://github.com/rubinho-m/proxyDemoService/blob/master/media/ws.gif" width="100%"/>


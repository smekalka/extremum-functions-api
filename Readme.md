### Общее описание

Подключение данной библиотеки к проекту позволяет развернуть соответствующий артефакт в extremum в качестве пакета serverless-функций, обеспечивая возможность вызова функций пакета посредством API платформы и назначения их обработчиками сигналов и триггеров хранилища. Каждая функция оформляется как класс в проекте.

### Подключение API для функций пакета
**1. Импорт библиотеки**
```
io.extremum:functions-api:<version>
```

**2. Добавление аннотации FunctionPackage с именем пакета к SpringBootApplication классу**
```kotlin
import io.extremum.common.annotation.function.FunctionPackage

@SpringBootApplication
@FunctionPackage(name = "my-package")
class MyFunctionApplication
```
Где ```my-package``` - название создаваемого пакета функций
Требования к названию:
 - состоит из букв английского алфавита, цифр, символа "-";
 - не может начинаться с цифры или с "-";
 - не может заканчиваться на символ "-";
 - длина должна быть от 3 до 63 символов.

**3. Создание необходимых функций**
 - функция должна иметь аннотацию ```@Function```;
 - функция должна быть унаследована от ```BasePackageFunction```, должна реализовывать ее методы ```onStorageTrigger``` и ```onSignal```;
 - функция должна иметь ```public``` поле типа ```Context``` с аннотацией ```@FunctionContext```;
 - вызываемый метод функции должен иметь аннотацию ```@FunctionMethod```.

Пример функции

```kotlin
import io.extremum.common.annotation.function.Function
import io.extremum.common.annotation.function.FunctionContext
import io.extremum.common.annotation.function.FunctionMethod
import io.extremum.functions.api.function.BasePackageFunction
import io.extremum.functions.api.function.model.Context
import io.extremum.functions.api.function.model.SignalParameters
import io.extremum.functions.api.function.model.StorageTriggerParameters

@Function(name = "number-func")
class NumberFunction : BasePackageFunction {

    @FunctionContext
    val context = Context.EMPTY

   /**
    * Выполняется при запросе к функции пакета (основной метод функции)
    */
    @FunctionMethod
    fun launch(params: Map<String, Any?>): Int {
        // на момент вызова в context-е заполнены headers из тела запроса

        // реализация метода
    }

    /**
     * Выполняется при получении сообщения от триггера Object Storage
     */
    override suspend fun onStorageTrigger(parameters: StorageTriggerParameters) {
        // на момент вызова в context-е заполнены headers

        // реализация метода
    }

    /**
     * Выполняется при получении сигнала
     */
    override suspend fun onSignal(parameters: SignalParameters) {
        // на момент вызова в context-е заполнены headers

        // реализация метода
    }
    
    // другие методы
}
```

Требования к имени функции:
 - Имя функции (в примере ```number-func```) может не совпадать с названием класса функции.
 - Требования к имени функции такие же как к названию пакета.
 - Имена функций должны быть уникальны в пределах одного пакета.

Требования к основному методу функции (с аннотацией @FunctionMethod):
 - Метод, помеченный аннотацией ```@FunctionMethod```, (или основной метод) должен быть только один в функции.
 - Метод должен принимать один и только один параметр. Это может быть свой объект, map, коллекция, число и др. 
   Параметр может быть ```nullable```.
 - Метод может возвращать любое значение, в том числе ```nullable```, или не возвращать вовсе, если не требуется.

**4. Добавление параметров в application.properties**

#### Основные параметры

```properties
extremum.functions.api.xAppId=
extremum.functions.api.baseUrl=
```
```extremum.ground.client.xAppId``` - id приложения (далее ```xAppId```)

```extremum.ground.client.baseUrl``` - основной url приложения (далее ```baseUrl```). Например, ```https://api.aj84.y.extremum.io```

Если их не указывать, то они заполнятся из переменных среды ```xAppId``` и ```apiBaseUrl``` соответственно.

#### Параметры для consul

```properties
extremum.functions.api.consul.uri=
extremum.functions.api.consul.trigger.table.path=
```

```extremum.functions.api.consul.uri``` - uri консула. Необязательный параметр. Если не указывать, то он будет сформирован из ```baseUrl```.

```extremum.functions.api.consul.trigger.table.path``` - дополнительный путь в uri консула до trigger table. 
По умолчанию ```/v1/kv/trigger_table_app${xAppId}```, где ```xAppId``` - переменная среды.

#### Параметры для keycloak

```properties
extremum.functions.api.keycloak.uri=
extremum.functions.api.keycloak.get.token.path=
extremum.functions.api.keycloak.serviceClientId=
extremum.functions.api.keycloak.serviceClientSecret=
extremum.functions.api.keycloak.username=
extremum.functions.api.keycloak.password=
```

```extremum.functions.api.keycloak.uri``` - uri keycloak. Необязательный параметр. Если не указывать, то он будет сформирован из
```baseUrl``` и ```xAppId```.

```extremum.functions.api.keycloak.get.token.path``` - путь до получения token. По умолчанию ```/realms/extremum/protocol/openid-connect/token```.

### Подключаемый api

После имплементации библиотеки доступны запросы
```
POST http://localhost:8080/
Content-Type: application/json
```

Где ```http://localhost:8080/``` - url сервера с приложением.

Тело зависит от назначения вызова. Доступны следующие запросы

#### Прямой вызов функции пакета

При прямом вызове функции тело имеет вид

```json
{
   "parameters": {
      "name": "Jack",
      "age": 23
   },
   "context": {
      "headers": {
         "Authorization": "Bearer ",
         "x-app-id": "appid"
      },
      "package": "my-package",
      "function": "number-func"
   }
}
```
Тип ```parameters``` должен соответствовать типу параметра основного метода функции ```number-func```.

В ```context.package``` указывается название вызываемого пакета. 
В ```context.function``` - название вызываемой функции.

При вызове функции заданный контекст заполняется в поле ```context``` функции
и вызывается основной метод функции с переданными параметрами.

Ответом на запрос будет результирующее значение основного метода функции.

#### Обработка триггера

```json
{
   "messages": [
      {
         "event_metadata": {
            "event_id": "bb1dd06d-a82c-49b4-af98-d8e0c5a1d8f0",
            "event_type": "yandex.cloud.events.storage.ObjectDelete",
            "created_at": "2019-12-19T14:17:47.847365Z",
            "tracing_context": {
               "trace_id": "dd52ace79c62892f"
            }
         },
         "details": {
            "bucket_id": "s3-for-trigger",
            "object_id": "dev/0_15a775_972dbde4_orig12.jpg"
         }
      }
   ]
}
```
Где ```messages``` - список сообщений.

Для каждого сообщения определяется, какие функции пакета подписаны на него. 
Затем для каждой из таких функций вызывается метод ```onStorageTrigger``` с нужными ей сообщениями.

#### Обработка сигнала

```json
{
   "messages": [
      {
         "event_metadata": {
            "event_id": "cce76685-5828-4304-a83d-95643c0507a0",
            "event_type": "yandex.cloud.events.messagequeue.QueueMessage",
            "created_at": "2019-09-24T00:54:28.980441Z"
         },
         "details": {
            "queue_id": "yrn:yc:ymq:ru-central1:21i6v06sqmsaoeon7nus:event-queue",
            "message": {
               "message_id": "cce76685-5828-4304-a83d-95643c0507a0",
               "md5_of_body": "d29343907090dff4cec4a9a0efb80d20",
               "body": "message body",
               "attributes": {
                  "SentTimestamp": "1569285804456"
               }
            }
         }
      }
   ]
}
```
Где ```messages``` - список сообщений.

Для каждого сообщения определяется, какие функции пакета подписаны на него.
Затем для каждой из таких функций вызывается метод ```onSignal``` с нужными ей сообщениями.
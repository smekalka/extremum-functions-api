### Подключение API для функций пакета
**1. Импорт библиотеки**
```
io.extremum:functions-api:<version>
```

**2. Добавление аннотации FunctionPackage с именем пакета к SpringBootApplication классу**
```kotlin
@SpringBootApplication
@FunctionPackage(name = "my-package")
class MyFunctionApplication
```
Где my-package - название создаваемого пакета функций
Требования к названию:
 - состоит из букв английского алфавита, цифр, символа "-";
 - не может начинаться с цифры или с "-";
 - не может заканчиваться на символ "-";
 - длина должна быть от 3 до 63 символов.

**3. Создание необходимых функций**
 - функция должна иметь аннотацию @Function;
 - функция должна быть унаследована от BasePackageFunction;
 - функция должна иметь public поле типа Context с аннотацией @FunctionContext;
 - вызываемый метод функции должен иметь аннотацию @FunctionMethod.

Пример функции
```kotlin
import io.extremum.common.annotation.function.Function
import io.extremum.common.annotation.function.FunctionContext
import io.extremum.common.annotation.function.FunctionMethod
import io.extremum.functions.api.model.Context

@Function(name = "number-func")
class NumberFunction : BasePackageFunction {

    @FunctionContext
    val context = Context.EMPTY

   /**
    * Выполняется при запросе к функции пакета
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

Требования к функции и к ее основному методу:

 - Имя функции (в примере "number-func") может не совпадать с названием класса функции.
 - Требования к имени функции такие же как к названию пакета.
 - Имена функций должны быть уникальны в пределах одного пакета.
 - Метод должен принимать один и только один параметр, это может быть свой объект, map, коллекция, число и др. 
   Параметр может быть nullable.
 - Метод может возвращать любое значение, в том числе nullable, или не возвращать вовсе, если не требуется.

**4. Добавление параметров в application.properties**
Или в переменные окружения контейнера
```
xAppId=
apiBaseUrl=
serviceClientId=
serviceClientSecret=
keycloakUri=
groundUri=
```

### Вызов функции пакета
После имплементации доступны запросы вида

```
POST http://localhost:8080/
Content-Type: application/json

{
  "parameters": {
    "name": "Jack",
    "age": 23
  },
  "context": {
    "headers": {
      "Authorization": "Bearer "
    },
    "package": "my-package",
    "function": "number-func"
  }
}
```
Тип "parameters" должен соответствовать типу параметра основного метода функции numberFunc.
Ответом на запрос будет результирующее значение основного метода в функции numberFunc.

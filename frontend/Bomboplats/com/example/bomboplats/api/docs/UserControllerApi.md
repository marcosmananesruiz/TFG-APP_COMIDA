# UserControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createImageUrl**](UserControllerApi.md#createImageUrl) | **GET** /users/imageUrl | Generar link para subir fotos de usuarios al S3 |
| [**deleteByID**](UserControllerApi.md#deleteByID) | **DELETE** /users/delete/{id} | Eliminar un usuario |
| [**findAll**](UserControllerApi.md#findAll) | **GET** /users/getAll | Obtener todos los usuarios |
| [**getByEmail**](UserControllerApi.md#getByEmail) | **GET** /users/getByEmail | Obtener un usuario por su Email |
| [**getByID**](UserControllerApi.md#getByID) | **GET** /users/get | Obtener un usuario por su ID |
| [**getPlatosFavoritos**](UserControllerApi.md#getPlatosFavoritos) | **GET** /users/  platosfavoritos | Obtener los platos favoritos de un usuario |
| [**getUserIDs**](UserControllerApi.md#getUserIDs) | **GET** /users/get/id | Obtener todos los IDs |
| [**login**](UserControllerApi.md#login) | **PUT** /users/login | Comprueba el login de un usuario |
| [**registerUser**](UserControllerApi.md#registerUser) | **POST** /users/register | Registrar un usuario |
| [**updatePassword**](UserControllerApi.md#updatePassword) | **PUT** /users/updatePass | Actualizar la contraseña de un usuario segun su id |
| [**updateUser**](UserControllerApi.md#updateUser) | **PUT** /users/save | Actualizar información de un usuario |


<a id="createImageUrl"></a>
# **createImageUrl**
> String createImageUrl(id)

Generar link para subir fotos de usuarios al S3

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.UserControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    UserControllerApi apiInstance = new UserControllerApi(defaultClient);
    String id = "id_example"; // String | 
    try {
      String result = apiInstance.createImageUrl(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserControllerApi#createImageUrl");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**|  | |

### Return type

**String**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Se genero el link |  -  |

<a id="deleteByID"></a>
# **deleteByID**
> Boolean deleteByID(id)

Eliminar un usuario

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.UserControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    UserControllerApi apiInstance = new UserControllerApi(defaultClient);
    String id = "id_example"; // String | 
    try {
      Boolean result = apiInstance.deleteByID(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserControllerApi#deleteByID");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**|  | |

### Return type

**Boolean**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | true: Usuario eliminado. false: Usuario no existía o hubo un error |  -  |

<a id="findAll"></a>
# **findAll**
> List&lt;User&gt; findAll()

Obtener todos los usuarios

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.UserControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    UserControllerApi apiInstance = new UserControllerApi(defaultClient);
    try {
      List<User> result = apiInstance.findAll();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserControllerApi#findAll");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;User&gt;**](User.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Usuarios encontrados |  -  |
| **404** | No se han encontrado usuarios |  -  |

<a id="getByEmail"></a>
# **getByEmail**
> User getByEmail(email)

Obtener un usuario por su Email

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.UserControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    UserControllerApi apiInstance = new UserControllerApi(defaultClient);
    String email = "email_example"; // String | 
    try {
      User result = apiInstance.getByEmail(email);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserControllerApi#getByEmail");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **email** | **String**|  | [optional] |

### Return type

[**User**](User.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Usuario encontrado |  -  |
| **404** | No se ha encontrado al usuario |  -  |
| **500** | Los parametros son incorrectos |  -  |

<a id="getByID"></a>
# **getByID**
> User getByID(id)

Obtener un usuario por su ID

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.UserControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    UserControllerApi apiInstance = new UserControllerApi(defaultClient);
    String id = "id_example"; // String | 
    try {
      User result = apiInstance.getByID(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserControllerApi#getByID");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**|  | [optional] |

### Return type

[**User**](User.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Usuario encontrado |  -  |
| **404** | No se ha encontrado al usuario |  -  |
| **500** | Los parámetros son incorrectos |  -  |

<a id="getPlatosFavoritos"></a>
# **getPlatosFavoritos**
> List&lt;Plato&gt; getPlatosFavoritos(id)

Obtener los platos favoritos de un usuario

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.UserControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    UserControllerApi apiInstance = new UserControllerApi(defaultClient);
    String id = "id_example"; // String | 
    try {
      List<Plato> result = apiInstance.getPlatosFavoritos(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserControllerApi#getPlatosFavoritos");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**|  | |

### Return type

[**List&lt;Plato&gt;**](Plato.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Platos favoritos del usuario encontrados correctamente |  -  |
| **404** | No se ha encontrado el usuario |  -  |

<a id="getUserIDs"></a>
# **getUserIDs**
> List&lt;String&gt; getUserIDs()

Obtener todos los IDs

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.UserControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    UserControllerApi apiInstance = new UserControllerApi(defaultClient);
    try {
      List<String> result = apiInstance.getUserIDs();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserControllerApi#getUserIDs");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

**List&lt;String&gt;**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Se encontraron todos los IDs |  -  |
| **404** | No hay ninguna ID registrada |  -  |

<a id="login"></a>
# **login**
> Boolean login(loginAttempt)

Comprueba el login de un usuario

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.UserControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    UserControllerApi apiInstance = new UserControllerApi(defaultClient);
    LoginAttempt loginAttempt = new LoginAttempt(); // LoginAttempt | 
    try {
      Boolean result = apiInstance.login(loginAttempt);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserControllerApi#login");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **loginAttempt** | [**LoginAttempt**](LoginAttempt.md)|  | |

### Return type

**Boolean**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | true si el login es correcto, false si el login es incorrecto o no se encuentra ese email |  -  |

<a id="registerUser"></a>
# **registerUser**
> User registerUser(userRegister)

Registrar un usuario

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.UserControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    UserControllerApi apiInstance = new UserControllerApi(defaultClient);
    UserRegister userRegister = new UserRegister(); // UserRegister | 
    try {
      User result = apiInstance.registerUser(userRegister);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserControllerApi#registerUser");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userRegister** | [**UserRegister**](UserRegister.md)|  | |

### Return type

[**User**](User.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | true: Usuario registrado. false: Usuario ya existía o hubo un error |  -  |

<a id="updatePassword"></a>
# **updatePassword**
> Boolean updatePassword(userId, password)

Actualizar la contraseña de un usuario segun su id

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.UserControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    UserControllerApi apiInstance = new UserControllerApi(defaultClient);
    String userId = "userId_example"; // String | 
    String password = "password_example"; // String | 
    try {
      Boolean result = apiInstance.updatePassword(userId, password);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserControllerApi#updatePassword");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userId** | **String**|  | |
| **password** | **String**|  | |

### Return type

**Boolean**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | true: Se actualizo la contraseña correctamente |  -  |
| **404** | No se ha encontrado al usuario con ese ID |  -  |

<a id="updateUser"></a>
# **updateUser**
> Boolean updateUser(user)

Actualizar información de un usuario

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.UserControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    UserControllerApi apiInstance = new UserControllerApi(defaultClient);
    User user = new User(); // User | 
    try {
      Boolean result = apiInstance.updateUser(user);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserControllerApi#updateUser");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **user** | [**User**](User.md)|  | |

### Return type

**Boolean**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | true: Usuario actualizado. false: Usuario no existía o hubo un error |  -  |


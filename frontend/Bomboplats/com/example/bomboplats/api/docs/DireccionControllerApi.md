# DireccionControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deleteById**](DireccionControllerApi.md#deleteById) | **DELETE** /direccion/delete/{id} | Borrar una dirección por su Id |
| [**findAll4**](DireccionControllerApi.md#findAll4) | **GET** /direccion/getAll | Obtener direcciones |
| [**getDireccionById**](DireccionControllerApi.md#getDireccionById) | **GET** /direccion/get |  |
| [**getDireccionIDs**](DireccionControllerApi.md#getDireccionIDs) | **GET** /direccion/get/id | Obtener unicamente los ID de las direcciones |
| [**getDireccionOfRestaurante**](DireccionControllerApi.md#getDireccionOfRestaurante) | **GET** /direccion/getByRestaurante |  |
| [**getDireccionOfUser**](DireccionControllerApi.md#getDireccionOfUser) | **GET** /direccion/getByUser |  |
| [**registerDireccion**](DireccionControllerApi.md#registerDireccion) | **POST** /direccion/register | Registrar una dirección |
| [**updateDireccion**](DireccionControllerApi.md#updateDireccion) | **PUT** /direccion/save | Actualizar una dirección |


<a id="deleteById"></a>
# **deleteById**
> Boolean deleteById(id)

Borrar una dirección por su Id

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.DireccionControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DireccionControllerApi apiInstance = new DireccionControllerApi(defaultClient);
    String id = "id_example"; // String | 
    try {
      Boolean result = apiInstance.deleteById(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DireccionControllerApi#deleteById");
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
| **200** | true: Borrado exitoso. false: El registro no existe o se produjo un error |  -  |

<a id="findAll4"></a>
# **findAll4**
> List&lt;Direccion&gt; findAll4()

Obtener direcciones

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.DireccionControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DireccionControllerApi apiInstance = new DireccionControllerApi(defaultClient);
    try {
      List<Direccion> result = apiInstance.findAll4();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DireccionControllerApi#findAll4");
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

[**List&lt;Direccion&gt;**](Direccion.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Direcciones encontradas |  -  |
| **404** | No se encontraron direcciones |  -  |

<a id="getDireccionById"></a>
# **getDireccionById**
> Direccion getDireccionById(id)



### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.DireccionControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DireccionControllerApi apiInstance = new DireccionControllerApi(defaultClient);
    String id = "id_example"; // String | 
    try {
      Direccion result = apiInstance.getDireccionById(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DireccionControllerApi#getDireccionById");
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

[**Direccion**](Direccion.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Dirección encontrada |  -  |
| **404** | No se encontró la dirección |  -  |
| **500** | parametros incorrectos |  -  |

<a id="getDireccionIDs"></a>
# **getDireccionIDs**
> List&lt;String&gt; getDireccionIDs()

Obtener unicamente los ID de las direcciones

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.DireccionControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DireccionControllerApi apiInstance = new DireccionControllerApi(defaultClient);
    try {
      List<String> result = apiInstance.getDireccionIDs();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DireccionControllerApi#getDireccionIDs");
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
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Se han obtenido todos los IDs exitosamente |  -  |
| **404** | No se han encontrado IDs |  -  |

<a id="getDireccionOfRestaurante"></a>
# **getDireccionOfRestaurante**
> List&lt;Direccion&gt; getDireccionOfRestaurante(restaurante)



### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.DireccionControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DireccionControllerApi apiInstance = new DireccionControllerApi(defaultClient);
    String restaurante = "restaurante_example"; // String | 
    try {
      List<Direccion> result = apiInstance.getDireccionOfRestaurante(restaurante);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DireccionControllerApi#getDireccionOfRestaurante");
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
| **restaurante** | **String**|  | [optional] |

### Return type

[**List&lt;Direccion&gt;**](Direccion.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Dirección encontrada |  -  |
| **404** | No se encontró la dirección |  -  |
| **500** | Parametros incorrectos |  -  |

<a id="getDireccionOfUser"></a>
# **getDireccionOfUser**
> List&lt;Direccion&gt; getDireccionOfUser(user)



### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.DireccionControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DireccionControllerApi apiInstance = new DireccionControllerApi(defaultClient);
    String user = "user_example"; // String | 
    try {
      List<Direccion> result = apiInstance.getDireccionOfUser(user);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DireccionControllerApi#getDireccionOfUser");
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
| **user** | **String**|  | [optional] |

### Return type

[**List&lt;Direccion&gt;**](Direccion.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Dirección encontrada |  -  |
| **404** | No se encontró la dirección |  -  |
| **500** | Parametros incorrectos |  -  |

<a id="registerDireccion"></a>
# **registerDireccion**
> Direccion registerDireccion(direccion)

Registrar una dirección

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.DireccionControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DireccionControllerApi apiInstance = new DireccionControllerApi(defaultClient);
    Direccion direccion = new Direccion(); // Direccion | 
    try {
      Direccion result = apiInstance.registerDireccion(direccion);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DireccionControllerApi#registerDireccion");
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
| **direccion** | [**Direccion**](Direccion.md)|  | |

### Return type

[**Direccion**](Direccion.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | true: Registro exitoso. false: El registro ya existe o se produjo un error |  -  |

<a id="updateDireccion"></a>
# **updateDireccion**
> Boolean updateDireccion(direccion)

Actualizar una dirección

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.DireccionControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DireccionControllerApi apiInstance = new DireccionControllerApi(defaultClient);
    Direccion direccion = new Direccion(); // Direccion | 
    try {
      Boolean result = apiInstance.updateDireccion(direccion);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DireccionControllerApi#updateDireccion");
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
| **direccion** | [**Direccion**](Direccion.md)|  | |

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
| **200** | true: Actualization exitosa. false: El registro no existe o se produjo un error |  -  |


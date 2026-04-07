# RestauranteControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deleteRestaurante**](RestauranteControllerApi.md#deleteRestaurante) | **DELETE** /restaurante/delete/{id} | Eliminar un restaurante |
| [**findAll1**](RestauranteControllerApi.md#findAll1) | **GET** /restaurante/getAll | Obtener todos los restaurantes |
| [**getByDescription**](RestauranteControllerApi.md#getByDescription) | **GET** /restaurante/getporescription | Buscar restaurantes por descripción |
| [**getByNombre**](RestauranteControllerApi.md#getByNombre) | **GET** /restaurante/getpornombre | Buscar restaurantes por nombre |
| [**getByTag**](RestauranteControllerApi.md#getByTag) | **GET** /restaurante/getportag | Buscar restaurantes por tag |
| [**getRestauranteById**](RestauranteControllerApi.md#getRestauranteById) | **GET** /restaurante/get/{id} | Obtener restaurante por ID |
| [**getRestauranteIconUploadUrl**](RestauranteControllerApi.md#getRestauranteIconUploadUrl) | **GET** /restaurante/icon-upload-url/{id} | Obtener URL prefirmada para subir foto de restaurante |
| [**register**](RestauranteControllerApi.md#register) | **POST** /restaurante/register | Registrar un restaurante |
| [**registerPlato**](RestauranteControllerApi.md#registerPlato) | **POST** /restaurante/{idRestaurante}/plato/register | Registrar un plato asociado a un restaurante |
| [**updateRestaurante**](RestauranteControllerApi.md#updateRestaurante) | **PUT** /restaurante/save | Actualizar un restaurante |


<a id="deleteRestaurante"></a>
# **deleteRestaurante**
> Boolean deleteRestaurante(id)

Eliminar un restaurante

### Example
```java
// Import classes:
import com.example.bomboplats.api.ApiClient;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.Configuration;
import com.example.bomboplats.api.models.*;
import com.example.bomboplats.api.RestauranteControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    RestauranteControllerApi apiInstance = new RestauranteControllerApi(defaultClient);
    String id = "id_example"; // String | 
    try {
      Boolean result = apiInstance.deleteRestaurante(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RestauranteControllerApi#deleteRestaurante");
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
| **200** | true: Restaurante eliminado. false: No existía o hubo error |  -  |

<a id="findAll1"></a>
# **findAll1**
> List&lt;Restaurante&gt; findAll1()

Obtener todos los restaurantes

### Example
```java
// Import classes:
import com.example.bomboplats.api.ApiClient;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.Configuration;
import com.example.bomboplats.api.models.*;
import com.example.bomboplats.api.RestauranteControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    RestauranteControllerApi apiInstance = new RestauranteControllerApi(defaultClient);
    try {
      List<Restaurante> result = apiInstance.findAll1();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RestauranteControllerApi#findAll1");
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

[**List&lt;Restaurante&gt;**](Restaurante.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Restaurantes encontrados |  -  |
| **404** | No se han encontrado restaurantes |  -  |

<a id="getByDescription"></a>
# **getByDescription**
> List&lt;Restaurante&gt; getByDescription(description)

Buscar restaurantes por descripción

### Example
```java
// Import classes:
import com.example.bomboplats.api.ApiClient;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.Configuration;
import com.example.bomboplats.api.models.*;
import com.example.bomboplats.api.RestauranteControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    RestauranteControllerApi apiInstance = new RestauranteControllerApi(defaultClient);
    String description = "description_example"; // String | 
    try {
      List<Restaurante> result = apiInstance.getByDescription(description);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RestauranteControllerApi#getByDescription");
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
| **description** | **String**|  | |

### Return type

[**List&lt;Restaurante&gt;**](Restaurante.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Restaurantes encontrados |  -  |
| **404** | No se han encontrado restaurantes |  -  |

<a id="getByNombre"></a>
# **getByNombre**
> List&lt;Restaurante&gt; getByNombre(nombre)

Buscar restaurantes por nombre

### Example
```java
// Import classes:
import com.example.bomboplats.api.ApiClient;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.Configuration;
import com.example.bomboplats.api.models.*;
import com.example.bomboplats.api.RestauranteControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    RestauranteControllerApi apiInstance = new RestauranteControllerApi(defaultClient);
    String nombre = "nombre_example"; // String | 
    try {
      List<Restaurante> result = apiInstance.getByNombre(nombre);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RestauranteControllerApi#getByNombre");
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
| **nombre** | **String**|  | |

### Return type

[**List&lt;Restaurante&gt;**](Restaurante.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Restaurantes encontrados |  -  |
| **404** | No se han encontrado restaurantes |  -  |

<a id="getByTag"></a>
# **getByTag**
> List&lt;Restaurante&gt; getByTag(tag)

Buscar restaurantes por tag

### Example
```java
// Import classes:
import com.example.bomboplats.api.ApiClient;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.Configuration;
import com.example.bomboplats.api.models.*;
import com.example.bomboplats.api.RestauranteControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    RestauranteControllerApi apiInstance = new RestauranteControllerApi(defaultClient);
    String tag = "tag_example"; // String | 
    try {
      List<Restaurante> result = apiInstance.getByTag(tag);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RestauranteControllerApi#getByTag");
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
| **tag** | **String**|  | |

### Return type

[**List&lt;Restaurante&gt;**](Restaurante.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Restaurantes encontrados |  -  |
| **404** | No se han encontrado restaurantes |  -  |

<a id="getRestauranteById"></a>
# **getRestauranteById**
> Restaurante getRestauranteById(id)

Obtener restaurante por ID

### Example
```java
// Import classes:
import com.example.bomboplats.api.ApiClient;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.Configuration;
import com.example.bomboplats.api.models.*;
import com.example.bomboplats.api.RestauranteControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    RestauranteControllerApi apiInstance = new RestauranteControllerApi(defaultClient);
    String id = "id_example"; // String | 
    try {
      Restaurante result = apiInstance.getRestauranteById(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RestauranteControllerApi#getRestauranteById");
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

[**Restaurante**](Restaurante.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Restaurante encontrado |  -  |
| **404** | No se ha encontrado el restaurante |  -  |
| **500** | Parámetros incorrectos |  -  |

<a id="getRestauranteIconUploadUrl"></a>
# **getRestauranteIconUploadUrl**
> String getRestauranteIconUploadUrl(id, index)

Obtener URL prefirmada para subir foto de restaurante

### Example
```java
// Import classes:
import com.example.bomboplats.api.ApiClient;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.Configuration;
import com.example.bomboplats.api.models.*;
import com.example.bomboplats.api.RestauranteControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    RestauranteControllerApi apiInstance = new RestauranteControllerApi(defaultClient);
    String id = "id_example"; // String | 
    Integer index = 0; // Integer | 
    try {
      String result = apiInstance.getRestauranteIconUploadUrl(id, index);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RestauranteControllerApi#getRestauranteIconUploadUrl");
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
| **index** | **Integer**|  | [optional] [default to 0] |

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
| **200** | URL generada |  -  |
| **404** | Restaurante no encontrado |  -  |

<a id="register"></a>
# **register**
> Restaurante register(restaurante)

Registrar un restaurante

### Example
```java
// Import classes:
import com.example.bomboplats.api.ApiClient;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.Configuration;
import com.example.bomboplats.api.models.*;
import com.example.bomboplats.api.RestauranteControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    RestauranteControllerApi apiInstance = new RestauranteControllerApi(defaultClient);
    Restaurante restaurante = new Restaurante(); // Restaurante | 
    try {
      Restaurante result = apiInstance.register(restaurante);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RestauranteControllerApi#register");
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
| **restaurante** | [**Restaurante**](Restaurante.md)|  | |

### Return type

[**Restaurante**](Restaurante.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Restaurante registrado con su ID generado |  -  |

<a id="registerPlato"></a>
# **registerPlato**
> Plato registerPlato(idRestaurante, plato)

Registrar un plato asociado a un restaurante

### Example
```java
// Import classes:
import com.example.bomboplats.api.ApiClient;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.Configuration;
import com.example.bomboplats.api.models.*;
import com.example.bomboplats.api.RestauranteControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    RestauranteControllerApi apiInstance = new RestauranteControllerApi(defaultClient);
    String idRestaurante = "idRestaurante_example"; // String | 
    Plato plato = new Plato(); // Plato | 
    try {
      Plato result = apiInstance.registerPlato(idRestaurante, plato);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RestauranteControllerApi#registerPlato");
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
| **idRestaurante** | **String**|  | |
| **plato** | [**Plato**](Plato.md)|  | |

### Return type

[**Plato**](Plato.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Plato registrado con su ID generado |  -  |

<a id="updateRestaurante"></a>
# **updateRestaurante**
> Boolean updateRestaurante(restaurante)

Actualizar un restaurante

### Example
```java
// Import classes:
import com.example.bomboplats.api.ApiClient;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.Configuration;
import com.example.bomboplats.api.models.*;
import com.example.bomboplats.api.RestauranteControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    RestauranteControllerApi apiInstance = new RestauranteControllerApi(defaultClient);
    Restaurante restaurante = new Restaurante(); // Restaurante | 
    try {
      Boolean result = apiInstance.updateRestaurante(restaurante);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RestauranteControllerApi#updateRestaurante");
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
| **restaurante** | [**Restaurante**](Restaurante.md)|  | |

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
| **200** | true: Restaurante actualizado. false: No existía o hubo error |  -  |


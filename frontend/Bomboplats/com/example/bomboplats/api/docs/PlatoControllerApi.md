# PlatoControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deletePlato**](PlatoControllerApi.md#deletePlato) | **DELETE** /plato/delete/{id} | Eliminar un plato por su ID |
| [**findAll2**](PlatoControllerApi.md#findAll2) | **GET** /plato/getAll | Obtener todos los platos |
| [**getByNombre1**](PlatoControllerApi.md#getByNombre1) | **GET** /plato/getpornombre | Buscar platos por nombre |
| [**getByRestaurante**](PlatoControllerApi.md#getByRestaurante) | **GET** /plato/getporid | Buscar platos por ID de restaurante |
| [**getByRestauranteAndNombre**](PlatoControllerApi.md#getByRestauranteAndNombre) | **GET** /plato/getporplatoynombre | Buscar platos por restaurante y nombre |
| [**getByTag1**](PlatoControllerApi.md#getByTag1) | **GET** /plato/getportag | Buscar platos por tag |
| [**getPlatoById**](PlatoControllerApi.md#getPlatoById) | **GET** /plato/get/{id} | Obtener un plato por su ID |
| [**getPlatoIconUploadUrl**](PlatoControllerApi.md#getPlatoIconUploadUrl) | **GET** /plato/icon-upload-url/{id} | Obtener URL prefirmada para subir foto de plato |
| [**register1**](PlatoControllerApi.md#register1) | **POST** /plato/register | Registrar un nuevo plato |
| [**updatePlato**](PlatoControllerApi.md#updatePlato) | **PUT** /plato/save | Actualizar un plato existente |


<a id="deletePlato"></a>
# **deletePlato**
> Boolean deletePlato(id)

Eliminar un plato por su ID

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PlatoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PlatoControllerApi apiInstance = new PlatoControllerApi(defaultClient);
    String id = "id_example"; // String | 
    try {
      Boolean result = apiInstance.deletePlato(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PlatoControllerApi#deletePlato");
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
| **200** | true: Plato eliminado. false: No existía o hubo error |  -  |

<a id="findAll2"></a>
# **findAll2**
> List&lt;Plato&gt; findAll2()

Obtener todos los platos

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PlatoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PlatoControllerApi apiInstance = new PlatoControllerApi(defaultClient);
    try {
      List<Plato> result = apiInstance.findAll2();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PlatoControllerApi#findAll2");
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

[**List&lt;Plato&gt;**](Plato.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Platos encontrados |  -  |
| **404** | No se han encontrado platos |  -  |

<a id="getByNombre1"></a>
# **getByNombre1**
> List&lt;Plato&gt; getByNombre1(nombre)

Buscar platos por nombre

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PlatoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PlatoControllerApi apiInstance = new PlatoControllerApi(defaultClient);
    String nombre = "nombre_example"; // String | 
    try {
      List<Plato> result = apiInstance.getByNombre1(nombre);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PlatoControllerApi#getByNombre1");
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

[**List&lt;Plato&gt;**](Plato.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Platos encontrados |  -  |
| **404** | No se han encontrado platos |  -  |

<a id="getByRestaurante"></a>
# **getByRestaurante**
> List&lt;Plato&gt; getByRestaurante(idRestaurante)

Buscar platos por ID de restaurante

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PlatoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PlatoControllerApi apiInstance = new PlatoControllerApi(defaultClient);
    String idRestaurante = "idRestaurante_example"; // String | 
    try {
      List<Plato> result = apiInstance.getByRestaurante(idRestaurante);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PlatoControllerApi#getByRestaurante");
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
| **200** | Platos encontrados |  -  |
| **404** | No se han encontrado platos |  -  |

<a id="getByRestauranteAndNombre"></a>
# **getByRestauranteAndNombre**
> List&lt;Plato&gt; getByRestauranteAndNombre(idRestaurante, nombre)

Buscar platos por restaurante y nombre

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PlatoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PlatoControllerApi apiInstance = new PlatoControllerApi(defaultClient);
    String idRestaurante = "idRestaurante_example"; // String | 
    String nombre = "nombre_example"; // String | 
    try {
      List<Plato> result = apiInstance.getByRestauranteAndNombre(idRestaurante, nombre);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PlatoControllerApi#getByRestauranteAndNombre");
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
| **nombre** | **String**|  | |

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
| **200** | Platos encontrados |  -  |
| **404** | No se han encontrado platos |  -  |

<a id="getByTag1"></a>
# **getByTag1**
> List&lt;Plato&gt; getByTag1(tag)

Buscar platos por tag

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PlatoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PlatoControllerApi apiInstance = new PlatoControllerApi(defaultClient);
    String tag = "tag_example"; // String | 
    try {
      List<Plato> result = apiInstance.getByTag1(tag);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PlatoControllerApi#getByTag1");
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

[**List&lt;Plato&gt;**](Plato.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Platos encontrados |  -  |
| **404** | No se han encontrado platos |  -  |

<a id="getPlatoById"></a>
# **getPlatoById**
> Plato getPlatoById(id)

Obtener un plato por su ID

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PlatoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PlatoControllerApi apiInstance = new PlatoControllerApi(defaultClient);
    String id = "id_example"; // String | 
    try {
      Plato result = apiInstance.getPlatoById(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PlatoControllerApi#getPlatoById");
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

[**Plato**](Plato.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Plato encontrado |  -  |
| **404** | No se ha encontrado el plato |  -  |
| **500** | Parámetros incorrectos |  -  |

<a id="getPlatoIconUploadUrl"></a>
# **getPlatoIconUploadUrl**
> String getPlatoIconUploadUrl(id)

Obtener URL prefirmada para subir foto de plato

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PlatoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PlatoControllerApi apiInstance = new PlatoControllerApi(defaultClient);
    String id = "id_example"; // String | 
    try {
      String result = apiInstance.getPlatoIconUploadUrl(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PlatoControllerApi#getPlatoIconUploadUrl");
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
| **200** | URL generada |  -  |
| **404** | Plato no encontrado |  -  |

<a id="register1"></a>
# **register1**
> Plato register1(plato)

Registrar un nuevo plato

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PlatoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PlatoControllerApi apiInstance = new PlatoControllerApi(defaultClient);
    Plato plato = new Plato(); // Plato | 
    try {
      Plato result = apiInstance.register1(plato);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PlatoControllerApi#register1");
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
| **200** | true: Plato registrado. false: Ya existía o hubo un error |  -  |

<a id="updatePlato"></a>
# **updatePlato**
> Boolean updatePlato(plato)

Actualizar un plato existente

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PlatoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PlatoControllerApi apiInstance = new PlatoControllerApi(defaultClient);
    Plato plato = new Plato(); // Plato | 
    try {
      Boolean result = apiInstance.updatePlato(plato);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PlatoControllerApi#updatePlato");
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
| **plato** | [**Plato**](Plato.md)|  | |

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
| **200** | true: Plato actualizado. false: No existía o hubo error |  -  |


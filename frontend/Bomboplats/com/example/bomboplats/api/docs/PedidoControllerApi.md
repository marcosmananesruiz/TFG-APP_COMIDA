# PedidoControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deletePedido**](PedidoControllerApi.md#deletePedido) | **DELETE** /pedido/delete/{id} | Borrar un pedido |
| [**findAll3**](PedidoControllerApi.md#findAll3) | **GET** /pedido/getAll | Obtener todos los pedidos |
| [**getPedidoByEstado**](PedidoControllerApi.md#getPedidoByEstado) | **GET** /pedido/getByEstado | Obtener pedidos segun su estado |
| [**getPedidoById**](PedidoControllerApi.md#getPedidoById) | **GET** /pedido/get | Obtener un pedido segun su ID |
| [**getPedidoByPlato**](PedidoControllerApi.md#getPedidoByPlato) | **GET** /pedido/getByPlato | Obtener todos los pedidos que se han hecho de un plato según su id |
| [**getPedidoByUser**](PedidoControllerApi.md#getPedidoByUser) | **GET** /pedido/getByUser | Obtener todos los pedidos de un usuario según su id |
| [**getPedidosIDs**](PedidoControllerApi.md#getPedidosIDs) | **GET** /pedido/get/id | Obtener unicamente el id de los pedidos |
| [**register2**](PedidoControllerApi.md#register2) | **POST** /pedido/register | Registrar un pedido |
| [**updatePedido**](PedidoControllerApi.md#updatePedido) | **PUT** /pedido/save | Actualizar un pedido |


<a id="deletePedido"></a>
# **deletePedido**
> Boolean deletePedido(id)

Borrar un pedido

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PedidoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PedidoControllerApi apiInstance = new PedidoControllerApi(defaultClient);
    String id = "id_example"; // String | 
    try {
      Boolean result = apiInstance.deletePedido(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PedidoControllerApi#deletePedido");
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
| **200** | true: Pedido borrado. false: Ese registro no existe o se ha producido un error |  -  |

<a id="findAll3"></a>
# **findAll3**
> List&lt;Pedido&gt; findAll3()

Obtener todos los pedidos

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PedidoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PedidoControllerApi apiInstance = new PedidoControllerApi(defaultClient);
    try {
      List<Pedido> result = apiInstance.findAll3();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PedidoControllerApi#findAll3");
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

[**List&lt;Pedido&gt;**](Pedido.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Pedidos encontrados |  -  |
| **404** | No se han encontrado pedidos |  -  |

<a id="getPedidoByEstado"></a>
# **getPedidoByEstado**
> List&lt;Pedido&gt; getPedidoByEstado(estado)

Obtener pedidos segun su estado

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PedidoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PedidoControllerApi apiInstance = new PedidoControllerApi(defaultClient);
    String estado = "PREPARING"; // String | 
    try {
      List<Pedido> result = apiInstance.getPedidoByEstado(estado);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PedidoControllerApi#getPedidoByEstado");
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
| **estado** | **String**|  | [optional] [enum: PREPARING, DELIVERING, DELIVERED] |

### Return type

[**List&lt;Pedido&gt;**](Pedido.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Pedidos Encontrados |  -  |
| **404** | No se han encontrado pedidos |  -  |
| **500** | Parametros incorrectos |  -  |

<a id="getPedidoById"></a>
# **getPedidoById**
> Pedido getPedidoById(id)

Obtener un pedido segun su ID

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PedidoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PedidoControllerApi apiInstance = new PedidoControllerApi(defaultClient);
    String id = "id_example"; // String | 
    try {
      Pedido result = apiInstance.getPedidoById(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PedidoControllerApi#getPedidoById");
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

[**Pedido**](Pedido.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Pedidos Encontrados |  -  |
| **404** | No se ha encontrado el pedido |  -  |

<a id="getPedidoByPlato"></a>
# **getPedidoByPlato**
> List&lt;Pedido&gt; getPedidoByPlato(plato)

Obtener todos los pedidos que se han hecho de un plato según su id

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PedidoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PedidoControllerApi apiInstance = new PedidoControllerApi(defaultClient);
    String plato = "plato_example"; // String | 
    try {
      List<Pedido> result = apiInstance.getPedidoByPlato(plato);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PedidoControllerApi#getPedidoByPlato");
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
| **plato** | **String**|  | [optional] |

### Return type

[**List&lt;Pedido&gt;**](Pedido.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Pedidos Encontrados |  -  |
| **404** | No se han encontrado pedidos |  -  |
| **500** | Parametros incorrectos |  -  |

<a id="getPedidoByUser"></a>
# **getPedidoByUser**
> List&lt;Pedido&gt; getPedidoByUser(user)

Obtener todos los pedidos de un usuario según su id

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PedidoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PedidoControllerApi apiInstance = new PedidoControllerApi(defaultClient);
    String user = "user_example"; // String | 
    try {
      List<Pedido> result = apiInstance.getPedidoByUser(user);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PedidoControllerApi#getPedidoByUser");
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

[**List&lt;Pedido&gt;**](Pedido.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Pedidos Encontrados |  -  |
| **404** | No se han encontrado pedidos |  -  |
| **500** | Parametros incorrects |  -  |

<a id="getPedidosIDs"></a>
# **getPedidosIDs**
> List&lt;String&gt; getPedidosIDs()

Obtener unicamente el id de los pedidos

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PedidoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PedidoControllerApi apiInstance = new PedidoControllerApi(defaultClient);
    try {
      List<String> result = apiInstance.getPedidosIDs();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PedidoControllerApi#getPedidosIDs");
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
| **404** | No hay ningun ID |  -  |

<a id="register2"></a>
# **register2**
> Pedido register2(pedido)

Registrar un pedido

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PedidoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PedidoControllerApi apiInstance = new PedidoControllerApi(defaultClient);
    Pedido pedido = new Pedido(); // Pedido | 
    try {
      Pedido result = apiInstance.register2(pedido);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PedidoControllerApi#register2");
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
| **pedido** | [**Pedido**](Pedido.md)|  | |

### Return type

[**Pedido**](Pedido.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | true: Pedido registrado. false: Ese registro ya existe o se ha producido un error |  -  |

<a id="updatePedido"></a>
# **updatePedido**
> Boolean updatePedido(pedido)

Actualizar un pedido

### Example
```java
// Import classes:
import com.example.bomboplats.api.invoker.ApiClient;
import com.example.bomboplats.api.invoker.ApiException;
import com.example.bomboplats.api.invoker.Configuration;
import com.example.bomboplats.api.invoker.models.*;
import com.example.bomboplats.api.PedidoControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    PedidoControllerApi apiInstance = new PedidoControllerApi(defaultClient);
    Pedido pedido = new Pedido(); // Pedido | 
    try {
      Boolean result = apiInstance.updatePedido(pedido);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PedidoControllerApi#updatePedido");
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
| **pedido** | [**Pedido**](Pedido.md)|  | |

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
| **200** | true: Pedido actualizado. false: Ese registro no existe o se ha producido un error |  -  |




# Pedido


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **String** |  |  [optional] |
|**plato** | [**Plato**](Plato.md) |  |  [optional] |
|**user** | [**User**](User.md) |  |  [optional] |
|**modifications** | **List&lt;String&gt;** |  |  [optional] |
|**estado** | [**EstadoEnum**](#EstadoEnum) |  |  [optional] |
|**entrega** | **OffsetDateTime** |  |  [optional] |



## Enum: EstadoEnum

| Name | Value |
|---- | -----|
| PREPARING | &quot;PREPARING&quot; |
| DELIVERING | &quot;DELIVERING&quot; |
| DELIVERED | &quot;DELIVERED&quot; |




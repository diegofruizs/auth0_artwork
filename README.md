<<<<<<< HEAD
# Tabla de contenidos
-  [Introducción](#introducción)
-  [API](#api-de-la-aplicación-artwork)
  - [Recurso Artist](#recurso-artist)
    - [GET /artists](#GET-/artists)
    - [GET /artists/{id}](#GET-/artists/{id})
    - [POST /artists](#POST-/artists)
    - [PUT /artists/{id}](#PUT-/artists/{id})
    - [DELETE /artists/{id}](#DELETE-/artists/{id})
    - [GET artists/{artistsid}/artworks](#GET-artists/{artistsid}/artworks)
    - [GET artists/{artistsid}/artworks/{artworksid}](#GET-artists/{artistsid}/artworks/{artworksid})
    - [POST artists/{artistsid}/artworks/{artworksid}](#POST-artists/{artistsid}/artworks/{artworksid})
    - [PUT artists/{artistsid}/artworks](#PUT-artists/{artistsid}/artworks)
    - [DELETE artists/{artistsid}/artworks/{artworksid}](#DELETE-artists/{artistsid}/artworks/{artworksid}])
  - [Recurso Client](#recurso-client)
    - [GET /clients](#GET-/clients)
    - [GET /clients/{id}](#GET-/clients/{id})
    - [POST /clients](#POST-/clients)
    - [PUT /clients/{id}](#PUT-/clients/{id})
    - [DELETE /clients/{id}](#DELETE-/clients/{id})
    - [GET clients/{clientsid}/wishlist](#GET-clients/{clientsid}/wishlist)
    - [GET clients/{clientsid}/wishlist/{wishlistid}](#GET-clients/{clientsid}/wishlist/{wishlistid})
    - [POST clients/{clientsid}/wishlist/{wishlistid}](#POST-clients/{clientsid}/wishlist/{wishlistid})
    - [PUT clients/{clientsid}/wishlist](#PUT-clients/{clientsid}/wishlist)
    - [DELETE clients/{clientsid}/wishlist/{wishlistid}](#DELETE-clients/{clientsid}/wishlist/{wishlistid}])
  - [Recurso Product](#recurso-product)
    - [GET /products](#GET-/products)
    - [GET /products/{id}](#GET-/products/{id})
    - [POST /products](#POST-/products)
    - [PUT /products/{id}](#PUT-/products/{id})
    - [DELETE /products/{id}](#DELETE-/products/{id})
  - [Recurso Category](#recurso-category)
    - [GET /categorys](#GET-/categorys)
    - [GET /categorys/{id}](#GET-/categorys/{id})
    - [POST /categorys](#POST-/categorys)
    - [PUT /categorys/{id}](#PUT-/categorys/{id})
    - [DELETE /categorys/{id}](#DELETE-/categorys/{id})

# API Rest
## Introducción
La comunicación entre cliente y servidor se realiza intercambiando objetos JSON. Para cada entidad se hace un mapeo a JSON, donde cada uno de sus atributos se transforma en una propiedad de un objeto JSON. Todos los servicios se generan en la URL /Artwork.api/api/. Por defecto, todas las entidades tienen un atributo `id`, con el cual se identifica cada registro:

```javascript
{
    id: '',
    attribute_1: '',
    attribute_2: '',
    ...
    attribute_n: ''
}
```

Cuando se transmite información sobre un registro específico, se realiza enviando un objeto con la estructura mencionada en la sección anterior.
La única excepción se presenta al solicitar al servidor una lista de los registros en la base de datos, que incluye información adicional para manejar paginación de lado del servidor en el header `X-Total-Count` y los registros se envían en el cuerpo del mensaje como un arreglo.

La respuesta del servidor al solicitar una colección presenta el siguiente formato:

```javascript
[{}, {}, {}, {}, {}, {}]
```

## API de la aplicación Artwork
### Recurso Artist
El objeto Artist tiene 2 representaciones JSON:	

#### Representación Minimum
```javascript
{
    id: '' /*Tipo Long*/,
    name: '' /*Tipo String*/
}
```




#### GET /artists

Retorna una colección de objetos Artist en representación Detail.

#### Parámetros

#### N/A

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|OK|Colección de [representaciones Detail](#recurso-artist)
409|Un objeto relacionado no existe|Mensaje de error
500|Error interno|Mensaje de error

#### GET /artists/{id}

Retorna una colección de objetos Artist en representación Detail.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Artist a consultar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|OK|Objeto Artist en [representaciones Detail](#recurso-artist)
404|No existe un objeto Artist con el ID solicitado|Mensaje de error
500|Error interno|Mensaje de error

#### POST /artists

Es el encargado de crear objetos Artist.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
body|body|Objeto Artist que será creado|Sí|[Representación Detail](#recurso-artist)

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
201|El objeto Artist ha sido creado|[Representación Detail](#recurso-artist)
409|Un objeto relacionado no existe|Mensaje de error
500|No se pudo crear el objeto Artist|Mensaje de error

#### PUT /artists/{id}

Es el encargado de actualizar objetos Artist.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Artist a actualizar|Sí|Integer
body|body|Objeto Artist nuevo|Sí|[Representación Detail](#recurso-artist)

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
201|El objeto Artist actualizado|[Representación Detail](#recurso-artist)
409|Un objeto relacionado no existe|Mensaje de error
500|No se pudo actualizar el objeto Artist|Mensaje de error

#### DELETE /artists/{id}

Elimina un objeto Artist.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Artist a eliminar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
204|Objeto eliminado|N/A
500|Error interno|Mensaje de error


#### GET artists/{artistsid}/artworks

Retorna una colección de objetos Artwork asociados a un objeto Artist en representación Detail.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Artist a consultar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|OK|Colección de objetos Artwork en [representación Detail](#recurso-artwork)
500|Error consultando artworks |Mensaje de error

#### GET artists/{artistsid}/artworks/{artworksid}

Retorna un objeto Artwork asociados a un objeto Artist en representación Detail.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
artistsid|Path|ID del objeto Artist a consultar|Sí|Integer
artworksid|Path|ID del objeto Artwork a consultar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|OK|Objeto Artwork en [representación Detail](#recurso-artwork)
404|No existe un objeto Artwork con el ID solicitado asociado al objeto Artist indicado |Mensaje de error
500|Error interno|Mensaje de error

#### POST artists/{artistsid}/artworks/{artworksid}

Asocia un objeto Artwork a un objeto Artist.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
artistsid|PathParam|ID del objeto Artist al cual se asociará el objeto Artwork|Sí|Integer
artworksid|PathParam|ID del objeto Artwork a asociar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|Objeto Artwork asociado|[Representación Detail de Artwork](#recurso-artwork)
500|No se pudo asociar el objeto Artwork|Mensaje de error

#### PUT artists/{artistsid}/artworks

Es el encargado de actualizar un objeto Artwork asociada a un objeto Artist.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
artistsid|Path|ID del objeto Artist cuya colección será remplazada|Sí|Integer
body|body|Colección de objetos Artwork|Sí|[Representación Detail](#recurso-artwork)

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|Se actualizo el objeto|Objeto Artwork en [Representación Detail](#recurso-artwork)
500|No se pudo actualizar|Mensaje de error

#### DELETE artists/{artistsid}/artworks/{artworksid}

Remueve un objeto Artwork asociado a un objeto Artist.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
artistsid|Path|ID del objeto Artist asociado al objeto Artwork|Sí|Integer
artworksid|Path|ID del objeto Artwork a remover|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
204|Objeto removido|N/A
500|Error interno|Mensaje de error


[Volver arriba](#tabla-de-contenidos)
### Recurso Client
El objeto Client tiene 2 representaciones JSON:	

#### Representación Minimum
```javascript
{
    id: '' /*Tipo Long*/,
    name: '' /*Tipo String*/
}
```




#### GET /clients

Retorna una colección de objetos Client en representación Detail.

#### Parámetros

#### N/A

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|OK|Colección de [representaciones Detail](#recurso-client)
409|Un objeto relacionado no existe|Mensaje de error
500|Error interno|Mensaje de error

#### GET /clients/{id}

Retorna una colección de objetos Client en representación Detail.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Client a consultar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|OK|Objeto Client en [representaciones Detail](#recurso-client)
404|No existe un objeto Client con el ID solicitado|Mensaje de error
500|Error interno|Mensaje de error

#### POST /clients

Es el encargado de crear objetos Client.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
body|body|Objeto Client que será creado|Sí|[Representación Detail](#recurso-client)

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
201|El objeto Client ha sido creado|[Representación Detail](#recurso-client)
409|Un objeto relacionado no existe|Mensaje de error
500|No se pudo crear el objeto Client|Mensaje de error

#### PUT /clients/{id}

Es el encargado de actualizar objetos Client.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Client a actualizar|Sí|Integer
body|body|Objeto Client nuevo|Sí|[Representación Detail](#recurso-client)

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
201|El objeto Client actualizado|[Representación Detail](#recurso-client)
409|Un objeto relacionado no existe|Mensaje de error
500|No se pudo actualizar el objeto Client|Mensaje de error

#### DELETE /clients/{id}

Elimina un objeto Client.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Client a eliminar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
204|Objeto eliminado|N/A
500|Error interno|Mensaje de error


#### GET clients/{clientsid}/wishlist

Retorna una colección de objetos Item asociados a un objeto Client en representación Detail.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Client a consultar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|OK|Colección de objetos Item en [representación Detail](#recurso-item)
500|Error consultando wishlist |Mensaje de error

#### GET clients/{clientsid}/wishlist/{wishlistid}

Retorna un objeto Item asociados a un objeto Client en representación Detail.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
clientsid|Path|ID del objeto Client a consultar|Sí|Integer
wishlistid|Path|ID del objeto Item a consultar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|OK|Objeto Item en [representación Detail](#recurso-item)
404|No existe un objeto Item con el ID solicitado asociado al objeto Client indicado |Mensaje de error
500|Error interno|Mensaje de error

#### POST clients/{clientsid}/wishlist/{wishlistid}

Asocia un objeto Item a un objeto Client.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
clientsid|PathParam|ID del objeto Client al cual se asociará el objeto Item|Sí|Integer
wishlistid|PathParam|ID del objeto Item a asociar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|Objeto Item asociado|[Representación Detail de Item](#recurso-item)
500|No se pudo asociar el objeto Item|Mensaje de error

#### PUT clients/{clientsid}/wishlist

Es el encargado de actualizar un objeto Item asociada a un objeto Client.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
clientsid|Path|ID del objeto Client cuya colección será remplazada|Sí|Integer
body|body|Colección de objetos Item|Sí|[Representación Detail](#recurso-item)

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|Se actualizo el objeto|Objeto Item en [Representación Detail](#recurso-item)
500|No se pudo actualizar|Mensaje de error

#### DELETE clients/{clientsid}/wishlist/{wishlistid}

Remueve un objeto Item asociado a un objeto Client.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
clientsid|Path|ID del objeto Client asociado al objeto Item|Sí|Integer
wishlistid|Path|ID del objeto Item a remover|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
204|Objeto removido|N/A
500|Error interno|Mensaje de error


[Volver arriba](#tabla-de-contenidos)
### Recurso Product
El objeto Product tiene 2 representaciones JSON:	

#### Representación Minimum
```javascript
{
    id: '' /*Tipo Long*/,
    name: '' /*Tipo String*/,
    price: '' /*Tipo Long*/
}
```




#### GET /products

Retorna una colección de objetos Product en representación Detail.

#### Parámetros

#### N/A

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|OK|Colección de [representaciones Detail](#recurso-product)
409|Un objeto relacionado no existe|Mensaje de error
500|Error interno|Mensaje de error

#### GET /products/{id}

Retorna una colección de objetos Product en representación Detail.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Product a consultar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|OK|Objeto Product en [representaciones Detail](#recurso-product)
404|No existe un objeto Product con el ID solicitado|Mensaje de error
500|Error interno|Mensaje de error

#### POST /products

Es el encargado de crear objetos Product.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
body|body|Objeto Product que será creado|Sí|[Representación Detail](#recurso-product)

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
201|El objeto Product ha sido creado|[Representación Detail](#recurso-product)
409|Un objeto relacionado no existe|Mensaje de error
500|No se pudo crear el objeto Product|Mensaje de error

#### PUT /products/{id}

Es el encargado de actualizar objetos Product.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Product a actualizar|Sí|Integer
body|body|Objeto Product nuevo|Sí|[Representación Detail](#recurso-product)

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
201|El objeto Product actualizado|[Representación Detail](#recurso-product)
409|Un objeto relacionado no existe|Mensaje de error
500|No se pudo actualizar el objeto Product|Mensaje de error

#### DELETE /products/{id}

Elimina un objeto Product.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Product a eliminar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
204|Objeto eliminado|N/A
500|Error interno|Mensaje de error



[Volver arriba](#tabla-de-contenidos)
### Recurso Category
El objeto Category tiene 2 representaciones JSON:	

#### Representación Minimum
```javascript
{
    id: '' /*Tipo Long*/,
    name: '' /*Tipo String*/
}
```

#### Representación Detail
```javascript
{
    // todo lo de la representación Minimum más los objetos Minimum con relación simple.
    parentCategory: {
    id: '' /*Tipo Long*/,
    name: '' /*Tipo String*/    }
}
```



#### GET /categorys

Retorna una colección de objetos Category en representación Detail.
Cada Category en la colección tiene embebidos los siguientes objetos: Category.

#### Parámetros

#### N/A

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|OK|Colección de [representaciones Detail](#recurso-category)
409|Un objeto relacionado no existe|Mensaje de error
500|Error interno|Mensaje de error

#### GET /categorys/{id}

Retorna una colección de objetos Category en representación Detail.
Cada Category en la colección tiene los siguientes objetos: Category.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Category a consultar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|OK|Objeto Category en [representaciones Detail](#recurso-category)
404|No existe un objeto Category con el ID solicitado|Mensaje de error
500|Error interno|Mensaje de error

#### POST /categorys

Es el encargado de crear objetos Category.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
body|body|Objeto Category que será creado|Sí|[Representación Detail](#recurso-category)

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
201|El objeto Category ha sido creado|[Representación Detail](#recurso-category)
409|Un objeto relacionado no existe|Mensaje de error
500|No se pudo crear el objeto Category|Mensaje de error

#### PUT /categorys/{id}

Es el encargado de actualizar objetos Category.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Category a actualizar|Sí|Integer
body|body|Objeto Category nuevo|Sí|[Representación Detail](#recurso-category)

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
201|El objeto Category actualizado|[Representación Detail](#recurso-category)
409|Un objeto relacionado no existe|Mensaje de error
500|No se pudo actualizar el objeto Category|Mensaje de error

#### DELETE /categorys/{id}

Elimina un objeto Category.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Category a eliminar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
204|Objeto eliminado|N/A
500|Error interno|Mensaje de error



[Volver arriba](#tabla-de-contenidos)
=======
# auth0_artwork
>>>>>>> origin/master

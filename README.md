# Bomboplats

Aplicación móvil Android destinada a la gestión de pedidos de comida.

- [Autores](#Autores)
- [Características](#Características)
- [Instalación](#Instalación)
- [Info](#Info)
- [Tecnologías y Despliegue](#Tecnologías-y-Despliegue)
- [Disclaimer](#Disclaimer)

## Autores

- **Backend Spring Boot**: José Cáceres, Marcos Mañanes
- **Frontend Android**: Alberto Catel, Jorge Pérez

## Características

- Mostrar un listado de platos organizado por categorías.
- Permitir consultar el detalle de un plato (precio, descripción e imagen).
- Implementar un carrito de compra funcional.
- Permitir la confirmación de un pedido.
- Consumir correctamente datos desde una API REST.
- Incluir gestión de usuarios con registro e inicio de sesión funcional.
- Soporte para múltiples restaurantes.
- Gestión de estados del pedido (pendiente, en preparación, entregado, etc.).
- Envío de notificaciones al usuario.
- Implementación de un pago simulado.
- Panel de administración para la gestión de platos y pedidos. (Repositorio: [bomboplats-admin-panel](https://github.com/marcosmananesruiz/bomboplats-admin-panel))

## Instalación

Descargar la última versión disponible en el apartado [Releases](https://github.com/marcosmananesruiz/TFG-APP_COMIDA/releases) del repositorio.

Para ejecutar la aplicación puede probar de dos formas:

- Descargarla en su propio dispositivo Android y ejecutar.
  - Si su dispositivo es muy reciente, es posible que sea necesario desactivar algún sistema de bloqueo para aplicaciones de origen desconocido incluido por el fabricante. Este procedimiento puede variar según la marca y el modelo del dispositivo, por lo que dependerá del móvil en el que se desee realizar la instalación.

  - Por ejemplo, en algunos dispositivos Samsung puede ser necesario desactivar temporalmente la función de Protección de aplicaciones para permitir la instalación correctamente.

- Descargarla en el ordenador y arrastrar la APK a un dispositivo Android emulado.

Para que la aplicación funcione correctamente, debe poder conectarse al servidor en la nube. Requerirá acceso a internet y que el servidor se encuentre encendido (revisar [Disclaimer](#Disclaimer) para más información).

**Credenciales de acceso**:

- **Email**: igm147@educa.madrid.org
- **Contraseña**: Villablanca

## Info

- El servidor de la aplicación cuenta con un dominio: http://bomboplats.run.place
- Desde este [enlace](http://bomboplats.run.place:4200/admin/) se puede conectar al panel de administración.

## Tecnologías y Despliegue

**Client:**
- Android 7+ 
- Angular 21.0 (Admin Panel)
- OpenAPI Generator: 7.20.0

**Server:**
- Spring Boot 4.0.2 con Java 25
- AWS:
  - **EC2**: Despliegue con Docker
  - **RDS**: PostgreSQL 17.0
  - **S3**: Almacenamiento de imágenes

## Disclaimer

En caso de querer probar esta aplicación fuera de la demostración designada por el tutor de prácticas, será necesario avisar previamente para habilitar el servidor de AWS. Esto se debe a que se utiliza un laboratorio gratuito con fines educativos, el cual no puede permanecer activo durante más de 4 horas consecutivas.
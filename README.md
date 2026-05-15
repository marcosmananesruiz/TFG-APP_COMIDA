
# Bomboplats

Aplicación móvil Android destinada a la gestión de pedidos de comida.

- [Autores](#Authors)
- [Caracteristicas](#Caracteristicas)
- [Instalación](#Instalación)
- [Info](#Info)
- [Tecnologias y Despliegue](#Tecnologias/Despliegue)
- [Disclaimer](#Disclaimer)






## Autores

- **Backend Spring Boot**: Jose Caceres, Marcos Mañanes
- **Frontend Android**: Alberto Catel, Jorge Peréz

## Caracteristicas

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

Descargar la ultima version disponible en el apartado [Releases]() del repositorio.

Para ejecutar la aplicacion puede probar de dos forma:

- Descargarlo en su propio dispositivo Android y ejecutar.
  - Si su dispositivo es muy reciente, es posible que sea necesario desactivar algún sistema de bloqueo para aplicaciones de origen desconocido incluido por el fabricante. Este procedimiento puede variar según la marca y el modelo del dispositivo, por lo que dependerá del móvil en el que se desee realizar la instalación.

  - Por ejemplo, en algunos dispositivos de Samsung puede ser necesario desactivar temporalmente la función de Protección de aplicaciones para permitir la instalación correctamente.
- Descargarlo en el ordenador y arratrar la APK a un dispositivo Android Emulado 

Para que la aplicacion funcione correctamente, debe poder conectarse al servidor en la nube. Requerira de acceso a internet y que el servidor se encuentre encendido (Revisar [Disclaimer](#Disclaimer) para mas información)

**Credenciales de Acceso**:

- **Email**: igm147@educa.madrid.org
- **Contraseña**: Villablanca
## Info

- El servidor de la aplicacion cuenta con un dominio: http://bomboplats.run.place
- Desde este [enlace](http://bomboplats.run.place:4200/admin/) se puede contectar al Panel de Administracion
## Tecnologias/Despliegue

**Client:** 
- Android (Disponible cualquier versión)
- Angular 21.0 (Admin Panel)
- OpenAPI Generator: 7.20.0

**Server:**
- SpringBoot 4.0.2
- AWS:
  - **EC2**: Despliegue con Docker 
  - **RDS**: PostgreSQL 17.0
  - **S3**: Almacenamiento de imagenes


## Disclaimer

En caso de querer probar esta aplicación fuera de la demostración designada por el tutor de prácticas, será necesario avisar previamente para habilitar el servidor de AWS. Esto se debe a que se utiliza un laboratorio gratuito con fines educativos, el cual no puede permanecer activo durante más de 4 horas consecutivas.
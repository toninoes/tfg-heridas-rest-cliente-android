# RESTheridApp (Cliente Android)
Cliente Android para el seguimiento de heridas en consultas de enfermería. Necesario [Servicio Web REST](https://github.com/toninoes/tfg-heridas-rest-servicio-web-spring "Servicio Web REST").

## Organización

### Herramientas-Tecnologías utilizadas

- Java SE Development Kit 8
- Android Studio 2.3.2
- JSON Web Token
- Retrofit
- Toasty
- JUnit
- Robotium

## Manual de usuario
A continuación se detallan las instrucciones de uso del sistema.

### Introducción
Este manual de usuario sólo hace referencia al uso de la aplicación móvil, debido a que el [servidor](https://github.com/toninoes/tfg-heridas-rest-servicio-web-spring "servidor") sólo se dedica a tratar las peticiones que recibe, por tanto es transparente para el usuario.

Veremos el funcionamiento de la aplicación distinguiendo los siguientes roles:
- Administrador: cuyo papel fundamental será administrar el sistema.
- Sanitario: el cual realiza y registra las distintas actuaciones desde el punto de vista sanitario.
- Paciente: podrán tener acceso a la evolución de sus procesos, sus citas, recomendaciones de cuidados, etc.

### Características
El funcionamiento del sistema es sencillo, tratando que las diferentes pantallas sean intuitivas y los pictogramas sean también descriptivos.

En primer lugar, tras autenticarse nos encontraremos con una pantalla principal. Esta pantalla contiene el menú que lleva hasta las distintas funcionalidades de la aplicación, pero dicha pantalla principal será diferente dependiendo del rol del usuario:
- Administrador. Podrá gestionar desde ese panel: 
  - Centros.
  - Salas.
  - Grupos diagnósticos.
  - Diagnósticos.
  - Procedimientos.
  - Administradores.
  - Sanitarios.
  - Pacientes.
  - Valoraciones.
  - Modificar su contraseña.
- Sanitario. El cual, desde ese panel podrá: 
  - Realizar y registrar las distintas actuaciones desde el punto de vista sanitario.
  - Planificar cuidados para los diferentes grupos de diagnósticos.
  - Ver la valoración de sus actuaciones.
  - Acceder a la agenda de citaciones.
  - Modificar su contraseña.
- Paciente. Podrá tener acceso a: 
  - La evolución de sus procesos.
  - Gestionar sus citas.
  - Acceder a las recomendaciones de cuidados de sus procesos.
  - Modificar su contraseña.
  
Otro aspecto muy importante es el registro. Aunque es un registro sencillo, éste es necesario para poder utilizar la aplicación, pero será un registro delegado, es decir, no parte del propio usuario, así de esta forma:
-Los administradores podrán registrar a:
  - Otros administradores
  - Sanitarios
  - Pacientes
- Los sanitarios podrán registrar sólo a pacientes.
- Los pacientes no pueden realizar el registro de ningún tipo de usuario.

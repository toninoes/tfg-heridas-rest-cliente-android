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

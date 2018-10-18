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
- **Administrador**. Podrá gestionar desde ese panel: 
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
- **Sanitario**. El cual, desde ese panel podrá: 
  - Realizar y registrar las distintas actuaciones desde el punto de vista sanitario.
  - Planificar cuidados para los diferentes grupos de diagnósticos.
  - Ver la valoración de sus actuaciones.
  - Acceder a la agenda de citaciones.
  - Modificar su contraseña.
- **Paciente**. Podrá tener acceso a: 
  - La evolución de sus procesos.
  - Gestionar sus citas.
  - Acceder a las recomendaciones de cuidados de sus procesos.
  - Modificar su contraseña.
  
Otro aspecto muy importante es el registro. Aunque es un registro sencillo, éste es necesario para poder utilizar la aplicación, pero será un registro delegado, es decir, no parte del propio usuario, así de esta forma:
- Los administradores podrán registrar a:
  - Otros administradores.
  - Sanitarios.
  - Pacientes.
- Los sanitarios podrán registrar sólo a pacientes.
- Los pacientes no pueden realizar el registro de ningún tipo de usuario.

Veamos un ejemplo de registro en el que un administrador registra a un sanitario. Para ello el administrador primero se tiene que identificar, tal y como puede verse en la siguiente imagen:

<img src="https://github.com/toninoes/tfg-heridas-rest-cliente-android/blob/master/img/identificacion.png" width="90%">

El administrador debe pulsar sobre el icono de «Sanitarios», tras ello, aparece un listado con los sanitarios del sistema, pulsará en el icono destinado a añadir un nuevo sanitario indicado con un «+» y verá una nueva pantalla donde indicar los datos de éste. 

Muy importante es el email, ya que será ahí donde se enviará un correo electrónico para que el interesado active su cuenta y defina su contraseña. Pueden verse estos pasos en la siguiente imagen:

<img src="https://github.com/toninoes/tfg-heridas-rest-cliente-android/blob/master/img/registroSanitario.png" width="90%">

En dicha figura puede apreciarse que ya aparece el sanitario recién registrado, pero aún no ha sido activado ni tiene contraseña para poder éste acceder al sistema.

Automáticamente el [servidor](https://github.com/toninoes/tfg-heridas-rest-servicio-web-spring "servidor") envía un correo electrónico al interesado para que pulse sobre un enlace. Esta acción implicará la activación del usuario en el sistema (y de esta forma se corrobora que el email facilitado es válido) y además el sanitario podrá definir su contraseña. Pueden observarse dichos pasos en la siguiente imagen:

<img src="https://github.com/toninoes/tfg-heridas-rest-cliente-android/blob/master/img/activarSanitario.png" width="90%">

Tras todo lo anterior el interesado podrá hacer uso de la aplicación.

### Requisitos previos
Además de estar registrado y haber completado el proceso de activación descrito en el apartado anterior, será necesario en cualquier caso disponer de un dispositivo móvil con Android (versión mínima 4.1) y conexión a Internet, pero además:
- Obviamente en el caso de los sanitarios, se necesitará que este dispositivo cuente con cámara para realizar las fotografías.
- Los pacientes deberán contar con una aplicación que permita la lectura de ficheros en formato PDF.

Indicar que al haberse desarrollado una aplicación genérica para cualquier tipo de organización, deberá cada usuario configurar (con los datos que ésta le proporcione) la aplicación con los datos necesarios para que se conecte con el [servicio](https://github.com/toninoes/tfg-heridas-rest-servicio-web-spring "servicio"). Dicho servicio podrá estar alojado en instalaciones propias de la organización o en otras ajenas, pero en cualquier caso es necesario que se proporcionen al interesado.

Para acceder a la configuración de estos parámetros habrá que pulsar sobre un icono habilitado en la pantalla de login, este icono se encuentra situado en la parte superior a la derecha, como puede verse en la imagen que se mostrará a continuación. 

<img src="https://github.com/toninoes/tfg-heridas-rest-cliente-android/blob/master/img/configurarConexion.png" width="90%">

En la anterior imagen puede verse que la aplicación está avisando que no se ha conectado correctamente con el servidor, indicando al usuario que debe realizar los ajustes oportunos para conseguirlo.

### Utilización
Vamos a pasar a detallar las instrucciones de uso de la aplicación. En concreto vamos a simular el proceso completo del uso de la aplicación que contemplará:
1. El paciente reserva cita para ser atendido en su centro de referencia.
2. Una vez en el centro, el sanitario atiende a éste y registra en el sistema las anotaciones pertinentes, es decir, proceso que presenta y sus sucesivas curas. Dichas curas podrán llevar asociadas una o más imágenes.
3. El paciente podrá descargar informe de su proceso en cualquier momento y descargar planes de cuidados asociados a dicho proceso.
4. Finalmente tras cada cura, el paciente podrá valorar de forma anónima la atención recibida.

Ya hemos visto anteriormente el menú principal del administrador, ahora presentamos además qué se encuentra en cada caso un sanitario o un paciente.

<img src="https://github.com/toninoes/tfg-heridas-rest-cliente-android/blob/master/img/menusInicio.png" width="90%">

#### Reservar cita
Una vez autenticado el paciente, deberá pulsar sobre el icono «Mis citas» que se encuentra en el menú principal. Tras esto, deberá seleccionar una de las salas del centro al que pertenece y una fecha deseada, tras pulsar en el botón «mostrar disponibilidad», el sistema le presenta los horarios disponibles para esa sala en la fecha seleccionada. El paciente pulsará sobre la que desea reservar y tras confirmación la cita será registrada a su nombre. Dicha cita podrá ser modificada o anulada por el propio paciente.

<img src="https://github.com/toninoes/tfg-heridas-rest-cliente-android/blob/master/img/registrarCita.png" width="90%">

#### Atención de dicho paciente por parte de un sanitario
Llegado el día de la cita, dicho paciente será atendido por un sanitario, el cual previamente habrá podido consultar la agenda de citas para saber el listado de pacientes que acudirán a una sala en una fecha determinada.

A partir de ahí el sanitario verá un listado con todos los procesos anteriores que dicho paciente ha sufrido, podrá abrir uno nuevo o seguir trabajando sobre uno previo, para de esta forma realizar una nueva cura sobre este. Al ser este un paciente de nueva incorporación no tiene procesos previos asignados. Se registra uno nuevo en el icono indicado con «+» y se rellenan los datos sanitarios pertinentes, tras guardarlo, se le asocia una primera cura a ese proceso, tal y como puede verse seguidamente:

<img src="https://github.com/toninoes/tfg-heridas-rest-cliente-android/blob/master/img/registrarProcesoCura.png" width="90%">

Finalmente el sanitario podrá añadir una o más imágenes asociadas a dicha cura, el origen de la imagen puede ser o bien la galería de imágenes del propio dispositivo o bien la propia cámara. Tras previsualizar la imagen y escribir alguna anotación si se desea, podrá pulsarse el botón «Subir al servidor».

<img src="https://github.com/toninoes/tfg-heridas-rest-cliente-android/blob/master/img/registrarImagen.png" width="90%">

#### Descarga de informes por parte del paciente
El paciente podrá descargar informe completo de un proceso concreto, para ello pulsará sobre el botón «Asistencias», tras esto podrá observar un listado con sus procesos, sobre los que podrá pulsar y posteriormente visualizar un listado de sus curas y/o pulsar sobre el botón para descargar el informe completo en formato PDF.

Del mismo modo podrá descargar las guías de cuidados asociadas a los procesos que presenta o ha presentado con anterioridad pulsando sobre el botón «Mis cuidados».

<img src="https://github.com/toninoes/tfg-heridas-rest-cliente-android/blob/master/img/descargainforme.png" width="90%">

#### Valoración de la atención recibida
Tras cada atención, el paciente tiene la oportunidad de asignar una valoración de forma anónima, para ello deberá pulsar sobre el botón «Valorar Asistencias», donde se le presentará un listado de las atenciones pendientes de valorar. Tras indicar la
pertinente valoración será eliminada de ese listado.

<img src="https://github.com/toninoes/tfg-heridas-rest-cliente-android/blob/master/img/valorar.png" width="90%">

...y esto es todo amigos!!. Quedan aún más funcionalidades por explicar, pero lo esencial está ya mostrado. 

Para finalizar decir que son libres de copiar, mejorar, adaptar o hacer lo que os plazca con este proyecto, pero por favor, no modifiquen el logotipo de la aplicación, no me van a negar que no es bonito ;)

<img src="https://github.com/toninoes/tfg-heridas-rest-cliente-android/blob/master/img/logotipoapp.png" width="90%">


Antonio Ruiz


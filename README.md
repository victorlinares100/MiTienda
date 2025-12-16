# TiendaRopaKotlin Victor Linares 

Características Principales del Proyecto

Este proyecto fue desarrollado utilizando el framework moderno de Android Jetpack Compose y la biblioteca Material 3
Diseño y Usabilidad

•	Diseño visual funcional con Material 3: La interfaz de usuario (UI) sigue las guías más recientes de diseño de Google, Material 3.

•	Navegación fluida: Implementada mediante Compose Navigation

•	Formularios validados: Todos los formularios de entrada de datos (registro, login, administración,productos etc ) incluyen validación en tiempo real. Esto asegura que los datos ingresados cumplan con los requisitos antes de ser enviados al backend.

 Arquitectura y Gestión de Datos
 
•	Almacenamiento local: Se utiliza un mecanismo de almacenamiento local (como SharedPreferences o Room,) 

•	Consumo de APIs externas: La aplicación se conecta con servicios de terceros (ej: API de imágenes) utilizando librerías como Retrofit para peticiones HTTP eficientes y manejo de JSON.

•	Conexión con microservicios en Spring Boot: La aplicación se integra con un ecosistema de microservicios desarrollado en Spring Boot para manejar toda la lógica del negocio (CRUD de productos, procesamiento de carrito, autenticación).

Documentación técnica del proyecto: 

Repositorio del Backend: El código fuente del backend se encuentra disponible en: https://github.com/victorlinares100/Backend_Tienda.git
Modelo de Datos: El backend implementa un modelo de entidades (Productos, Carrito, Usuarios, Categorías, etc.).
Despliegue y Persistencia:

•	La API está desplegada en la plataforma Render y con la pagina Uptime Robot retorna un ok cada 5 minutos para que la página no se caiga, y La persistencia de los datos (tablas de Login, Productos, Carrito, etc.) se maneja a través de una base de datos que está en Supabase.

Visualización de Datos: La funcionalidad del backend es totalmente verificable. Los endpoints y los datos de las entidades pueden ser visualizados y probados en tiempo real a través del Swagger UI provisto en la URL de Render.

Pruebas unitarias : Se realizaron las pruebas unitarias correspondientes a Todo el viewModel para comprobar la logica del negocio sin incluir obviamente al ViewModelFactory 

Admin : el admin cuenta con 5 pestañas  de productos,dashboard,gestion de categorias y usuarios y por ultimo una pestaña de alertas en la cual puedes ver los productos que tengan bajo stock 


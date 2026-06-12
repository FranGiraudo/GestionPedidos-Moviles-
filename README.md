# Gestión de Pedidos - App Móvil B2B 📱💼

¡Bienvenido al repositorio de **Gestión de Pedidos**! Esta es una aplicación Android nativa diseñada para facilitar el trabajo de distribuidores, preventistas y vendedores que necesitan gestionar clientes, cargar pedidos en tiempo real y llevar un control total de sus ventas directamente desde el teléfono.

## 🚀 Características Principales

*   **Gestión Completa de Pedidos:** Creá, editá y eliminá pedidos. Modificá las cantidades de productos sobre la marcha con recálculo automático del total.
*   **Comprobantes Digitales:** Olvidate del papel. Podés adjuntar fotos de tickets o remitos a cada pedido directamente desde tu galería (usando el moderno y seguro `PhotoPicker` de Android).
*   **Dashboard y Estadísticas:** Resumen en vivo de tus ventas del día, cotización del dólar blue actualizada por internet, y métricas de rendimiento.
*   **Modo Oscuro Inteligente:** Interfaz diseñada 100% en Jetpack Compose que se adapta automáticamente al tema de tu dispositivo para que no te encandile trabajando de noche.
*   **Persistencia Local:** La aplicación funciona aunque la cierres. Guarda todo gracias a la potencia de `Room` (Base de Datos) y `DataStore` (para tu perfil de usuario).
*   **Integración Nativa:** Compartí el resumen de un pedido rápidamente por WhatsApp o Email usando los "Intents" nativos de Android.

## 🛠️ Tecnologías y Arquitectura

Esta aplicación fue construida aplicando los estándares modernos de desarrollo Android sugeridos por Google:

*   **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) y Navigation Compose. Todo declarativo y con animaciones fluidas.
*   **Arquitectura:** MVVM (Model-View-ViewModel). Separación clara entre los datos, la lógica y la interfaz visual.
*   **Base de Datos:** [Room Database](https://developer.android.com/training/data-storage/room) con relaciones entre tablas (Pedidos, Productos, Clientes).
*   **Preferencias:** DataStore Preferences para guardar tu sesión y nombre de perfil de forma asincrónica.
*   **Operaciones Asíncronas:** Kotlin Coroutines & Flow. Nada bloquea la pantalla principal.
*   **Networking:** Retrofit2 y Gson para consumir APIs (ej. Dólar Blue) simulando entornos reales.
*   **Imágenes:** [Coil](https://coil-kt.github.io/coil/) para carga asíncrona de comprobantes en pantalla completa.

## 📥 Cómo compilar el proyecto

1. Cloná este repositorio en tu computadora.
2. Abrilo usando **Android Studio** (versión Iguana o superior recomendada).
3. Dejá que Gradle descargue todas las dependencias e indexe el proyecto.
4. Para generar el APK listo para instalar en tu celular:
   * En el menú superior de Android Studio, andá a **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
   * Cuando termine, te va a aparecer un cartelito flotante abajo a la derecha diciendo "Build APK(s) successfully". Tocá donde dice **"locate"** y te va a abrir la carpeta donde está tu archivo `app-debug.apk`. ¡Pasatelo al celular y listo!

---
*Desarrollado como proyecto de aplicación de tecnologías modernas de Android.*

# Gestión de Pedidos — App Móvil B2B 📦

Aplicación Android nativa para distribuidores y preventistas. Permite gestionar clientes, cargar pedidos en campo, llevar métricas de ventas y sincronizar datos con la nube, todo desde el celular.

---

## Capturas

![Demo de la aplicación](demo.gif)

> *Para ver la app en acción, instalá el APK de la raíz o correla desde Android Studio.*

---

## Características

| Feature | Detalle |
|---|---|
| **Gestión de pedidos** | Crear, editar, cancelar. Recálculo automático de totales y descuento de stock en tiempo real. |
| **Comprobantes digitales** | Adjuntar foto de remito/ticket a cada pedido con `PhotoPicker`. Visualización en pantalla completa con Coil. |
| **Dashboard en vivo** | Ventas totales del período, cotización del dólar blue vía API REST, últimos movimientos. |
| **Estadísticas** | Gráfico de barras animado (ventas 7 días), ranking de productos y clientes con barra de progreso. |
| **Sincronización en la nube** | POST a Supabase con todos los pedidos locales. Se dispara desde Perfil → "Sincronizar a la nube". |
| **Clientes y Productos** | ABM completo con búsqueda y filtros por estado. |
| **Modo oscuro** | Activable desde Ajustes. Se persiste en DataStore y aplica en toda la app. |
| **Sesión persistente** | Al cerrar y reabrir la app, el usuario queda logueado automáticamente. |
| **Compartir por WhatsApp / Email** | Intent nativo para compartir el resumen de un pedido. |
| **Internacionalización** | 85 strings en `values/strings.xml` + `values-en/strings.xml`. |

---

## Stack tecnológico

```
UI            Jetpack Compose + Navigation Compose + Material Design 3
Arquitectura  MVVM — 13 ViewModels, uno por pantalla, con StateFlow<UiState>
Base de datos Room — 4 entidades, 3 DAOs, 7 migraciones
Preferencias  DataStore (sesión, nombre de usuario, dark mode)
Networking    Retrofit 2 + Gson  →  dolarapi.com (GET) + Supabase (GET/POST)
Asincronismo  Kotlin Coroutines + Flow
Imágenes      Coil (comprobantes en pantalla completa)
Inyección     ServiceLocator manual (patrón de Google para proyectos medianos)
```

---

## Arquitectura

```
com.undef.gestionpedidos
├── data
│   ├── local        # Room: entidades, DAOs, AppDatabase, migraciones
│   ├── remote       # Retrofit: ApiService, DTOs (@SerializedName)
│   ├── repository   # Única fuente de verdad para la UI
│   └── prefs        # DataStore: UserPreferencesRepository
├── di               # ServiceLocator — inicializa y provee repositorios
├── domain
│   └── model        # Pedido, Cliente, Producto, LineaPedido (clases puras)
├── ui
│   ├── components   # Components.kt — átomos reutilizables (MoneyText, StatusPill, etc.)
│   ├── feature      # Una carpeta por pantalla: XxxScreen.kt + XxxViewModel.kt
│   ├── navigation   # AppNavHost.kt, AppDestination.kt
│   └── theme        # Color.kt, Type.kt, Theme.kt (paleta Grafito + Verde)
└── worker           # SyncWorker (WorkManager — sync periódica en background)
```

---

## Flujos de red implementados

### GET — Cotización dólar blue
```
DashboardScreen → DashboardViewModel.init()
  → FinanceRepository.getDolarBlue()
  → Retrofit @GET dolarapi.com/v1/dolares/blue
  → DolarResponse { venta: Double }
  → _uiState.update { dolarBlue = nf.format(venta) }
  → StateFlow emite → Compose re-renderiza la tarjeta
```

### POST — Sincronización a Supabase
```
ProfileScreen → viewModel.syncData()
  → OrderRepository.syncOrdersToCloud()
  → Lee pedidos de Room: getAllOrders().first()
  → Mapea Pedido → OrderSyncDto (campos con @SerializedName para columnas Supabase)
  → Retrofit @POST supabase.co/rest/v1/order_syncs
  → Supabase responde 204 → response.isSuccessful = true
  → ProfileViewModel actualiza syncStatus → Compose muestra confirmación
```

### GET con Room como caché — Categorías (Supabase)
```
CategoryRepository.getCategories()
  → @GET supabase.co/rest/v1/categories
  → Lista de CategoryDto
  → Guarda en CategoryDao (Room)
  → UI observa Flow<List<Category>> del DAO
```

---

## Cómo correr el proyecto

### Requisitos
- Android Studio Iguana o superior
- JDK 11+
- Dispositivo o emulador con API 26+

### Pasos
```bash
git clone https://github.com/FranGiraudo/GestionPedidos-Moviles-
```
1. Abrí el proyecto en Android Studio
2. Esperá que Gradle sincronice las dependencias
3. Presioná **Run ▶** (Shift+F10) para instalar en el emulador/dispositivo

### Generar APK
**Build → Build Bundle(s) / APK(s) → Build APK(s)**

El archivo queda en `app/build/outputs/apk/debug/app-debug.apk`.

---

## Base de datos (Room)

| Tabla | Descripción |
|---|---|
| `clients` | Clientes con CUIT, dirección, localidad, teléfono |
| `products` | Productos con código, categoría, precio unitario, stock |
| `orders` | Cabecera del pedido: cliente, estado, fechas, observaciones, URI comprobante |
| `order_lines` | Líneas del pedido: producto, cantidad, precio unitario, subtotal |
| `categories` | Categorías de productos (sincronizadas desde Supabase) |
| `users` | Usuarios registrados localmente (email + hash SHA-256) |

Migraciones: versión 1 → 7, todas escritas a mano con `Migration(from, to)`.

---

## Desarrollado por

- **Francisco Giraudo** — Desarrollo Android  
- **Mateo Sposito** — Análisis y Testing

*Trabajo Práctico Integrador — Desarrollo de Aplicaciones Móviles*

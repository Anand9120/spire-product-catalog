# Product Catalog & Offline Cart (Android)

An Android application built for the **SPIRE Lab (IISc Bangalore)** App Developer Assessment. The app connects to the **DummyJSON REST API** to browse, search, and view detailed product specifications, coupled with a robust, locally persisted shopping cart powered by **Room Database** that remains **100% functional offline**.

---

## 🌟 Key Features

### 1. Product Catalog & Listing
* Fetches rich product catalog from `https://dummyjson.com/products`.
* Displays product thumbnail, title, brand/category, price, discount pill, and star rating badge.
* Comprehensive UI state handling:
  * **Loading state**: Smooth circular indicator during network transactions.
  * **Empty results**: Friendly visual feedback when no items match filters.
  * **Network / API error handling**: Detects connectivity loss or HTTP failures with clear error messages.
  * **Retry mechanism**: One-tap "Try Again" button to re-trigger API fetching.
* Interactive category chip filters (Beauty, Fragrances, Furniture, Groceries, etc.).

### 2. Product Search
* Debounced search bar (350ms throttle) querying DummyJSON's `/products/search?q={query}` endpoint.
* Immediate feedback for no-match queries with a "Clear Search" button.
* Smooth keyboard IME search action with automatic focus dismissal.

### 3. Detailed Product View
* High-resolution product image gallery with thumbnail preview selector.
* Full product details: Title, Brand, Category, Rating, Stock availability badge (`In Stock (N units)` or `Out of Stock`), and Description.
* Extended specification highlights: Warranty information, Shipping details, and Return policy.
* Sticky bottom action bar:
  * One-tap "Add to Cart" with pricing badge.
  * Real-time Stepper (`-` [Quantity] `+`) when the item is already in the cart.
  * Quick navigation to the cart.

### 4. Shopping Cart & 100% Offline Persistence
* **Add & Adjust**: Add products directly from catalog or details screen; increase or decrease item quantities (capped at available inventory stock).
* **Remove & Clear**: Single item deletion or one-tap "Clear Cart" with confirmation dialog.
* **Real-time Totals**: Automatically calculates total item count and formatted aggregate price.
* **Zero Internet Required**: All cart interactions (viewing items, modifying quantities, removing items, calculating totals) are backed by a local **Room SQLite Database**. The cart remains available and fully consistent across app restarts, device reboots, or in Airplane mode.

---

## 🏛️ Architecture & Design Pattern

The application follows the official **Android Recommended Architecture (MVVM + Clean Architecture principles)**:

```
                  ┌──────────────────────┐
                  │ Jetpack Compose UI   │
                  │  (Screens, Views)    │
                  └──────────▲───────────┘
                             │ StateFlow / Events
                  ┌──────────┴───────────┐
                  │     ViewModel        │
                  │  (Catalog, Details,  │
                  │        Cart)         │
                  └──────────▲───────────┘
                             │ Flow / Suspend
                  ┌──────────┴───────────┐
                  │  Repository Layer    │
                  │ (ProductRepository,  │
                  │   CartRepository)    │
                  └─────▲──────────▲─────┘
                        │          │
         ┌──────────────┴──┐    ┌──┴──────────────┐
         │  Remote Source  │    │  Local Source   │
         │  (Retrofit API) │    │  (Room SQLite)  │
         │  [DummyJSON]    │    │  [CartDao]      │
         └─────────────────┘    └─────────────────┘
```

### Unidirectional Data Flow (UDF)
* ViewModels expose immutable `StateFlow<UiState>` to the Compose UI.
* UI emits user actions/intents (e.g. `onAddToCart`, `onSearchQueryChange`) to ViewModels.
* Room Database emits reactive `Flow<List<CartItemEntity>>`, ensuring any local cart change immediately reflects across all screens without manual synchronization.

---

## 📦 Libraries & Tech Stack

| Category | Technology / Library | Purpose |
|---|---|---|
| **Language** | Kotlin 2.0.0 | Type-safe, expressive, modern Android language |
| **UI Framework** | Jetpack Compose (BOM 2024.06.00) | Declarative reactive UI toolkit |
| **Design System** | Material 3 (`androidx.compose.material3`) | Modern styling, dynamic colors, elevation, chips, badges |
| **Navigation** | Navigation Compose (`2.7.7`) | Single-activity decoupled screen navigation with transitions |
| **Networking** | Retrofit 2.11.0 + OkHttp 4.12.0 | REST API client with HTTP logging and timeouts |
| **JSON Serialization**| Gson Converter 2.11.0 | Fast, proven DTO parsing |
| **Local Storage** | Room Database 2.6.1 + KSP | SQLite abstraction with Kotlin Coroutines & Flow support |
| **Image Loading** | Coil Compose 2.7.0 | Fast asynchronous image loading with memory & disk caching |
| **Asynchrony** | Kotlin Coroutines & Kotlin Flow | Concurrency, reactive streams, and debouncing |
| **Dependency Injection** | AppContainer (Manual DI) | Clean, compile-time safe dependency container without reflection overhead |
| **Testing** | JUnit 4, Kotlinx Coroutines Test | Unit testing for domain logic and data mapping |

---

## 💾 Local Storage Approach

For offline cart persistence, **Room (SQLite abstraction)** was selected over SharedPreferences/DataStore:
1. **Relational Structure**: A cart item is a structured entity containing `productId`, `title`, `price`, `thumbnail`, `quantity`, `stock`, `brand`, and `category`.
2. **Reactive Streams**: Room natively produces Kotlin `Flow`, allowing the UI to react instantly whenever an item is added, updated, or removed.
3. **ACID Transactions & Data Integrity**: SQLite transactions ensure atomic quantity increments and eliminate race conditions during rapid tapping.
4. **Offline Resilience**: Reads and writes occur entirely on disk (`Dispatchers.IO`), meaning zero network latency and uninterrupted offline capability.

---

## ⚙️ Setup & Build Instructions

### Prerequisites
* **Android Studio**: Ladybug / Koala or newer (or command-line Android SDK)
* **JDK**: OpenJDK 17 or higher
* **Android SDK**: Compile SDK `35`, Min SDK `24`

### Build via Command Line
```bash
# Clone the repository
git clone https://github.com/<your-username>/spire-product-catalog.git
cd spire-product-catalog

# Grant execution permissions to Gradle wrapper
chmod +x ./gradlew

# Run Unit Tests
./gradlew testDebugUnitTest

# Build Debug APK
./gradlew assembleDebug
```
The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 💡 Important Design Decisions

1. **Reactive Cart Synchronization Across Screens**:
   Rather than manually updating UI state when an item is added, the `CartRepository` emits a database `Flow`. Both the `CatalogScreen` (badges on product cards) and `ProductDetailsScreen` (stepper state) observe this stream, guaranteeing consistent state across all destinations.
2. **Debounced Search**:
   Direct keystroke API requests cause high network traffic and UI jitter. A `350ms` debounce window ensures queries are only dispatched when the user pauses typing.
3. **Graceful Error Handling**:
   Network failures differentiate between `UnknownHostException` (no internet connection), `SocketTimeoutException` (server timeout), and generic HTTP errors to provide clear, actionable feedback to users.
4. **AppContainer DI**:
   Using an `AppContainer` pattern provides pure dependency injection without annotation processing build overhead, making builds fast and unit testing straightforward with test doubles.

---

## ⚠️ Known Limitations
* **Catalog Offline Caching**: The assessment specified that the *cart* must remain fully functional offline (which is 100% persisted in Room). The product catalog itself currently fetches fresh data from DummyJSON when online. Full offline caching for all 100+ catalog items with Cache-Control / Room catalog mirroring could be added as a next step.
* **Payment Integration**: The checkout action currently simulates an order placement with a toast confirmation and cart clearing, as no payment gateway API was specified.

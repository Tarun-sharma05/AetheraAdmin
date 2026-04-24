# Aethera Admin

<div align="center">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"/>
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black"/>
  <img src="https://img.shields.io/badge/Hilt-2196F3?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Material%203-6750A4?style=for-the-badge&logo=material-design&logoColor=white"/>
</div>

<br/>

> A professional **Android admin panel** for the Aethera e-commerce platform. Built with Kotlin, Jetpack Compose & Material 3 — designed to give store managers full control over products, categories, orders, and customers from a single, elegant interface.

---

## 📱 Preview

https://github.com/user-attachments/assets/VIDEO_TOKEN_PLACEHOLDER

> *Dashboard overview — KPI cards, weekly sales chart, order status donut, business activity feed.*
> *(Replace the link above after uploading via GitHub's video attach feature)*

---

## ✨ Features

### ✅ Implemented
| Feature | Description |
|---|---|
| 📊 **Dashboard** | KPI cards (Products, Categories, Orders, Revenue), weekly sales bar chart, order status donut, live business activity feed, quick-action bento grid |
| 📦 **Product Management** | Add, list products with image upload to Firebase Storage |
| 🗂️ **Category Management** | Add and list categories |
| 🛒 **Orders Screen** | View all orders with status chips (Pending / Shipped / Delivered / Cancelled) |
| 👥 **Customers Screen** | Read-only customer list with joined date |
| 📈 **Analytics Screen** | Weekly sales chart stub (full analytics coming soon) |

### 🔒 Coming Soon
- Analytics & Reports (revenue trends, top products)
- Discounts & Promotions management
- Delivery Tracking
- Push Notifications
- Admin-level customer editing

---

## 🏗️ Architecture

This project follows a **clean, feature-based MVVM** architecture with strict layer separation.

```
app/
├── common/                    # Constants, ResultState
├── data/
│   ├── di/                    # Hilt AppModule (all repository bindings)
│   └── repository/            # Firebase implementations
│       ├── ProductRepositoryImpl
│       ├── CategoryRepositoryImpl
│       ├── StorageRepositoryImpl
│       ├── DashboardRepositoryImpl
│       ├── OrderRepositoryImpl
│       └── UserRepositoryImpl
├── domain/
│   ├── models/                # Clean domain models
│   │   ├── Product, Category, Order, OrderItem
│   │   ├── User, DashboardStats, ChartPoint
│   │   └── RecentActivityItem, LowStockProduct
│   └── repository/            # Repository interfaces (contracts)
└── presentation/
    ├── analytics/             # AnalyticsViewModel
    ├── category/              # CategoryViewModel, AddCategoryViewModel
    ├── customers/             # CustomersViewModel
    ├── dashboard/             # DashboardViewModel + DashboardUiState
    ├── orders/                # OrdersViewModel
    ├── product/               # ProductViewModel, AddProductViewModel, EditProductViewModel
    ├── navigation/            # NavGraph (Navigation 3), Route, BottomNavBar
    └── Screen/                # All Composable screens
```

### Key Architecture Decisions
- **Feature-scoped ViewModels** — one ViewModel per screen, no shared god-objects
- **Repository pattern** — domain interfaces implemented by Firebase data layer
- **Unidirectional data flow** — `StateFlow<UiState>` exposed by each ViewModel
- **Navigation 3** — type-safe, serializable routes using `@Serializable` data objects

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Design System | Stitch (Indigo M3 palette, Manrope font) |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt / Dagger |
| Navigation | Navigation 3 (`androidx.navigation3`) |
| Async | Kotlin Coroutines + StateFlow |
| Image Loading | Coil |
| Image Upload | Firebase Storage |
| Database | Firebase Firestore |
| Serialization | Kotlinx Serialization |

---

## 🎨 Design System

The UI is based on a **Google Stitch**-generated design system:

- **Font**: [Manrope](https://fonts.google.com/specimen/Manrope) (via Google Fonts provider)
- **Primary**: `#2D4ADD` (Indigo)
- **Tertiary**: `#712AE2` (Violet) — used for accents and order stats
- **Surface**: `#F7F9FB` (Cool-white)
- **Shapes**: Material 3 rounded corners (8dp standard, 16dp cards)
- **Elevation**: Tonal layers with soft ambient shadows

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Meerkat or later
- JDK 17+
- A Firebase project with Firestore and Storage enabled

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Tarun-sharma05/AetheraAdmin.git
   cd AetheraAdmin
   ```

2. **Add Firebase config**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Download `google-services.json`
   - Place it in the `app/` directory

3. **Firestore Collections**
   The app reads from these Firestore collections (uppercase naming):
   ```
   PRODUCTS    — product documents
   CATEGORY    — category documents
   ORDERS      — order documents
   USERS       — user/customer documents
   ```

4. **Build & Run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or open in Android Studio and click **Run**.

---

## 📂 Firestore Schema

### `PRODUCTS` collection
```json
{
  "id": "auto",
  "name": "string",
  "description": "string",
  "categoryName": "string",
  "price": "number",
  "finalPrice": "number",
  "stockQuantity": "number",
  "imageUrl": "string",
  "isActive": "boolean",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

### `ORDERS` collection
```json
{
  "orderId": "string",
  "userId": "string",
  "totalAmount": "number",
  "status": "PENDING | SHIPPED | DELIVERED | CANCELLED",
  "paymentStatus": "string",
  "createdAt": "timestamp"
}
```

---

## ⚠️ Notes

- `google-services.json` is excluded from version control — never commit it.
- The weekly sales chart and order status donut will show empty states until the `ORDERS` collection is populated.
- Manrope font requires an internet connection on first launch to download via GMS.

---

## 🤝 Contributing

This project is part of the **Aethera** ecosystem. Contributions, issues and feature requests are welcome.

---

<div align="center">
  Made with ❤️ by <a href="https://github.com/Tarun-sharma05">Tarun Sharma</a>
</div>

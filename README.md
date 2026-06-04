# 🛒 Online Shopping Cart System (Java Swing)

A desktop-based retail application built with **Java Swing**. This system allows users to browse a product catalog, manage a shopping cart with real-time stock reservation logic, and proceed through a multi-step checkout process with various payment methods.

---

## ✨ Key Features

* **Real-Time Stock Management:** Features a "Reserved Stock" system. When an item is added to the cart, it is reserved so other users (or sessions) can't claim it, but the main stock isn't deducted until checkout is finalized.
* **Dual-Pane GUI:** A split-view interface using `JSplitPane` to display available products on the left and the user's active cart on the right.
* **Dynamic Payment Methods:** The `CheckoutManager` determines available payment gateways (e.g., Cash on Delivery, Easypaisa, Credit Card) based on the total order value.
* **Robust Error Handling:** Custom exceptions handle common retail edge cases like `InsufficientStockException` and `EmptyCartException`.
* **Validation:** Input dialogs include validation to ensure users enter valid numeric quantities.

---

## 🏗️ Project Architecture

The project follows an object-oriented approach to separate UI logic from business rules:

| Class/Interface | Responsibility |
| :--- | :--- |
| **`Main`** | Entry point; handles the Swing UI components and event listeners. |
| **`Product`** | Data model for items, including price, stock, and reservation counts. |
| **`ShoppingCart`** | Logic for adding, removing, and updating item quantities. |
| **`CheckoutManager`** | The "Brain"—calculates totals, validates payments, and finalizes stock deduction. |
| **`PaymentMethod`** | Interface using the Strategy Pattern to implement various payment gateways. |
| **`Utility`** | Contains search algorithms for list management. |

---

## 🚀 Getting Started

### Prerequisites
* **Java Development Kit (JDK) 8 or higher.**
* An IDE (IntelliJ, Eclipse, VS Code) or a terminal.

### Installation & Running
1. **Clone the repository** (or copy the code into a file named `Main.java`).
2. **Compile the code:**
   ```bash
   javac Main.java
import SwiftUI

/// Checkout demo built from a client design ("Carrito 4" and "Carrito 5" in Figma).
///
/// Launch argument: `-screen cart|checkout` picks the first screen. Without it,
/// the app opens on the cart, and "Continuar" goes to the checkout screen.
@main
struct CheckoutApp: App {
    var body: some Scene {
        WindowGroup {
            RootView(initialScreen: Screen(rawValue: UserDefaults.standard.string(forKey: "screen") ?? "") ?? .cart)
        }
    }
}

enum Screen: String {
    case cart
    case checkout
}

struct RootView: View {
    let initialScreen: Screen
    @State private var path: [Screen] = []
    @State private var items = CartItem.samples

    var body: some View {
        NavigationStack(path: $path) {
            Group {
                switch initialScreen {
                case .cart:
                    CartScreen(items: $items) { path.append(.checkout) }
                case .checkout:
                    CheckoutScreen(items: items.filter(\.isSelected))
                }
            }
            .navigationDestination(for: Screen.self) { _ in
                CheckoutScreen(items: items.filter(\.isSelected))
            }
            .toolbar(.hidden, for: .navigationBar)
        }
    }
}

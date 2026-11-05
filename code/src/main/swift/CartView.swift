import SwiftUI

struct CartView: View {
    @State private var products = Product.samples
    @State private var toast: String?

    private var total: Int {
        products.reduce(0) { $0 + $1.price * $1.quantity }
    }

    var body: some View {
        VStack(spacing: 0) {
            header
            ScrollView {
                VStack(spacing: 12) {
                    ForEach($products) { $product in
                        ProductRow(product: $product) {
                            delete(product)
                        }
                    }
                }
                .padding(16)
            }
            footer
        }
        .background(Color(.systemGroupedBackground))
        .overlay(alignment: .bottom) {
            if let toast {
                Text(toast)
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(.white)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 10)
                    .background(Color.black.opacity(0.85), in: Capsule())
                    .padding(.bottom, 140)
                    .transition(.opacity)
            }
        }
    }

    private func delete(_ product: Product) {
        products.removeAll { $0.id == product.id }
        let message = "\(product.name) eliminado del carrito"
        withAnimation { toast = message }
        Task {
            try? await Task.sleep(for: .seconds(3))
            withAnimation { if toast == message { toast = nil } }
        }
    }

    private var header: some View {
        HStack(spacing: 12) {
            Button {} label: {
                Image(systemName: "arrow.left")
                    .font(.title3.weight(.semibold))
                    .frame(width: 44, height: 44)
            }
            .accessibilityLabel("Flecha izquierda")

            HStack(spacing: 8) {
                Image(systemName: "cart.fill")
                    .accessibilityLabel("Carrito")
                Text("Carrito").font(.title2.bold())
            }
            Spacer()
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 4)
        .background(Color(.systemBackground))
    }

    private var footer: some View {
        VStack(spacing: 12) {
            HStack {
                Text("Total")
                Spacer()
                Text(Price.format(total)).bold()
            }
            .font(.title3)
            .accessibilityElement(children: .combine)

            Button {} label: {
                Text("Pagar")
                    .font(.headline)
                    .frame(maxWidth: .infinity, minHeight: 50)
            }
            .buttonStyle(.borderedProminent)
        }
        .padding(16)
        .background(Color(.systemBackground))
    }
}

struct ProductRow: View {
    @Binding var product: Product
    let onDelete: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: 14) {
                Image(systemName: product.symbol)
                    .font(.title)
                    .frame(width: 56, height: 56)
                    .background(Color.accentColor.opacity(0.12), in: RoundedRectangle(cornerRadius: 10))
                VStack(alignment: .leading, spacing: 4) {
                    Text(product.name).font(.headline)
                    Text(Price.format(product.price)).foregroundStyle(.secondary)
                    HStack(spacing: 4) {
                        Image(systemName: "star.fill").foregroundStyle(.orange)
                        Text(product.rating.formatted(.number.precision(.fractionLength(1))))
                    }
                    .font(.subheadline)
                }
            }
            HStack {
                QuantityControl(quantity: $product.quantity)
                Spacer()
                Button(role: .destructive, action: onDelete) {
                    Label("Eliminar", systemImage: "trash")
                        .font(.subheadline)
                }
            }
        }
        .padding(16)
        .background(Color(.secondarySystemGroupedBackground), in: RoundedRectangle(cornerRadius: 14))
    }
}

struct QuantityControl: View {
    @Binding var quantity: Int

    var body: some View {
        HStack(spacing: 0) {
            Image(systemName: "minus")
                .frame(width: 40, height: 36)
                .contentShape(Rectangle())
                .onTapGesture { decrement() }
                .accessibilityLabel("Menos")
            Text("\(quantity)")
                .font(.headline.monospacedDigit())
                .frame(minWidth: 28)
            Image(systemName: "plus")
                .frame(width: 40, height: 36)
                .contentShape(Rectangle())
                .onTapGesture { increment() }
                .accessibilityLabel("Más")
        }
        .background(Color(.tertiarySystemFill), in: Capsule())
    }

    private func increment() { quantity = min(quantity + 1, 9) }
    private func decrement() { quantity = max(quantity - 1, 1) }
}

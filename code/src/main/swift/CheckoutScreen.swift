import SwiftUI

struct CheckoutScreen: View {
    let items: [CartItem]
    @Environment(\.dismiss) private var dismiss
    @State private var couponCode = "NOVI0326"
    @State private var couponApplied = false

    private var total: Int { items.reduce(0) { $0 + $1.price * $1.quantity } }

    var body: some View {
        VStack(spacing: 0) {
            ScreenHeader(title: "Realizar compra") { dismiss() }
            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    Text("Orden de compra")
                        .font(.headline)
                        .padding(.horizontal, 16)

                    VStack(alignment: .leading, spacing: 16) {
                        ForEach(items) { item in
                            OrderItemRow(item: item)
                        }
                        Divider()
                        HStack {
                            Text("Total").font(.headline)
                            Spacer()
                            Text(Money.format(total) + ".00").font(.headline)
                        }
                    }
                    .padding(16)
                    .background(Color(.systemBackground), in: RoundedRectangle(cornerRadius: 12))
                    .padding(.horizontal, 16)

                    couponSection
                        .padding(.horizontal, 16)

                    VStack(spacing: 0) {
                        ForEach(1...4, id: \.self) { _ in
                            ChevronRow(symbol: nil, title: "Revisión de compra")
                        }
                    }
                    .padding(.horizontal, 16)
                    .background(Color(.systemBackground), in: RoundedRectangle(cornerRadius: 12))
                    .padding(.horizontal, 16)
                }
                .padding(.vertical, 12)
            }
            CheckoutFooter(onContinue: {})
        }
        .background(Color(.systemGroupedBackground))
        .toolbar(.hidden, for: .navigationBar)
    }

    private var couponSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: 10) {
                Image(systemName: "percent")
                Text("Añadir cupón")
            }
            HStack(spacing: 12) {
                TextField("", text: $couponCode)
                    .textFieldStyle(.plain)
                    .padding(.horizontal, 12)
                    .frame(height: 44)
                    .overlay(RoundedRectangle(cornerRadius: 8).stroke(Color(.separator)))
                    .accessibilityIdentifier("coupon-field")
                Button("Redimir", action: redeem)
                    .buttonStyle(.borderedProminent)
                    .tint(Brand.orange)
                    .controlSize(.large)
                    .accessibilityIdentifier("redeem")
            }
            if couponApplied {
                Text("Cupón aplicado: -50% de descuento")
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(.green)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(12)
                    .background(Color.green.opacity(0.1), in: RoundedRectangle(cornerRadius: 8))
                    .overlay(RoundedRectangle(cornerRadius: 8).stroke(Color.green.opacity(0.5)))
                    .accessibilityIdentifier("coupon-applied")
            }
        }
        .padding(16)
        .background(Color(.systemBackground), in: RoundedRectangle(cornerRadius: 12))
    }

    private func redeem() {
        withAnimation { couponApplied = true }
    }
}

struct OrderItemRow: View {
    let item: CartItem

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Vendido por \(item.seller)").font(.caption).foregroundStyle(.secondary)
            HStack(alignment: .top, spacing: 10) {
                Image(systemName: item.symbol)
                    .font(.title)
                    .foregroundStyle(item.tint)
                    .frame(width: 44, height: 56)
                VStack(alignment: .leading, spacing: 2) {
                    Text(item.name).font(.subheadline)
                    Text("SKU: \(item.sku)").font(.caption).foregroundStyle(.secondary)
                }
            }
            HStack {
                Text("Cant. \(item.quantity)").font(.subheadline)
                Spacer()
                PriceStack(originalPrice: item.originalPrice, price: item.price,
                           weeklyPayment: item.weeklyPayment, weeks: item.weeks)
            }
        }
    }
}

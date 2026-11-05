import SwiftUI

struct CartScreen: View {
    @Binding var items: [CartItem]
    let onContinue: () -> Void
    @State private var pickUpInStore = false

    private var allSelected: Binding<Bool> {
        Binding(
            get: { items.allSatisfy(\.isSelected) },
            set: { value in for i in items.indices { items[i].isSelected = value } }
        )
    }

    var body: some View {
        VStack(spacing: 0) {
            ScreenHeader(title: "Carrito")
            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    HStack(spacing: 4) {
                        Checkbox(isOn: allSelected)
                        Text("Seleccionar todo").font(.subheadline)
                    }
                    .padding(.horizontal, 8)

                    Text("Productos con promo al pagar con Préstamo de Banco Azteca")
                        .font(.headline)
                        .padding(.horizontal, 16)

                    VStack(alignment: .leading, spacing: 0) {
                        HStack(spacing: 4) {
                            Text("Promo exclusiva")
                            Text("Promo 1, 2, 3").foregroundStyle(Brand.orange)
                        }
                        .font(.subheadline)
                        .padding(16)
                        Divider()
                        ForEach($items) { $item in
                            CartItemRow(item: $item) {
                                items.removeAll { $0.id == item.id }
                            }
                            if item.id != items.last?.id { Divider().padding(.horizontal, 16) }
                        }
                    }
                    .background(Color(.systemBackground), in: RoundedRectangle(cornerRadius: 12))
                    .padding(.horizontal, 16)

                    StorePickupToggle(isOn: $pickUpInStore)
                        .padding(.horizontal, 16)

                    VStack(spacing: 8) {
                        ChevronRow(symbol: "percent", title: "Añadir cupón")
                        ChevronRow(symbol: "checkmark.seal.fill", title: "Garantía extendida")
                    }
                    .padding(.horizontal, 16)
                    .background(Color(.systemBackground), in: RoundedRectangle(cornerRadius: 12))
                    .padding(.horizontal, 16)
                }
                .padding(.vertical, 12)
            }
            CheckoutFooter(onContinue: onContinue)
        }
        .background(Color(.systemGroupedBackground))
    }
}

struct CartItemRow: View {
    @Binding var item: CartItem
    let onDelete: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 6) {
                    Text("Vendido por \(item.seller)")
                        .font(.caption)
                        .foregroundStyle(.secondary)
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
                }
                Spacer()
                Checkbox(isOn: $item.isSelected)
            }

            HStack {
                Button(action: onDelete) {
                    Image(systemName: "trash")
                        .foregroundStyle(Brand.orange)
                        .frame(width: 36, height: 36)
                        .overlay(RoundedRectangle(cornerRadius: 8).stroke(Color(.separator)))
                }

                Menu {
                    ForEach(1...9, id: \.self) { n in
                        Button("\(n)") { item.quantity = n }
                    }
                } label: {
                    HStack(spacing: 6) {
                        Text("\(item.quantity)")
                        Image(systemName: "chevron.down").foregroundStyle(Brand.orange)
                    }
                    .foregroundStyle(.primary)
                    .padding(.horizontal, 12)
                    .frame(height: 36)
                    .overlay(RoundedRectangle(cornerRadius: 8).stroke(Color(.separator)))
                }

                Spacer()
                PriceStack(originalPrice: item.originalPrice, price: item.price,
                           weeklyPayment: item.weeklyPayment, weeks: item.weeks)
            }
        }
        .padding(16)
        .accessibilityIdentifier("item-\(item.id)")
    }
}

struct StorePickupToggle: View {
    @Binding var isOn: Bool

    var body: some View {
        HStack {
            Text("Recoger en tienda física").font(.subheadline)
            Spacer()
            ZStack(alignment: isOn ? .trailing : .leading) {
                Capsule().fill(isOn ? Brand.orange : Color(.systemGray4))
                    .frame(width: 72, height: 36)
                Circle().fill(.white).frame(width: 30, height: 30).padding(3)
                Image(systemName: "storefront")
                    .font(.caption)
                    .frame(width: 36)
                    .offset(x: isOn ? -36 : 36)
            }
            .onTapGesture { withAnimation { isOn.toggle() } }
        }
        .padding(16)
        .background(Color(.systemBackground), in: RoundedRectangle(cornerRadius: 12))
    }
}

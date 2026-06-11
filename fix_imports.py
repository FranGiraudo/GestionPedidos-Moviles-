import os
import re

def update_file(path, replacements):
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    for old, new in replacements:
        content = re.sub(old, new, content)
        
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

# DASHBOARD
update_file('app/src/main/java/com/undef/gestionpedidos/ui/feature/dashboard/DashboardScreen.kt', [
    (r'import androidx.compose.runtime.getValue', r'import androidx.compose.runtime.getValue\nimport com.undef.gestionpedidos.ui.components.SummaryCard\nimport com.undef.gestionpedidos.ui.components.RecentOrderCard')
])

# ORDERS
update_file('app/src/main/java/com/undef/gestionpedidos/ui/feature/orders/OrdersScreen.kt', [
    (r'import androidx.compose.runtime.getValue', r'import androidx.compose.runtime.getValue\nimport com.undef.gestionpedidos.ui.components.OrderHistoryCard')
])

# PROFILE
update_file('app/src/main/java/com/undef/gestionpedidos/ui/feature/profile/ProfileScreen.kt', [
    (r'import androidx.compose.runtime.getValue', r'import androidx.compose.runtime.getValue\nimport com.undef.gestionpedidos.ui.components.ProfileInfoRow')
])

# ORDER DETAIL
update_file('app/src/main/java/com/undef/gestionpedidos/ui/feature/orderdetail/OrderDetailScreen.kt', [
    (r'val pedidoOriginal = MockData\.pedidos\.find \{ it\.id == orderId \}', ''),
    (r'pedidoOriginal', 'order'),
    (r'items\(order\.lineas\)', r'items(order?.lineas ?: emptyList())')
])

print("Finished fixing imports")

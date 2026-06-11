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
    (r'import androidx.compose.ui.unit.dp', r'import androidx.compose.ui.unit.dp\nimport androidx.lifecycle.compose.collectAsStateWithLifecycle\nimport androidx.lifecycle.viewmodel.compose.viewModel\nimport androidx.compose.runtime.getValue'),
    (r'fun DashboardScreen\([\s\S]*?\)\s*\{', r'fun DashboardScreen(\n    onNavigateToNewOrder: () -> Unit,\n    onNavigateToNewClient: () -> Unit,\n    onNavigateToOrderDetail: (Int) -> Unit,\n    onNavigateToProfile: () -> Unit,\n    viewModel: DashboardViewModel = viewModel()\n) {\n    val uiState by viewModel.uiState.collectAsStateWithLifecycle()'),
    (r'import com.undef.gestionpedidos.data.mock.MockData\n?', ''),
    (r'MockData\.pedidos', 'uiState.pedidosRecientes')
])

# ORDERS
update_file('app/src/main/java/com/undef/gestionpedidos/ui/feature/orders/OrdersScreen.kt', [
    (r'import androidx.compose.ui.unit.dp', r'import androidx.compose.ui.unit.dp\nimport androidx.lifecycle.compose.collectAsStateWithLifecycle\nimport androidx.lifecycle.viewmodel.compose.viewModel\nimport androidx.compose.runtime.getValue'),
    (r'fun OrdersScreen\([\s\S]*?\)\s*\{', r'fun OrdersScreen(\n    onNavigateToNewOrder: () -> Unit,\n    onNavigateToOrderDetail: (Int) -> Unit,\n    viewModel: OrdersViewModel = viewModel()\n) {\n    val uiState by viewModel.uiState.collectAsStateWithLifecycle()'),
    (r'import com.undef.gestionpedidos.data.mock.MockData\n?', ''),
    (r'var searchQuery by remember \{ mutableStateOf\(""\) \}\n', ''),
    (r'value = searchQuery', 'value = uiState.searchQuery'),
    (r'searchQuery = it', 'viewModel.updateSearchQuery(it)'),
    (r'MockData\.pedidos', 'uiState.orders')
])

# CLIENTS
update_file('app/src/main/java/com/undef/gestionpedidos/ui/feature/clients/ClientsScreen.kt', [
    (r'import androidx.compose.ui.unit.dp', r'import androidx.compose.ui.unit.dp\nimport androidx.lifecycle.compose.collectAsStateWithLifecycle\nimport androidx.lifecycle.viewmodel.compose.viewModel\nimport androidx.compose.runtime.getValue'),
    (r'fun ClientsScreen\([\s\S]*?\)\s*\{', r'fun ClientsScreen(\n    onNavigateToNewClient: () -> Unit,\n    viewModel: ClientsViewModel = viewModel()\n) {\n    val uiState by viewModel.uiState.collectAsStateWithLifecycle()'),
    (r'import com.undef.gestionpedidos.data.mock.MockData\n?', ''),
    (r'var searchQuery by remember \{ mutableStateOf\(""\) \}\n', ''),
    (r'value = searchQuery', 'value = uiState.searchQuery'),
    (r'searchQuery = it', 'viewModel.updateSearchQuery(it)'),
    (r'MockData\.clientes', 'uiState.clients')
])

# ORDER DETAIL
update_file('app/src/main/java/com/undef/gestionpedidos/ui/feature/orderdetail/OrderDetailScreen.kt', [
    (r'import androidx.compose.ui.unit.dp', r'import androidx.compose.ui.unit.dp\nimport androidx.lifecycle.compose.collectAsStateWithLifecycle\nimport androidx.lifecycle.viewmodel.compose.viewModel\nimport androidx.compose.runtime.getValue'),
    (r'fun OrderDetailScreen\([\s\S]*?\)\s*\{', r'fun OrderDetailScreen(\n    orderId: Int,\n    onNavigateBack: () -> Unit,\n    viewModel: OrderDetailViewModel = viewModel(factory = OrderDetailViewModelFactory(orderId))\n) {\n    val uiState by viewModel.uiState.collectAsStateWithLifecycle()\n    val order = uiState.order'),
    (r'import com.undef.gestionpedidos.data.mock.MockData\n?', ''),
    (r'val order = MockData\.pedidos\.find \{ it\.id == orderId \}\n', '')
])

# PROFILE
update_file('app/src/main/java/com/undef/gestionpedidos/ui/feature/profile/ProfileScreen.kt', [
    (r'import androidx.compose.ui.unit.dp', r'import androidx.compose.ui.unit.dp\nimport androidx.lifecycle.compose.collectAsStateWithLifecycle\nimport androidx.lifecycle.viewmodel.compose.viewModel\nimport androidx.compose.runtime.getValue'),
    (r'fun ProfileScreen\([\s\S]*?\)\s*\{', r'fun ProfileScreen(\n    onNavigateBack: () -> Unit,\n    onNavigateToSettings: () -> Unit,\n    onLogout: () -> Unit,\n    viewModel: ProfileViewModel = viewModel()\n) {\n    val uiState by viewModel.uiState.collectAsStateWithLifecycle()')
])

# SETTINGS
update_file('app/src/main/java/com/undef/gestionpedidos/ui/feature/settings/SettingsScreen.kt', [
    (r'import androidx.compose.ui.unit.dp', r'import androidx.compose.ui.unit.dp\nimport androidx.lifecycle.compose.collectAsStateWithLifecycle\nimport androidx.lifecycle.viewmodel.compose.viewModel\nimport androidx.compose.runtime.getValue'),
    (r'fun SettingsScreen\([\s\S]*?\)\s*\{', r'fun SettingsScreen(\n    onNavigateBack: () -> Unit,\n    viewModel: SettingsViewModel = viewModel()\n) {\n    val uiState by viewModel.uiState.collectAsStateWithLifecycle()'),
    (r'var notificaciones by remember \{ mutableStateOf\(true\) \}\n', ''),
    (r'var modoOscuro by remember \{ mutableStateOf\(false\) \}\n', ''),
    (r'checked = notificaciones', 'checked = uiState.notificacionesActivas'),
    (r'onCheckedChange = \{ notificaciones = it \}', 'onCheckedChange = { viewModel.toggleNotificaciones(it) }'),
    (r'checked = modoOscuro', 'checked = uiState.modoOscuroActivo'),
    (r'onCheckedChange = \{ modoOscuro = it \}', 'onCheckedChange = { viewModel.toggleModoOscuro(it) }')
])

# STATISTICS
update_file('app/src/main/java/com/undef/gestionpedidos/ui/feature/statistics/StatisticsScreen.kt', [
    (r'import androidx.compose.ui.unit.dp', r'import androidx.compose.ui.unit.dp\nimport androidx.lifecycle.compose.collectAsStateWithLifecycle\nimport androidx.lifecycle.viewmodel.compose.viewModel\nimport androidx.compose.runtime.getValue'),
    (r'fun StatisticsScreen\(\)\s*\{', r'fun StatisticsScreen(viewModel: StatisticsViewModel = viewModel()) {\n    val uiState by viewModel.uiState.collectAsStateWithLifecycle()'),
])

print("Finished replacing")

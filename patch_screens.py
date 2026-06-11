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
    (r'import androidx.compose.ui.unit.dp', r'import androidx.compose.ui.unit.dp\nimport androidx.compose.animation.AnimatedVisibility\nimport androidx.compose.animation.core.tween\nimport androidx.compose.animation.fadeIn\nimport androidx.compose.animation.slideInVertically\nimport androidx.compose.runtime.LaunchedEffect\nimport androidx.compose.runtime.mutableStateOf\nimport androidx.compose.runtime.remember\nimport androidx.compose.runtime.setValue'),
    (r'val uiState by viewModel.uiState.collectAsStateWithLifecycle\(\)', r'val uiState by viewModel.uiState.collectAsStateWithLifecycle()\n    var isVisible by remember { mutableStateOf(false) }\n    LaunchedEffect(Unit) { isVisible = true }'),
    (r'Column\([\s\S]*?padding\(16.dp\)', r'Column(\n            modifier = Modifier\n                .fillMaxSize()\n                .padding(paddingValues)\n                .padding(horizontal = 16.dp)\n        ) {\n            Spacer(modifier = Modifier.height(8.dp))'),
    (r'RecentOrderCard\(order = order\) \{ onNavigateToOrderDetail\(order.id\) \}', r'AnimatedVisibility(\n                        visible = isVisible,\n                        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300), initialOffsetY = { 50 })\n                    ) {\n                        RecentOrderCard(order = order) { onNavigateToOrderDetail(order.id) }\n                    }')
])

# ORDERS
update_file('app/src/main/java/com/undef/gestionpedidos/ui/feature/orders/OrdersScreen.kt', [
    (r'import androidx.compose.ui.unit.dp', r'import androidx.compose.ui.unit.dp\nimport androidx.compose.animation.AnimatedVisibility\nimport androidx.compose.animation.core.tween\nimport androidx.compose.animation.fadeIn\nimport androidx.compose.animation.slideInVertically\nimport androidx.compose.runtime.LaunchedEffect\nimport androidx.compose.runtime.mutableStateOf\nimport androidx.compose.runtime.remember\nimport androidx.compose.runtime.setValue'),
    (r'val uiState by viewModel.uiState.collectAsStateWithLifecycle\(\)', r'val uiState by viewModel.uiState.collectAsStateWithLifecycle()\n    var isVisible by remember { mutableStateOf(false) }\n    LaunchedEffect(Unit) { isVisible = true }'),
    (r'OrderHistoryCard\(order\) \{ onNavigateToOrderDetail\(order.id\) \}', r'AnimatedVisibility(\n                        visible = isVisible,\n                        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300), initialOffsetY = { 50 })\n                    ) {\n                        OrderHistoryCard(order) { onNavigateToOrderDetail(order.id) }\n                    }')
])

print("Finished UI patch")

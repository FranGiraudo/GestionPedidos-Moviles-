import os
import re

def update_file(path, replacements):
    try:
        with open(path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        for old, new in replacements:
            content = re.sub(old, new, content)
            
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Updated {path}")
    except Exception as e:
        print(f"Error reading/writing {path}: {e}")

screens = [
    'app/src/main/java/com/undef/gestionpedidos/ui/feature/profile/ProfileScreen.kt',
    'app/src/main/java/com/undef/gestionpedidos/ui/feature/settings/SettingsScreen.kt',
    'app/src/main/java/com/undef/gestionpedidos/ui/feature/statistics/StatisticsScreen.kt',
    'app/src/main/java/com/undef/gestionpedidos/ui/feature/neworder/NewOrderScreen.kt',
    'app/src/main/java/com/undef/gestionpedidos/ui/feature/newclient/NewClientScreen.kt',
    'app/src/main/java/com/undef/gestionpedidos/ui/feature/orderdetail/OrderDetailScreen.kt',
    'app/src/main/java/com/undef/gestionpedidos/ui/feature/clients/ClientsScreen.kt'
]

replacements = [
    # Change TopAppBar colors
    (r'colors = TopAppBarDefaults.topAppBarColors\([\s\S]*?containerColor = MaterialTheme.colorScheme.primary,[\s\S]*?titleContentColor = MaterialTheme.colorScheme.onPrimary,[\s\S]*?navigationIconContentColor = MaterialTheme.colorScheme.onPrimary[\s\S]*?\)',
     r'colors = TopAppBarDefaults.topAppBarColors(\n                    containerColor = MaterialTheme.colorScheme.background,\n                    titleContentColor = MaterialTheme.colorScheme.onBackground,\n                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground\n                )'),
    
    # Change standard Cards to ElevatedCards
    (r'Card\(', r'ElevatedCard('),
    (r'CardDefaults\.cardColors\(containerColor = MaterialTheme\.colorScheme\.surface\)', r'CardDefaults.elevatedCardColors(containerColor = com.undef.gestionpedidos.ui.theme.CardSurface)'),
    (r'CardDefaults\.cardElevation\(defaultElevation = 2\.dp\)', r'CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)'),
    
    # Increase corner radii
    (r'RoundedCornerShape\(12\.dp\)', r'RoundedCornerShape(24.dp)'),
    (r'RoundedCornerShape\(16\.dp\)', r'RoundedCornerShape(24.dp)'),

    # Fix imports if needed
    (r'import androidx\.compose\.material3\.Card', r'import androidx.compose.material3.Card\nimport androidx.compose.material3.ElevatedCard')
]

for screen in screens:
    update_file(screen, replacements)

print("Finished patching all screens")

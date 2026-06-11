import os
import re

def update_file(path):
    try:
        with open(path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Add CardDefaults if not present
        if 'import androidx.compose.material3.CardDefaults' not in content:
            content = content.replace('import androidx.compose.runtime.Composable', 'import androidx.compose.runtime.Composable\nimport androidx.compose.material3.CardDefaults')
        
        # Some screens might use ElevatedCardDefaults if AI hallucinated it, fix that too
        content = content.replace('ElevatedCardDefaults', 'CardDefaults')

        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Fixed {path}")
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

for screen in screens:
    update_file(screen)

print("Finished fixing imports")

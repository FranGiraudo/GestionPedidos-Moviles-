import os
import re

def deduplicate_imports(path):
    with open(path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    out_lines = []
    imports_seen = set()
    
    for line in lines:
        if line.startswith('import '):
            if line.strip() in imports_seen:
                continue
            imports_seen.add(line.strip())
        out_lines.append(line)
        
    with open(path, 'w', encoding='utf-8') as f:
        f.writelines(out_lines)

screens = [
    'app/src/main/java/com/undef/gestionpedidos/ui/feature/profile/ProfileScreen.kt',
    'app/src/main/java/com/undef/gestionpedidos/ui/feature/settings/SettingsScreen.kt',
    'app/src/main/java/com/undef/gestionpedidos/ui/feature/statistics/StatisticsScreen.kt',
    'app/src/main/java/com/undef/gestionpedidos/ui/feature/neworder/NewOrderScreen.kt',
    'app/src/main/java/com/undef/gestionpedidos/ui/feature/newclient/NewClientScreen.kt',
    'app/src/main/java/com/undef/gestionpedidos/ui/feature/orderdetail/OrderDetailScreen.kt',
    'app/src/main/java/com/undef/gestionpedidos/ui/feature/clients/ClientsScreen.kt'
]

for s in screens:
    deduplicate_imports(s)
    print(f"Cleaned {s}")

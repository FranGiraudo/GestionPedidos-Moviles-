import os
import re

def update_file(path, replacements):
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    for old, new in replacements:
        content = re.sub(old, new, content)
        
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

update_file('app/src/main/java/com/undef/gestionpedidos/ui/feature/dashboard/DashboardScreen.kt', [
    (r'SummaryCard\(\s*title = "Ventas del Mes",\s*value = "\$ 1.250.000",\s*modifier = Modifier.weight\(1f\)\s*\)',
     r'SummaryCard(\n                title = "Ventas del Mes",\n                value = "$ 1.250.000",\n                bgColor = com.undef.gestionpedidos.ui.theme.DarkBlue,\n                fgColor = androidx.compose.ui.graphics.Color.White,\n                modifier = Modifier.weight(1f)\n            )'),
    (r'SummaryCard\(\s*title = "Pedidos Hoy",\s*value = "12",\s*modifier = Modifier.weight\(1f\)\s*\)',
     r'SummaryCard(\n                title = "Pedidos Hoy",\n                value = "12",\n                bgColor = com.undef.gestionpedidos.ui.theme.Lavender,\n                fgColor = com.undef.gestionpedidos.ui.theme.DarkBlue,\n                modifier = Modifier.weight(1f)\n            )')
])

print("Finished final patches")

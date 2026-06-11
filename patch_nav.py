import os
import re

def update_file(path, replacements):
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    for old, new in replacements:
        content = re.sub(old, new, content)
        
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

# APPNAVHOST
nav_replacement = """    Scaffold(
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                androidx.compose.material3.Surface(
                    color = com.undef.gestionpedidos.ui.theme.BottomNavBg,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp),
                    modifier = Modifier.padding(16.dp).androidx.compose.foundation.layout.fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).androidx.compose.foundation.layout.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        bottomNavItems.forEach { (route, title, icon) ->
                            val isSelected = currentRoute == route
                            androidx.compose.foundation.layout.Column(
                                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                                modifier = Modifier.androidx.compose.foundation.clickable {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            ) {
                                androidx.compose.material3.Surface(
                                    color = if (isSelected) com.undef.gestionpedidos.ui.theme.BottomNavSelected else androidx.compose.ui.graphics.Color.Transparent,
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                                ) {
                                    Icon(
                                        imageVector = icon, 
                                        contentDescription = title,
                                        tint = if (isSelected) com.undef.gestionpedidos.ui.theme.BottomNavIconActive else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                                    )
                                }
                                Text(
                                    text = title, 
                                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) com.undef.gestionpedidos.ui.theme.BottomNavIconActive else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    )"""

update_file('app/src/main/java/com/undef/gestionpedidos/ui/navigation/AppNavHost.kt', [
    (r'    Scaffold\([\s\S]*?            }\n        }\n    \)', nav_replacement)
])

print("Finished AppNavHost patch")

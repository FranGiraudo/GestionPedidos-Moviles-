import os
import re

UI_DIR = "app/src/main/java/com/undef/gestionpedidos/ui/feature"
STRINGS_ES = "app/src/main/res/values/strings.xml"
STRINGS_EN = "app/src/main/res/values-en/strings.xml"

# Find strings in Text("...") or Text(text = "...")
# Only match simple strings without string interpolation ($)
pattern_text1 = re.compile(r'Text\(\s*"([^"$]+?)"\s*\)')
pattern_text2 = re.compile(r'Text\(\s*text\s*=\s*"([^"$]+?)"(.*?)\)', re.DOTALL)

def slugify(text):
    s = text.lower().strip()
    s = re.sub(r'[^a-z0-9]+', '_', s)
    s = s.strip('_')
    return 'txt_' + s[:20]

strings_dict = {}

for root, _, files in os.walk(UI_DIR):
    for f in files:
        if f.endswith(".kt"):
            path = os.path.join(root, f)
            with open(path, "r", encoding="utf-8") as file:
                content = file.read()
            
            orig_content = content
            
            # Find and replace Text("...")
            for match in pattern_text1.finditer(orig_content):
                val = match.group(1)
                key = slugify(val)
                strings_dict[key] = val
                # replace in content
                content = content.replace(f'Text("{val}")', f'Text(stringResource(com.undef.gestionpedidos.R.string.{key}))')
                
            # Find and replace Text(text = "...")
            for match in pattern_text2.finditer(orig_content):
                val = match.group(1)
                rest = match.group(2)
                key = slugify(val)
                strings_dict[key] = val
                # replace in content
                content = content.replace(f'text = "{val}"', f'text = stringResource(com.undef.gestionpedidos.R.string.{key})')

            if content != orig_content:
                if "import androidx.compose.ui.res.stringResource" not in content:
                    content = content.replace("import androidx.compose.runtime.Composable", "import androidx.compose.runtime.Composable\nimport androidx.compose.ui.res.stringResource")
                with open(path, "w", encoding="utf-8") as file:
                    file.write(content)

# Update XML
def append_to_xml(path, dictionary):
    with open(path, "r", encoding="utf-8") as file:
        xml = file.read()
    
    insert_idx = xml.rfind("</resources>")
    if insert_idx == -1:
        return
        
    new_elements = ""
    for k, v in dictionary.items():
        if f'name="{k}"' not in xml:
            new_elements += f'    <string name="{k}">{v}</string>\n'
            
    xml = xml[:insert_idx] + new_elements + xml[insert_idx:]
    with open(path, "w", encoding="utf-8") as file:
        file.write(xml)

append_to_xml(STRINGS_ES, strings_dict)
append_to_xml(STRINGS_EN, strings_dict) # Leaving same text for EN as placeholder, user can translate later

print("Extracted strings: ", len(strings_dict))

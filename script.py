import os
import re

d="/home/aenri/codes/mewsic/mewsic-jaad/src/commonMain/kotlin/"
p=r'(?:java|javax)\.(\w+(?:\.\w+)*)'
s_ = set()
def r(f):
    for file in f[2]:
        if file.endswith(".kt"):
            # print("Processing "+file)
            with open(f[0]+"/"+file,"r") as k:
                c=k.read()
            c = transform_text(c, file)
            with open(f[0]+"/"+file,"w") as k:
                k.write(c)

# for f in os.walk(d):
#     r(f)
# for cls in sorted(set(s_)):
#     print(cls)
"""
Now, make a script that does the following
- changes all references from these java/javax classes to the other class, and make sure that they're imported unless otherwise specified.
java.io.InputStream -> org.mewsic.commons.streams.api.InputStream
java.io.OutputStream -> org.mewsic.commons.streams.api.OutputStream
(noimport) java.io.IOException -> Exception
java.io.FileInputStream -> org.mewsic.commons.streams.ileInputStream
(noimport) java.util.ArrayList -> ArrayList
(noimport) java.util.HashMap -> HashMap
- lists all files with references to the following classes, making sure to list the classes that the file references
java.util.Logging.Logger
java.util.Logging.Level
java.text.SimpleDateFormat
java.util.Date
java.io.PushbackInputStream
java.io.File
java.io.RandomAccessFile
java.lang.Class
java.lang.reflect.Constructor
- remove all of the following classpaths from files that use them, and add the corresponding import statements
java.lang.Math -> kotlin.math.*
- remove any java or javax imports that have a class name ending in "Exception" and convert the class construction to Exception with an initializer containing the original class name (e.g. java.io.IOException("Can't read file!") -> Exception("IOException: Can't read file!"))
- change all references from [0] to [1] and add the import statement [2]
java.lang.System.arraycopy -> Arrays.arraycopy -> import org.mewsic.commons.lang.Arrays
Guidelines:
- only allow the same import statement to be added once
- 
"""

import re

def transform_text(text: str, filename: str):
    def _a(text: str):
        replacements = {
        'java.io.InputStream': ('org.mewsic.commons.streams.api.InputStream', True),
        'java.io.OutputStream': ('org.mewsic.commons.streams.api.OutputStream', True),
        'java.io.IOException': ('Exception', False),
        'java.io.FileInputStream': ('org.mewsic.commons.streams.ileInputStream', True),
        'java.util.ArrayList': ('ArrayList', False),
        'java.util.HashMap': ('HashMap', False)
        }
        
        for old_class, (new_class, add_import) in replacements.items():
            text = re.sub(old_class, new_class, text)
            if add_import:
                package_line = None
                lines = text.split("\n")
                for i, line in enumerate(lines):
                    if line.startswith("package"):
                        package_line = i
                        break
                lines.insert(package_line + 1, f"import {new_class}")
                text = "\n".join(lines)
        return text
    def _b(text: str, filename: str):
        references = [
            'java.util.Logging.Logger',
            'java.util.Logging.Level',
            'java.text.SimpleDateFormat',
            'java.util.Date',
            'java.io.PushbackInputStream',
            'java.io.File',
            'java.io.RandomAccessFile',
            'java.lang.Class',
            'java.lang.reflect.Constructor'
        ]
        
        found = []
        for ref in references:
            if re.search(ref, text):
                found.append(ref)
        
        if found:
            print(f"File {filename} references: {', '.join(found)}")
        return text
    def _c(text: str):
        text = re.sub(r'\bjava\.lang\.System\.arraycopy\b', "Arrays.arraycopy", text)
        
        lines = text.split("\n")
        for i, line in enumerate(lines):
            if line.startswith("package"):
                lines.insert(i + 1, "import org.mewsic.commons.lang.Arrays")
                break
        text = "\n".join(lines)
        return text
    def _d(text: str):
        lines = text.split("\n")
        imports = []
        for i, line in enumerate(lines):
            if line.startswith("import"):
                if line not in imports:
                    imports.append(line)
                else:
                    lines.pop(i)
        text = "\n".join(lines)
        
        return text
    
    text = _a(text)
    text = _b(text, filename)
    text = _c(text)
    text = _d(text)
    return text

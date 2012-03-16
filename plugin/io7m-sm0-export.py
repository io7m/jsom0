#!BPY

# Copyright © 2012 http://io7m.com
# 
# Permission to use, copy, modify, and/or distribute this software for any
# purpose with or without fee is hereby granted, provided that the above
# copyright notice and this permission notice appear in all copies.
# 
# THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
# WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
# MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
# ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
# WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
# ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
# OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

bl_info = {
  "name":        "io7m-sm0 Export",
  "description": "Export to io7m-sm0 format",
  "author":      "io7m.com",
  "version":     (1, 0),
  "blender":     (2, 6, 0),
  "api":         31236,
  "location":    "File > Export",
  "warning":     "",
  "category":    "Import-Export"
}

import bpy
import bpy_extras
import sys

def sm0_info(message):
  print("sm0-export: info: " + message)
#enddef

def sm0_zero_vertex():
  return dict(position=[0.0,0.0,0.0],normal=[0.0,0.0,0.0],uv=[0.0,0.0])
#enddef

def sm0_zero_triangle():
  return dict(index=[0,0,0])
#enddef

def sm0_export(caller, context, file_path):
  sm0_info("exporting %s" % (file_path))
 
  fd  = open(file_path, 'w')
  out = fd.write

  for material in bpy.data.materials:
    n  = material.name
    d  = material.diffuse_color
    am = [0.0, 0.0, 0.0]
    s  = material.specular_color
    sa = material.specular_intensity
    sh = material.specular_hardness
    al = material.alpha
    ti = material.active_texture_index
    t  = material.active_texture

    out("material;\n")
    out("  name     \"%s\";\n"       % (n))
    out("  diffuse   %f %f %f;\n"    % (d[0],  d[1],  d[2]))
    out("  ambient   %f %f %f;\n"    % (am[0], am[1], am[2]))
    out("  specular  %f %f %f %f;\n" % (s[0],  s[1],  s[2], sa))
    out("  shininess %f;\n"          % (sh))
    out("  alpha     %f;\n"          % (al))
    if t is not None:
      if t.type == 'IMAGE':
        ts = material.texture_slots[ti]
        assert (t.image is not None)

        if ts.texture_coords == 'NORMAL':
          out("  texture   \"%s\" %f map_chrome;\n" % (t.image.filepath, 1.0))
        else:
          out("  texture   \"%s\" %f map_uv;\n" % (t.image.filepath, 1.0))
        #endif
      #endif
    #endif
    out("end;\n")
    out("\n")
  #endfor

  fd.flush()
  fd.close()
  return {'FINISHED'}
#enddef

class ExportSM0(bpy.types.Operator, bpy_extras.io_utils.ExportHelper):
  bl_idname    = "export.sm0_export"
  bl_label     = "Export io7m-sm0"
  filename_ext = ".sm0"

  @classmethod
  def poll(cls, context):
    return context.active_object is not None
  #enddef

  def execute(self, context):
    return sm0_export(self, context, self.filepath)
  #enddef
#end class

def sm0_export_menu_func(self, context):
  self.layout.operator(ExportSM0.bl_idname, text="io7m-sm0 (.sm0)")
#enddef

def register():
  bpy.utils.register_class(ExportSM0)
  bpy.types.INFO_MT_file_export.append(sm0_export_menu_func)
#enddef

def unregister():
  bpy.utils.unregister_class(ExportSM0)
  bpy.types.INFO_MT_file_export.remove(sm0_export_menu_func)
#enddef

if __name__ == "__main__":
  register()
#endif


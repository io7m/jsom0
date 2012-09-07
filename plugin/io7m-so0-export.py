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
  "name":        "io7m-so0 Export",
  "description": "Export to io7m-so0 format",
  "author":      "io7m.com",
  "version":     (1, 1),
  "blender":     (2, 6, 3),
  "api":         31236,
  "location":    "File > Export",
  "warning":     "",
  "category":    "Import-Export"
}

import bpy
import bpy_extras
import bpy_types
import sys
import time

def so0_info(message):
  print("so0-export: info: " + message)
#enddef

def so0_debug(message):
  print("so0-export: debug: " + message)
#enddef

def so0_zero_vertex():
  return dict(position=[0.0,0.0,0.0],normal=[0.0,0.0,0.0],uv=[0.0,0.0],used=False)
#enddef

def so0_zero_triangle():
  return dict(index=[0,0,0])
#enddef

def so0_vertex_add(vertices, index, vertex):
  so0_debug("vertex %d [%f %f %f] [%f %f %f] [%f %f]" %
    (index,
     vertex["position"][0],
     vertex["position"][1],
     vertex["position"][2],
     vertex["normal"][0],
     vertex["normal"][1],
     vertex["normal"][2],
     vertex["uv"][0],
     vertex["uv"][1]))

  assert index < len (vertices)

  #
  # Convert from Blender's coordinate system to OpenGL default.
  #
  y = vertex["position"][1]
  z = vertex["position"][2]
  vertex["position"][1] = -z
  vertex["position"][2] = y

  #
  # If the vertex at index hasn't been assigned, assign it.
  #
  if vertices[index]["used"] == False:
    so0_debug("vertex %d unused, assigning" % index)
    vertices[index]         = vertex
    vertices[index]["used"] = True
    return index
  #endif

  existing = vertices[index]
  assert existing["used"] == True

  so0_debug("vertex %d used, checking [%f %f] == [%f %f]" %
    (index,
     existing["uv"][0],
     existing["uv"][1],
     vertex["uv"][0],
     vertex["uv"][1]))

  #
  # Check to see if the existing vertex at this index has the same
  # UV coordinates.
  #
  same_uv = (existing["uv"][0] == vertex["uv"][0]) and (existing["uv"][1] == vertex["uv"][1])
  if same_uv:
    so0_debug("vertex %d shares uv, reusing" % index)
    return index
  #endif

  so0_debug("vertex %d has different uv, creating new vertex" % index)
  vertices.append(vertex)

  new_index = len (vertices) - 1
  vertices[new_index]["used"] = True

  return new_index
#enddef

def so0_process_mesh(caller, context, fd, mesh):
  num_vertices  = len (mesh.vertices)
  num_faces     = len (mesh.polygons)
  vertices      = []
  triangles     = []
  textured      = len (mesh.uv_textures) > 0
  out           = fd.write

  if textured:
    so0_info("mesh \"%s\" (%d faces, %d vertices, textured)" % (mesh.name, num_faces, num_vertices))
  else:
    so0_info("mesh \"%s\" (%d faces, %d vertices, untextured)" % (mesh.name, num_faces, num_vertices))
  #endif

  #
  # Preallocate vertex array.
  #
  for index in range(num_vertices):
    vertices.append(so0_zero_vertex())
  #endfor

  #
  # Blender stores UV values per face, as opposed to per vertex.
  # Extract UV coordinates per face and store them with the
  # relevant vertex.
  #
  # There's UV data for a mesh if 'UV unwrap' has been used on it.
  # By default, it's called "UVMap". This plugin ignores anything
  # beyond the first UV map.
  #
  if textured:
    so0_debug("writing textured mesh")

    uv_layer = mesh.uv_layers['UVMap'].data

    for poly in mesh.polygons:
      size = len (poly.vertices)
      assert ((size == 3) or (size == 4))

      if size == 3:
        lo0 = poly.loop_start + 0
        lo1 = poly.loop_start + 1
        lo2 = poly.loop_start + 2

        vi0 = mesh.loops[lo0].vertex_index
        vi1 = mesh.loops[lo1].vertex_index
        vi2 = mesh.loops[lo2].vertex_index

        v0 = so0_zero_vertex()
        v0["position"] = mesh.vertices[vi0].co
        v0["normal"]   = mesh.vertices[vi0].normal
        v0["uv"]       = uv_layer[lo0].uv

        v1 = so0_zero_vertex()
        v1["position"] = mesh.vertices[vi1].co
        v1["normal"]   = mesh.vertices[vi1].normal
        v1["uv"]       = uv_layer[lo1].uv

        v2 = so0_zero_vertex()
        v2["position"] = mesh.vertices[vi2].co
        v2["normal"]   = mesh.vertices[vi2].normal
        v2["uv"]       = uv_layer[lo2].uv

        new_vi0 = so0_vertex_add(vertices, vi0, v0)
        new_vi1 = so0_vertex_add(vertices, vi1, v1)
        new_vi2 = so0_vertex_add(vertices, vi2, v2)

        t0 = [new_vi0, new_vi1, new_vi2]

        triangles.append(t0)
      else:
        lo0 = poly.loop_start + 0
        lo1 = poly.loop_start + 1
        lo2 = poly.loop_start + 2
        lo3 = poly.loop_start + 3

        vi0 = mesh.loops[lo0].vertex_index
        vi1 = mesh.loops[lo1].vertex_index
        vi2 = mesh.loops[lo2].vertex_index
        vi3 = mesh.loops[lo3].vertex_index

        v0 = so0_zero_vertex()
        v0["position"] = mesh.vertices[vi0].co
        v0["normal"]   = mesh.vertices[vi0].normal
        v0["uv"]       = uv_layer[lo0].uv

        v1 = so0_zero_vertex()
        v1["position"] = mesh.vertices[vi1].co
        v1["normal"]   = mesh.vertices[vi1].normal
        v1["uv"]       = uv_layer[lo1].uv

        v2 = so0_zero_vertex()
        v2["position"] = mesh.vertices[vi2].co
        v2["normal"]   = mesh.vertices[vi2].normal
        v2["uv"]       = uv_layer[lo2].uv

        v3 = so0_zero_vertex()
        v3["position"] = mesh.vertices[vi3].co
        v3["normal"]   = mesh.vertices[vi3].normal
        v3["uv"]       = uv_layer[lo3].uv

        new_vi0 = so0_vertex_add(vertices, vi0, v0)
        new_vi1 = so0_vertex_add(vertices, vi1, v1)
        new_vi2 = so0_vertex_add(vertices, vi2, v2)
        new_vi3 = so0_vertex_add(vertices, vi3, v3)

        t0 = [new_vi0, new_vi1, new_vi2]
        t1 = [new_vi0, new_vi2, new_vi3]

        triangles.append(t0)
        triangles.append(t1)
      #endif
    #endfor
  else:
    so0_debug("writing untextured mesh")

    for poly in mesh.polygons:
      size = len (poly.vertices)
      assert ((size == 3) or (size == 4))

      if size == 3:
        lo0 = poly.loop_start + 0
        lo1 = poly.loop_start + 1
        lo2 = poly.loop_start + 2

        vi0 = mesh.loops[lo0].vertex_index
        vi1 = mesh.loops[lo1].vertex_index
        vi2 = mesh.loops[lo2].vertex_index

        v0 = so0_zero_vertex()
        v0["position"] = mesh.vertices[vi0].co
        v0["normal"]   = mesh.vertices[vi0].normal

        v1 = so0_zero_vertex()
        v1["position"] = mesh.vertices[vi1].co
        v1["normal"]   = mesh.vertices[vi1].normal

        v2 = so0_zero_vertex()
        v2["position"] = mesh.vertices[vi2].co
        v2["normal"]   = mesh.vertices[vi2].normal

        new_vi0 = so0_vertex_add(vertices, vi0, v0)
        new_vi1 = so0_vertex_add(vertices, vi1, v1)
        new_vi2 = so0_vertex_add(vertices, vi2, v2)

        t0 = [new_vi0, new_vi1, new_vi2]

        triangles.append(t0)
      else:
        lo0 = poly.loop_start + 0
        lo1 = poly.loop_start + 1
        lo2 = poly.loop_start + 2
        lo3 = poly.loop_start + 3

        vi0 = mesh.loops[lo0].vertex_index
        vi1 = mesh.loops[lo1].vertex_index
        vi2 = mesh.loops[lo2].vertex_index
        vi3 = mesh.loops[lo3].vertex_index

        v0 = so0_zero_vertex()
        v0["position"] = mesh.vertices[vi0].co
        v0["normal"]   = mesh.vertices[vi0].normal

        v1 = so0_zero_vertex()
        v1["position"] = mesh.vertices[vi1].co
        v1["normal"]   = mesh.vertices[vi1].normal

        v2 = so0_zero_vertex()
        v2["position"] = mesh.vertices[vi2].co
        v2["normal"]   = mesh.vertices[vi2].normal

        v3 = so0_zero_vertex()
        v3["position"] = mesh.vertices[vi3].co
        v3["normal"]   = mesh.vertices[vi3].normal

        new_vi0 = so0_vertex_add(vertices, vi0, v0)
        new_vi1 = so0_vertex_add(vertices, vi1, v1)
        new_vi2 = so0_vertex_add(vertices, vi2, v2)
        new_vi3 = so0_vertex_add(vertices, vi3, v3)

        t0 = [new_vi0, new_vi1, new_vi2]
        t1 = [new_vi0, new_vi2, new_vi3]

        triangles.append(t0)
        triangles.append(t1)
      #endif
    #endfor
  #endif

  #
  # Write out the object data.
  #
  out("object;\n")
  out("  name \"" + mesh.name + "\";\n")

  #
  # Ignore anything other than the first material.
  #
  assert (len (mesh.materials) > 0)
  material = mesh.materials[0]
  if material is None:
    caller.report({'ERROR'}, "Mesh \"%s\" has no material" % (mesh.name))
    return {'CANCELLED'}
  #endif
  out("  material_name \"%s\";\n" % (material.name))
  out("\n")

  #
  # Vertex array.
  #
  out("  vertices;\n")
  if textured:
    out("    array " + str (len(vertices)) + " vertex_p3n3t2;\n")
    for v in vertices:
      p = v["position"]
      n = v["normal"]
      u = v["uv"]
      out("      vertex_p3n3t2;\n")
      out("        position %f %f %f;\n" % (p[0], p[1], p[2]))
      out("        normal   %f %f %f;\n" % (n[0], n[1], n[2]))
      out("        uv       %f %f;\n"    % (u[0], u[1]))
      out("      end;\n")
    #endfor
    out("    end;\n")
  else:
    out("    array " + str (num_vertices) + " vertex_p3n3;\n")
    for v in vertices:
      p = v["position"]
      n = v["normal"]
      out("      vertex_p3n3;\n")
      out("        position %f %f %f;\n" % (p[0], p[1], p[2]))
      out("        normal   %f %f %f;\n" % (n[0], n[1], n[2]))
      out("      end;\n")
    #endfor
    out("    end;\n")
  #endif
  out("  end;\n")

  #
  # Write resulting triangles.
  #
  out("  triangles;\n")
  out("    array " + str (len(triangles)) + " triangle;\n")
  for t in triangles:
    out("      triangle %d %d %d;\n" % (t[0], t[1], t[2]))
  #endfor
  out("    end;\n")
  out("  end;\n")
  out("end;\n")
  out("\n")
#enddef

def so0_export(caller, context, file_path, objects):
  so0_info("exporting %s" % (file_path))

  fd = open(file_path, 'w')
  fd.write("-- Generated " + time.strftime("%Y-%m-%dT%H:%M:%S %z") + "\n")
  fd.write("\n")

  for obj in objects:
    if (type(obj.data) != bpy_types.Mesh):
      continue
    #endif

    so0_process_mesh(caller, context, fd, obj.data)
  #endfor

  fd.flush()
  fd.close()
  return {'FINISHED'}
#enddef

class ExportSO0(bpy.types.Operator, bpy_extras.io_utils.ExportHelper):
  bl_idname    = "export.so0_export"
  bl_label     = "Export io7m-so0"
  filename_ext = ".so0"
  only_visible = bpy.props.BoolProperty(name="Only visible objects", description="Only export visible objects", default=False)

  @classmethod
  def poll(cls, context):
    return context.active_object is not None
  #enddef

  def execute(self, context):
    if self.only_visible:
      return so0_export(self, context, self.filepath, context.visible_objects)
    else:
      return so0_export(self, context, self.filepath, bpy.data.objects)
    #endif
  #enddef
#end class

def so0_export_menu_func(self, context):
  self.layout.operator(ExportSO0.bl_idname, text="io7m-so0 (.so0)")
#enddef

def register():
  bpy.utils.register_class(ExportSO0)
  bpy.types.INFO_MT_file_export.append(so0_export_menu_func)
#enddef

def unregister():
  bpy.utils.unregister_class(ExportSO0)
  bpy.types.INFO_MT_file_export.remove(so0_export_menu_func)
#enddef

if __name__ == "__main__":
  register()
#endif


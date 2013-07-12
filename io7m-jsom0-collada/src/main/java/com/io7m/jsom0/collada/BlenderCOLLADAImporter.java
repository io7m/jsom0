/*
 * Copyright © 2013 <code@io7m.com> http://io7m.com
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jsom0.collada;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nonnull;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.XPathContext;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jlog.Log;
import com.io7m.jsom0.ModelMaterial;
import com.io7m.jsom0.VertexType;
import com.io7m.jtensors.VectorI2F;
import com.io7m.jtensors.VectorI3F;
import com.io7m.jtensors.VectorI4F;
import com.io7m.jtensors.VectorM3F;
import com.io7m.jtensors.VectorM4F;
import com.io7m.jtensors.VectorReadable3F;
import com.io7m.jtensors.VectorReadable4F;

/**
 * A program to extract information from the COLLADA data exported by Blender.
 */

public final class BlenderCOLLADAImporter
{
  private static class ColladaModel
  {
    @Nonnull Map<String, ColladaObject> objects;

    ColladaModel()
    {
      this.objects = new HashMap<String, ColladaObject>();
    }
  }

  private static class ColladaObject
  {
    final @Nonnull ArrayList<ColladaPoly> polygons;

    /**
     * Positions are shared between "vertices" in the model. So, in the
     * example of a cube, there'll be 8 vertex positions (one for each corner
     * of the cube).
     */

    final @Nonnull ArrayList<VectorI3F>   positions;

    /**
     * Normal vectors are stored per-polygon. So, in the example of a cube,
     * there'll be (6 * 2 = 12) normal vectors (six square faces each
     * represented by two triangles).
     */

    final @Nonnull ArrayList<VectorI3F>   normals;

    /**
     * Texture coordinates appear to be completely unshared per "vertex". So,
     * in the case of a cube, there'll be (12 * 3 = 36) texture coordinate
     * vectors (twelve polygons, with three vertices per polygon, and one
     * texture coordinate vector for each vertex).
     */

    final @Nonnull ArrayList<VectorI2F>   texcoords;
    final @Nonnull ColladaVertexType      vertex_type;
    final @Nonnull String                 name;
    final @Nonnull ModelMaterial          material;

    ColladaObject(
      final @Nonnull String name,
      final @Nonnull ModelMaterial material,
      final @Nonnull ColladaVertexType vertex_type)
    {
      this.name = name;
      this.material = material;
      this.vertex_type = vertex_type;
      this.polygons = new ArrayList<BlenderCOLLADAImporter.ColladaPoly>();
      this.positions = new ArrayList<VectorI3F>();
      this.normals = new ArrayList<VectorI3F>();
      this.texcoords = new ArrayList<VectorI2F>();
    }
  }

  /**
   * A polygon specification, indexing into the arrays in
   * {@link ColladaObject}.
   */

  private static class ColladaPoly
  {
    int pos0  = -1;
    int pos1  = -1;
    int pos2  = -1;
    int norm0 = -1;
    int norm1 = -1;
    int norm2 = -1;
    int uv0   = -1;
    int uv1   = -1;
    int uv2   = -1;

    ColladaPoly()
    {

    }
  }

  private abstract static class ColladaVertexType
  {
    final @Nonnull VertexType type;

    ColladaVertexType(
      final @Nonnull VertexType type)
    {
      this.type = type;
    }
  }

  private final static class ColladaVertexTypeP3N3 extends ColladaVertexType
  {
    final int offset_position;
    final int offset_normal;

    ColladaVertexTypeP3N3(
      final int offset_position,
      final int offset_normal)
    {
      super(VertexType.VERTEX_TYPE_P3N3);
      this.offset_position = offset_position;
      this.offset_normal = offset_normal;
    }
  }

  private final static class ColladaVertexTypeP3N3T2 extends
    ColladaVertexType
  {
    final int offset_position;
    final int offset_normal;
    final int offset_uv;

    ColladaVertexTypeP3N3T2(
      final int offset_position,
      final int offset_normal,
      final int offset_uv)
    {
      super(VertexType.VERTEX_TYPE_P3N3T2);
      this.offset_position = offset_position;
      this.offset_normal = offset_normal;
      this.offset_uv = offset_uv;
    }
  }

  private static class Jsom0Model
  {
    private final @Nonnull HashMap<String, Jsom0Object> objects;

    Jsom0Model(
      final @Nonnull ColladaModel source,
      final @Nonnull Log log)
    {
      this.objects = new HashMap<String, Jsom0Object>();

      for (final ColladaObject o : source.objects.values()) {
        this.objects.put(o.name, new Jsom0Object(o, log));
      }
    }

    @SuppressWarnings("boxing") public void write(
      final PrintStream out)
    {
      for (final Jsom0Object o : this.objects.values()) {
        out.println("object;");

        out.print("  name \"");
        out.print(o.name);
        out.println("\";");

        out.print("  material_name \"");
        out.print(o.material.name);
        out.println("\";");

        out.println("  vertices;");

        {
          out.print("    array ");
          out.print(o.vertices.size());
          out.print(" ");

          switch (o.type) {
            case VERTEX_TYPE_P3N3:
            {
              out.println("vertex_p3n3;");

              for (final Jsom0Vertex v : o.vertices) {
                final Jsom0VertexP3N3 v_actual = (Jsom0VertexP3N3) v;

                out.println("      vertex_p3n3;");

                out.print("        position ");
                out.print(String.format("%.6f", v_actual.position.x));
                out.print(" ");
                out.print(String.format("%.6f", v_actual.position.y));
                out.print(" ");
                out.print(String.format("%.6f", v_actual.position.z));
                out.println(";");

                out.print("        normal ");
                out.print(String.format("%.6f", v_actual.normal.x));
                out.print(" ");
                out.print(String.format("%.6f", v_actual.normal.y));
                out.print(" ");
                out.print(String.format("%.6f", v_actual.normal.z));
                out.println(";");

                out.println("      end;");
              }
              break;
            }
            case VERTEX_TYPE_P3N3T2:
            {
              out.println("vertex_p3n3t2;");

              for (final Jsom0Vertex v : o.vertices) {
                final Jsom0VertexP3N3T2 v_actual = (Jsom0VertexP3N3T2) v;

                out.println("      vertex_p3n3t2;");

                out.print("        position ");
                out.print(String.format("%.6f", v_actual.position.x));
                out.print(" ");
                out.print(String.format("%.6f", v_actual.position.y));
                out.print(" ");
                out.print(String.format("%.6f", v_actual.position.z));
                out.println(";");

                out.print("        normal ");
                out.print(String.format("%.6f", v_actual.normal.x));
                out.print(" ");
                out.print(String.format("%.6f", v_actual.normal.y));
                out.print(" ");
                out.print(String.format("%.6f", v_actual.normal.z));
                out.println(";");

                out.print("        uv ");
                out.print(String.format("%.6f", v_actual.texcoord.x));
                out.print(" ");
                out.print(String.format("%.6f", v_actual.texcoord.y));
                out.println(";");

                out.println("      end;");
              }
              break;
            }
          }
          out.println("    end;");
        }

        out.println("  end;");
        out.println("  triangles;");
        out.print("    array ");
        out.print(o.polygons.size());
        out.println(" triangle;");

        for (final Jsom0Triangle p : o.polygons) {
          out.print("      triangle ");
          out.print(p.v0);
          out.print(" ");
          out.print(p.v1);
          out.print(" ");
          out.print(p.v2);
          out.println(";");
        }

        out.println("    end;");
        out.println("  end;");
        out.println("end;");
      }
    }
  }

  private static class Jsom0Object
  {
    final @Nonnull Map<Jsom0Vertex, Integer> vertices_map;
    final @Nonnull ArrayList<Jsom0Vertex>    vertices;
    final @Nonnull VertexType                type;
    final @Nonnull ArrayList<Jsom0Triangle>  polygons;
    final @Nonnull String                    name;
    final @Nonnull ModelMaterial             material;

    Jsom0Object(
      final @Nonnull ColladaObject source,
      final @Nonnull Log log)
    {
      this.vertices_map = new HashMap<Jsom0Vertex, Integer>();
      this.vertices = new ArrayList<Jsom0Vertex>();
      this.polygons = new ArrayList<Jsom0Triangle>();
      this.type = source.vertex_type.type;
      this.name = source.name;
      this.material = source.material;

      switch (this.type) {
        case VERTEX_TYPE_P3N3:
        {
          for (final ColladaPoly poly : source.polygons) {
            final VectorI3F p0 = source.positions.get(poly.pos0);
            final VectorI3F p1 = source.positions.get(poly.pos1);
            final VectorI3F p2 = source.positions.get(poly.pos2);

            final VectorI3F n0 = source.normals.get(poly.norm0);
            final VectorI3F n1 = source.normals.get(poly.norm1);
            final VectorI3F n2 = source.normals.get(poly.norm2);

            // Convert from Blender's coordinate system to OpenGL
            final VectorI3F p0_gl = Jsom0Object.axesBlenderToOpenGL(p0);
            final VectorI3F p1_gl = Jsom0Object.axesBlenderToOpenGL(p1);
            final VectorI3F p2_gl = Jsom0Object.axesBlenderToOpenGL(p2);

            final Jsom0VertexP3N3 v0 = new Jsom0VertexP3N3(p0_gl, n0);
            final Jsom0VertexP3N3 v1 = new Jsom0VertexP3N3(p1_gl, n1);
            final Jsom0VertexP3N3 v2 = new Jsom0VertexP3N3(p2_gl, n2);

            this.reuseAddVertex(v0, v1, v2);
          }
          break;
        }
        case VERTEX_TYPE_P3N3T2:
        {
          for (final ColladaPoly poly : source.polygons) {
            final VectorI3F p0 = source.positions.get(poly.pos0);
            final VectorI3F p1 = source.positions.get(poly.pos1);
            final VectorI3F p2 = source.positions.get(poly.pos2);

            final VectorI3F n0 = source.normals.get(poly.norm0);
            final VectorI3F n1 = source.normals.get(poly.norm1);
            final VectorI3F n2 = source.normals.get(poly.norm2);

            final VectorI2F t0 = source.texcoords.get(poly.uv0);
            final VectorI2F t1 = source.texcoords.get(poly.uv1);
            final VectorI2F t2 = source.texcoords.get(poly.uv2);

            // Convert from Blender's coordinate system to OpenGL
            final VectorI3F p0_gl = Jsom0Object.axesBlenderToOpenGL(p0);
            final VectorI3F p1_gl = Jsom0Object.axesBlenderToOpenGL(p1);
            final VectorI3F p2_gl = Jsom0Object.axesBlenderToOpenGL(p2);

            final Jsom0VertexP3N3T2 v0 = new Jsom0VertexP3N3T2(p0_gl, n0, t0);
            final Jsom0VertexP3N3T2 v1 = new Jsom0VertexP3N3T2(p1_gl, n1, t1);
            final Jsom0VertexP3N3T2 v2 = new Jsom0VertexP3N3T2(p2_gl, n2, t2);

            this.reuseAddVertex(v0, v1, v2);
          }
          break;
        }
      }

      log.debug("object has "
        + this.vertices.size()
        + " vertices after sharing");
    }

    private static @Nonnull VectorI3F axesBlenderToOpenGL(
      final @Nonnull VectorI3F v)
    {
      return new VectorI3F(v.x, -v.z, v.y);
    }

    private void reuseAddVertex(
      final @Nonnull Jsom0Vertex v0,
      final @Nonnull Jsom0Vertex v1,
      final @Nonnull Jsom0Vertex v2)
    {
      int v0i = -1;
      int v1i = -1;
      int v2i = -1;

      if (this.vertices_map.containsKey(v0)) {
        v0i = this.vertices_map.get(v0).intValue();
      } else {
        this.vertices.add(v0);
        v0i = this.vertices.size() - 1;
        this.vertices_map.put(v0, Integer.valueOf(v0i));
      }

      if (this.vertices_map.containsKey(v1)) {
        v1i = this.vertices_map.get(v1).intValue();
      } else {
        this.vertices.add(v1);
        v1i = this.vertices.size() - 1;
        this.vertices_map.put(v1, Integer.valueOf(v1i));
      }

      if (this.vertices_map.containsKey(v2)) {
        v2i = this.vertices_map.get(v2).intValue();
      } else {
        this.vertices.add(v2);
        v2i = this.vertices.size() - 1;
        this.vertices_map.put(v2, Integer.valueOf(v2i));
      }

      assert v0i != -1;
      assert v1i != -1;
      assert v2i != -1;

      this.polygons.add(new Jsom0Triangle(v0i, v1i, v2i));
    }
  }

  private static class Jsom0Triangle
  {
    final int v0;
    final int v1;
    final int v2;

    Jsom0Triangle(
      final int v0,
      final int v1,
      final int v2)
    {
      this.v0 = v0;
      this.v1 = v1;
      this.v2 = v2;
    }
  }

  private abstract static class Jsom0Vertex
  {
    final @Nonnull VertexType type;

    Jsom0Vertex(
      final @Nonnull VertexType type)
    {
      this.type = type;
    }
  }

  private static class Jsom0VertexP3N3 extends Jsom0Vertex
  {
    final @Nonnull VectorI3F position;
    final @Nonnull VectorI3F normal;

    Jsom0VertexP3N3(
      final @Nonnull VectorI3F position,
      final @Nonnull VectorI3F normal)
    {
      super(VertexType.VERTEX_TYPE_P3N3);
      this.position = position;
      this.normal = normal;
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final Jsom0VertexP3N3 other = (Jsom0VertexP3N3) obj;
      if (!this.normal.equals(other.normal)) {
        return false;
      }
      if (!this.position.equals(other.position)) {
        return false;
      }
      return true;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.normal.hashCode();
      result = (prime * result) + this.position.hashCode();
      return result;
    }
  }

  private static class Jsom0VertexP3N3T2 extends Jsom0Vertex
  {
    final @Nonnull VectorI3F position;
    final @Nonnull VectorI3F normal;
    final @Nonnull VectorI2F texcoord;

    Jsom0VertexP3N3T2(
      final @Nonnull VectorI3F position,
      final @Nonnull VectorI3F normal,
      final @Nonnull VectorI2F texcoord)
    {
      super(VertexType.VERTEX_TYPE_P3N3T2);
      this.position = position;
      this.normal = normal;
      this.texcoord = texcoord;
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final Jsom0VertexP3N3T2 other = (Jsom0VertexP3N3T2) obj;
      if (!this.normal.equals(other.normal)) {
        return false;
      }
      if (!this.position.equals(other.position)) {
        return false;
      }
      if (!this.texcoord.equals(other.texcoord)) {
        return false;
      }
      return true;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.normal.hashCode();
      result = (prime * result) + this.position.hashCode();
      result = (prime * result) + this.texcoord.hashCode();
      return result;
    }
  }

  private static final @Nonnull URI COLLADA_URI;

  static {
    try {
      COLLADA_URI = new URI("http://www.collada.org/2005/11/COLLADASchema");
    } catch (final URISyntaxException e) {
      throw new UnreachableCodeException();
    }
  }

  private static @Nonnull VectorReadable3F getEffectAmbient(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element effect_elem)
  {
    assert effect_elem.getLocalName().equals("effect");

    final Nodes nodes =
      effect_elem.query(
        "c:profile_COMMON/c:technique/c:phong/c:ambient/c:color",
        xpc);
    assert nodes.size() == 1;
    final Element elem = (Element) nodes.get(0);

    final String[] components = elem.getValue().split("\\s+");
    assert components.length == 4;

    final float x = Float.parseFloat(components[0]);
    final float y = Float.parseFloat(components[1]);
    final float z = Float.parseFloat(components[2]);
    return new VectorI4F(x, y, z, 1.0f);
  }

  private static @Nonnull Element getEffectByAddress(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element root,
    final @Nonnull String effect_url)
  {
    assert root.getLocalName().equals("COLLADA");
    assert effect_url.startsWith("#");

    final StringBuilder e_query = new StringBuilder();
    e_query.append("/c:COLLADA/c:library_effects/c:effect[@id='");
    e_query.append(effect_url.substring(1));
    e_query.append("']");

    final Nodes effects_nodes = root.query(e_query.toString(), xpc);
    assert effects_nodes.size() == 1;
    return (Element) effects_nodes.get(0);
  }

  private static @Nonnull VectorReadable3F getEffectDiffuse(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element effect_elem)
  {
    assert effect_elem.getLocalName().equals("effect");

    final Nodes nodes =
      effect_elem.query(
        "c:profile_COMMON/c:technique/c:phong/c:diffuse/c:color",
        xpc);

    /**
     * Unfortunately, Blender's COLLADA exporter places a "texture" element in
     * the diffuse section if the model is textured. This loses information!
     * If there's no color element but there is a texture element, return a
     * pure white diffuse color.
     */

    if (nodes.size() < 1) {
      final Nodes texture =
        effect_elem.query(
          "c:profile_COMMON/c:technique/c:phong/c:diffuse/c:texture",
          xpc);
      if (texture.size() == 1) {
        return new VectorI4F(1.0f, 1.0f, 1.0f, 1.0f);
      }
    }

    assert nodes.size() == 1;
    final Element elem = (Element) nodes.get(0);

    final String[] components = elem.getValue().split("\\s+");
    assert components.length == 4;

    final float x = Float.parseFloat(components[0]);
    final float y = Float.parseFloat(components[1]);
    final float z = Float.parseFloat(components[2]);
    return new VectorI4F(x, y, z, 1.0f);
  }

  private static float getEffectShininess(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element effect_elem)
  {
    assert effect_elem.getLocalName().equals("effect");

    final Nodes nodes =
      effect_elem.query(
        "c:profile_COMMON/c:technique/c:phong/c:shininess/c:float",
        xpc);
    assert nodes.size() == 1;
    final Element elem = (Element) nodes.get(0);
    return Float.parseFloat(elem.getValue());
  }

  private static @Nonnull VectorReadable4F getEffectSpecular(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element effect_elem)
  {
    assert effect_elem.getLocalName().equals("effect");

    final Nodes nodes =
      effect_elem.query(
        "c:profile_COMMON/c:technique/c:phong/c:specular/c:color",
        xpc);
    assert nodes.size() == 1;
    final Element elem = (Element) nodes.get(0);

    final String[] components = elem.getValue().split("\\s+");
    assert components.length == 4;

    final float x = Float.parseFloat(components[0]);
    final float y = Float.parseFloat(components[1]);
    final float z = Float.parseFloat(components[2]);
    final float w = Float.parseFloat(components[3]);
    return new VectorI4F(x, y, z, w);
  }

  private static @Nonnull Nodes getGeometries(
    final @Nonnull XPathContext xpc,
    final @Nonnull Document document)
  {
    return document.query("/c:COLLADA/c:library_geometries/c:geometry", xpc);
  }

  private static Element getMaterialByAddress(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element root,
    final @Nonnull String material_address)
  {
    assert root.getLocalName().equals("COLLADA");

    final StringBuilder m_query = new StringBuilder();
    m_query.append("/c:COLLADA/c:library_materials/c:material[@id='");
    m_query.append(material_address);
    m_query.append("']");

    final Nodes material_nodes = root.query(m_query.toString(), xpc);
    assert material_nodes.size() == 1;
    final Element material_elem = (Element) material_nodes.get(0);
    return material_elem;
  }

  private static @Nonnull ModelMaterial getMaterialWithId(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element root,
    final @Nonnull String material_address)
    throws ConstraintError
  {
    assert root.getLocalName().equals("COLLADA");

    final Element material_elem =
      BlenderCOLLADAImporter
        .getMaterialByAddress(xpc, root, material_address);

    final Attribute material_name = material_elem.getAttribute("name");
    assert material_name != null;

    final Elements instance_effects =
      material_elem.getChildElements(
        "instance_effect",
        BlenderCOLLADAImporter.COLLADA_URI.toString());
    assert instance_effects.size() == 1;
    final Element instance_effect = instance_effects.get(0);
    final String effect_url = instance_effect.getAttribute("url").getValue();
    assert effect_url != null;

    final Element effect_elem =
      BlenderCOLLADAImporter.getEffectByAddress(xpc, root, effect_url);

    return BlenderCOLLADAImporter.processEffectElementMaterial(
      xpc,
      material_name.getValue(),
      effect_elem);
  }

  private static @Nonnull Element getMeshFromGeometry(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element geometry)
  {
    assert geometry.getLocalName().equals("geometry");

    final Nodes mesh = geometry.query("c:mesh", xpc);
    assert mesh.size() == 1;
    return (Element) mesh.get(0);
  }

  private static @Nonnull Element getNormalArrayElementFromMesh(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element mesh)
  {
    assert mesh.getLocalName().equals("mesh");

    final String vertex_positions_source_name =
      BlenderCOLLADAImporter.getNormalSourceName(xpc, mesh);

    final StringBuilder source_query = new StringBuilder();
    source_query.append("c:source[@id='");
    source_query.append(vertex_positions_source_name);
    source_query.append("']");

    final Nodes vertex_position_source_nodes =
      mesh.query(source_query.toString(), xpc);
    assert vertex_position_source_nodes.size() == 1;
    final Element vertex_position_source =
      (Element) vertex_position_source_nodes.get(0);

    final Elements float_array_nodes =
      vertex_position_source.getChildElements(
        "float_array",
        BlenderCOLLADAImporter.COLLADA_URI.toString());
    assert float_array_nodes.size() == 1;
    return float_array_nodes.get(0);
  }

  private static @Nonnull String[] getNormalArrayFromMesh(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element mesh)
  {
    assert mesh.getLocalName().equals("mesh");

    final Element array =
      BlenderCOLLADAImporter.getNormalArrayElementFromMesh(xpc, mesh);
    return array.getValue().split("\\s+");
  }

  private static String getNormalSourceName(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element mesh)
  {
    assert mesh.getLocalName().equals("mesh");

    final Nodes normal_input_nodes =
      mesh.query("c:polylist/c:input[@semantic='NORMAL']", xpc);
    assert normal_input_nodes.size() == 1;
    final Element input_elem = (Element) normal_input_nodes.get(0);
    assert input_elem != null;
    final Attribute source_attr = input_elem.getAttribute("source");
    assert source_attr != null;
    final String source_name = source_attr.getValue();
    assert source_name.startsWith("#");
    return source_name.substring(1);
  }

  private static @Nonnull Element getPolylistFromMesh(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element mesh)
  {
    assert mesh.getLocalName().equals("mesh");

    final Nodes polylist = mesh.query("c:polylist", xpc);
    assert polylist.size() == 1;
    return (Element) polylist.get(0);
  }

  private static @Nonnull String getPolylistMaterialAddress(
    final @Nonnull Element polylist)
  {
    assert polylist.getLocalName().equals("polylist");
    return polylist.getAttribute("material").getValue();
  }

  private static @Nonnull String[] getPolylistPolyArray(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element polylist)
  {
    assert polylist.getLocalName().equals("polylist");
    final String s =
      BlenderCOLLADAImporter
        .getPolylistPolyArrayElement(xpc, polylist)
        .getValue();
    return s.split("\\s+");
  }

  private static @Nonnull Element getPolylistPolyArrayElement(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element polylist)
  {
    assert polylist.getLocalName().equals("polylist");

    final Nodes poly_array_nodes = polylist.query("c:p", xpc);
    assert poly_array_nodes.size() == 1;
    final Element poly_array = (Element) poly_array_nodes.get(0);
    assert poly_array != null;
    return poly_array;
  }

  private static @Nonnull Element getTexCoordArrayElementFromMesh(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element mesh)
  {
    assert mesh.getLocalName().equals("mesh");

    final String vertex_positions_source_name =
      BlenderCOLLADAImporter.getTexCoordSourceName(xpc, mesh);

    final StringBuilder source_query = new StringBuilder();
    source_query.append("c:source[@id='");
    source_query.append(vertex_positions_source_name);
    source_query.append("']");

    final Nodes vertex_position_source_nodes =
      mesh.query(source_query.toString(), xpc);
    assert vertex_position_source_nodes.size() == 1;
    final Element vertex_position_source =
      (Element) vertex_position_source_nodes.get(0);

    final Elements float_array_nodes =
      vertex_position_source.getChildElements(
        "float_array",
        BlenderCOLLADAImporter.COLLADA_URI.toString());
    assert float_array_nodes.size() == 1;
    return float_array_nodes.get(0);
  }

  private static @Nonnull String[] getTexCoordArrayFromMesh(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element mesh)
  {
    assert mesh.getLocalName().equals("mesh");

    final Element array =
      BlenderCOLLADAImporter.getTexCoordArrayElementFromMesh(xpc, mesh);
    return array.getValue().split("\\s+");
  }

  private static String getTexCoordSourceName(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element mesh)
  {
    assert mesh.getLocalName().equals("mesh");

    final Nodes normal_input_nodes =
      mesh.query("c:polylist/c:input[@semantic='TEXCOORD']", xpc);
    assert normal_input_nodes.size() == 1;
    final Element input_elem = (Element) normal_input_nodes.get(0);
    assert input_elem != null;
    final Attribute source_attr = input_elem.getAttribute("source");
    assert source_attr != null;
    final String source_name = source_attr.getValue();
    assert source_name.startsWith("#");
    return source_name.substring(1);
  }

  private static @Nonnull Element getVertexPositionArrayElementFromMesh(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element mesh)
  {
    assert mesh.getLocalName().equals("mesh");

    final String vertex_positions_source_name =
      BlenderCOLLADAImporter.getVertexPositionSourceName(xpc, mesh);

    final StringBuilder source_query = new StringBuilder();
    source_query.append("c:source[@id='");
    source_query.append(vertex_positions_source_name);
    source_query.append("']");

    final Nodes vertex_position_source_nodes =
      mesh.query(source_query.toString(), xpc);
    assert vertex_position_source_nodes.size() == 1;
    final Element vertex_position_source =
      (Element) vertex_position_source_nodes.get(0);

    final Elements float_array_nodes =
      vertex_position_source.getChildElements(
        "float_array",
        BlenderCOLLADAImporter.COLLADA_URI.toString());
    assert float_array_nodes.size() == 1;
    return float_array_nodes.get(0);
  }

  private static @Nonnull String[] getVertexPositionArrayFromMesh(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element mesh)
  {
    assert mesh.getLocalName().equals("mesh");

    final Element array =
      BlenderCOLLADAImporter.getVertexPositionArrayElementFromMesh(xpc, mesh);
    return array.getValue().split("\\s+");
  }

  private static @Nonnull String getVertexPositionSourceName(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element mesh)
  {
    assert mesh.getLocalName().equals("mesh");

    final Nodes position_input_nodes =
      mesh.query("c:vertices/c:input[@semantic='POSITION']", xpc);
    assert position_input_nodes.size() == 1;
    final Element input_elem = (Element) position_input_nodes.get(0);
    assert input_elem != null;
    final Attribute source_attr = input_elem.getAttribute("source");
    assert source_attr != null;
    final String source_name = source_attr.getValue();
    assert source_name.startsWith("#");
    return source_name.substring(1);
  }

  private static @Nonnull ColladaVertexType getVertexType(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element polylist)
  {
    final Nodes input_p = polylist.query("c:input[@semantic='VERTEX']", xpc);
    final Nodes input_n = polylist.query("c:input[@semantic='NORMAL']", xpc);
    final Nodes input_t =
      polylist.query("c:input[@semantic='TEXCOORD']", xpc);

    assert input_p.size() == 1;
    assert input_n.size() == 1;
    assert (input_t.size() >= 0) && (input_t.size() <= 1);

    final String offset_ps =
      ((Element) input_p.get(0)).getAttribute("offset").getValue();
    final String offset_ns =
      ((Element) input_n.get(0)).getAttribute("offset").getValue();

    final Integer offset_p = Integer.valueOf(offset_ps);
    final Integer offset_n = Integer.valueOf(offset_ns);

    if (input_t.size() > 0) {
      final String offset_ts =
        ((Element) input_t.get(0)).getAttribute("offset").getValue();
      final Integer offset_t = Integer.valueOf(offset_ts);
      return new ColladaVertexTypeP3N3T2(
        offset_p.intValue(),
        offset_n.intValue(),
        offset_t.intValue());
    }

    return new ColladaVertexTypeP3N3(offset_p.intValue(), offset_n.intValue());
  }

  static @Nonnull ColladaModel load(
    final @Nonnull Log log,
    final @Nonnull Document document)
    throws ConstraintError
  {
    Constraints.constrainNotNull(document, "Document");
    Constraints.constrainArbitrary(
      document
        .getRootElement()
        .getNamespaceURI()
        .equals(BlenderCOLLADAImporter.COLLADA_URI.toString()),
      "Document is a COLLADA document");

    final XPathContext xpc =
      new XPathContext("c", BlenderCOLLADAImporter.COLLADA_URI.toString());

    final ColladaModel model = new ColladaModel();
    final Nodes geometries =
      BlenderCOLLADAImporter.getGeometries(xpc, document);
    for (int index = 0; index < geometries.size(); ++index) {
      final Element geom = (Element) geometries.get(index);
      final ColladaObject object =
        BlenderCOLLADAImporter.processGeometry(xpc, geom, log);
      assert model.objects.containsKey(object.name) == false;
      model.objects.put(object.name, object);
    }

    return model;
  }

  public static void main(
    final String args[])
  {
    if (args.length < 2) {
      System.err.println("usage: collada.conf file.xml");
      System.exit(1);
    }

    FileInputStream props_in = null;

    try {
      final Builder parser = new Builder();
      final Properties props = new Properties();
      props_in = new FileInputStream(args[0]);
      props.load(props_in);
      final Log log = new Log(props, "com.io7m.jsom0", "collada");
      final Document document = parser.build(new File(args[1]));
      final ColladaModel cmodel = BlenderCOLLADAImporter.load(log, document);
      final Jsom0Model jmodel = new Jsom0Model(cmodel, log);
      jmodel.write(System.out);
    } catch (final ValidityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.exit(1);
    } catch (final ParsingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.exit(1);
    } catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.exit(1);
    } catch (final ConstraintError e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.exit(1);
    } finally {
      if (props_in != null) {
        try {
          props_in.close();
        } catch (final IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
          System.exit(1);
        }
      }
    }
  }

  private static void populateNormalArray(
    final @Nonnull XPathContext xpc,
    final @Nonnull ColladaObject model,
    final @Nonnull Element mesh,
    final @Nonnull Log log)
  {
    assert mesh.getLocalName().equals("mesh");

    final String[] vpa =
      BlenderCOLLADAImporter.getNormalArrayFromMesh(xpc, mesh);

    assert vpa.length > 0;
    assert (vpa.length % 3) == 0;

    for (int index = 0; index < vpa.length; index += 3) {
      final Float x = Float.valueOf(vpa[index + 0]);
      final Float y = Float.valueOf(vpa[index + 1]);
      final Float z = Float.valueOf(vpa[index + 2]);
      model.normals.add(new VectorI3F(x.floatValue(), y.floatValue(), z
        .floatValue()));
    }

    log.debug("loaded " + model.normals.size() + " polygon normals");
  }

  private static void populatePolyArray(
    final @Nonnull XPathContext xpc,
    final @Nonnull ColladaObject model,
    final @Nonnull Log log,
    final @Nonnull Element polylist)
  {
    assert polylist.getLocalName().equals("polylist");

    final Attribute count_attr = polylist.getAttribute("count");
    final Integer count = Integer.valueOf(count_attr.getValue());
    log.debug("expecting " + count.toString() + " polygons");

    final String[] indices =
      BlenderCOLLADAImporter.getPolylistPolyArray(xpc, polylist);
    assert indices.length > 0;
    assert (indices.length % 3) == 0;

    switch (model.vertex_type.type) {
      case VERTEX_TYPE_P3N3:
      {
        final ColladaVertexTypeP3N3 vt =
          (ColladaVertexTypeP3N3) model.vertex_type;
        log.debug("vertex position index offset : " + vt.offset_position);
        log.debug("vertex normal index offset   : " + vt.offset_normal);

        final int stride = 6; // 2 * 3 indices
        for (int offset = 0; offset < indices.length; offset += stride) {
          final ColladaPoly p = new ColladaPoly();

          final int tri0_off = 0 + offset;
          final int tri1_off = 2 + offset;
          final int tri2_off = 4 + offset;

          p.pos0 = Integer.parseInt(indices[tri0_off + vt.offset_position]);
          assert (p.pos0 >= 0) && (p.pos0 < model.positions.size());
          p.pos1 = Integer.parseInt(indices[tri1_off + vt.offset_position]);
          assert (p.pos1 >= 0) && (p.pos1 < model.positions.size());
          p.pos2 = Integer.parseInt(indices[tri2_off + vt.offset_position]);
          assert (p.pos2 >= 0) && (p.pos2 < model.positions.size());

          p.norm0 = Integer.parseInt(indices[tri0_off + vt.offset_normal]);
          assert (p.norm0 >= 0) && (p.norm0 < model.normals.size());
          p.norm1 = Integer.parseInt(indices[tri1_off + vt.offset_normal]);
          assert (p.norm1 >= 0) && (p.norm1 < model.normals.size());
          p.norm2 = Integer.parseInt(indices[tri2_off + vt.offset_normal]);
          assert (p.norm2 >= 0) && (p.norm2 < model.normals.size());

          model.polygons.add(p);
        }
        break;
      }
      case VERTEX_TYPE_P3N3T2:
      {
        final ColladaVertexTypeP3N3T2 vt =
          (ColladaVertexTypeP3N3T2) model.vertex_type;
        log.debug("vertex position index offset : " + vt.offset_position);
        log.debug("vertex normal index offset   : " + vt.offset_normal);
        log.debug("vertex uv index offset       : " + vt.offset_uv);

        final int stride = 9; // 3 * 3 indices
        for (int offset = 0; offset < indices.length; offset += stride) {
          final ColladaPoly p = new ColladaPoly();

          final int tri0_off = 0 + offset;
          final int tri1_off = 3 + offset;
          final int tri2_off = 6 + offset;

          p.pos0 = Integer.parseInt(indices[tri0_off + vt.offset_position]);
          assert (p.pos0 >= 0) && (p.pos0 < model.positions.size());
          p.pos1 = Integer.parseInt(indices[tri1_off + vt.offset_position]);
          assert (p.pos1 >= 0) && (p.pos1 < model.positions.size());
          p.pos2 = Integer.parseInt(indices[tri2_off + vt.offset_position]);
          assert (p.pos2 >= 0) && (p.pos2 < model.positions.size());

          p.norm0 = Integer.parseInt(indices[tri0_off + vt.offset_normal]);
          assert (p.norm0 >= 0) && (p.norm0 < model.normals.size());
          p.norm1 = Integer.parseInt(indices[tri1_off + vt.offset_normal]);
          assert (p.norm1 >= 0) && (p.norm1 < model.normals.size());
          p.norm2 = Integer.parseInt(indices[tri2_off + vt.offset_normal]);
          assert (p.norm2 >= 0) && (p.norm2 < model.normals.size());

          p.uv0 = Integer.parseInt(indices[tri0_off + vt.offset_uv]);
          assert (p.uv0 >= 0) && (p.uv0 < model.texcoords.size());
          p.uv1 = Integer.parseInt(indices[tri1_off + vt.offset_uv]);
          assert (p.uv1 >= 0) && (p.uv1 < model.texcoords.size());
          p.uv2 = Integer.parseInt(indices[tri2_off + vt.offset_uv]);
          assert (p.uv2 >= 0) && (p.uv2 < model.texcoords.size());
          model.polygons.add(p);
        }

        break;
      }
    }

    log.debug("loaded " + model.polygons.size() + " polygons");
    assert model.polygons.size() == count.intValue();
  }

  private static void populateTexCoordArray(
    final @Nonnull XPathContext xpc,
    final @Nonnull ColladaObject model,
    final @Nonnull Element mesh,
    final @Nonnull Log log)
  {
    assert mesh.getLocalName().equals("mesh");

    final String[] vpa =
      BlenderCOLLADAImporter.getTexCoordArrayFromMesh(xpc, mesh);

    assert vpa.length > 0;
    assert (vpa.length % 2) == 0;

    for (int index = 0; index < vpa.length; index += 2) {
      final Float s = Float.valueOf(vpa[index + 0]);
      final Float t = Float.valueOf(vpa[index + 1]);
      model.texcoords.add(new VectorI2F(s.floatValue(), t.floatValue()));
    }

    log.debug("loaded "
      + model.texcoords.size()
      + " vertex texture coordinates");
  }

  private static void populateVertexArray(
    final @Nonnull XPathContext xpc,
    final @Nonnull ColladaObject model,
    final @Nonnull Element mesh,
    final @Nonnull Log log)
  {
    assert mesh.getLocalName().equals("mesh");

    final String[] vpa =
      BlenderCOLLADAImporter.getVertexPositionArrayFromMesh(xpc, mesh);

    assert vpa.length > 0;
    assert (vpa.length % 3) == 0;

    for (int index = 0; index < vpa.length; index += 3) {
      final Float x = Float.valueOf(vpa[index + 0]);
      final Float y = Float.valueOf(vpa[index + 1]);
      final Float z = Float.valueOf(vpa[index + 2]);
      model.positions.add(new VectorI3F(x.floatValue(), y.floatValue(), z
        .floatValue()));
    }

    log.debug("loaded " + model.positions.size() + " vertex positions");
  }

  private static @Nonnull ModelMaterial processEffectElementMaterial(
    final @Nonnull XPathContext xpc,
    final @Nonnull String material_name,
    final @Nonnull Element effect_elem)
    throws ConstraintError
  {
    assert effect_elem.getLocalName().equals("effect");

    final ModelMaterial m = new ModelMaterial(material_name);
    VectorM3F.copy(
      BlenderCOLLADAImporter.getEffectAmbient(xpc, effect_elem),
      m.ambient);
    VectorM3F.copy(
      BlenderCOLLADAImporter.getEffectDiffuse(xpc, effect_elem),
      m.diffuse);
    VectorM4F.copy(
      BlenderCOLLADAImporter.getEffectSpecular(xpc, effect_elem),
      m.specular);
    m.shininess = BlenderCOLLADAImporter.getEffectShininess(xpc, effect_elem);

    return m;
  }

  private static @Nonnull ColladaObject processGeometry(
    final @Nonnull XPathContext xpc,
    final @Nonnull Element geometry,
    final @Nonnull Log log)
    throws ConstraintError
  {
    assert geometry.getLocalName().equals("geometry");

    final Attribute name_attr = geometry.getAttribute("name");
    assert name_attr != null;
    final String name = name_attr.getValue();
    log.debug("Loaded model named '" + name + "'");

    final Element mesh =
      BlenderCOLLADAImporter.getMeshFromGeometry(xpc, geometry);
    return BlenderCOLLADAImporter.processMesh(xpc, name, mesh, log);
  }

  private static @Nonnull ColladaObject processMesh(
    final @Nonnull XPathContext xpc,
    final @Nonnull String name,
    final @Nonnull Element mesh,
    final @Nonnull Log log)
    throws ConstraintError
  {
    assert mesh.getLocalName().equals("mesh");

    final Element polylist =
      BlenderCOLLADAImporter.getPolylistFromMesh(xpc, mesh);

    final String material_address =
      BlenderCOLLADAImporter.getPolylistMaterialAddress(polylist);
    final ModelMaterial material =
      BlenderCOLLADAImporter.getMaterialWithId(xpc, mesh
        .getDocument()
        .getRootElement(), material_address);

    final ColladaVertexType vertex_type =
      BlenderCOLLADAImporter.getVertexType(xpc, polylist);
    final @Nonnull ColladaObject model =
      new ColladaObject(name, material, vertex_type);

    BlenderCOLLADAImporter.populateVertexArray(xpc, model, mesh, log);
    BlenderCOLLADAImporter.populateNormalArray(xpc, model, mesh, log);
    BlenderCOLLADAImporter.populateTexCoordArray(xpc, model, mesh, log);
    BlenderCOLLADAImporter.populatePolyArray(xpc, model, log, polylist);

    return model;
  }

  private BlenderCOLLADAImporter()
  {
    throw new UnreachableCodeException();
  }
}

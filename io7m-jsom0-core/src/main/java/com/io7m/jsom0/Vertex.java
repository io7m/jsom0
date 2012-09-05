package com.io7m.jsom0;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.ArrayBufferAttribute;
import com.io7m.jcanephora.ArrayBufferDescriptor;
import com.io7m.jcanephora.GLScalarType;

public final class Vertex
{
  private static final @Nonnull HashMap<String, VertexType> vertex_types      =
                                                                                new HashMap<String, VertexType>();

  private static final @Nonnull Set<String>                 vertex_type_names =
                                                                                new TreeSet<String>();

  static {
    try {
      Vertex.vertex_type_names.add(Vertex
        .vertexTypeName(VertexType.VERTEX_TYPE_P3N3));
      Vertex.vertex_type_names.add(Vertex
        .vertexTypeName(VertexType.VERTEX_TYPE_P3N3T2));

      Vertex.vertex_types.put(
        Vertex.vertexTypeName(VertexType.VERTEX_TYPE_P3N3),
        VertexType.VERTEX_TYPE_P3N3);
      Vertex.vertex_types.put(
        Vertex.vertexTypeName(VertexType.VERTEX_TYPE_P3N3T2),
        VertexType.VERTEX_TYPE_P3N3T2);
    } catch (final ConstraintError e) {
      throw new AssertionError(e.getMessage());
    }
  }

  public static VertexType lookupTypeByName(
    final @Nonnull String name)
    throws ConstraintError
  {
    return Vertex.vertex_types
      .get(Constraints.constrainNotNull(name, "name"));
  }

  public static boolean validTypeName(
    final @Nonnull String name)
    throws ConstraintError
  {
    return Vertex.vertex_type_names.contains(Constraints.constrainNotNull(
      name,
      "type name"));
  }

  public static ArrayBufferDescriptor vertexTypeDescriptor(
    final @Nonnull VertexType type)
    throws ConstraintError
  {
    switch (Constraints.constrainNotNull(type, "type")) {
      case VERTEX_TYPE_P3N3:
      {
        return new ArrayBufferDescriptor(new ArrayBufferAttribute[] {
          new ArrayBufferAttribute("position", GLScalarType.TYPE_FLOAT, 3),
          new ArrayBufferAttribute("normal", GLScalarType.TYPE_FLOAT, 3) });
      }
      case VERTEX_TYPE_P3N3T2:
      {
        return new ArrayBufferDescriptor(new ArrayBufferAttribute[] {
          new ArrayBufferAttribute("position", GLScalarType.TYPE_FLOAT, 3),
          new ArrayBufferAttribute("normal", GLScalarType.TYPE_FLOAT, 3),
          new ArrayBufferAttribute("uv", GLScalarType.TYPE_FLOAT, 2) });
      }
    }

    /* UNREACHABLE */
    throw new AssertionError("unreachable code: report this bug!");
  }

  public static @Nonnull String vertexTypeName(
    final @Nonnull VertexType type)
    throws ConstraintError
  {
    switch (Constraints.constrainNotNull(type, "type")) {
      case VERTEX_TYPE_P3N3:
        return "vertex_p3n3";
      case VERTEX_TYPE_P3N3T2:
        return "vertex_p3n3t2";
    }

    /* UNREACHABLE */
    throw new AssertionError("unreachable code: report this bug!");
  }

  public static Set<String> vertexTypeNames()
  {
    return Collections.unmodifiableSet(Vertex.vertex_type_names);
  }
}

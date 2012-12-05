package com.io7m.jsom0;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jcanephora.ArrayBufferAttribute;
import com.io7m.jcanephora.ArrayBufferDescriptor;
import com.io7m.jcanephora.GLScalarType;

public final class VertexTypeInformation
{
  private static final @Nonnull HashMap<String, VertexType> vertex_types;
  private static final @Nonnull Set<String>                 vertex_type_names;

  static {
    try {
      vertex_type_names = new TreeSet<String>();
      VertexTypeInformation.vertex_type_names.add(VertexTypeInformation
        .vertexTypeName(VertexType.VERTEX_TYPE_P3N3));
      VertexTypeInformation.vertex_type_names.add(VertexTypeInformation
        .vertexTypeName(VertexType.VERTEX_TYPE_P3N3T2));

      vertex_types = new HashMap<String, VertexType>();
      VertexTypeInformation.vertex_types.put(
        VertexTypeInformation.vertexTypeName(VertexType.VERTEX_TYPE_P3N3),
        VertexType.VERTEX_TYPE_P3N3);
      VertexTypeInformation.vertex_types.put(
        VertexTypeInformation.vertexTypeName(VertexType.VERTEX_TYPE_P3N3T2),
        VertexType.VERTEX_TYPE_P3N3T2);
    } catch (final ConstraintError e) {
      throw new AssertionError(e.getMessage());
    }
  }

  public static VertexType lookupTypeByName(
    final @Nonnull String name)
    throws ConstraintError
  {
    return VertexTypeInformation.vertex_types.get(Constraints
      .constrainNotNull(name, "name"));
  }

  public static boolean validTypeName(
    final @Nonnull String name)
    throws ConstraintError
  {
    return VertexTypeInformation.vertex_type_names.contains(Constraints
      .constrainNotNull(name, "type name"));
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

    throw new UnreachableCodeException();
  }

  public static Set<String> vertexTypeNames()
  {
    return Collections
      .unmodifiableSet(VertexTypeInformation.vertex_type_names);
  }

  public static final @Nonnull ArrayBufferDescriptor VERTEX_P3N3_TYPE;
  public static final @Nonnull ArrayBufferDescriptor VERTEX_P3N3T2_TYPE;

  static {
    try {
      final ArrayBufferAttribute[] a = new ArrayBufferAttribute[2];
      a[0] =
        new ArrayBufferAttribute(
          "vertex_position",
          GLScalarType.TYPE_FLOAT,
          3);
      a[1] =
        new ArrayBufferAttribute("vertex_normal", GLScalarType.TYPE_FLOAT, 3);
      VERTEX_P3N3_TYPE = new ArrayBufferDescriptor(a);
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException();
    }

    try {
      final ArrayBufferAttribute[] a = new ArrayBufferAttribute[3];
      a[0] =
        new ArrayBufferAttribute(
          "vertex_position",
          GLScalarType.TYPE_FLOAT,
          3);
      a[1] =
        new ArrayBufferAttribute("vertex_normal", GLScalarType.TYPE_FLOAT, 3);
      a[2] =
        new ArrayBufferAttribute("vertex_uv", GLScalarType.TYPE_FLOAT, 2);
      VERTEX_P3N3T2_TYPE = new ArrayBufferDescriptor(a);
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException();
    }
  }

  public static @Nonnull ArrayBufferDescriptor typeDescriptor(
    final @Nonnull VertexType type)
  {
    switch (type) {
      case VERTEX_TYPE_P3N3:
      {
        return VertexTypeInformation.VERTEX_P3N3_TYPE;
      }
      case VERTEX_TYPE_P3N3T2:
      {
        return VertexTypeInformation.VERTEX_P3N3T2_TYPE;
      }
    }

    throw new UnreachableCodeException();
  }
}

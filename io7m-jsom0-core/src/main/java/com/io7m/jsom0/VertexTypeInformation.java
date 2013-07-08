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

package com.io7m.jsom0;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

public final class VertexTypeInformation
{
  private static final @Nonnull EnumMap<VertexType, String> vertex_types_to_names;
  private static final @Nonnull Map<String, VertexType>     vertex_names_to_types;

  static {
    vertex_names_to_types = new HashMap<String, VertexType>();
    vertex_types_to_names = new EnumMap<VertexType, String>(VertexType.class);
    VertexTypeInformation.add(VertexType.VERTEX_TYPE_P3N3, "vertex_p3n3");
    VertexTypeInformation.add(VertexType.VERTEX_TYPE_P3N3T2, "vertex_p3n3t2");
  }

  private static void add(
    final @Nonnull VertexType type,
    final @Nonnull String name)
  {
    VertexTypeInformation.vertex_names_to_types.put(name, type);
    VertexTypeInformation.vertex_types_to_names.put(type, name);
  }

  public static @Nonnull VertexType lookupTypeByName(
    final @Nonnull String name)
    throws ConstraintError
  {
    Constraints.constrainArbitrary(
      VertexTypeInformation.validTypeName(name),
      "Type name is valid");
    return VertexTypeInformation.vertex_names_to_types.get(name);
  }

  public static boolean validTypeName(
    final @Nonnull String name)
    throws ConstraintError
  {
    return VertexTypeInformation.vertex_names_to_types
      .containsKey(Constraints.constrainNotNull(name, "type name"));
  }

  public static @Nonnull String vertexTypeName(
    final @Nonnull VertexType type)
    throws ConstraintError
  {
    return VertexTypeInformation.vertex_types_to_names.get(Constraints
      .constrainNotNull(type, "Vertex type"));
  }

  public static Set<String> vertexTypeNames()
  {
    return Collections
      .unmodifiableSet(VertexTypeInformation.vertex_names_to_types.keySet());
  }
}

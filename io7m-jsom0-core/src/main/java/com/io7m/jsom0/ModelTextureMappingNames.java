package com.io7m.jsom0;

import java.util.HashMap;
import java.util.Set;

import javax.annotation.Nonnull;

import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Indeterminate;

public final class ModelTextureMappingNames
{
  private static final @Nonnull HashMap<String, ModelTextureMapping> names;

  static {
    names = ModelTextureMappingNames.makeNames();
  }

  private static final @Nonnull String failMessage(
    final @Nonnull String name)
  {
    final StringBuilder b = new StringBuilder();
    b.append("expected one of {");

    final Set<String> keys = ModelTextureMappingNames.names.keySet();
    final int size = keys.size();
    int index = 0;

    for (final String key_name : keys) {
      b.append("'");
      b.append(key_name);
      b.append("'");
      if ((index + 1) < size) {
        b.append(" | ");
      }
      ++index;
    }

    b.append("} but got '");
    b.append(name);
    b.append("'");

    return b.toString();
  }

  private static final @Nonnull
    HashMap<String, ModelTextureMapping>
    makeNames()
  {
    final HashMap<String, ModelTextureMapping> m =
      new HashMap<String, ModelTextureMapping>();
    m.put("map_uv", ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);
    m.put("map_chrome", ModelTextureMapping.MODEL_TEXTURE_MAPPING_CHROME);
    return m;
  }

  public static @Nonnull String name(
    final @Nonnull ModelTextureMapping mapping)
  {
    switch (mapping) {
      case MODEL_TEXTURE_MAPPING_CHROME:
        return "map_chrome";
      case MODEL_TEXTURE_MAPPING_UV:
        return "map_uv";
    }

    throw new UnreachableCodeException();
  }

  public static @Nonnull Indeterminate<ModelTextureMapping, String> value(
    final @Nonnull String name)
  {
    if (ModelTextureMappingNames.names.containsKey(name)) {
      return new Indeterminate.Success<ModelTextureMapping, String>(
        ModelTextureMappingNames.names.get(name));
    }
    return new Indeterminate.Failure<ModelTextureMapping, String>(
      ModelTextureMappingNames.failMessage(name));
  }

  private ModelTextureMappingNames()
  {
    throw new UnreachableCodeException();
  }
}

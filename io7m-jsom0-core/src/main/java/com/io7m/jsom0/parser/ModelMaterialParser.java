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

package com.io7m.jsom0.parser;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Indeterminate;
import com.io7m.jaux.functional.Indeterminate.Failure;
import com.io7m.jaux.functional.Indeterminate.Success;
import com.io7m.jlog.Log;
import com.io7m.jsom0.ModelMaterial;
import com.io7m.jsom0.ModelTexture;
import com.io7m.jsom0.ModelTextureMapping;
import com.io7m.jsom0.ModelTextureMappingNames;
import com.io7m.jtensors.VectorM3F;
import com.io7m.jtensors.VectorM4F;

public final class ModelMaterialParser
{
  private final @Nonnull ModelObjectLexer lexer;
  private final @Nonnull Log              log;
  private final @Nonnull String           file_name;
  private @CheckForNull ModelObjectToken  token;

  public ModelMaterialParser(
    final @Nonnull String file_name,
    final @Nonnull InputStream in,
    final @Nonnull Log log)
    throws ConstraintError,
      IOException,
      Error
  {
    this.log =
      new Log(
        Constraints.constrainNotNull(log, "log output"),
        "material-parser");
    this.lexer =
      new ModelObjectLexer(Constraints.constrainNotNull(in, "input stream"));
    this.file_name = Constraints.constrainNotNull(file_name, "file name");
    this.token = this.lexer.token();

    this.log.debug("created");
  }

  private void consume(
    final @Nonnull ModelObjectTokenType type)
    throws IOException,
      Error
  {
    assert (this.token != null);

    if (this.token.type != type) {
      throw Error.unexpectedToken(this.token, type);
    }

    this.token = this.lexer.token();
  }

  private void consumeSymbol(
    final @Nonnull String symbol)
    throws IOException,
      Error
  {
    assert this.token != null;
    assert symbol != null;

    if (this.token.type != ModelObjectTokenType.OBJECT_TOKEN_SYMBOL) {
      throw Error.expectedSymbol(this.token);
    }
    if (this.token.value.equals(symbol) == false) {
      throw Error.unexpectedSymbol(this.token, symbol);
    }

    this.token = this.lexer.token();
  }

  private float materialAlpha()
    throws IOException,
      Error
  {
    this.consumeSymbol("alpha");
    final float value = Float.parseFloat(this.token.value);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_FLOAT);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    return value;
  }

  private @Nonnull VectorM3F materialAmbient()
    throws IOException,
      Error
  {
    this.consumeSymbol("ambient");
    final VectorM3F v = this.vector3f();
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    return v;
  }

  private @Nonnull VectorM3F materialDiffuse()
    throws IOException,
      Error
  {
    this.consumeSymbol("diffuse");
    final VectorM3F v = this.vector3f();
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    return v;
  }

  private @Nonnull String materialName()
    throws IOException,
      Error
  {
    this.consumeSymbol("name");
    final String value = this.token.value;
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_STRING);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    return value;
  }

  private float materialShininess()
    throws IOException,
      Error
  {
    this.consumeSymbol("shininess");
    final float value = Float.parseFloat(this.token.value);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_FLOAT);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    return value;
  }

  private @Nonnull VectorM4F materialSpecular()
    throws IOException,
      Error
  {
    this.consumeSymbol("specular");
    final VectorM4F v = this.vector4f();
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    return v;
  }

  private ModelTexture materialTexture()
    throws IOException,
      Error,
      ConstraintError
  {
    this.consumeSymbol("texture");
    final String name = this.token.value;
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_STRING);
    final float alpha = Float.parseFloat(this.token.value);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_FLOAT);
    final ModelTextureMapping mapping = this.materialTextureMapping();
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    final ModelTexture texture = new ModelTexture(name, mapping);
    texture.alpha = alpha;
    return texture;
  }

  private @Nonnull ModelTextureMapping materialTextureMapping()
    throws IOException,
      Error
  {
    final Indeterminate<ModelTextureMapping, String> result =
      ModelTextureMappingNames.value(this.token.value);

    switch (result.type) {
      case FAILURE:
      {
        final Failure<ModelTextureMapping, String> f =
          (Failure<ModelTextureMapping, String>) result;
        throw Error.unexpectedTokenAlt(this.token.position, f.value);
      }
      case SUCCESS:
      {
        final Success<ModelTextureMapping, String> s =
          (Success<ModelTextureMapping, String>) result;
        this.consume(ModelObjectTokenType.OBJECT_TOKEN_SYMBOL);
        return s.value;
      }
    }

    throw new UnreachableCodeException();
  }

  public @Nonnull ModelMaterial modelMaterial()
    throws IOException,
      Error,
      ConstraintError
  {
    assert this.log != null;
    assert this.file_name != null;

    this.consumeSymbol("material");
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    String name = null;
    VectorM3F diffuse = null;
    VectorM3F ambient = null;
    VectorM4F specular = null;
    Float shininess = null;
    Float alpha = null;
    ModelTexture texture = null;

    for (;;) {
      if (this.token.value.equals("name")) {
        if (name == null) {
          name = this.materialName();
        } else {
          throw Error.duplicateElement("name");
        }
      } else if (this.token.value.equals("diffuse")) {
        if (diffuse == null) {
          diffuse = this.materialDiffuse();
        } else {
          throw Error.duplicateElement("diffuse");
        }
      } else if (this.token.value.equals("ambient")) {
        if (ambient == null) {
          ambient = this.materialAmbient();
        } else {
          throw Error.duplicateElement("ambient");
        }
      } else if (this.token.value.equals("specular")) {
        if (specular == null) {
          specular = this.materialSpecular();
        } else {
          throw Error.duplicateElement("specular");
        }
      } else if (this.token.value.equals("shininess")) {
        if (shininess == null) {
          shininess = Float.valueOf(this.materialShininess());
        } else {
          throw Error.duplicateElement("shininess");
        }
      } else if (this.token.value.equals("alpha")) {
        if (alpha == null) {
          alpha = Float.valueOf(this.materialAlpha());
        } else {
          throw Error.duplicateElement("alpha");
        }
      } else if (this.token.value.equals("texture")) {
        if (texture == null) {
          texture = this.materialTexture();
        } else {
          throw Error.duplicateElement("texture");
        }
      } else if (this.token.value.equals("end")) {
        this.consumeSymbol("end");
        this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
        break;
      }
    }

    if (alpha == null) {
      throw Error.missingElement("alpha");
    }
    if (diffuse == null) {
      throw Error.missingElement("diffuse");
    }
    if (ambient == null) {
      throw Error.missingElement("ambient");
    }
    if (specular == null) {
      throw Error.missingElement("specular");
    }
    if (shininess == null) {
      throw Error.missingElement("shininess");
    }
    if (name == null) {
      throw Error.missingElement("name");
    }

    if (texture != null) {
      final ModelMaterial material = new ModelMaterial(name, texture);
      VectorM3F.copy(diffuse, material.diffuse);
      VectorM3F.copy(ambient, material.ambient);
      VectorM4F.copy(specular, material.specular);
      material.shininess = shininess.floatValue();
      material.alpha = alpha.floatValue();
      return material;
    }

    final ModelMaterial material = new ModelMaterial(name);
    VectorM3F.copy(diffuse, material.diffuse);
    VectorM3F.copy(ambient, material.ambient);
    VectorM4F.copy(specular, material.specular);
    material.shininess = shininess.floatValue();
    material.alpha = alpha.floatValue();
    return material;

  }

  private @Nonnull VectorM3F vector3f()
    throws IOException,
      Error
  {
    final VectorM3F v = new VectorM3F();

    v.x = Float.parseFloat(this.token.value);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_FLOAT);
    v.y = Float.parseFloat(this.token.value);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_FLOAT);
    v.z = Float.parseFloat(this.token.value);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_FLOAT);

    return v;
  }

  private @Nonnull VectorM4F vector4f()
    throws IOException,
      Error
  {
    final VectorM4F v = new VectorM4F();

    v.x = Float.parseFloat(this.token.value);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_FLOAT);
    v.y = Float.parseFloat(this.token.value);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_FLOAT);
    v.z = Float.parseFloat(this.token.value);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_FLOAT);
    v.w = Float.parseFloat(this.token.value);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_FLOAT);

    return v;
  }
}

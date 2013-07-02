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

package com.io7m.jsom0.parser.contracts;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jaux.functional.Option.Type;
import com.io7m.jsom0.ModelMaterial;
import com.io7m.jsom0.ModelTexture;
import com.io7m.jsom0.ModelTextureMapping;
import com.io7m.jsom0.parser.Error;
import com.io7m.jsom0.parser.ModelMaterialParser;
import com.io7m.jsom0.parser.ModelObjectTokenType;

public abstract class ModelMaterialParserTestContract implements
  ModelMaterialParserContract
{
  @Override @SuppressWarnings("resource") public
    ModelMaterialParser
    getParser(
      final String file)
      throws IOException,
        Error,
        ConstraintError
  {
    final InputStream fstream =
      ModelMaterialParserTestContract.class.getResourceAsStream(file);
    final ModelMaterialParser parser =
      new ModelMaterialParser(file, fstream, this.getLog());
    return parser;
  }

  @Test(expected = Error.class) public void testInvalidDuplicateAlpha()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/inv-duplicate-alpha.i7m");

    try {
      parser.modelMaterial();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_DUPLICATE_ELEMENT,
        e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidDuplicateAmbient()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/inv-duplicate-ambient.i7m");

    try {
      parser.modelMaterial();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_DUPLICATE_ELEMENT,
        e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidDuplicateDiffuse()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/inv-duplicate-diffuse.i7m");

    try {
      parser.modelMaterial();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_DUPLICATE_ELEMENT,
        e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidDuplicateName()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/inv-duplicate-name.i7m");

    try {
      parser.modelMaterial();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_DUPLICATE_ELEMENT,
        e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidDuplicateShininess()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/inv-duplicate-shininess.i7m");

    try {
      parser.modelMaterial();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_DUPLICATE_ELEMENT,
        e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidDuplicateSpecular()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/inv-duplicate-specular.i7m");

    try {
      parser.modelMaterial();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_DUPLICATE_ELEMENT,
        e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidDuplicateTexture()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/inv-duplicate-texture.i7m");

    try {
      parser.modelMaterial();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_DUPLICATE_ELEMENT,
        e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidEmpty()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/inv-empty.i7m");

    try {
      parser.modelMaterial();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_UNEXPECTED_TOKEN,
        e.errorCode());
      Assert.assertEquals(
        ModelObjectTokenType.OBJECT_TOKEN_SYMBOL,
        e.expectedTokenType());
      Assert.assertEquals(
        ModelObjectTokenType.OBJECT_TOKEN_EOF,
        e.receivedTokenType());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidMissingAlpha()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/inv-missing-alpha.i7m");

    try {
      parser.modelMaterial();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_MISSING_ELEMENT,
        e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidMissingAmbient()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/inv-missing-ambient.i7m");

    try {
      parser.modelMaterial();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_MISSING_ELEMENT,
        e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidMissingDiffuse()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/inv-missing-diffuse.i7m");

    try {
      parser.modelMaterial();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_MISSING_ELEMENT,
        e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidMissingName()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/inv-missing-name.i7m");

    try {
      parser.modelMaterial();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_MISSING_ELEMENT,
        e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidMissingShininess()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/inv-missing-shininess.i7m");

    try {
      parser.modelMaterial();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_MISSING_ELEMENT,
        e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidMissingSpecular()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/inv-missing-specular.i7m");

    try {
      parser.modelMaterial();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_MISSING_ELEMENT,
        e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidSemi()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/inv-semi.i7m");

    try {
      parser.modelMaterial();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_UNEXPECTED_TOKEN,
        e.errorCode());
      Assert.assertEquals(
        ModelObjectTokenType.OBJECT_TOKEN_SYMBOL,
        e.expectedTokenType());
      Assert.assertEquals(
        ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON,
        e.receivedTokenType());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidTextureBadMap()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/inv-texture-bad-map.i7m");

    try {
      parser.modelMaterial();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_UNEXPECTED_SYMBOL,
        e.errorCode());
      throw e;
    }
  }

  @Test public void testValidSimple0()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/val-simple0.i7m");
    final ModelMaterial m = parser.modelMaterial();

    Assert.assertTrue(m.name.equals("name"));

    Assert.assertTrue(m.diffuse.x == 0.1f);
    Assert.assertTrue(m.diffuse.y == 0.2f);
    Assert.assertTrue(m.diffuse.z == 0.3f);

    Assert.assertTrue(m.ambient.x == 0.5f);
    Assert.assertTrue(m.ambient.y == 0.6f);
    Assert.assertTrue(m.ambient.z == 0.7f);

    Assert.assertTrue(m.specular.x == 0.01f);
    Assert.assertTrue(m.specular.y == 0.02f);
    Assert.assertTrue(m.specular.z == 0.03f);
    Assert.assertTrue(m.specular.w == 1.0f);

    Assert.assertTrue(m.shininess == 256.0f);

    Assert.assertTrue(m.texture.type == Type.OPTION_NONE);
  }

  @Test public void testValidSimple1()
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelMaterialParser parser =
      this.getParser("/com/io7m/jsom0/val-simple1.i7m");
    final ModelMaterial m = parser.modelMaterial();

    Assert.assertTrue(m.name.equals("name"));

    Assert.assertTrue(m.diffuse.x == 0.1f);
    Assert.assertTrue(m.diffuse.y == 0.2f);
    Assert.assertTrue(m.diffuse.z == 0.3f);

    Assert.assertTrue(m.ambient.x == 0.5f);
    Assert.assertTrue(m.ambient.y == 0.6f);
    Assert.assertTrue(m.ambient.z == 0.7f);

    Assert.assertTrue(m.specular.x == 0.01f);
    Assert.assertTrue(m.specular.y == 0.02f);
    Assert.assertTrue(m.specular.z == 0.03f);

    Assert.assertTrue(m.shininess == 256.0f);

    Assert.assertTrue(m.texture.type == Type.OPTION_SOME);
    final Option.Some<ModelTexture> t = (Some<ModelTexture>) m.texture;
    Assert.assertTrue(t.value.name.equals("texture"));
    Assert.assertTrue(t.value.alpha == 1.0f);
    Assert
      .assertTrue(t.value.mapping == ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);
  }
}

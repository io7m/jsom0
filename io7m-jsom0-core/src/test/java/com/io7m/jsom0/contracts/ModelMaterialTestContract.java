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

package com.io7m.jsom0.contracts;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jsom0.ModelMaterial;
import com.io7m.jsom0.ModelTexture;
import com.io7m.jsom0.ModelTextureMapping;

public abstract class ModelMaterialTestContract implements
  JSOM0GLUnmappedTestContract
{
  /**
   * toString() is not dependent on reference equality.
   */

  @SuppressWarnings("static-method") @Test public
    void
    testTexturedToStringNotSame()
      throws ConstraintError
  {
    final ModelTexture t0 =
      new ModelTexture(
        "texture0",
        ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);
    final ModelTexture t1 =
      new ModelTexture(
        "texture1",
        ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);

    final ModelMaterial m0 = new ModelMaterial("material", t0);
    final ModelMaterial m1 = new ModelMaterial("material", t1);

    Assert.assertFalse(m0.toString().equals(m1.toString()));
  }

  /**
   * toString() is not dependent on reference equality.
   */

  @SuppressWarnings("static-method") @Test public
    void
    testTexturedToStringSame()
      throws ConstraintError
  {
    final ModelTexture t =
      new ModelTexture(
        "texture",
        ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);
    final ModelMaterial m0 = new ModelMaterial("material", t);
    final ModelMaterial m1 = new ModelMaterial("material", t);
    Assert.assertEquals(m1.toString(), m0.toString());
  }

  /**
   * toString() is not dependent on reference equality.
   */

  @SuppressWarnings("static-method") @Test public void testToStringSame()
    throws ConstraintError
  {
    final ModelMaterial m0 = new ModelMaterial("material");
    final ModelMaterial m1 = new ModelMaterial("material");
    Assert.assertEquals(m1.toString(), m0.toString());
  }
}

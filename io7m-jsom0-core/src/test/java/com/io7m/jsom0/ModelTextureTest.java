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

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;

public class ModelTextureTest
{
  @SuppressWarnings({ "static-method" }) @Test public void testIdentity()
    throws ConstraintError
  {
    final ModelTexture t =
      new ModelTexture("name", ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);

    Assert.assertEquals(
      ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV,
      t.getMapping());
    Assert.assertEquals(1.0f, t.getAlpha(), 0.00001f);
    Assert.assertEquals("name", t.getName());
  }

  @SuppressWarnings({ "unused", "static-method" }) @Test(
    expected = ConstraintError.class) public void testMappingNull()
    throws ConstraintError
  {
    new ModelTexture("name", null);
  }

  @SuppressWarnings({ "unused", "static-method" }) @Test(
    expected = ConstraintError.class) public void testNameNull()
    throws ConstraintError
  {
    new ModelTexture(null, ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);
  }

  @SuppressWarnings("static-method") @Test(expected = ConstraintError.class) public
    void
    testSetAlphaHigh()
      throws ConstraintError
  {
    final ModelTexture t =
      new ModelTexture("name", ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);
    t.setAlpha(2.0f);
  }

  @SuppressWarnings({ "static-method" }) @Test public
    void
    testSetAlphaIdentity()
      throws ConstraintError
  {
    final ModelTexture t =
      new ModelTexture("name", ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);
    t.setAlpha(0.5f);
    Assert.assertEquals(0.5f, t.getAlpha(), 0.0001f);
  }

  @SuppressWarnings("static-method") @Test(expected = ConstraintError.class) public
    void
    testSetAlphaLow()
      throws ConstraintError
  {
    final ModelTexture t =
      new ModelTexture("name", ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);
    t.setAlpha(-1.0f);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testToStringInequivalence()
      throws ConstraintError
  {
    final ModelTexture t0 =
      new ModelTexture("name0", ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);
    final ModelTexture t1 =
      new ModelTexture(
        "name0",
        ModelTextureMapping.MODEL_TEXTURE_MAPPING_CHROME);
    final ModelTexture t2 =
      new ModelTexture("name1", ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);
    final ModelTexture t3 =
      new ModelTexture(
        "name1",
        ModelTextureMapping.MODEL_TEXTURE_MAPPING_CHROME);

    Assert.assertFalse(t0.toString().equals(t1.toString()));
    Assert.assertFalse(t0.toString().equals(t2.toString()));
    Assert.assertFalse(t0.toString().equals(t3.toString()));
  }
}

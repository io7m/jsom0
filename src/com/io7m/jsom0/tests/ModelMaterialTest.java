package com.io7m.jsom0.tests;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jsom0.ModelMaterial;
import com.io7m.jsom0.ModelTexture;
import com.io7m.jsom0.ModelTextureMapping;

public class ModelMaterialTest
{
  /**
   * toString() is not dependent on reference equality.
   */

  @Test public void testTexturedToStringNotSame()
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

  @Test public void testTexturedToStringSame()
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

  @Test public void testToStringSame()
    throws ConstraintError
  {
    final ModelMaterial m0 = new ModelMaterial("material");
    final ModelMaterial m1 = new ModelMaterial("material");
    Assert.assertEquals(m1.toString(), m0.toString());
  }
}

package com.io7m.jsom0.tests;

import junit.framework.Assert;

import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jsom0.ModelTexture;
import com.io7m.jsom0.ModelTextureMapping;

public class ModelTextureTest
{
  @SuppressWarnings("boxing") @Test public void testIdentity()
    throws ConstraintError
  {
    final ModelTexture t =
      new ModelTexture("name", ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);

    Assert.assertEquals(
      ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV,
      t.getMapping());
    Assert.assertEquals(1.0f, t.getAlpha());
    Assert.assertEquals("name", t.getName());
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testMappingNull()
      throws ConstraintError
  {
    new ModelTexture("name", null);
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testNameNull()
      throws ConstraintError
  {
    new ModelTexture(null, ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);
  }

  @Test(expected = ConstraintError.class) public void testSetAlphaHigh()
    throws ConstraintError
  {
    final ModelTexture t =
      new ModelTexture("name", ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);
    t.setAlpha(2.0f);
  }

  @SuppressWarnings("boxing") @Test public void testSetAlphaIdentity()
    throws ConstraintError
  {
    final ModelTexture t =
      new ModelTexture("name", ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);
    t.setAlpha(0.5f);
    Assert.assertEquals(0.5f, t.getAlpha());
  }

  @Test(expected = ConstraintError.class) public void testSetAlphaLow()
    throws ConstraintError
  {
    final ModelTexture t =
      new ModelTexture("name", ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);
    t.setAlpha(-1.0f);
  }

  @Test public void testToStringInequivalence()
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

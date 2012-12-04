package com.io7m.jsom0;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jtensors.VectorI3F;

public class TestModelObjectTest
{
  @SuppressWarnings("static-method") @Test public void testCompare()
    throws ConstraintError
  {
    final TestModelObject o0 =
      new TestModelObject("A", "M", VectorI3F.ZERO, VectorI3F.ZERO);
    final TestModelObject o0x =
      new TestModelObject("A", "M", VectorI3F.ZERO, VectorI3F.ZERO);
    final TestModelObject o1 =
      new TestModelObject("B", "M", VectorI3F.ZERO, VectorI3F.ZERO);
    final TestModelObject o2 =
      new TestModelObject("C", "M", VectorI3F.ZERO, VectorI3F.ZERO);

    // Reflexive
    Assert.assertTrue(o0.compareTo(o0) == 0);

    // Symmetric
    Assert.assertTrue(o0.compareTo(o0x) == 0);
    Assert.assertTrue(o0x.compareTo(o0) == 0);

    // Ordered, transitive
    Assert.assertTrue(o0.compareTo(o1) < 0);
    Assert.assertTrue(o1.compareTo(o2) < 0);
    Assert.assertTrue(o0.compareTo(o2) < 0);
    Assert.assertTrue(o1.compareTo(o0) > 0);
    Assert.assertTrue(o2.compareTo(o1) > 0);
    Assert.assertTrue(o2.compareTo(o0) > 0);
  }
}

package com.io7m.jsom0;

import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jtensors.VectorI3F;

public class ModelTest
{
  @SuppressWarnings("static-method") @Test(expected = ConstraintError.class) public
    void
    testModelAddTwiceFails()
      throws ConstraintError
  {
    final Model<TestModelObject> m = new Model<TestModelObject>();
    final TestModelObject o =
      new TestModelObject("name", "material", VectorI3F.ZERO, VectorI3F.ZERO);

    m.addObject(o);
    m.addObject(o);
  }
}

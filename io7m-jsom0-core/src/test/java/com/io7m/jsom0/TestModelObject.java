package com.io7m.jsom0;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jsom0.ModelObject;
import com.io7m.jtensors.VectorReadable3F;

public final class TestModelObject extends ModelObject
{
  TestModelObject(
    final @Nonnull String name,
    final @Nonnull String material_name,
    final @Nonnull VectorReadable3F bound_lower,
    final @Nonnull VectorReadable3F bound_upper)
    throws ConstraintError
  {
    super(name, material_name, bound_lower, bound_upper);
  }
}

package com.io7m.jsom0.demos;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterfaceEmbedded;
import com.io7m.jtensors.MatrixReadable3x3F;
import com.io7m.jtensors.MatrixReadable4x4F;

interface MatrixProgram
{
  public void putModelviewMatrix(
    final @Nonnull GLInterfaceEmbedded gl,
    final @Nonnull MatrixReadable4x4F m)
    throws GLException,
      ConstraintError;

  public void putNormalMatrix(
    final @Nonnull GLInterfaceEmbedded gl,
    final @Nonnull MatrixReadable3x3F m)
    throws GLException,
      ConstraintError;

  public void putProjectionMatrix(
    final @Nonnull GLInterfaceEmbedded gl,
    final @Nonnull MatrixReadable4x4F m)
    throws GLException,
      ConstraintError;
}

package com.io7m.jsom0.demos;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLShaders;
import com.io7m.jtensors.MatrixReadable3x3F;
import com.io7m.jtensors.MatrixReadable4x4F;

interface MatrixProgram
{
  public void putModelMatrix(
    final @Nonnull GLShaders gl,
    final @Nonnull MatrixReadable4x4F m)
    throws GLException,
      ConstraintError;

  public void putNormalMatrix(
    final @Nonnull GLShaders gl,
    final @Nonnull MatrixReadable3x3F m)
    throws GLException,
      ConstraintError;

  public void putProjectionMatrix(
    final @Nonnull GLShaders gl,
    final @Nonnull MatrixReadable4x4F m)
    throws GLException,
      ConstraintError;

  public void putViewMatrix(
    final @Nonnull GLShaders gl,
    final @Nonnull MatrixReadable4x4F m)
    throws GLException,
      ConstraintError;
}

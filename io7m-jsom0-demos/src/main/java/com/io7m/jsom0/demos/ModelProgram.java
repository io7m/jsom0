package com.io7m.jsom0.demos;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.CompilableProgram;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLShaders;
import com.io7m.jcanephora.ProgramAttribute;
import com.io7m.jcanephora.UsableProgram;

public abstract class ModelProgram implements
  UsableProgram,
  CompilableProgram,
  MatrixProgram,
  PhongProgram
{
  abstract @Nonnull ProgramAttribute getPositionAttribute()
    throws ConstraintError;

  abstract @Nonnull ProgramAttribute getNormalAttribute()
    throws ConstraintError;

  abstract void putAlpha(
    final @Nonnull GLShaders gl,
    final float alpha)
    throws ConstraintError,
      GLException;
}

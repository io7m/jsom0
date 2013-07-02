package com.io7m.jsom0.demos;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLShaders;
import com.io7m.jtensors.VectorReadable3F;
import com.io7m.jtensors.VectorReadable4F;

public interface PhongProgram
{
  public void putAmbient(
    final @Nonnull GLShaders gl,
    final @Nonnull VectorReadable3F ambient)
    throws GLException,
      ConstraintError;

  public void putDiffuse(
    final @Nonnull GLShaders gl,
    final @Nonnull VectorReadable3F diffuse)
    throws GLException,
      ConstraintError;

  public void putLightColor(
    final @Nonnull GLShaders gl,
    final @Nonnull VectorReadable3F rgb)
    throws GLException,
      ConstraintError;

  public void putLightPosition(
    final @Nonnull GLShaders gl,
    final @Nonnull VectorReadable3F position)
    throws GLException,
      ConstraintError;

  public void putLightPower(
    final @Nonnull GLShaders gl,
    final float power)
    throws GLException,
      ConstraintError;

  public void putShininess(
    final @Nonnull GLShaders gl,
    final float shininess)
    throws GLException,
      ConstraintError;

  public void putSpecular(
    final @Nonnull GLShaders gl,
    final @Nonnull VectorReadable4F specular)
    throws GLException,
      ConstraintError;
}

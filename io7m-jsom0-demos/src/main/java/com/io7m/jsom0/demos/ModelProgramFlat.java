package com.io7m.jsom0.demos;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLCompileException;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterfaceEmbedded;
import com.io7m.jcanephora.Program;
import com.io7m.jcanephora.ProgramAttribute;
import com.io7m.jcanephora.ProgramUniform;
import com.io7m.jlog.Log;
import com.io7m.jtensors.MatrixReadable3x3F;
import com.io7m.jtensors.MatrixReadable4x4F;
import com.io7m.jtensors.VectorReadable3F;
import com.io7m.jtensors.VectorReadable4F;
import com.io7m.jvvfs.FilesystemAPI;
import com.io7m.jvvfs.FilesystemError;
import com.io7m.jvvfs.PathVirtual;

public final class ModelProgramFlat extends ModelProgram
{
  private final Program program;

  public ModelProgramFlat(
    final @Nonnull Log log)
    throws ConstraintError
  {
    this.program = new Program("flat", log);
  }

  @Override public void activate(
    final @Nonnull GLInterfaceEmbedded gl)
    throws ConstraintError,
      GLException
  {
    this.program.activate(gl);
  }

  @Override public void addFragmentShader(
    final @Nonnull PathVirtual path)
    throws ConstraintError
  {
    this.program.addFragmentShader(path);
  }

  @Override public void addVertexShader(
    final @Nonnull PathVirtual path)
    throws ConstraintError
  {
    this.program.addVertexShader(path);
  }

  @Override public void compile(
    final @Nonnull FilesystemAPI fs,
    final @Nonnull GLInterfaceEmbedded gl)
    throws ConstraintError,
      GLCompileException
  {
    this.program.compile(fs, gl);
  }

  @Override public void deactivate(
    final @Nonnull GLInterfaceEmbedded gl)
    throws ConstraintError,
      GLException
  {
    this.program.deactivate(gl);
  }

  @Override public @Nonnull ProgramAttribute getNormalAttribute()
    throws ConstraintError
  {
    final ProgramAttribute pn = this.program.getAttribute("vertex_normal");
    Constraints.constrainNotNull(pn, "Attribute");
    return pn;
  }

  @Override public @Nonnull ProgramAttribute getPositionAttribute()
    throws ConstraintError
  {
    final ProgramAttribute pa = this.program.getAttribute("vertex_position");
    Constraints.constrainNotNull(pa, "Attribute");
    return pa;
  }

  @Override void putAlpha(
    final @Nonnull GLInterfaceEmbedded gl,
    final float alpha)
    throws ConstraintError,
      GLException
  {
    final ProgramUniform u = this.program.getUniform("alpha");
    Constraints.constrainNotNull(u, "Uniform");
    gl.programPutUniformFloat(u, alpha);
  }

  @Override public void putAmbient(
    final @Nonnull GLInterfaceEmbedded gl,
    final @Nonnull VectorReadable3F ambient)
    throws GLException,
      ConstraintError
  {
    final ProgramUniform u = this.program.getUniform("material_ambient");
    Constraints.constrainNotNull(u, "Uniform");
    gl.programPutUniformVector3f(u, ambient);
  }

  public void putColor(
    final @Nonnull GLInterfaceEmbedded gl,
    final @Nonnull VectorReadable4F color)
    throws ConstraintError,
      GLException
  {
    final ProgramUniform u = this.program.getUniform("color");
    Constraints.constrainNotNull(u, "Uniform");
    gl.programPutUniformVector4f(u, color);
  }

  @Override public void putDiffuse(
    final @Nonnull GLInterfaceEmbedded gl,
    final @Nonnull VectorReadable3F diffuse)
    throws GLException,
      ConstraintError
  {
    final ProgramUniform u = this.program.getUniform("material_diffuse");
    Constraints.constrainNotNull(u, "Uniform");
    gl.programPutUniformVector3f(u, diffuse);
  }

  @Override public void putLightColor(
    final @Nonnull GLInterfaceEmbedded gl,
    final @Nonnull VectorReadable3F rgb)
    throws GLException,
      ConstraintError
  {
    final ProgramUniform u = this.program.getUniform("light_color");
    Constraints.constrainNotNull(u, "Uniform");
    gl.programPutUniformVector3f(u, rgb);
  }

  @Override public void putLightPosition(
    final @Nonnull GLInterfaceEmbedded gl,
    final @Nonnull VectorReadable3F position)
    throws GLException,
      ConstraintError
  {
    final ProgramUniform u = this.program.getUniform("light_position");
    Constraints.constrainNotNull(u, "Uniform");
    gl.programPutUniformVector3f(u, position);
  }

  @Override public void putLightPower(
    final @Nonnull GLInterfaceEmbedded gl,
    final float power)
    throws GLException,
      ConstraintError
  {
    final ProgramUniform u = this.program.getUniform("light_power");
    Constraints.constrainNotNull(u, "Uniform");
    gl.programPutUniformFloat(u, power);
  }

  @Override public void putModelMatrix(
    final @Nonnull GLInterfaceEmbedded gl,
    final @Nonnull MatrixReadable4x4F m)
    throws GLException,
      ConstraintError
  {
    final ProgramUniform u = this.program.getUniform("matrix_model");
    Constraints.constrainNotNull(u, "Uniform");
    gl.programPutUniformMatrix4x4f(u, m);
  }

  @Override public void putNormalMatrix(
    final @Nonnull GLInterfaceEmbedded gl,
    final @Nonnull MatrixReadable3x3F m)
    throws GLException,
      ConstraintError
  {
    final ProgramUniform u = this.program.getUniform("matrix_normal");
    Constraints.constrainNotNull(u, "Uniform");
    gl.programPutUniformMatrix3x3f(u, m);
  }

  @Override public void putProjectionMatrix(
    final @Nonnull GLInterfaceEmbedded gl,
    final @Nonnull MatrixReadable4x4F m)
    throws GLException,
      ConstraintError
  {
    final ProgramUniform u = this.program.getUniform("matrix_projection");
    Constraints.constrainNotNull(u, "Uniform");
    gl.programPutUniformMatrix4x4f(u, m);
  }

  @Override public void putShininess(
    final @Nonnull GLInterfaceEmbedded gl,
    final float shininess)
    throws GLException,
      ConstraintError
  {
    final ProgramUniform u = this.program.getUniform("material_shininess");
    Constraints.constrainNotNull(u, "Uniform");
    gl.programPutUniformFloat(u, shininess);
  }

  @Override public void putSpecular(
    final @Nonnull GLInterfaceEmbedded gl,
    final @Nonnull VectorReadable4F specular)
    throws GLException,
      ConstraintError
  {
    final ProgramUniform u = this.program.getUniform("material_specular");
    Constraints.constrainNotNull(u, "Uniform");
    gl.programPutUniformVector4f(u, specular);
  }

  @Override public void putViewMatrix(
    final @Nonnull GLInterfaceEmbedded gl,
    final @Nonnull MatrixReadable4x4F m)
    throws GLException,
      ConstraintError
  {
    final ProgramUniform u = this.program.getUniform("matrix_view");
    Constraints.constrainNotNull(u, "Uniform");
    gl.programPutUniformMatrix4x4f(u, m);
  }

  @Override public void removeFragmentShader(
    final @Nonnull PathVirtual path,
    final @Nonnull GLInterfaceEmbedded gl)
    throws ConstraintError,
      GLException
  {
    this.program.removeFragmentShader(path, gl);
  }

  @Override public void removeVertexShader(
    final @Nonnull PathVirtual path,
    final @Nonnull GLInterfaceEmbedded gl)
    throws ConstraintError,
      GLException
  {
    this.program.removeVertexShader(path, gl);
  }

  @Override public boolean requiresCompilation(
    final @Nonnull FilesystemAPI fs,
    final @Nonnull GLInterfaceEmbedded gl)
    throws FilesystemError,
      ConstraintError
  {
    return this.program.requiresCompilation(fs, gl);
  }
}

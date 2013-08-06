/*
 * Copyright © 2013 <code@io7m.com> http://io7m.com
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jsom0.demos;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jcanephora.JCGLSLVersion;
import com.io7m.jcanephora.JCGLUnsupportedException;
import com.io7m.jvvfs.PathVirtual;

public final class SMVShaderPaths
{
  private static @Nonnull PathVirtual BASE;

  static {
    try {
      SMVShaderPaths.BASE =
        PathVirtual.ofString("/com/io7m/jsom0/demos/shaders");
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException();
    }
  }

  private static @Nonnull String getShaderDirectoryName(
    final @Nonnull JCGLSLVersion version)
    throws JCGLUnsupportedException
  {
    switch (version.getAPI()) {
      case JCGL_ES:
      {
        switch (version.getVersionMajor()) {
          case 1:
            return "glsles100";
          case 3:
            return "glsles300";
          default:
            throw new JCGLUnsupportedException("Unsupported GLSL ES version "
              + version);
        }
      }
      case JCGL_FULL:
      {
        switch (version.getVersionMajor()) {
          case 1:
          {
            switch (version.getVersionMinor()) {
              case 10:
                return "glsl110";
              case 30:
                return "glsl130";
              default:
                return "glsl140";
            }
          }
        }
      }
    }

    throw new UnreachableCodeException();
  }

  public static @Nonnull PathVirtual getShader(
    final @Nonnull JCGLSLVersion version,
    final @Nonnull String name)
    throws JCGLUnsupportedException,
      ConstraintError
  {
    return SMVShaderPaths.BASE.appendName(
      SMVShaderPaths.getShaderDirectoryName(version)).appendName(name);
  }
}

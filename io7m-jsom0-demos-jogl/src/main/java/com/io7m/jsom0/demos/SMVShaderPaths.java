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
import com.io7m.jcanephora.GLUnsupportedException;
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
    final boolean is_es,
    final int version_major,
    final int version_minor)
    throws GLUnsupportedException
  {
    if (is_es) {
      if (version_major == 2) {
        return "gles2";
      }
      if (version_major == 3) {
        return "gles3";
      }
      throw new GLUnsupportedException("Unsupported ES version "
        + version_major
        + "."
        + version_minor);
    }

    if (version_major == 2) {
      return "gl21";
    }

    if (version_major == 3) {
      if (version_minor == 0) {
        return "gl30";
      }
    }

    return "gl31";
  }

  public static @Nonnull PathVirtual getShader(
    final boolean is_es,
    final int version_major,
    final int version_minor,
    final @Nonnull String name)
    throws GLUnsupportedException,
      ConstraintError
  {
    return SMVShaderPaths.BASE.appendName(
      SMVShaderPaths.getShaderDirectoryName(
        is_es,
        version_major,
        version_minor)).appendName(name);
  }
}
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

import com.io7m.jtensors.MatrixM4x4F;
import com.io7m.jtensors.VectorI3F;
import com.io7m.jtensors.VectorM3F;

public final class SMVCamera
{
  private final @Nonnull VectorM3F origin;
  private final @Nonnull VectorM3F target;

  public SMVCamera()
  {
    this.origin = new VectorM3F(0.0f, 2.0f, 0.0f);
    this.target = new VectorM3F(0.0f, 2.0f, -5.0f);
  }

  void setPosition(
    final float x,
    final float y,
    final float z)
  {
    this.origin.x = x;
    this.origin.y = y;
    this.origin.z = z;
  }

  void setTarget(
    final float x,
    final float y,
    final float z)
  {
    this.target.x = x;
    this.target.y = y;
    this.target.z = z;
  }

  void makeViewMatrix(
    final @Nonnull MatrixM4x4F.Context context,
    final @Nonnull MatrixM4x4F m)
  {
    MatrixM4x4F.lookAtWithContext(
      context,
      this.origin,
      this.target,
      new VectorI3F(0.0f, 1.0f, 0.0f),
      m);
  }
}

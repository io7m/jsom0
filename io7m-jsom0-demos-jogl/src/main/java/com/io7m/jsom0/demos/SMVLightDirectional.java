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

import com.io7m.jtensors.VectorI3F;
import com.io7m.jtensors.VectorI4F;
import com.io7m.jtensors.VectorReadable3F;
import com.io7m.jtensors.VectorReadable4F;

final class SMVLightDirectional
{
  private final @Nonnull VectorI4F direction;
  private final @Nonnull VectorI3F colour;
  private final float              intensity;

  public SMVLightDirectional(
    final @Nonnull VectorI4F direction,
    final @Nonnull VectorI3F colour,
    final float intensity)
  {
    assert direction.w == 0.0f;

    this.direction = direction;
    this.colour = colour;
    this.intensity = intensity;
  }

  public @Nonnull VectorReadable4F getDirection()
  {
    return this.direction;
  }

  public @Nonnull VectorReadable3F getColour()
  {
    return this.colour;
  }

  public float getIntensity()
  {
    return this.intensity;
  }
}

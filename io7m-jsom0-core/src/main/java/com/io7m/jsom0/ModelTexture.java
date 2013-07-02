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

package com.io7m.jsom0;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

public final class ModelTexture
{
  public final @Nonnull String              name;
  public float                              alpha;
  public final @Nonnull ModelTextureMapping mapping;

  private static final @Nonnull String      EOL;

  static {
    EOL = System.getProperty("line.separator");
  }

  public ModelTexture(
    final @Nonnull String name,
    final @Nonnull ModelTextureMapping mapping)
    throws ConstraintError
  {
    this.name = Constraints.constrainNotNull(name, "name");
    this.mapping = Constraints.constrainNotNull(mapping, "mapping");
    this.alpha = 1.0f;
  }

  public float getAlpha()
  {
    return this.alpha;
  }

  public @Nonnull ModelTextureMapping getMapping()
  {
    return this.mapping;
  }

  public @Nonnull String getName()
  {
    return this.name;
  }

  public void setAlpha(
    final float alpha)
    throws ConstraintError
  {
    this.alpha = Constraints.constrainRange(alpha, 0.0f, 1.0f, "Alpha value");
  }

  @Override public @Nonnull String toString()
  {
    final StringBuilder b = new StringBuilder();

    b.append("  texture   \"");
    b.append(this.getName());
    b.append("\" ");
    b.append(this.getAlpha());
    b.append(" ");
    b.append(ModelTextureMappingNames.name(this.getMapping()));
    b.append(";");
    b.append(ModelTexture.EOL);

    return b.toString();
  }
}

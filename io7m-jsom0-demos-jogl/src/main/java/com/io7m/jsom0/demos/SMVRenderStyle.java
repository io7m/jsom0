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

public enum SMVRenderStyle
{
  RENDER_STYLE_TEXTURED_FLAT("textured-flat", true),
  RENDER_STYLE_TEXTURED_FLAT_CHROME("textured-flat-chrome", true),
  RENDER_STYLE_NORMALS_ONLY("normals-only", true),
  RENDER_STYLE_NORMALS_ONLY_EYESPACE("normals-only-eyespace", true),
  RENDER_STYLE_TEXTURED_LIT("textured-lit", true),
  RENDER_STYLE_VERTEX_COLOR("vertex-color", false),
  RENDER_STYLE_COLOR("color", false);

  private final @Nonnull String name;
  private final boolean         selectable;

  private SMVRenderStyle(
    final @Nonnull String name,
    final boolean selectable)
  {
    this.name = name;
    this.selectable = selectable;
  }

  @Nonnull String getName()
  {
    return this.name;
  }

  boolean getSelectable()
  {
    return this.selectable;
  }
}

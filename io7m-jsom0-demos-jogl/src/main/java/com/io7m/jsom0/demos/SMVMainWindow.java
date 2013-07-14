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
import javax.swing.JFrame;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jlog.Log;
import com.io7m.jvvfs.FilesystemError;

public final class SMVMainWindow extends JFrame
{
  private static final long serialVersionUID = -853042557031333371L;
  private final SMVMainPanel    panel;

  SMVMainWindow(
    final @Nonnull SMVConfig config,
    final @Nonnull Log log)
    throws ConstraintError,
      FilesystemError
  {
    this.panel = new SMVMainPanel(config, log);
    this.getContentPane().add(this.panel);
  }
}

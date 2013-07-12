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

import java.awt.Dimension;

import javax.annotation.Nonnull;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import net.java.dev.designgridlayout.DesignGridLayout;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jlog.Log;
import com.io7m.jvvfs.FilesystemError;
import com.jogamp.opengl.util.FPSAnimator;

public class SMVMainPanel extends JPanel
{
  private static final long serialVersionUID = 6107820323163155729L;

  private static class CanvasContainer extends JPanel
  {
    private static final long          serialVersionUID;
    private final @Nonnull SMVGLCanvas canvas;

    static {
      serialVersionUID = 9031666475369374710L;
    }

    CanvasContainer(
      final @Nonnull SMVConfig config,
      final @Nonnull Log log)
      throws ConstraintError,
        FilesystemError
    {
      this.setBorder(BorderFactory.createTitledBorder("OpenGL"));

      this.canvas = SMVGLCanvas.makeCanvas(config, log);
      this.canvas.setPreferredSize(new Dimension(400, 400));

      final FPSAnimator animator = new FPSAnimator(this.canvas, 60);
      animator.start();

      this.add(this.canvas);
    }

    @Nonnull SMVGLCanvas getCanvas()
    {
      return this.canvas;
    }
  }

  private final @Nonnull CanvasContainer     canvas_container;
  private final @Nonnull SMVGLCanvasControls control_container;

  SMVMainPanel(
    final @Nonnull SMVConfig config,
    final @Nonnull Log log)
    throws ConstraintError,
      FilesystemError
  {
    this.canvas_container = new CanvasContainer(config, log);
    this.control_container =
      new SMVGLCanvasControls(this.canvas_container.getCanvas());

    final DesignGridLayout layout = new DesignGridLayout(this);
    layout
      .row()
      .grid()
      .add(this.canvas_container)
      .add(this.control_container);
  }
}

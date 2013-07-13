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
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jlog.Log;
import com.io7m.jvvfs.FilesystemError;
import com.jogamp.opengl.util.FPSAnimator;

public class SMVMainPanel extends JPanel
{
  private static final long                  serialVersionUID;

  static {
    serialVersionUID = 6107820323163155729L;
  }

  private final @Nonnull JSplitPane          pane;
  private final @Nonnull SMVGLCanvas         canvas;
  private final @Nonnull SMVGLCanvasControls controls;
  private final @Nonnull JPanel              canvas_panel;

  SMVMainPanel(
    final @Nonnull SMVConfig config,
    final @Nonnull Log log)
    throws ConstraintError,
      FilesystemError
  {
    this.pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    /**
     * The OpenGL canvas is placed inside a JPanel here because it misbehaves
     * if it's directly placed in a JSplitPane (due to Swing limitations).
     */

    this.canvas = SMVGLCanvas.makeCanvas(config, log);
    this.canvas.setPreferredSize(new Dimension(400, 400));
    this.canvas_panel = new JPanel();
    this.canvas_panel.add(this.canvas);

    final FPSAnimator animator = new FPSAnimator(this.canvas, 60);
    animator.start();

    this.controls = new SMVGLCanvasControls(this.canvas);

    this.pane.add(this.canvas_panel);
    this.pane.add(this.controls);
    this.add(this.pane);
  }
}

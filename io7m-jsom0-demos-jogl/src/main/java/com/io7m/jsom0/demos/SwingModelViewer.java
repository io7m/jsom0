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

import java.util.Properties;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jlog.Log;
import com.io7m.jvvfs.FilesystemError;

public final class SwingModelViewer
{
  public static void main(
    final String args[])
  {
    final SMVConfig config = new SMVConfig();
    final Properties props = new Properties();
    props.setProperty("com.io7m.jsom0.logs.model-viewer", "true");
    props.setProperty("com.io7m.jsom0.logs.model-viewer.filesystem", "false");
    props.setProperty(
      "com.io7m.jsom0.logs.model-viewer.view-renderer.object-parser",
      "false");
    props.setProperty("com.io7m.jsom0.model-viewer.level", "LOG_DEBUG");
    final Log log = new Log(props, "com.io7m.jsom0", "model-viewer");

    SwingUtilities.invokeLater(new Runnable() {
      @Override public void run()
      {
        log.debug("starting");

        try {
          final SMVMainWindow window = new SMVMainWindow(config, log);
          window.setTitle("SwingModelViewer");
          window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
          window.pack();
          window.setVisible(true);
          window.setMinimumSize(window.getSize());
        } catch (final ConstraintError e) {
          SMVErrorBox.showError("Constraint error", e);
        } catch (final FilesystemError e) {
          SMVErrorBox.showError("Filesystem error", e);
        }
      }
    });
  }
}

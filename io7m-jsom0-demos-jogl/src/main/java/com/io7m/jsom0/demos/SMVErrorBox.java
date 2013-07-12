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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.annotation.Nonnull;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public final class SMVErrorBox
{
  private static void showErrorActual(
    final @Nonnull String title,
    final @Nonnull Throwable e)
  {
    final StringWriter writer = new StringWriter();
    e.printStackTrace(new PrintWriter(writer));

    final JTextArea text = new JTextArea();
    text.setEditable(false);
    text.setText(writer.toString());

    final JScrollPane pane = new JScrollPane(text);
    pane.setPreferredSize(new Dimension(600, 320));

    final JLabel label = new JLabel(title);
    label.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

    final BorderLayout layout = new BorderLayout();
    final JPanel panel = new JPanel(layout);
    panel.add(label, BorderLayout.NORTH);
    panel.add(pane, BorderLayout.SOUTH);

    e.printStackTrace();

    JOptionPane.showMessageDialog(
      null,
      panel,
      title,
      JOptionPane.ERROR_MESSAGE);
  }

  public static void showError(
    final @Nonnull String title,
    final @Nonnull Throwable e)
  {
    SwingUtilities.invokeLater(new Runnable() {
      @SuppressWarnings("synthetic-access") @Override public void run()
      {
        SMVErrorBox.showErrorActual(title, e);
      }
    });
  }
}

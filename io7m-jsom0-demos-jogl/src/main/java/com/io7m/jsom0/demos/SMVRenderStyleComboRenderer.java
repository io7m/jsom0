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

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * A renderer for a combo box that allows the selection of rendering styles.
 */

final class SMVRenderStyleComboRenderer extends JLabel implements
  ListCellRenderer<SMVRenderStyle>
{
  private static final long serialVersionUID = 6934075552794731676L;

  SMVRenderStyleComboRenderer()
  {

  }

  @Override public Component getListCellRendererComponent(
    final JList<? extends SMVRenderStyle> list,
    final SMVRenderStyle value,
    final int index,
    final boolean isSelected,
    final boolean cellHasFocus)
  {
    if (isSelected) {
      this.setBackground(list.getSelectionBackground());
      this.setForeground(list.getSelectionForeground());
    } else {
      this.setBackground(this.getBackground());
      this.setForeground(this.getForeground());
    }

    this.setText(value.getName());
    return this;
  }
}

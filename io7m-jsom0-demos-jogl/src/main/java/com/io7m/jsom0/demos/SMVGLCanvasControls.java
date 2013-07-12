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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.annotation.Nonnull;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.java.dev.designgridlayout.DesignGridLayout;
import net.java.dev.designgridlayout.Tag;

import com.io7m.jtensors.VectorM3F;

final class SMVGLCanvasControls extends JPanel
{
  private static final class CameraControls extends JPanel
  {
    private static final long         serialVersionUID;

    static {
      serialVersionUID = -6469218528918251954L;
    }

    private final @Nonnull JTextField origin_x;
    private final @Nonnull JTextField origin_y;
    private final @Nonnull JTextField origin_z;
    private final @Nonnull JTextField target_x;
    private final @Nonnull JTextField target_y;
    private final @Nonnull JTextField target_z;
    private final @Nonnull JButton    update;

    CameraControls(
      final @Nonnull SMVGLCanvas canvas)
    {
      this.setBorder(BorderFactory.createTitledBorder("Camera"));

      final DesignGridLayout dg = new DesignGridLayout(this);

      this.origin_x = new JTextField("0.0");
      this.origin_y = new JTextField("2.0");
      this.origin_z = new JTextField("0.0");
      this.target_x = new JTextField("0.0");
      this.target_y = new JTextField("2.0");
      this.target_z = new JTextField("-5.0");
      this.update = new JButton("Update");
      this.update.addActionListener(new ActionListener() {
        @Override public void actionPerformed(
          final @Nonnull ActionEvent e)
        {
          try {
            final VectorM3F origin_new = new VectorM3F();
            final VectorM3F target_new = new VectorM3F();

            origin_new.x =
              Float.parseFloat(CameraControls.this.origin_x.getText());
            origin_new.y =
              Float.parseFloat(CameraControls.this.origin_y.getText());
            origin_new.z =
              Float.parseFloat(CameraControls.this.origin_z.getText());

            target_new.x =
              Float.parseFloat(CameraControls.this.target_x.getText());
            target_new.y =
              Float.parseFloat(CameraControls.this.target_y.getText());
            target_new.z =
              Float.parseFloat(CameraControls.this.target_z.getText());

            canvas.setCameraOriginTarget(origin_new, target_new);

          } catch (final NumberFormatException x) {
            SMVErrorBox.showError("Number format error", x);
          }
        }
      });

      dg
        .row()
        .grid()
        .add(new JLabel("Origin"))
        .add(this.origin_x)
        .add(this.origin_y)
        .add(this.origin_z);

      dg
        .row()
        .grid()
        .add(new JLabel("Target"))
        .add(this.target_x)
        .add(this.target_y)
        .add(this.target_z);

      dg.row().bar().add(this.update, Tag.OK);
    }
  }

  private static final class DataControls extends JPanel
  {
    private static final long                          serialVersionUID;
    protected final @Nonnull JTextField                model_field;
    private final @Nonnull JButton                     model_browse;
    private final @Nonnull JComboBox<String>           object_menu;
    protected final @Nonnull JComboBox<SMVRenderStyle> model_render_style;
    protected final @Nonnull JTextField                material_field;
    private final @Nonnull JButton                     material_browse;

    static {
      serialVersionUID = 3789062523559521691L;
    }

    DataControls(
      final @Nonnull SMVGLCanvas canvas)
    {
      this.setBorder(BorderFactory.createTitledBorder("Data"));

      this.object_menu = new JComboBox<String>();
      this.model_field = new JTextField();
      this.model_field.setEditable(false);

      this.model_browse = new JButton("Open...");
      this.model_browse.addActionListener(new ActionListener() {
        @Override public void actionPerformed(
          final @Nonnull ActionEvent e)
        {
          final JFileChooser chooser = new JFileChooser();
          chooser.setMultiSelectionEnabled(false);

          final int r = chooser.showOpenDialog(DataControls.this);
          switch (r) {
            case JFileChooser.APPROVE_OPTION:
            {
              final File file = chooser.getSelectedFile();
              canvas.loadModel(file);
              DataControls.this.model_field.setText(file.toString());
              break;
            }
            case JFileChooser.CANCEL_OPTION:
            {
              break;
            }
            case JFileChooser.ERROR_OPTION:
            {
              break;
            }
          }
        }
      });

      this.material_field = new JTextField();
      this.material_field.setEditable(false);

      this.material_browse = new JButton("Open...");
      this.material_browse.addActionListener(new ActionListener() {
        @Override public void actionPerformed(
          final @Nonnull ActionEvent e)
        {
          final JFileChooser chooser = new JFileChooser();
          chooser.setMultiSelectionEnabled(false);

          final int r = chooser.showOpenDialog(DataControls.this);
          switch (r) {
            case JFileChooser.APPROVE_OPTION:
            {
              final File file = chooser.getSelectedFile();
              canvas.loadMaterial(file);
              DataControls.this.material_field.setText(file.toString());
              break;
            }
            case JFileChooser.CANCEL_OPTION:
            {
              break;
            }
            case JFileChooser.ERROR_OPTION:
            {
              break;
            }
          }
        }
      });

      this.model_render_style = new SMVRenderStyleSelector();
      this.model_render_style.addActionListener(new ActionListener() {
        @Override public void actionPerformed(
          final @Nonnull ActionEvent e)
        {
          final SMVRenderStyle style =
            (SMVRenderStyle) DataControls.this.model_render_style
              .getSelectedItem();
          canvas.selectRenderStyle(style);
        }
      });

      final DesignGridLayout dg = new DesignGridLayout(this);

      dg
        .row()
        .grid()
        .add(new JLabel("Model file"))
        .add(this.model_field)
        .add(this.model_browse);

      dg.row().grid().add(new JLabel("Object")).add(this.object_menu, 2);

      dg
        .row()
        .grid()
        .add(new JLabel("Material file"))
        .add(this.material_field)
        .add(this.material_browse);

      dg
        .row()
        .grid()
        .add(new JLabel("Render"))
        .add(this.model_render_style, 2);
    }
  }

  private static final class ModelControls extends JPanel
  {
    private static final long         serialVersionUID;

    static {
      serialVersionUID = -6469218528918251954L;
    }

    private final @Nonnull JTextField origin_x;
    private final @Nonnull JTextField origin_y;
    private final @Nonnull JTextField origin_z;
    private final @Nonnull JTextField rotate_x;
    private final @Nonnull JTextField rotate_y;
    private final @Nonnull JTextField rotate_z;
    private final @Nonnull JButton    update;

    ModelControls(
      final @Nonnull SMVGLCanvas canvas)
    {
      this.setBorder(BorderFactory.createTitledBorder("Model"));

      final DesignGridLayout dg = new DesignGridLayout(this);

      this.origin_x = new JTextField("0.0");
      this.origin_y = new JTextField("2.0");
      this.origin_z = new JTextField("-10.0");
      this.rotate_x = new JTextField("0.0");
      this.rotate_y = new JTextField("0.0");
      this.rotate_z = new JTextField("0.0");

      this.update = new JButton("Update");
      this.update.addActionListener(new ActionListener() {
        @Override public void actionPerformed(
          final @Nonnull ActionEvent e)
        {
          try {
            final VectorM3F origin_new = new VectorM3F();
            final VectorM3F rotate_new = new VectorM3F();

            origin_new.x =
              Float.parseFloat(ModelControls.this.origin_x.getText());
            origin_new.y =
              Float.parseFloat(ModelControls.this.origin_y.getText());
            origin_new.z =
              Float.parseFloat(ModelControls.this.origin_z.getText());

            rotate_new.x =
              Float.parseFloat(ModelControls.this.rotate_x.getText());
            rotate_new.y =
              Float.parseFloat(ModelControls.this.rotate_y.getText());
            rotate_new.z =
              Float.parseFloat(ModelControls.this.rotate_z.getText());

            canvas.setModelPosition(origin_new);
            canvas.setModelRotations(rotate_new);

          } catch (final NumberFormatException x) {
            SMVErrorBox.showError("Number format error", x);
          }
        }
      });

      dg
        .row()
        .grid()
        .add(new JLabel("Position"))
        .add(this.origin_x)
        .add(this.origin_y)
        .add(this.origin_z);

      dg
        .row()
        .grid()
        .add(new JLabel("Rotation"))
        .add(this.rotate_x)
        .add(this.rotate_y)
        .add(this.rotate_z);

      dg.row().bar().add(this.update, Tag.OK);
    }
  }

  private static final long             serialVersionUID;

  static {
    serialVersionUID = -3663209745613242332L;
  }

  private final @Nonnull DataControls   d_controls;
  private final @Nonnull ModelControls  m_controls;
  private final @Nonnull CameraControls c_controls;

  SMVGLCanvasControls(
    final @Nonnull SMVGLCanvas canvas)
  {
    this.d_controls = new DataControls(canvas);
    this.m_controls = new ModelControls(canvas);
    this.c_controls = new CameraControls(canvas);

    final DesignGridLayout dg = new DesignGridLayout(this);
    dg.row().grid().add(this.d_controls);
    dg.row().grid().add(this.m_controls);
    dg.row().grid().add(this.c_controls);
  }
}

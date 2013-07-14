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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.java.dev.designgridlayout.DesignGridLayout;
import net.java.dev.designgridlayout.Tag;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jsom0.ModelMaterial;
import com.io7m.jsom0.ModelTexture;
import com.io7m.jsom0.ModelTextureMapping;
import com.io7m.jtensors.VectorM3F;

final class SMVGLCanvasControls extends JTabbedPane
{
  private static final class DataTabModelControls extends JPanel
  {
    private static final long                          serialVersionUID;
    protected final @Nonnull JTextField                model_field;
    private final @Nonnull JButton                     model_browse;
    protected final @Nonnull JComboBox<String>         object_menu;
    protected final @Nonnull JComboBox<SMVRenderStyle> model_render_style;

    static {
      serialVersionUID = 4407901430152351612L;
    }

    DataTabModelControls(
      final @Nonnull SMVGLCanvas canvas)
    {
      this.setBorder(BorderFactory.createTitledBorder("Model"));

      this.object_menu = new JComboBox<String>();
      this.object_menu.addActionListener(new ActionListener() {
        @Override public void actionPerformed(
          final @Nonnull ActionEvent e)
        {
          canvas.selectObject((String) DataTabModelControls.this.object_menu
            .getSelectedItem());
        }
      });

      this.model_field = new JTextField();
      this.model_field.setEditable(false);

      this.model_browse = new JButton("Open...");
      this.model_browse.addActionListener(new ActionListener() {
        @Override public void actionPerformed(
          final @Nonnull ActionEvent e)
        {
          final JFileChooser chooser = new JFileChooser();
          chooser.setMultiSelectionEnabled(false);

          final int r = chooser.showOpenDialog(DataTabModelControls.this);
          switch (r) {
            case JFileChooser.APPROVE_OPTION:
            {
              final File file = chooser.getSelectedFile();
              canvas.loadModel(file, DataTabModelControls.this.object_menu);
              DataTabModelControls.this.model_field.setText(file.toString());
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
            (SMVRenderStyle) DataTabModelControls.this.model_render_style
              .getSelectedItem();
          canvas.selectRenderStyle(style);
        }
      });

      final DesignGridLayout dg = new DesignGridLayout(this);

      dg
        .row()
        .grid()
        .add(new JLabel("File"))
        .add(this.model_field, 3)
        .add(this.model_browse);

      dg.row().grid().add(new JLabel("Object")).add(this.object_menu, 4);

      dg
        .row()
        .grid()
        .add(new JLabel("Render"))
        .add(this.model_render_style, 4);
    }
  }

  private static final class DataTabMaterialControls extends JPanel
  {
    private static final long           serialVersionUID;

    static {
      serialVersionUID = 4407901430152351612L;
    }

    protected final @Nonnull JTextField diffuse_r;
    protected final @Nonnull JTextField diffuse_g;
    protected final @Nonnull JTextField diffuse_b;
    protected final @Nonnull JTextField ambient_r;
    protected final @Nonnull JTextField ambient_g;
    protected final @Nonnull JTextField ambient_b;
    protected final @Nonnull JTextField specular_r;
    protected final @Nonnull JTextField specular_g;
    protected final @Nonnull JTextField specular_b;
    protected final @Nonnull JTextField specular_a;
    protected final @Nonnull JTextField shininess;
    protected final @Nonnull JTextField texture;
    protected final @Nonnull JButton    texture_browse;
    protected final @Nonnull JButton    texture_clear;
    protected final @Nonnull JButton    update;

    protected @Nonnull ModelMaterial makeMaterial()
      throws ConstraintError
    {
      ModelMaterial m = new ModelMaterial("custom");

      if (this.texture.getText().length() > 0) {
        final ModelTexture t =
          new ModelTexture(
            this.texture.getText(),
            ModelTextureMapping.MODEL_TEXTURE_MAPPING_UV);
        m = new ModelMaterial("custom", t);
      } else {
        m = new ModelMaterial("custom");
      }

      m.diffuse.x = Float.parseFloat(this.diffuse_r.getText());
      m.diffuse.y = Float.parseFloat(this.diffuse_g.getText());
      m.diffuse.z = Float.parseFloat(this.diffuse_b.getText());

      m.ambient.x = Float.parseFloat(this.ambient_r.getText());
      m.ambient.y = Float.parseFloat(this.ambient_g.getText());
      m.ambient.z = Float.parseFloat(this.ambient_b.getText());

      m.specular.x = Float.parseFloat(this.specular_r.getText());
      m.specular.y = Float.parseFloat(this.specular_g.getText());
      m.specular.z = Float.parseFloat(this.specular_b.getText());
      m.specular.w = Float.parseFloat(this.specular_a.getText());

      m.shininess = Float.parseFloat(this.shininess.getText());
      m.alpha = 1.0f;

      return m;
    }

    DataTabMaterialControls(
      final @Nonnull SMVGLCanvas canvas)
    {
      this.setBorder(BorderFactory.createTitledBorder("Material"));

      this.diffuse_r = new JTextField("1.0");
      this.diffuse_g = new JTextField("0.0");
      this.diffuse_b = new JTextField("0.0");

      this.ambient_r = new JTextField("0.0");
      this.ambient_g = new JTextField("0.0");
      this.ambient_b = new JTextField("0.0");

      this.specular_r = new JTextField("1.0");
      this.specular_g = new JTextField("1.0");
      this.specular_b = new JTextField("1.0");
      this.specular_a = new JTextField("1.0");

      this.shininess = new JTextField("50.0");

      this.texture = new JTextField();
      this.texture.setEditable(false);
      this.texture_browse = new JButton("Open...");
      this.texture_browse
        .setToolTipText("Select an image from the filesystem");
      this.texture_browse.addActionListener(new ActionListener() {
        @Override public void actionPerformed(
          final ActionEvent e)
        {
          final JFileChooser chooser = new JFileChooser();
          chooser.setMultiSelectionEnabled(false);

          final int r = chooser.showOpenDialog(DataTabMaterialControls.this);
          switch (r) {
            case JFileChooser.APPROVE_OPTION:
            {
              final File file = chooser.getSelectedFile();
              DataTabMaterialControls.this.texture.setText(file.toString());
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

      this.texture_clear = new JButton("Unset");
      this.texture_clear.setToolTipText("Forget the selected texture");

      this.update = new JButton("Update");
      this.update.addActionListener(new ActionListener() {
        @Override public void actionPerformed(
          final ActionEvent e)
        {
          try {
            final ModelMaterial m =
              DataTabMaterialControls.this.makeMaterial();
            canvas.setMaterial(m);
          } catch (final ConstraintError x) {
            SMVErrorBox.showError("Constraint error", x);
          } catch (final NumberFormatException x) {
            SMVErrorBox.showError("Number format error", x);
          }
        }
      });

      final DesignGridLayout dg = new DesignGridLayout(this);

      dg
        .row()
        .grid()
        .add(new JLabel("Ambient"))
        .add(this.ambient_r)
        .add(this.ambient_g)
        .add(this.ambient_b, 2);

      dg
        .row()
        .grid()
        .add(new JLabel("Diffuse"))
        .add(this.diffuse_r)
        .add(this.diffuse_g)
        .add(this.diffuse_b, 2);

      dg
        .row()
        .grid()
        .add(new JLabel("Specular"))
        .add(this.specular_r)
        .add(this.specular_g)
        .add(this.specular_b)
        .add(this.specular_a);

      dg.row().grid().add(new JLabel("Shininess")).add(this.shininess, 4);

      dg
        .row()
        .grid()
        .add(new JLabel("Texture"))
        .add(this.texture, 2)
        .add(this.texture_browse)
        .add(this.texture_clear);

      dg.row().bar().add(this.update, Tag.APPLY);
    }
  }

  private static final class DataTab extends JPanel
  {
    private static final long                      serialVersionUID;

    static {
      serialVersionUID = 3789062523559521691L;
    }

    private final @Nonnull DataTabModelControls    mo_controls;
    private final @Nonnull DataTabMaterialControls ma_controls;

    DataTab(
      final @Nonnull SMVGLCanvas canvas)
    {
      this.mo_controls = new DataTabModelControls(canvas);
      this.ma_controls = new DataTabMaterialControls(canvas);

      final DesignGridLayout dg = new DesignGridLayout(this);
      dg.row().grid().add(this.mo_controls);
      dg.row().grid().add(this.ma_controls);
    }
  }

  private static class ViewTab extends JPanel
  {
    private static final long                    serialVersionUID;

    static {
      serialVersionUID = -1892389128933242332L;
    }

    private final @Nonnull ViewTabModelControls  m_controls;
    private final @Nonnull ViewTabCameraControls c_controls;

    ViewTab(
      final @Nonnull SMVGLCanvas canvas)
    {
      this.m_controls = new ViewTabModelControls(canvas);
      this.c_controls = new ViewTabCameraControls(canvas);

      final DesignGridLayout dg = new DesignGridLayout(this);
      dg.row().grid().add(this.m_controls);
      dg.row().grid().add(this.c_controls);
    }
  }

  private static final class ViewTabCameraControls extends JPanel
  {
    private static final long           serialVersionUID;

    static {
      serialVersionUID = -6469218528918251954L;
    }

    protected final @Nonnull JTextField origin_x;
    protected final @Nonnull JTextField origin_y;
    protected final @Nonnull JTextField origin_z;
    protected final @Nonnull JTextField target_x;
    protected final @Nonnull JTextField target_y;
    protected final @Nonnull JTextField target_z;
    protected final @Nonnull JButton    update;

    ViewTabCameraControls(
      final @Nonnull SMVGLCanvas canvas)
    {
      this.setBorder(BorderFactory.createTitledBorder("Camera"));

      final DesignGridLayout dg = new DesignGridLayout(this);

      this.origin_x =
        new JTextField(Float.toString(SMVGLCanvas.INITIAL_ORIGIN_CAMERA
          .getXF()));
      this.origin_y =
        new JTextField(Float.toString(SMVGLCanvas.INITIAL_ORIGIN_CAMERA
          .getYF()));
      this.origin_z =
        new JTextField(Float.toString(SMVGLCanvas.INITIAL_ORIGIN_CAMERA
          .getZF()));
      this.target_x =
        new JTextField(Float.toString(SMVGLCanvas.INITIAL_ORIGIN_MODEL
          .getXF()));
      this.target_y =
        new JTextField(Float.toString(SMVGLCanvas.INITIAL_ORIGIN_MODEL
          .getYF()));
      this.target_z =
        new JTextField(Float.toString(SMVGLCanvas.INITIAL_ORIGIN_MODEL
          .getZF()));

      this.update = new JButton("Update");
      this.update.addActionListener(new ActionListener() {
        @Override public void actionPerformed(
          final @Nonnull ActionEvent e)
        {
          try {
            final VectorM3F origin_new = new VectorM3F();
            final VectorM3F target_new = new VectorM3F();

            origin_new.x =
              Float.parseFloat(ViewTabCameraControls.this.origin_x.getText());
            origin_new.y =
              Float.parseFloat(ViewTabCameraControls.this.origin_y.getText());
            origin_new.z =
              Float.parseFloat(ViewTabCameraControls.this.origin_z.getText());

            target_new.x =
              Float.parseFloat(ViewTabCameraControls.this.target_x.getText());
            target_new.y =
              Float.parseFloat(ViewTabCameraControls.this.target_y.getText());
            target_new.z =
              Float.parseFloat(ViewTabCameraControls.this.target_z.getText());

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

  private static final class ViewTabModelControls extends JPanel
  {
    private static final long           serialVersionUID;

    static {
      serialVersionUID = -6469218528918251954L;
    }

    protected final @Nonnull JTextField origin_x;
    protected final @Nonnull JTextField origin_y;
    protected final @Nonnull JTextField origin_z;
    protected final @Nonnull JTextField rotate_x;
    protected final @Nonnull JTextField rotate_y;
    protected final @Nonnull JTextField rotate_z;
    protected final @Nonnull JButton    update;
    protected final @Nonnull JCheckBox  rotating_y;

    ViewTabModelControls(
      final @Nonnull SMVGLCanvas canvas)
    {
      this.setBorder(BorderFactory.createTitledBorder("Model"));

      final DesignGridLayout dg = new DesignGridLayout(this);

      this.origin_x =
        new JTextField(Float.toString(SMVGLCanvas.INITIAL_ORIGIN_MODEL
          .getXF()));
      this.origin_y =
        new JTextField(Float.toString(SMVGLCanvas.INITIAL_ORIGIN_MODEL
          .getYF()));
      this.origin_z =
        new JTextField(Float.toString(SMVGLCanvas.INITIAL_ORIGIN_MODEL
          .getZF()));

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
              Float.parseFloat(ViewTabModelControls.this.origin_x.getText());
            origin_new.y =
              Float.parseFloat(ViewTabModelControls.this.origin_y.getText());
            origin_new.z =
              Float.parseFloat(ViewTabModelControls.this.origin_z.getText());

            rotate_new.x =
              Float.parseFloat(ViewTabModelControls.this.rotate_x.getText());
            rotate_new.y =
              Float.parseFloat(ViewTabModelControls.this.rotate_y.getText());
            rotate_new.z =
              Float.parseFloat(ViewTabModelControls.this.rotate_z.getText());

            canvas.setModelPosition(origin_new);
            canvas.setModelRotations(rotate_new);
            canvas.setWandering(ViewTabModelControls.this.rotating_y
              .isSelected());

          } catch (final NumberFormatException x) {
            SMVErrorBox.showError("Number format error", x);
          }
        }
      });

      this.rotating_y = new JCheckBox();
      this.rotating_y.setSelected(false);

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

      dg
        .row()
        .grid()
        .add(new JLabel("Wandering"))
        .add(this.rotating_y, 1)
        .empty(2);
      dg.row().bar().add(this.update, Tag.OK);
    }
  }

  private static final long      serialVersionUID;

  static {
    serialVersionUID = -3663209745613242332L;
  }

  private final @Nonnull DataTab d_tab;
  private final @Nonnull ViewTab v_tab;

  SMVGLCanvasControls(
    final @Nonnull SMVGLCanvas canvas)
  {
    this.d_tab = new DataTab(canvas);
    this.v_tab = new ViewTab(canvas);

    this.add("Data", this.d_tab);
    this.add("View", this.v_tab);
  }
}

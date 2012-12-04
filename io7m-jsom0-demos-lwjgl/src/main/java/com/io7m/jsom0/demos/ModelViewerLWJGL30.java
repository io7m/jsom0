package com.io7m.jsom0.demos;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.Nonnull;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLCompileException;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jcanephora.GLInterface_LWJGL30;
import com.io7m.jlog.Log;
import com.io7m.jsom0.parser.Error;
import com.io7m.jvvfs.FilesystemError;

public final class ModelViewerLWJGL30
{
  static void createDisplay(
    final String name,
    final int width,
    final int height)
  {
    try {
      /*
       * XXX: As a temporary measure, do not specifically ask for an OpenGL
       * 3.0 context until a reliable means of obtaining one can be found.
       * Mesa 8.0.1 claims to support 3.0 and then fails to create a context,
       * whereas previous versions claimed only to support 2.1 but happily
       * created working 3.0 contexts anyway.
       */

      Display.setDisplayMode(new DisplayMode(width, height));
      Display.setTitle(name);
      Display.create();

      // Display.setDisplayMode(new DisplayMode(width, height));
      // Display.setTitle(name);
      // final PixelFormat format = new PixelFormat();
      // final ContextAttribs attributes =
      // new ContextAttribs(3, 0).withForwardCompatible(true);
      // Display.create(format, attributes);
    } catch (final LWJGLException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void main(
    final @Nonnull String[] args)
  {
    try {
      if (args.length != 4) {
        ModelViewerLWJGL30.usage();
      }

      ModelViewerLWJGL30.createDisplay("Model viewer", 640, 480);

      final String texture_directory = args[0];
      final String file_material = args[1];
      final String file_model = args[2];
      final String object_name = args[3];

      final Properties p = new Properties();
      p.setProperty("com.io7m.jsom0.demos.level", "LOG_DEBUG");
      p.setProperty("com.io7m.jsom0.demos.logs.mv", "true");
      p.setProperty(
        "com.io7m.jsom0.demos.logs.mv.model-viewer.filesystem",
        "false");
      p.setProperty(
        "com.io7m.jsom0.demos.logs.mv.model-viewer.object-parser",
        "true");

      final Log log = new Log(p, "com.io7m.jsom0.demos", "mv");
      final GLInterface g = new GLInterface_LWJGL30(log);

      final ModelViewer viewer =
        new ModelViewer(
          g,
          log,
          texture_directory,
          file_material,
          file_model,
          object_name);

      while (Display.isCloseRequested() == false) {
        ModelViewerLWJGL30.input(viewer);
        viewer.render(g);

        Display.update();
        Display.sync(60);
      }

      Display.destroy();
    } catch (final GLException e) {
      ErrorBox.showError("OpenGL error", e);
    } catch (final IOException e) {
      ErrorBox.showError("I/O error", e);
    } catch (final Error e) {
      ErrorBox.showError("Model error", e);
    } catch (final GLCompileException e) {
      ErrorBox.showError("Shader compilation error", e);
    } catch (final ConstraintError e) {
      ErrorBox.showError("Constraint error", e);
    } catch (final FilesystemError e) {
      ErrorBox.showError("Filesystem error", e);
    }
  }

  private static void input(
    final @Nonnull ModelViewer viewer)
  {
    while (Keyboard.next()) {
      if (Keyboard.getEventKey() == Keyboard.KEY_W) {
        if (Keyboard.getEventKeyState()) {
          viewer.moveCameraZ(-1.0f);
        }
      }
      if (Keyboard.getEventKey() == Keyboard.KEY_S) {
        if (Keyboard.getEventKeyState()) {
          viewer.moveCameraZ(+1.0f);
        }
      }

      if (Keyboard.getEventKey() == Keyboard.KEY_G) {
        if (Keyboard.getEventKeyState()) {
          viewer.moveCameraY(-1.0f);
        }
      }
      if (Keyboard.getEventKey() == Keyboard.KEY_B) {
        if (Keyboard.getEventKeyState()) {
          viewer.moveCameraY(+1.0f);
        }
      }

      if (Keyboard.getEventKey() == Keyboard.KEY_D) {
        if (Keyboard.getEventKeyState()) {
          viewer.rotateY(0.1f);
        }
      }
      if (Keyboard.getEventKey() == Keyboard.KEY_A) {
        if (Keyboard.getEventKeyState()) {
          viewer.rotateY(-0.1f);
        }
      }

      if (Keyboard.getEventKey() == Keyboard.KEY_F) {
        if (Keyboard.getEventKeyState()) {
          viewer.rotateX(0.1f);
        }
      }
      if (Keyboard.getEventKey() == Keyboard.KEY_V) {
        if (Keyboard.getEventKeyState()) {
          viewer.rotateX(-0.1f);
        }
      }

      if (Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
        if (Keyboard.getEventKeyState()) {
          viewer.toggleWireframe();
        }
      }
    }
  }

  private static void usage()
  {
    System.err
      .println("model-viewer: usage: texture-directory file.i7m file.i7o object-name");
    System.exit(1);
  }
}

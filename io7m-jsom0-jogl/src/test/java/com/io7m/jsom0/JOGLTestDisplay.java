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

package com.io7m.jsom0;

import java.util.Properties;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLOffscreenAutoDrawable;
import javax.media.opengl.GLProfile;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLImplementation;
import com.io7m.jcanephora.GLImplementationJOGL;
import com.io7m.jcanephora.GLUnsupportedException;
import com.io7m.jlog.Log;
import com.io7m.jvvfs.FSCapabilityAll;
import com.io7m.jvvfs.Filesystem;

public final class JOGLTestDisplay
{
  private static GLOffscreenAutoDrawable buffer;
  static final String                    LOG_DESTINATION_OPENGL_ES_2_0;
  static final String                    LOG_DESTINATION_OPENGL_3_X;
  static final String                    LOG_DESTINATION_OPENGL_2_1;

  static {
    LOG_DESTINATION_OPENGL_ES_2_0 = "jogl_es_2_0-test";
    LOG_DESTINATION_OPENGL_3_X = "jogl_3_x-test";
    LOG_DESTINATION_OPENGL_2_1 = "jogl_2_1-test";
  }

  private static GLOffscreenAutoDrawable createOffscreenDrawable(
    final GLProfile profile,
    final int width,
    final int height)
  {
    final GLCapabilities cap = new GLCapabilities(profile);
    cap.setFBO(true);

    final GLDrawableFactory f = GLDrawableFactory.getFactory(profile);
    final GLOffscreenAutoDrawable k =
      f.createOffscreenAutoDrawable(null, cap, null, width, height, null);

    return k;
  }

  private static GLContext getContext(
    final GLProfile profile)
  {
    if (JOGLTestDisplay.buffer != null) {
      final GLContext context = JOGLTestDisplay.buffer.getContext();
      context.release();
      JOGLTestDisplay.buffer.destroy();
    }

    JOGLTestDisplay.buffer =
      JOGLTestDisplay.createOffscreenDrawable(profile, 640, 480);

    final GLContext context = JOGLTestDisplay.buffer.getContext();
    final int r = context.makeCurrent();
    if (r == GLContext.CONTEXT_NOT_CURRENT) {
      throw new AssertionError("Could not make context current");
    }

    return context;
  }

  static Log getLog(
    final String destination)
  {
    final Properties properties = new Properties();
    properties.setProperty(
      "com.io7m.jcanephora." + destination + ".logs",
      "true");
    properties.setProperty(
      "com.io7m.jcanephora." + destination + ".level",
      "LOG_DEBUG");
    return new Log(properties, "com.io7m.jcanephora", destination);
  }

  public static boolean isOpenGL3Supported()
  {
    final boolean a = GLProfile.isAvailable(GLProfile.GL3);
    System.err.println("OpenGL 3_X available: " + a);
    return a;
  }

  public static boolean isOpenGL21WithExtensionsSupported()
  {
    final boolean a = GLProfile.isAvailable(GLProfile.GL2);
    System.err.println("OpenGL 2_1 available: " + a);
    return a;
  }

  public static boolean isOpenGLES2Supported()
  {
    final boolean a = GLProfile.isAvailable(GLProfile.GLES2);
    System.err.println("OpenGL ES2 available: " + a);
    return a;
  }

  public static GLImplementation makeContextWithOpenGL_ES2()
    throws GLException,
      GLUnsupportedException,
      ConstraintError
  {
    final Log log =
      JOGLTestDisplay.getLog(JOGLTestDisplay.LOG_DESTINATION_OPENGL_ES_2_0);

    final GLContext ctx =
      JOGLTestDisplay.getContext(GLProfile.get(GLProfile.GLES2));
    final GLImplementation gi = new GLImplementationJOGL(ctx, log);

    return gi;
  }

  public static GLImplementation makeContextWithOpenGL3_X()
    throws GLException,
      GLUnsupportedException,
      ConstraintError
  {
    final Log log =
      JOGLTestDisplay.getLog(JOGLTestDisplay.LOG_DESTINATION_OPENGL_3_X);

    final GLContext ctx =
      JOGLTestDisplay.getContext(GLProfile.get(GLProfile.GL3));
    final GLImplementation gi = new GLImplementationJOGL(ctx, log);
    return gi;
  }

  public static GLImplementation makeContextWithOpenGL2_1()
    throws GLException,
      GLUnsupportedException,
      ConstraintError
  {
    final Log log =
      JOGLTestDisplay.getLog(JOGLTestDisplay.LOG_DESTINATION_OPENGL_2_1);

    final GLContext ctx =
      JOGLTestDisplay.getContext(GLProfile.get(GLProfile.GL2));
    final GLImplementation gi = new GLImplementationJOGL(ctx, log);
    return gi;
  }

  private JOGLTestDisplay()
  {
    throw new UnreachableCodeException();
  }

  static FSCapabilityAll getFS(
    final String destination)
  {
    try {
      return Filesystem.makeWithoutArchiveDirectory(JOGLTestDisplay
        .getLog(destination));
    } catch (final ConstraintError e) {
      throw new java.lang.RuntimeException(e);
    }
  }
}

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
import java.util.StringTokenizer;

import javax.annotation.Nonnull;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Pair;
import com.io7m.jcanephora.JCGLException;
import com.io7m.jcanephora.JCGLImplementation;
import com.io7m.jcanephora.JCGLImplementationLWJGL;
import com.io7m.jcanephora.JCGLUnsupportedException;
import com.io7m.jlog.Log;
import com.io7m.jvvfs.FSCapabilityAll;
import com.io7m.jvvfs.Filesystem;

public final class LWJGLTestContext
{
  private static final class Profile
  {
    int     version_major;
    int     version_minor;
    boolean version_es;
    int     width;
    int     height;

    Profile()
    {

    }
  }

  private static final Profile PROFILE_OPENGL_3_0;
  private static final Profile PROFILE_OPENGL_ES_2_0;
  private static final Profile PROFILE_OPENGL_3_X;
  private static final Profile PROFILE_OPENGL_2_1;

  static {
    PROFILE_OPENGL_2_1 = new Profile();
    LWJGLTestContext.PROFILE_OPENGL_2_1.version_major = 2;
    LWJGLTestContext.PROFILE_OPENGL_2_1.version_minor = 1;
    LWJGLTestContext.PROFILE_OPENGL_2_1.version_es = false;
    LWJGLTestContext.PROFILE_OPENGL_2_1.width = 640;
    LWJGLTestContext.PROFILE_OPENGL_2_1.height = 480;

    PROFILE_OPENGL_3_0 = new Profile();
    LWJGLTestContext.PROFILE_OPENGL_3_0.version_major = 3;
    LWJGLTestContext.PROFILE_OPENGL_3_0.version_minor = 0;
    LWJGLTestContext.PROFILE_OPENGL_3_0.version_es = false;
    LWJGLTestContext.PROFILE_OPENGL_3_0.width = 640;
    LWJGLTestContext.PROFILE_OPENGL_3_0.height = 480;

    PROFILE_OPENGL_3_X = new Profile();
    LWJGLTestContext.PROFILE_OPENGL_3_X.version_major = 3;
    LWJGLTestContext.PROFILE_OPENGL_3_X.version_minor = 1;
    LWJGLTestContext.PROFILE_OPENGL_3_X.version_es = false;
    LWJGLTestContext.PROFILE_OPENGL_3_X.width = 640;
    LWJGLTestContext.PROFILE_OPENGL_3_X.height = 480;

    PROFILE_OPENGL_ES_2_0 = new Profile();
    LWJGLTestContext.PROFILE_OPENGL_ES_2_0.version_major = 2;
    LWJGLTestContext.PROFILE_OPENGL_ES_2_0.version_minor = 0;
    LWJGLTestContext.PROFILE_OPENGL_ES_2_0.version_es = true;
    LWJGLTestContext.PROFILE_OPENGL_ES_2_0.width = 640;
    LWJGLTestContext.PROFILE_OPENGL_ES_2_0.height = 480;
  }

  static final String          LOG_DESTINATION_OPENGL_ES_2_0;
  static final String          LOG_DESTINATION_OPENGL_3_X;
  static final String          LOG_DESTINATION_OPENGL_3_0;
  static final String          LOG_DESTINATION_OPENGL_2_1;

  static {
    LOG_DESTINATION_OPENGL_ES_2_0 = "lwjgl_es_2_0-test";
    LOG_DESTINATION_OPENGL_3_X = "lwjgl_3_x-test";
    LOG_DESTINATION_OPENGL_3_0 = "lwjgl_3_0-test";
    LOG_DESTINATION_OPENGL_2_1 = "lwjgl_2_1-test";
  }

  private static Pbuffer       buffer = null;

  static Pbuffer createOffscreenDisplay(
    final Profile want)
  {
    try {
      final Pbuffer pbuffer = LWJGLTestContext.makePbuffer(want);
      pbuffer.makeCurrent();
      return pbuffer;
    } catch (final LWJGLException e) {
      e.printStackTrace();
      System.exit(1);
    }

    throw new UnreachableCodeException();
  }

  static FSCapabilityAll getFS(
    final String destination)
  {
    try {
      return Filesystem.makeWithoutArchiveDirectory(LWJGLTestContext
        .getLog(destination));
    } catch (final ConstraintError e) {
      throw new java.lang.RuntimeException(e);
    }
  }

  static Log getLog(
    final String destination)
  {
    final Properties properties = new Properties();
    return new Log(properties, "com.io7m.jcanephora", destination);
  }

  /**
   * Return <code>true</code> iff the created context is exactly OpenGL 2.1.
   */

  @SuppressWarnings("boxing") public static boolean isOpenGL21Supported()
  {
    try {
      final Pbuffer pb =
        LWJGLTestContext.makePbuffer(LWJGLTestContext.PROFILE_OPENGL_2_1);

      final String version = GL11.glGetString(GL11.GL_VERSION);
      final Pair<Integer, Integer> p =
        LWJGLTestContext.metaParseVersion(version);

      final boolean correct =
        ((p.first == 2) && (p.second == 1) && (LWJGLTestContext
          .metaVersionIsES(version) == false));

      if (correct) {
        System.err.println("Context " + version + " is 2.1");
      } else {
        System.err.println("Context " + version + " is not 2.1");
      }

      pb.releaseContext();
      pb.destroy();
      return correct;
    } catch (final LWJGLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Return <code>true</code> iff the created context is exactly OpenGL 3.0.
   */

  @SuppressWarnings("boxing") public static boolean isOpenGL30Supported()
  {
    try {
      final Pbuffer pb =
        LWJGLTestContext.makePbuffer(LWJGLTestContext.PROFILE_OPENGL_3_0);

      final String version = GL11.glGetString(GL11.GL_VERSION);
      final Pair<Integer, Integer> p =
        LWJGLTestContext.metaParseVersion(version);

      final boolean correct =
        ((p.first >= 3) && (p.second == 0) && (LWJGLTestContext
          .metaVersionIsES(version) == false));
      if (correct) {
        System.err.println("Context " + version + " is 3.0");
      } else {
        System.err.println("Context " + version + " is not 3.0");
      }

      pb.releaseContext();
      pb.destroy();
      return correct;
    } catch (final LWJGLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Return <code>true</code> iff the created context is OpenGL 3.n, where n
   * >= 1.
   */

  @SuppressWarnings("boxing") public static boolean isOpenGL3xSupported()
  {
    try {
      final Pbuffer pb =
        LWJGLTestContext.makePbuffer(LWJGLTestContext.PROFILE_OPENGL_3_X);

      final String version = GL11.glGetString(GL11.GL_VERSION);
      final Pair<Integer, Integer> p =
        LWJGLTestContext.metaParseVersion(version);

      final boolean correct =
        ((p.first >= 3) && (p.second >= 1) && (LWJGLTestContext
          .metaVersionIsES(version) == false));
      if (correct) {
        System.err.println("Context " + version + " is 3.n, where n >= 1");
      } else {
        System.err
          .println("Context " + version + " is not 3.n, where n >= 1");
      }

      pb.releaseContext();
      pb.destroy();
      return correct;
    } catch (final LWJGLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Return <code>true</code> iff the created context is exactly OpenGL ES2.
   */

  public static boolean isOpenGLES2Supported()
  {
    /**
     * TODO: OpenGL ES2 support seems to be completely broken as of LWJGL
     * 2.8.4 - most functions erroneously raise a "function not supported"
     * error.
     */

    return false;
  }

  public static JCGLImplementation makeContextWithOpenGL_ES2()
    throws JCGLException,
      JCGLUnsupportedException,
      ConstraintError
  {
    final Log log =
      LWJGLTestContext.getLog(LWJGLTestContext.LOG_DESTINATION_OPENGL_ES_2_0);
    final JCGLImplementation gi =
      LWJGLTestContext.makeImplementationWithOpenGL_ES2(log);
    return gi;
  }

  public static JCGLImplementation makeContextWithOpenGL21_X()
    throws JCGLException,
      JCGLUnsupportedException,
      ConstraintError
  {
    final Log log =
      LWJGLTestContext.getLog(LWJGLTestContext.LOG_DESTINATION_OPENGL_2_1);
    final JCGLImplementation gi =
      LWJGLTestContext.makeImplementationWithOpenGL_2_1(log);
    return gi;
  }

  public static JCGLImplementation makeContextWithOpenGL3_0()
    throws JCGLException,
      JCGLUnsupportedException,
      ConstraintError
  {
    final Log log =
      LWJGLTestContext.getLog(LWJGLTestContext.LOG_DESTINATION_OPENGL_3_0);
    final JCGLImplementation gi =
      LWJGLTestContext.makeImplementationWithOpenGL_3_0(log);
    return gi;
  }

  public static JCGLImplementation makeContextWithOpenGL3_X()
    throws JCGLException,
      JCGLUnsupportedException,
      ConstraintError
  {
    final Log log =
      LWJGLTestContext.getLog(LWJGLTestContext.LOG_DESTINATION_OPENGL_3_X);
    final JCGLImplementation gi =
      LWJGLTestContext.makeImplementationWithOpenGL_3_X(log);
    return gi;
  }

  public static JCGLImplementation makeImplementationWithOpenGL_2_1(
    final Log log)
    throws JCGLException,
      JCGLUnsupportedException,
      ConstraintError
  {
    LWJGLTestContext.openContext(LWJGLTestContext.PROFILE_OPENGL_2_1);
    return new JCGLImplementationLWJGL(log);
  }

  public static JCGLImplementation makeImplementationWithOpenGL_3_0(
    final Log log)
    throws JCGLException,
      JCGLUnsupportedException,
      ConstraintError
  {
    LWJGLTestContext.openContext(LWJGLTestContext.PROFILE_OPENGL_3_0);
    return new JCGLImplementationLWJGL(log);
  }

  public static JCGLImplementation makeImplementationWithOpenGL_3_X(
    final Log log)
    throws JCGLException,
      JCGLUnsupportedException,
      ConstraintError
  {
    LWJGLTestContext.openContext(LWJGLTestContext.PROFILE_OPENGL_3_X);
    return new JCGLImplementationLWJGL(log);
  }

  public static JCGLImplementation makeImplementationWithOpenGL_ES2(
    final Log log)
    throws JCGLException,
      JCGLUnsupportedException,
      ConstraintError
  {
    LWJGLTestContext.openContext(LWJGLTestContext.PROFILE_OPENGL_ES_2_0);
    return new JCGLImplementationLWJGL(log);
  }

  static Pbuffer makePbuffer(
    final Profile want)
    throws LWJGLException
  {
    final PixelFormat pixel_format = new PixelFormat(8, 24, 8);
    final ContextAttribs attribs =
      new ContextAttribs(want.version_major, want.version_minor);

    ContextAttribs attribs_w;
    if (want.version_es) {
      attribs_w = attribs.withProfileES(want.version_es);
    } else {
      attribs_w = attribs;
    }

    final Pbuffer pbuffer =
      new Pbuffer(
        want.width,
        want.height,
        pixel_format,
        null,
        null,
        attribs_w);

    pbuffer.makeCurrent();
    return pbuffer;
  }

  static Pair<Integer, Integer> metaParseVersion(
    final @Nonnull String v0)
  {
    final String v1 = v0.replaceFirst("^OpenGL ES ", "");
    final StringTokenizer tdot = new StringTokenizer(v1, ".");
    final String vmaj = tdot.nextToken();
    final String rest = tdot.nextToken();
    final StringTokenizer tspa = new StringTokenizer(rest, " ");
    final String vmin = tspa.nextToken();
    return new Pair<Integer, Integer>(
      Integer.valueOf(vmaj),
      Integer.valueOf(vmin));
  }

  static boolean metaVersionIsES(
    final @Nonnull String v0)
  {
    return v0.startsWith("OpenGL ES");
  }

  private static Pbuffer openContext(
    final Profile want)
  {
    if (LWJGLTestContext.buffer != null) {
      LWJGLTestContext.buffer.destroy();
    }

    LWJGLTestContext.buffer = LWJGLTestContext.createOffscreenDisplay(want);
    return LWJGLTestContext.buffer;
  }

  private LWJGLTestContext()
  {
    throw new UnreachableCodeException();
  }
}

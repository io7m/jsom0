package com.io7m.jsom0.tests;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.PropertyUtils;
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.ArrayBufferAttribute;
import com.io7m.jcanephora.ArrayBufferDescriptor;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jcanephora.GLInterfaceLWJGL30;
import com.io7m.jcanephora.GLScalarType;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jlog.Log;
import com.io7m.jsom0.Model;
import com.io7m.jsom0.ModelObject;

public class ModelTest
{
  @SuppressWarnings("static-method") private final GLInterface getGL()
    throws IOException,
      ConstraintError,
      GLException
  {
    final Log log =
      new Log(
        PropertyUtils.loadFromFile("io7m-jsom0.properties"),
        "com.io7m.jsom0",
        "test");
    return new GLInterfaceLWJGL30(log);
  }

  @Before public void setUp()
    throws Exception
  {
    try {
      Display.setDisplayMode(new DisplayMode(1, 1));
      Display.setTitle("ModelTest");
      Display.create();
    } catch (final LWJGLException e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  @After public void tearDown()
    throws Exception
  {
    Display.destroy();
  }

  @Test(expected = ConstraintError.class) public
    void
    testModelAddTwiceFails()
      throws IOException,
        ConstraintError,
        GLException
  {
    final GLInterface gl = this.getGL();
    final ArrayBufferDescriptor d =
      new ArrayBufferDescriptor(
        new ArrayBufferAttribute[] { new ArrayBufferAttribute(
          "nothing",
          GLScalarType.TYPE_BYTE,
          1) });
    final ArrayBuffer vb = gl.arrayBufferAllocate(1, d);
    final IndexBuffer ib = gl.indexBufferAllocate(vb, 3);

    final Model m = new Model();
    final ModelObject o = new ModelObject("name", "material", vb, ib);

    m.addObject(o);
    m.addObject(o);
  }
}

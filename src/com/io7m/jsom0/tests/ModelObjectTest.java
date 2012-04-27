package com.io7m.jsom0.tests;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
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
import com.io7m.jsom0.ModelObject;

public class ModelObjectTest
{
  @SuppressWarnings("static-method") private final GLInterface getGL()
    throws IOException,
      ConstraintError
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
      Display.setTitle("ModelObjectTest");
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

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testArrayNull()
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
    new ModelObject("name", "material", null, ib);
  }

  @Test public void testIdentities()
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
    final ModelObject o = new ModelObject("name", "material", vb, ib);

    Assert.assertEquals("name", o.getName());
    Assert.assertEquals("material", o.getMaterialName());
    Assert.assertTrue(vb == o.getArrayBuffer());
    Assert.assertTrue(ib == o.getIndexBuffer());
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testIndexNull()
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
    new ModelObject("name", "material", vb, null);
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testMaterialNull()
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
    new ModelObject("name", null, vb, ib);
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testNameNull()
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
    new ModelObject(null, "material", vb, ib);
  }

  @Test public void testToStringMaterialDifferent()
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

    final ModelObject o0 = new ModelObject("name", "material0", vb, ib);
    final ModelObject o1 = new ModelObject("name", "material1", vb, ib);

    final String s0 = o0.toString();
    final String s1 = o1.toString();

    Assert.assertFalse(s0.equals(s1));
  }

  @Test public void testToStringNameDifferent()
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

    final ModelObject o0 = new ModelObject("name0", "material", vb, ib);
    final ModelObject o1 = new ModelObject("name1", "material", vb, ib);

    final String s0 = o0.toString();
    final String s1 = o1.toString();

    Assert.assertFalse(s0.equals(s1));
  }
}

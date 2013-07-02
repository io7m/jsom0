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

package com.io7m.jsom0.contracts;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.ArrayBufferAttribute;
import com.io7m.jcanephora.ArrayBufferDescriptor;
import com.io7m.jcanephora.GLArrayBuffers;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLIndexBuffers;
import com.io7m.jcanephora.GLScalarType;
import com.io7m.jcanephora.GLUnsupportedException;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jcanephora.UsageHint;
import com.io7m.jsom0.ModelObjectVBO;
import com.io7m.jtensors.VectorI3F;

public abstract class ModelObjectVBOTestContract<G extends GLArrayBuffers & GLIndexBuffers> implements
  JSOM0GLUnmappedTestContract
{
  @Before public final void checkSupport()
  {
    Assume.assumeTrue(this.isSupported());
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testArrayNull()
      throws ConstraintError,
        GLException,
        GLUnsupportedException
  {
    final G gl = this.makeNewGL();
    final ArrayBufferDescriptor d =
      new ArrayBufferDescriptor(
        new ArrayBufferAttribute[] { new ArrayBufferAttribute(
          "nothing",
          GLScalarType.TYPE_BYTE,
          1) });
    final ArrayBuffer vb =
      gl.arrayBufferAllocate(1, d, UsageHint.USAGE_STATIC_READ);
    final IndexBuffer ib = gl.indexBufferAllocate(vb, 3);
    new ModelObjectVBO(
      "name",
      "material",
      VectorI3F.ZERO,
      VectorI3F.ZERO,
      null,
      ib);
  }

  @Test public void testIdentities()
    throws ConstraintError,
      GLException,
      GLUnsupportedException
  {
    final G gl = this.makeNewGL();
    final ArrayBufferDescriptor d =
      new ArrayBufferDescriptor(
        new ArrayBufferAttribute[] { new ArrayBufferAttribute(
          "nothing",
          GLScalarType.TYPE_BYTE,
          1) });
    final ArrayBuffer vb =
      gl.arrayBufferAllocate(1, d, UsageHint.USAGE_STATIC_READ);
    final IndexBuffer ib = gl.indexBufferAllocate(vb, 3);
    final ModelObjectVBO o =
      new ModelObjectVBO(
        "name",
        "material",
        VectorI3F.ZERO,
        VectorI3F.ZERO,
        vb,
        ib);

    Assert.assertEquals("name", o.getName());
    Assert.assertEquals("material", o.getMaterialName());
    Assert.assertTrue(vb == o.getArrayBuffer());
    Assert.assertTrue(ib == o.getIndexBuffer());
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testIndexNull()
      throws ConstraintError,
        GLException,
        GLUnsupportedException
  {
    final G gl = this.makeNewGL();
    final ArrayBufferDescriptor d =
      new ArrayBufferDescriptor(
        new ArrayBufferAttribute[] { new ArrayBufferAttribute(
          "nothing",
          GLScalarType.TYPE_BYTE,
          1) });
    final ArrayBuffer vb =
      gl.arrayBufferAllocate(1, d, UsageHint.USAGE_STATIC_READ);
    new ModelObjectVBO(
      "name",
      "material",
      VectorI3F.ZERO,
      VectorI3F.ZERO,
      vb,
      null);
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testMaterialNull()
      throws ConstraintError,
        GLException,
        GLUnsupportedException
  {
    final G gl = this.makeNewGL();
    final ArrayBufferDescriptor d =
      new ArrayBufferDescriptor(
        new ArrayBufferAttribute[] { new ArrayBufferAttribute(
          "nothing",
          GLScalarType.TYPE_BYTE,
          1) });
    final ArrayBuffer vb =
      gl.arrayBufferAllocate(1, d, UsageHint.USAGE_STATIC_READ);
    final IndexBuffer ib = gl.indexBufferAllocate(vb, 3);
    new ModelObjectVBO("name", null, VectorI3F.ZERO, VectorI3F.ZERO, vb, ib);
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testNameNull()
      throws ConstraintError,
        GLException,
        GLUnsupportedException
  {
    final G gl = this.makeNewGL();
    final ArrayBufferDescriptor d =
      new ArrayBufferDescriptor(
        new ArrayBufferAttribute[] { new ArrayBufferAttribute(
          "nothing",
          GLScalarType.TYPE_BYTE,
          1) });
    final ArrayBuffer vb =
      gl.arrayBufferAllocate(1, d, UsageHint.USAGE_STATIC_READ);
    final IndexBuffer ib = gl.indexBufferAllocate(vb, 3);
    new ModelObjectVBO(
      null,
      "material",
      VectorI3F.ZERO,
      VectorI3F.ZERO,
      vb,
      ib);
  }

  @Test public void testToStringMaterialDifferent()
    throws ConstraintError,
      GLException,
      GLUnsupportedException
  {
    final G gl = this.makeNewGL();
    final ArrayBufferDescriptor d =
      new ArrayBufferDescriptor(
        new ArrayBufferAttribute[] { new ArrayBufferAttribute(
          "nothing",
          GLScalarType.TYPE_BYTE,
          1) });
    final ArrayBuffer vb =
      gl.arrayBufferAllocate(1, d, UsageHint.USAGE_STATIC_READ);
    final IndexBuffer ib = gl.indexBufferAllocate(vb, 3);

    final ModelObjectVBO o0 =
      new ModelObjectVBO(
        "name",
        "material0",
        VectorI3F.ZERO,
        VectorI3F.ZERO,
        vb,
        ib);
    final ModelObjectVBO o1 =
      new ModelObjectVBO(
        "name",
        "material1",
        VectorI3F.ZERO,
        VectorI3F.ZERO,
        vb,
        ib);

    final String s0 = o0.toString();
    final String s1 = o1.toString();

    Assert.assertFalse(s0.equals(s1));
  }

  @Test public void testToStringNameDifferent()
    throws ConstraintError,
      GLException,
      GLUnsupportedException
  {
    final G gl = this.makeNewGL();
    final ArrayBufferDescriptor d =
      new ArrayBufferDescriptor(
        new ArrayBufferAttribute[] { new ArrayBufferAttribute(
          "nothing",
          GLScalarType.TYPE_BYTE,
          1) });
    final ArrayBuffer vb =
      gl.arrayBufferAllocate(1, d, UsageHint.USAGE_STATIC_READ);
    final IndexBuffer ib = gl.indexBufferAllocate(vb, 3);

    final ModelObjectVBO o0 =
      new ModelObjectVBO(
        "name0",
        "material",
        VectorI3F.ZERO,
        VectorI3F.ZERO,
        vb,
        ib);
    final ModelObjectVBO o1 =
      new ModelObjectVBO(
        "name1",
        "material",
        VectorI3F.ZERO,
        VectorI3F.ZERO,
        vb,
        ib);

    final String s0 = o0.toString();
    final String s1 = o1.toString();

    Assert.assertFalse(s0.equals(s1));
  }
}

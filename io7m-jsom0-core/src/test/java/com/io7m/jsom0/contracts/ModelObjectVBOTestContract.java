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
import com.io7m.jcanephora.ArrayBufferAttributeDescriptor;
import com.io7m.jcanephora.ArrayBufferTypeDescriptor;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jcanephora.JCGLArrayBuffers;
import com.io7m.jcanephora.JCGLException;
import com.io7m.jcanephora.JCGLIndexBuffers;
import com.io7m.jcanephora.JCGLScalarType;
import com.io7m.jcanephora.JCGLUnsupportedException;
import com.io7m.jcanephora.UsageHint;
import com.io7m.jsom0.ModelObjectVBO;
import com.io7m.jsom0.NameNormalAttribute;
import com.io7m.jsom0.NamePositionAttribute;
import com.io7m.jsom0.NameUVAttribute;
import com.io7m.jtensors.VectorI3F;

public abstract class ModelObjectVBOTestContract<G extends JCGLArrayBuffers & JCGLIndexBuffers> implements
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
        JCGLException,
        JCGLUnsupportedException
  {
    final G gl = this.makeNewGL();
    final ArrayBufferTypeDescriptor d =
      new ArrayBufferTypeDescriptor(
        new ArrayBufferAttributeDescriptor[] { new ArrayBufferAttributeDescriptor(
          "nothing",
          JCGLScalarType.TYPE_BYTE,
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
      ib,
      new NamePositionAttribute("position"),
      new NameNormalAttribute("normal"),
      new NameUVAttribute("uv"));
  }

  @Test public void testIdentities()
    throws ConstraintError,
      JCGLException,
      JCGLUnsupportedException
  {
    final G gl = this.makeNewGL();
    final ArrayBufferTypeDescriptor d =
      new ArrayBufferTypeDescriptor(
        new ArrayBufferAttributeDescriptor[] { new ArrayBufferAttributeDescriptor(
          "nothing",
          JCGLScalarType.TYPE_BYTE,
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
        ib,
        new NamePositionAttribute("position"),
        new NameNormalAttribute("normal"),
        new NameUVAttribute("uv"));

    Assert.assertEquals("name", o.getName());
    Assert.assertEquals("material", o.getMaterialName());
    Assert.assertTrue(vb == o.getArrayBuffer());
    Assert.assertTrue(ib == o.getIndexBuffer());
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testIndexNull()
      throws ConstraintError,
        JCGLException,
        JCGLUnsupportedException
  {
    final G gl = this.makeNewGL();
    final ArrayBufferTypeDescriptor d =
      new ArrayBufferTypeDescriptor(
        new ArrayBufferAttributeDescriptor[] { new ArrayBufferAttributeDescriptor(
          "nothing",
          JCGLScalarType.TYPE_BYTE,
          1) });
    final ArrayBuffer vb =
      gl.arrayBufferAllocate(1, d, UsageHint.USAGE_STATIC_READ);
    new ModelObjectVBO(
      "name",
      "material",
      VectorI3F.ZERO,
      VectorI3F.ZERO,
      vb,
      null,
      new NamePositionAttribute("position"),
      new NameNormalAttribute("normal"),
      new NameUVAttribute("uv"));
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testMaterialNull()
      throws ConstraintError,
        JCGLException,
        JCGLUnsupportedException
  {
    final G gl = this.makeNewGL();
    final ArrayBufferTypeDescriptor d =
      new ArrayBufferTypeDescriptor(
        new ArrayBufferAttributeDescriptor[] { new ArrayBufferAttributeDescriptor(
          "nothing",
          JCGLScalarType.TYPE_BYTE,
          1) });
    final ArrayBuffer vb =
      gl.arrayBufferAllocate(1, d, UsageHint.USAGE_STATIC_READ);
    final IndexBuffer ib = gl.indexBufferAllocate(vb, 3);
    new ModelObjectVBO(
      "name",
      null,
      VectorI3F.ZERO,
      VectorI3F.ZERO,
      vb,
      ib,
      new NamePositionAttribute("position"),
      new NameNormalAttribute("normal"),
      new NameUVAttribute("uv"));
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testNameNull()
      throws ConstraintError,
        JCGLException,
        JCGLUnsupportedException
  {
    final G gl = this.makeNewGL();
    final ArrayBufferTypeDescriptor d =
      new ArrayBufferTypeDescriptor(
        new ArrayBufferAttributeDescriptor[] { new ArrayBufferAttributeDescriptor(
          "nothing",
          JCGLScalarType.TYPE_BYTE,
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
      ib,
      new NamePositionAttribute("position"),
      new NameNormalAttribute("normal"),
      new NameUVAttribute("uv"));
  }

  @Test public void testToStringMaterialDifferent()
    throws ConstraintError,
      JCGLException,
      JCGLUnsupportedException
  {
    final G gl = this.makeNewGL();
    final ArrayBufferTypeDescriptor d =
      new ArrayBufferTypeDescriptor(
        new ArrayBufferAttributeDescriptor[] { new ArrayBufferAttributeDescriptor(
          "nothing",
          JCGLScalarType.TYPE_BYTE,
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
        ib,
        new NamePositionAttribute("position"),
        new NameNormalAttribute("normal"),
        new NameUVAttribute("uv"));

    final ModelObjectVBO o1 =
      new ModelObjectVBO(
        "name",
        "material1",
        VectorI3F.ZERO,
        VectorI3F.ZERO,
        vb,
        ib,
        new NamePositionAttribute("position"),
        new NameNormalAttribute("normal"),
        new NameUVAttribute("uv"));

    final String s0 = o0.toString();
    final String s1 = o1.toString();

    Assert.assertFalse(s0.equals(s1));
  }

  @Test public void testToStringNameDifferent()
    throws ConstraintError,
      JCGLException,
      JCGLUnsupportedException
  {
    final G gl = this.makeNewGL();
    final ArrayBufferTypeDescriptor d =
      new ArrayBufferTypeDescriptor(
        new ArrayBufferAttributeDescriptor[] { new ArrayBufferAttributeDescriptor(
          "nothing",
          JCGLScalarType.TYPE_BYTE,
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
        ib,
        new NamePositionAttribute("position"),
        new NameNormalAttribute("normal"),
        new NameUVAttribute("uv"));

    final ModelObjectVBO o1 =
      new ModelObjectVBO(
        "name1",
        "material",
        VectorI3F.ZERO,
        VectorI3F.ZERO,
        vb,
        ib,
        new NamePositionAttribute("position"),
        new NameNormalAttribute("normal"),
        new NameUVAttribute("uv"));

    final String s0 = o0.toString();
    final String s1 = o1.toString();

    Assert.assertFalse(s0.equals(s1));
  }
}

package com.io7m.jsom0.contracts;

import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.ArrayBufferAttribute;
import com.io7m.jcanephora.ArrayBufferDescriptor;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jcanephora.GLScalarType;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jsom0.Model;
import com.io7m.jsom0.ModelObject;

public abstract class ModelTestContract implements Jsom0GLTestContract
{
  @Test(expected = ConstraintError.class) public
    void
    testModelAddTwiceFails()
      throws ConstraintError,
        GLException
  {
    final GLInterface gl = this.makeNewGL();
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

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

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jtensors.VectorI3F;

public class TestModelObjectTest
{
  @SuppressWarnings("static-method") @Test public void testCompare()
    throws ConstraintError
  {
    final TestModelObject o0 =
      new TestModelObject("A", "M", VectorI3F.ZERO, VectorI3F.ZERO);
    final TestModelObject o0x =
      new TestModelObject("A", "M", VectorI3F.ZERO, VectorI3F.ZERO);
    final TestModelObject o1 =
      new TestModelObject("B", "M", VectorI3F.ZERO, VectorI3F.ZERO);
    final TestModelObject o2 =
      new TestModelObject("C", "M", VectorI3F.ZERO, VectorI3F.ZERO);

    // Reflexive
    Assert.assertTrue(o0.compareTo(o0) == 0);

    // Symmetric
    Assert.assertTrue(o0.compareTo(o0x) == 0);
    Assert.assertTrue(o0x.compareTo(o0) == 0);

    // Ordered, transitive
    Assert.assertTrue(o0.compareTo(o1) < 0);
    Assert.assertTrue(o1.compareTo(o2) < 0);
    Assert.assertTrue(o0.compareTo(o2) < 0);
    Assert.assertTrue(o1.compareTo(o0) > 0);
    Assert.assertTrue(o2.compareTo(o1) > 0);
    Assert.assertTrue(o2.compareTo(o0) > 0);
  }
}

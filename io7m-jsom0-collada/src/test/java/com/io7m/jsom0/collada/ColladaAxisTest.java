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

package com.io7m.jsom0.collada;

import javax.annotation.Nonnull;

import net.java.quickcheck.Generator;
import net.java.quickcheck.QuickCheck;
import net.java.quickcheck.characteristic.AbstractCharacteristic;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.AlmostEqualFloat;
import com.io7m.jaux.AlmostEqualFloat.ContextRelative;
import com.io7m.jtensors.MatrixM4x4F;
import com.io7m.jtensors.MatrixM4x4F.Context;
import com.io7m.jtensors.VectorI3F;
import com.io7m.jtensors.VectorReadable3F;

public final class ColladaAxisTest
{
  static class VectorI3FGenerator implements Generator<VectorI3F>
  {
    public VectorI3FGenerator()
    {

    }

    @Override public VectorI3F next()
    {
      final float x = (float) Math.random();
      final float y = (float) Math.random();
      final float z = (float) Math.random();
      return new VectorI3F(x, y, z);
    }
  }

  private static @Nonnull ContextRelative makeRelativeContext()
  {
    final ContextRelative cr = new AlmostEqualFloat.ContextRelative();
    cr.setMaxAbsoluteDifference(0.0000000000000001f);
    return cr;
  }

  @SuppressWarnings("static-method") @Test public void testXUpToXUp()
  {
    final MatrixM4x4F matrix = new MatrixM4x4F();
    final Context context = new MatrixM4x4F.Context();

    QuickCheck.forAllVerbose(
      new VectorI3FGenerator(),
      new AbstractCharacteristic<VectorI3F>() {
        @Override protected void doSpecify(
          final @Nonnull VectorI3F v)
          throws Throwable
        {
          Assert.assertEquals(v, ColladaAxis.convertAxes(
            context,
            matrix,
            ColladaAxis.COLLADA_AXIS_X_UP,
            v,
            ColladaAxis.COLLADA_AXIS_X_UP));
        }
      });
  }

  @SuppressWarnings("static-method") @Test public void testXUpToYUp()
  {
    final MatrixM4x4F matrix = new MatrixM4x4F();
    final Context context = new MatrixM4x4F.Context();
    final ContextRelative cr = ColladaAxisTest.makeRelativeContext();

    final VectorI3F v_in = new VectorI3F(1, 0, 0);
    final VectorI3F v_exp = new VectorI3F(0, 1, 0);
    final VectorReadable3F v_out =
      ColladaAxis.convertAxes(
        context,
        matrix,
        ColladaAxis.COLLADA_AXIS_X_UP,
        v_in,
        ColladaAxis.COLLADA_AXIS_Y_UP);

    System.out.println("v_in  : " + v_in);
    System.out.println("v_exp : " + v_exp);
    System.out.println("v_out : " + v_out);

    Assert.assertTrue(VectorI3F.almostEqual(cr, v_exp, v_out));
  }

  @SuppressWarnings("static-method") @Test public void testXUpToZUp()
  {
    final MatrixM4x4F matrix = new MatrixM4x4F();
    final Context context = new MatrixM4x4F.Context();
    final ContextRelative cr = ColladaAxisTest.makeRelativeContext();

    final VectorI3F v_in = new VectorI3F(1, 0, 0);
    final VectorI3F v_exp = new VectorI3F(0, 0, 1);
    final VectorReadable3F v_out =
      ColladaAxis.convertAxes(
        context,
        matrix,
        ColladaAxis.COLLADA_AXIS_X_UP,
        v_in,
        ColladaAxis.COLLADA_AXIS_Z_UP);

    System.out.println("v_in  : " + v_in);
    System.out.println("v_exp : " + v_exp);
    System.out.println("v_out : " + v_out);

    Assert.assertTrue(VectorI3F.almostEqual(cr, v_exp, v_out));
  }

  @SuppressWarnings("static-method") @Test public void testYUpToXUp()
  {
    final MatrixM4x4F matrix = new MatrixM4x4F();
    final Context context = new MatrixM4x4F.Context();
    final ContextRelative cr = ColladaAxisTest.makeRelativeContext();

    final VectorI3F v_in = new VectorI3F(0, 1, 0);
    final VectorI3F v_exp = new VectorI3F(1, 0, 0);
    final VectorReadable3F v_out =
      ColladaAxis.convertAxes(
        context,
        matrix,
        ColladaAxis.COLLADA_AXIS_Y_UP,
        v_in,
        ColladaAxis.COLLADA_AXIS_X_UP);

    System.out.println("v_in  : " + v_in);
    System.out.println("v_exp : " + v_exp);
    System.out.println("v_out : " + v_out);

    Assert.assertTrue(VectorI3F.almostEqual(cr, v_exp, v_out));
  }

  @SuppressWarnings("static-method") @Test public void testYUpToYUp()
  {
    final MatrixM4x4F matrix = new MatrixM4x4F();
    final Context context = new MatrixM4x4F.Context();

    QuickCheck.forAllVerbose(
      new VectorI3FGenerator(),
      new AbstractCharacteristic<VectorI3F>() {
        @Override protected void doSpecify(
          final @Nonnull VectorI3F v)
          throws Throwable
        {
          Assert.assertEquals(v, ColladaAxis.convertAxes(
            context,
            matrix,
            ColladaAxis.COLLADA_AXIS_Y_UP,
            v,
            ColladaAxis.COLLADA_AXIS_Y_UP));
        }
      });
  }

  @SuppressWarnings("static-method") @Test public void testYUpToZUp()
  {
    final MatrixM4x4F matrix = new MatrixM4x4F();
    final Context context = new MatrixM4x4F.Context();
    final ContextRelative cr = ColladaAxisTest.makeRelativeContext();

    final VectorI3F v_in = new VectorI3F(0, 1, 0);
    final VectorI3F v_exp = new VectorI3F(0, 0, 1);
    final VectorReadable3F v_out =
      ColladaAxis.convertAxes(
        context,
        matrix,
        ColladaAxis.COLLADA_AXIS_Y_UP,
        v_in,
        ColladaAxis.COLLADA_AXIS_Z_UP);

    System.out.println("v_in  : " + v_in);
    System.out.println("v_exp : " + v_exp);
    System.out.println("v_out : " + v_out);

    Assert.assertTrue(VectorI3F.almostEqual(cr, v_exp, v_out));
  }

  @SuppressWarnings("static-method") @Test public void testZUpToXUp()
  {
    final MatrixM4x4F matrix = new MatrixM4x4F();
    final Context context = new MatrixM4x4F.Context();
    final ContextRelative cr = ColladaAxisTest.makeRelativeContext();

    final VectorI3F v_in = new VectorI3F(0, 0, 1);
    final VectorI3F v_exp = new VectorI3F(1, 0, 0);
    final VectorReadable3F v_out =
      ColladaAxis.convertAxes(
        context,
        matrix,
        ColladaAxis.COLLADA_AXIS_Z_UP,
        v_in,
        ColladaAxis.COLLADA_AXIS_X_UP);

    System.out.println("v_in  : " + v_in);
    System.out.println("v_exp : " + v_exp);
    System.out.println("v_out : " + v_out);

    Assert.assertTrue(VectorI3F.almostEqual(cr, v_exp, v_out));
  }

  @SuppressWarnings("static-method") @Test public void testZUpToYUp()
  {
    final MatrixM4x4F matrix = new MatrixM4x4F();
    final Context context = new MatrixM4x4F.Context();
    final ContextRelative cr = ColladaAxisTest.makeRelativeContext();

    final VectorI3F v_in = new VectorI3F(0, 0, 1);
    final VectorI3F v_exp = new VectorI3F(0, 1, 0);
    final VectorReadable3F v_out =
      ColladaAxis.convertAxes(
        context,
        matrix,
        ColladaAxis.COLLADA_AXIS_Z_UP,
        v_in,
        ColladaAxis.COLLADA_AXIS_Y_UP);

    System.out.println("v_in  : " + v_in);
    System.out.println("v_exp : " + v_exp);
    System.out.println("v_out : " + v_out);

    Assert.assertTrue(VectorI3F.almostEqual(cr, v_exp, v_out));
  }

  @SuppressWarnings("static-method") @Test public void testZUpToZUp()
  {
    final MatrixM4x4F matrix = new MatrixM4x4F();
    final Context context = new MatrixM4x4F.Context();

    QuickCheck.forAllVerbose(
      new VectorI3FGenerator(),
      new AbstractCharacteristic<VectorI3F>() {
        @Override protected void doSpecify(
          final @Nonnull VectorI3F v)
          throws Throwable
        {
          Assert.assertEquals(v, ColladaAxis.convertAxes(
            context,
            matrix,
            ColladaAxis.COLLADA_AXIS_Z_UP,
            v,
            ColladaAxis.COLLADA_AXIS_Z_UP));
        }
      });
  }
}

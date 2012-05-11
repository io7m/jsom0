package com.io7m.jsom0.tests.parser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.PropertyUtils;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterfaceLWJGL30;
import com.io7m.jlog.Log;
import com.io7m.jsom0.parser.Error;
import com.io7m.jsom0.parser.ModelObjectParser;
import com.io7m.jsom0.parser.ModelObjectTokenType;

public class ModelObjectParserTest
{
  private static ModelObjectParser getParser(
    final String file)
    throws IOException,
      Error,
      ConstraintError,
      GLException
  {
    final Log log =
      new Log(
        PropertyUtils.loadFromFile("io7m-jsom0.properties"),
        "com.io7m.jsom0",
        "test");

    final BufferedInputStream stream =
      new BufferedInputStream(new FileInputStream(file));
    final GLInterfaceLWJGL30 gl = new GLInterfaceLWJGL30(log);
    final ModelObjectParser parser =
      new ModelObjectParser(file, stream, gl, log);
    return parser;
  }

  @Before public void setUp()
    throws Exception
  {
    try {
      Display.setDisplayMode(new DisplayMode(1, 1));
      Display.setTitle("ModelObjectParserMain");
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

  @Test(expected = Error.class) public void testInvalidEmpty()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    try {
      final ModelObjectParser parser =
        ModelObjectParserTest.getParser("test-data/inv-empty.i7o");
      parser.modelObject();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_UNEXPECTED_TOKEN,
        e.errorCode());
      Assert.assertEquals(
        ModelObjectTokenType.OBJECT_TOKEN_SYMBOL,
        e.expectedTokenType());
      Assert.assertEquals(
        ModelObjectTokenType.OBJECT_TOKEN_EOF,
        e.receivedTokenType());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidIndexArrayBadType()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    try {
      final ModelObjectParser parser =
        ModelObjectParserTest
          .getParser("test-data/inv-index-array-bad-type.i7o");
      parser.modelObject();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_UNEXPECTED_SYMBOL,
        e.errorCode());
      Assert.assertEquals(
        ModelObjectTokenType.OBJECT_TOKEN_SYMBOL,
        e.receivedTokenType());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidIndexArrayBadType2()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    try {
      final ModelObjectParser parser =
        ModelObjectParserTest
          .getParser("test-data/inv-index-array-bad-type2.i7o");
      parser.modelObject();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_UNEXPECTED_SYMBOL,
        e.errorCode());
      Assert.assertEquals(
        ModelObjectTokenType.OBJECT_TOKEN_SYMBOL,
        e.receivedTokenType());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidIndexArrayLarge()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    try {
      final ModelObjectParser parser =
        ModelObjectParserTest
          .getParser("test-data/inv-index-array-large.i7o");
      parser.modelObject();
    } catch (final Error e) {
      Assert.assertEquals(Error.Code.ERROR_CODE_RANGE_ERROR, e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidIndexArrayZero()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    try {
      final ModelObjectParser parser =
        ModelObjectParserTest.getParser("test-data/inv-index-array-zero.i7o");
      parser.modelObject();
    } catch (final Error e) {
      Assert.assertEquals(Error.Code.ERROR_CODE_RANGE_ERROR, e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidIndexNegative()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    try {
      final ModelObjectParser parser =
        ModelObjectParserTest
          .getParser("test-data/inv-index-array-negative-index.i7o");
      parser.modelObject();
    } catch (final Error e) {
      Assert.assertEquals(Error.Code.ERROR_CODE_RANGE_ERROR, e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidIndexOut()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    try {
      final ModelObjectParser parser =
        ModelObjectParserTest.getParser("test-data/inv-index-array-out.i7o");
      parser.modelObject();
    } catch (final Error e) {
      Assert.assertEquals(Error.Code.ERROR_CODE_RANGE_ERROR, e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public
    void
    testInvalidMaterialNameNotString()
      throws IOException,
        Error,
        GLException,
        ConstraintError
  {
    try {
      final ModelObjectParser parser =
        ModelObjectParserTest
          .getParser("test-data/inv-material-name-not-string.i7o");
      parser.modelObject();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_UNEXPECTED_TOKEN,
        e.errorCode());
      Assert.assertEquals(
        ModelObjectTokenType.OBJECT_TOKEN_LITERAL_STRING,
        e.expectedTokenType());
      Assert.assertEquals(
        ModelObjectTokenType.OBJECT_TOKEN_LITERAL_DECIMAL,
        e.receivedTokenType());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidNameNotString()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    try {
      final ModelObjectParser parser =
        ModelObjectParserTest.getParser("test-data/inv-name-not-string.i7o");
      parser.modelObject();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_UNEXPECTED_TOKEN,
        e.errorCode());
      Assert.assertEquals(
        ModelObjectTokenType.OBJECT_TOKEN_LITERAL_STRING,
        e.expectedTokenType());
      Assert.assertEquals(
        ModelObjectTokenType.OBJECT_TOKEN_LITERAL_DECIMAL,
        e.receivedTokenType());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidSemi()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    try {
      final ModelObjectParser parser =
        ModelObjectParserTest.getParser("test-data/inv-semi.i7o");
      parser.modelObject();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_UNEXPECTED_TOKEN,
        e.errorCode());
      Assert.assertEquals(
        ModelObjectTokenType.OBJECT_TOKEN_SYMBOL,
        e.expectedTokenType());
      Assert.assertEquals(
        ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON,
        e.receivedTokenType());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidVertexArrayBadType()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    try {
      final ModelObjectParser parser =
        ModelObjectParserTest
          .getParser("test-data/inv-vertex-array-bad-type.i7o");
      parser.modelObject();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_UNEXPECTED_TOKEN,
        e.errorCode());
      Assert.assertEquals(
        ModelObjectTokenType.OBJECT_TOKEN_SYMBOL,
        e.receivedTokenType());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidVertexArrayBadType2()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    try {
      final ModelObjectParser parser =
        ModelObjectParserTest
          .getParser("test-data/inv-vertex-array-bad-type2.i7o");
      parser.modelObject();
    } catch (final Error e) {
      Assert.assertEquals(
        Error.Code.ERROR_CODE_UNEXPECTED_SYMBOL,
        e.errorCode());
      Assert.assertEquals(
        ModelObjectTokenType.OBJECT_TOKEN_SYMBOL,
        e.receivedTokenType());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidVertexArrayLarge()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    try {
      final ModelObjectParser parser =
        ModelObjectParserTest
          .getParser("test-data/inv-vertex-array-large.i7o");
      parser.modelObject();
    } catch (final Error e) {
      Assert.assertEquals(Error.Code.ERROR_CODE_RANGE_ERROR, e.errorCode());
      throw e;
    }
  }

  @Test(expected = Error.class) public void testInvalidVertexArrayZero()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    try {
      final ModelObjectParser parser =
        ModelObjectParserTest
          .getParser("test-data/inv-vertex-array-zero.i7o");
      parser.modelObject();
    } catch (final Error e) {
      Assert.assertEquals(Error.Code.ERROR_CODE_RANGE_ERROR, e.errorCode());
      throw e;
    }
  }

  @Test public void testValidComplex0()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    final ModelObjectParser parser =
      ModelObjectParserTest.getParser("test-data/val-complex.i7o");
    parser.modelObject();
  }

  @Test public void testValidComplex1()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    final ModelObjectParser parser =
      ModelObjectParserTest.getParser("test-data/val-complex1.i7o");
    parser.modelObject();
  }

  @Test public void testValidSimple0()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    final ModelObjectParser parser =
      ModelObjectParserTest.getParser("test-data/val-simple0.i7o");
    parser.modelObject();
  }

  @Test public void testValidSimple1()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    final ModelObjectParser parser =
      ModelObjectParserTest.getParser("test-data/val-simple1.i7o");
    parser.modelObject();
  }
}

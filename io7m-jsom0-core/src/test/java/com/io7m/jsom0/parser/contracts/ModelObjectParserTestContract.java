package com.io7m.jsom0.parser.contracts;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Nonnull;

import junit.framework.Assert;

import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jlog.Log;
import com.io7m.jsom0.parser.Error;
import com.io7m.jsom0.parser.ModelObjectParser;
import com.io7m.jsom0.parser.ModelObjectTokenType;

public abstract class ModelObjectParserTestContract implements
  ModelObjectParserContract
{
  @Override @SuppressWarnings("resource") public ModelObjectParser getParser(
    final @Nonnull String file,
    final @Nonnull GLInterface gl)
    throws IOException,
      Error,
      ConstraintError
  {
    final InputStream rstream =
      ModelMaterialParserTestContract.class
        .getResourceAsStream("/com/io7m/jsom0/test.properties");
    final Properties props = new Properties();
    props.load(rstream);
    rstream.close();
    final Log log = new Log(props, "com.io7m.jsom0", "test");

    final InputStream fstream =
      ModelMaterialParserTestContract.class.getResourceAsStream(file);
    final ModelObjectParser parser =
      new ModelObjectParser(file, fstream, gl, log);
    return parser;
  }

  @Test(expected = Error.class) public void testInvalidEmpty()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    try {
      final ModelObjectParser parser =
        this.getParser("/com/io7m/jsom0/inv-empty.i7o", this.makeNewGL());
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
        this.getParser(
          "/com/io7m/jsom0/inv-index-array-bad-type.i7o",
          this.makeNewGL());
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
        this.getParser(
          "/com/io7m/jsom0/inv-index-array-bad-type2.i7o",
          this.makeNewGL());
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
        this.getParser(
          "/com/io7m/jsom0/inv-index-array-large.i7o",
          this.makeNewGL());
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
        this.getParser(
          "/com/io7m/jsom0/inv-index-array-zero.i7o",
          this.makeNewGL());
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
        this.getParser(
          "/com/io7m/jsom0/inv-index-array-negative-index.i7o",
          this.makeNewGL());
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
        this.getParser(
          "/com/io7m/jsom0/inv-index-array-out.i7o",
          this.makeNewGL());
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
        this.getParser(
          "/com/io7m/jsom0/inv-material-name-not-string.i7o",
          this.makeNewGL());
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
        this.getParser(
          "/com/io7m/jsom0/inv-name-not-string.i7o",
          this.makeNewGL());
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
        this.getParser("/com/io7m/jsom0/inv-semi.i7o", this.makeNewGL());
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
        this.getParser(
          "/com/io7m/jsom0/inv-vertex-array-bad-type.i7o",
          this.makeNewGL());
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
        this.getParser(
          "/com/io7m/jsom0/inv-vertex-array-bad-type2.i7o",
          this.makeNewGL());
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
        this.getParser(
          "/com/io7m/jsom0/inv-vertex-array-large.i7o",
          this.makeNewGL());
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
        this.getParser(
          "/com/io7m/jsom0/inv-vertex-array-zero.i7o",
          this.makeNewGL());
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
      this.getParser("/com/io7m/jsom0/val-complex.i7o", this.makeNewGL());
    parser.modelObject();
  }

  @Test public void testValidComplex1()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    final ModelObjectParser parser =
      this.getParser("/com/io7m/jsom0/val-complex1.i7o", this.makeNewGL());
    parser.modelObject();
  }

  @Test public void testValidSimple0()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    final ModelObjectParser parser =
      this.getParser("/com/io7m/jsom0/val-simple0.i7o", this.makeNewGL());
    parser.modelObject();
  }

  @Test public void testValidSimple1()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    final ModelObjectParser parser =
      this.getParser("/com/io7m/jsom0/val-simple1.i7o", this.makeNewGL());
    parser.modelObject();
  }
}

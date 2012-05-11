package com.io7m.jsom0.tests.parser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
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
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterfaceLWJGL30;
import com.io7m.jlog.Log;
import com.io7m.jsom0.Model;
import com.io7m.jsom0.parser.Error;
import com.io7m.jsom0.parser.ModelParser;

public class ModelParserTest
{
  private static ModelParser getParser(
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
    return ModelParser.makeParser(file, stream, gl, log);
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

  @Test public void testValidMultiComplex0()
    throws IOException,
      Error,
      ConstraintError,
      GLException
  {
    final ModelParser parser =
      ModelParserTest.getParser("test-data/val-multi-complex0.i7o");
    final Model model = parser.model();
    Assert.assertTrue(model.exists("bluecube"));
    Assert.assertTrue(model.exists("chromedonut"));
    Assert.assertTrue(model.exists("greencube"));
    Assert.assertTrue(model.exists("greentri"));
    Assert.assertTrue(model.exists("multicube"));
    Assert.assertTrue(model.exists("redcube"));
    Assert.assertTrue(model.exists("wasppyr"));
    Assert.assertTrue(model.exists("wasptri"));
  }

  @Test public void testValidSimple0()
    throws IOException,
      Error,
      ConstraintError,
      GLException
  {
    final ModelParser parser =
      ModelParserTest.getParser("test-data/val-simple0.i7o");
    final Model model = parser.model();
    Assert.assertTrue(model.exists("name"));
  }
}

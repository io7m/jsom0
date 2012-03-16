package com.io7m.jsom0.tests.parser;

import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.PropertyUtils;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterfaceLWJGL30;
import com.io7m.jlog.Log;
import com.io7m.jsom0.ModelObject;
import com.io7m.jsom0.parser.Error;
import com.io7m.jsom0.parser.ModelObjectParser;

public final class ModelObjectParserMain
{
  private static Log getLog()
    throws IOException,
      ConstraintError
  {
    return new Log(
      PropertyUtils.loadFromFile("io7m-jsom0.properties"),
      "com.io7m.jsom0",
      "main");
  }

  private static void initDisplay()
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

  public static void main(
    final String args[])
    throws IOException,
      ConstraintError,
      Error,
      GLException
  {
    final Log log = ModelObjectParserMain.getLog();
    final GLInterfaceLWJGL30 gl = new GLInterfaceLWJGL30(log);
    final ModelObjectParser parser =
      new ModelObjectParser("<stdin>", System.in, gl, log);
    ModelObjectParserMain.initDisplay();

    final ModelObject object = parser.modelObject();
    System.out.println(object);
  }
}

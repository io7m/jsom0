package com.io7m.jsom0.tests.parser;

import java.io.IOException;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.PropertyUtils;
import com.io7m.jlog.Log;
import com.io7m.jsom0.ModelMaterial;
import com.io7m.jsom0.parser.Error;
import com.io7m.jsom0.parser.ModelMaterialParser;

public final class ModelMaterialParserMain
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

  public static void main(
    final String args[])
    throws IOException,
      ConstraintError,
      Error
  {
    final ModelMaterialParser parser =
      new ModelMaterialParser(
        "<stdin>",
        System.in,
        ModelMaterialParserMain.getLog());

    final ModelMaterial material = parser.modelMaterial();
    System.out.println(material);
  }
}

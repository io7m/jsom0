package com.io7m.jsom0.parser.contracts;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jsom0.Model;
import com.io7m.jsom0.parser.Error;
import com.io7m.jsom0.parser.ModelParser;

public abstract class ModelParserTestContract implements ModelParserContract
{
  @Test public void testValidMultiComplex0()
    throws IOException,
      Error,
      ConstraintError,
      GLException
  {
    final ModelParser parser =
      this.getParser("/com/io7m/jsom0/val-multi-complex0.i7o");
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
      this.getParser("/com/io7m/jsom0/val-simple0.i7o");
    final Model model = parser.model();
    Assert.assertTrue(model.exists("name"));
  }
}

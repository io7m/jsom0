package com.io7m.jsom0.contracts;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterfaceEmbedded;

public interface JSOM0GLEmbeddedTestContract extends JSOM0LogTestContract
{
  public GLInterfaceEmbedded makeNewGL()
    throws GLException,
      ConstraintError;
}

package com.io7m.jsom0.contracts;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;

public interface JSOM0GLTestContract extends JSOM0LogTestContract
{
  public GLInterface makeNewGL()
    throws GLException,
      ConstraintError;
}

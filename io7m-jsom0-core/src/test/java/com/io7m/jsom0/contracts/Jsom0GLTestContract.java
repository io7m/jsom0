package com.io7m.jsom0.contracts;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jlog.Log;

public interface Jsom0GLTestContract
{
  public Log getLog();

  public GLInterface makeNewGL()
    throws GLException,
      ConstraintError;
}

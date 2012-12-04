package com.io7m.jsom0;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Nonnull;

import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jlog.Log;

public final class TestUtilities
{
  private TestUtilities()
  {
    throw new UnreachableCodeException();
  }

  public static @Nonnull Log getLog()
    throws IOException
  {
    final InputStream rstream =
      TestUtilities.class
        .getResourceAsStream("/com/io7m/jsom0/test.properties");
    final Properties props = new Properties();
    props.load(rstream);
    rstream.close();
    return new Log(props, "com.io7m.jsom0", "test");
  }
}

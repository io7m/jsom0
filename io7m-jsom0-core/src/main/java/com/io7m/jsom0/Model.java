/*
 * Copyright © 2013 <code@io7m.com> http://io7m.com
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jsom0;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Function;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Unit;

public final class Model<O extends ModelObject>
{
  private final @Nonnull HashMap<String, O> objects;

  public Model()
  {
    this.objects = new HashMap<String, O>();
  }

  public void addObject(
    final @Nonnull O object)
    throws ConstraintError
  {
    Constraints.constrainNotNull(object, "object");
    Constraints.constrainArbitrary(
      this.objects.containsKey(object.getName()) == false,
      "object " + object.getName() + " nonexistent");
    this.objects.put(object.getName(), object);
  }

  public boolean exists(
    final @Nonnull String name)
    throws ConstraintError
  {
    Constraints.constrainNotNull(name, "object name");
    return this.objects.containsKey(name);
  }

  public @Nonnull void forEachObject(
    final @Nonnull Function<O, Unit> f)
  {
    for (final Entry<String, O> entry : this.objects.entrySet()) {
      f.call(entry.getValue());
    }
  }

  public @Nonnull Option<O> get(
    final @Nonnull String name)
    throws ConstraintError
  {
    Constraints.constrainNotNull(name, "object name");
    if (this.objects.containsKey(name)) {
      return new Option.Some<O>(this.objects.get(name));
    }
    return new Option.None<O>();
  }
}

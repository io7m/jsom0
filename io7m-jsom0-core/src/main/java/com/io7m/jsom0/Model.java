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

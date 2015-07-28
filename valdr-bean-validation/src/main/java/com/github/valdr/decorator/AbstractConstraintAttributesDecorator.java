package com.github.valdr.decorator;

import lombok.AccessLevel;
import lombok.Getter;
import com.github.valdr.ConstraintAttributes;
import com.github.valdr.MinimalObjectMap;

/**
 * Base implementation of a wrapper around {@link ConstraintAttributes}. It ensures that all sub classes provide a
 * constructor that accepts such a map.
 */
public abstract class AbstractConstraintAttributesDecorator implements MinimalObjectMap {

  @Getter(AccessLevel.PROTECTED)
  private final ConstraintAttributes decoratee;

  /**
   * Constructor that accepts the decoratee which is wrapped by this decorator.
   *
   * @param decoratee wrapped {@link ConstraintAttributes}
   */
  public AbstractConstraintAttributesDecorator(final ConstraintAttributes decoratee) {
    this.decoratee = decoratee;
  }

  @Override
  public Object put(final String key, final Object value) {
    return decoratee.put(key, value);
  }

  @Override
  public int size() {
    return decoratee.size();
  }

  public ConstraintAttributes getDecoratee() {
      return decoratee;
  }
}

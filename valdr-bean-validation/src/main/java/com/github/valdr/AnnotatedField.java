package com.github.valdr;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import com.google.common.collect.Iterables;

/**
 * Wrapper around a field with Bean Validation (and possibly other) annotations. Allows to extract validation rules
 * based on those annotations.
 */
public class AnnotatedField {
  private final Field field;
  private final Iterable<Class<? extends Annotation>> relevantAnnotationClasses;

  /**
   * @param field                     wrapped field
   * @param relevantAnnotationClasses only these annotation classes are considered when {@link
   *                                  AnnotatedField#extractValidationRules()} is invoked
   */
  AnnotatedField(final Field field, final Iterable<Class<? extends Annotation>> relevantAnnotationClasses) {
    this.field = field;
    this.relevantAnnotationClasses = relevantAnnotationClasses;
  }

  /**
   * Parses all annotations and builds validation rules for the relevant ones.
   *
   * @return validation rules (one per annotation)
 * @throws SecurityException
 * @throws NoSuchMethodException
 * @throws InvocationTargetException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 * @throws InstantiationException
   * @see AnnotatedField(Class, Iterable)
   */
  FieldConstraints extractValidationRules() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    Annotation[] annotations = field.getAnnotations();
    FieldConstraints fieldConstraints = new FieldConstraints();

    for (Annotation annotation : annotations) {
      if (Iterables.contains(relevantAnnotationClasses, annotation.annotationType())) {
        ConstraintAttributes constraintAttributes = new ConstraintAttributes(annotation, field.getType());
        BuiltInConstraint supportedValidator = BuiltInConstraint.valueOfAnnotationClassOrNull(annotation
          .annotationType());
        if (supportedValidator == null) {
          fieldConstraints.put(annotation.annotationType().getName(), constraintAttributes);
        } else {
          fieldConstraints.put(supportedValidator.toString(),
            supportedValidator.createDecoratorFor(constraintAttributes));
        }
      }
    }

    return fieldConstraints;
  }
}

package com.github.valdr;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.reflections.ReflectionUtils;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

/**
 * Wrapper around a class with Bean Validation (and possibly other) annotations. Allows to extract validation rules
 * based on those annotations.
 */
public class AnnotatedClass {
  private final Class clazz;
  private final List<String> excludedFields;
  private final Iterable<Class<? extends Annotation>> relevantAnnotationClasses;

  /**
   * @param clazz                     wrapped class
   * @param excludedFields            collection of fully qualified field names which are skipped by the parser
   * @param relevantAnnotationClasses only these annotation classes are considered when {@link
   *                                  AnnotatedClass#extractValidationRules()} is invoked
   */
  AnnotatedClass(final Class clazz, final List<String> excludedFields, final Iterable<Class<? extends Annotation>>
    relevantAnnotationClasses) {
    this.clazz = clazz;
    this.excludedFields = excludedFields;
    this.relevantAnnotationClasses = relevantAnnotationClasses;
  }

  /**
   * Parses all fields and builds validation rules for those with relevant annotations.
   *
   * @return validation rules for all fields that have at least one rule
 * @throws SecurityException
 * @throws NoSuchMethodException
 * @throws InvocationTargetException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 * @throws InstantiationException
   * @see AnnotatedClass(Class, Iterable)
   */
  ClassConstraints extractValidationRules() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    final ClassConstraints classConstraints = new ClassConstraints();
    Set<Field> allFields = ReflectionUtils.getAllFields(clazz, buildAnnotationsPredicate());
    for (Field field : allFields) {
      if (isNotExcluded(field)) {
        FieldConstraints fieldValidationRules = new AnnotatedField(field,
          relevantAnnotationClasses).extractValidationRules();
        if (fieldValidationRules.size() > 0) {
          classConstraints.put(field.getName(), fieldValidationRules);
        }
      }
    }
    return classConstraints;
  }

  private boolean isNotExcluded(final Field field) {
    String fullyQualifiedFieldName = field.getDeclaringClass().getName() + "#" + field.getName();
    return !excludedFields.contains(fullyQualifiedFieldName);
  }

  private Predicate<? super Field> buildAnnotationsPredicate() {
    Collection<Predicate<? super Field>> predicates = Lists.newArrayList();
    for (Class<? extends Annotation> annotationClass : relevantAnnotationClasses) {
      predicates.add(ReflectionUtils.withAnnotation(annotationClass));
    }
    return Predicates.or(predicates);
  }
}

package com.github.valdr;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import java.lang.reflect.InvocationTargetException;
import org.junit.Test;
import com.github.valdr.model.a.TestModelWithASingleAnnotatedMember;
import com.google.common.collect.Lists;

public class AnnotatedClassTest {

  /**
   * See method name.
 * @throws SecurityException
 * @throws NoSuchMethodException
 * @throws InvocationTargetException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 * @throws InstantiationException
   */
  @Test
  public void shouldNotReturnExcludedField() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    // given
    AnnotatedClass annotatedClass = new AnnotatedClass(TestModelWithASingleAnnotatedMember.class, Lists.newArrayList(
      TestModelWithASingleAnnotatedMember.class.getName()
        + "#notNullString"), BuiltInConstraint.getAllBeanValidationAnnotations());
    // when
    ClassConstraints classConstraints = annotatedClass.extractValidationRules();
    // then
    assertThat(classConstraints.entrySet(), is(empty()));
  }
}

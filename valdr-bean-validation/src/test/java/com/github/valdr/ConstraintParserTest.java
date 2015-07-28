package com.github.valdr;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.valdr.model.a.TestModelWithASingleAnnotatedMember;
import com.github.valdr.model.b.TestModelWithCustomValidator;
import com.github.valdr.model.c.TestModelWithASingleAnnotatedMemberWithCustomMessageKey;
import com.github.valdr.model.d.SubClassWithNoValidatedMembers;
import com.github.valdr.model.d.SuperClassWithValidatedMember;
import com.github.valdr.model.e.TestModelClassWithLotsOfIrrelevantAnnotations;
import com.github.valdr.model.f.TestModelWithHibernateEmailAnnotation;
import com.github.valdr.model.g.TestModelWithHibernateUrlAnnotation;
import com.github.valdr.model.h.TestModelWithPatterns;
import com.github.valdr.model.validation.CustomValidation;
import com.google.common.collect.Lists;

public class ConstraintParserTest {
  private static final String LS = System.getProperty("line.separator");
  private ConstraintParser parser;

  /**
   * See method name.
 * @throws JsonProcessingException
 * @throws SecurityException
 * @throws NoSuchMethodException
 * @throws InvocationTargetException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 * @throws InstantiationException
   */
  @Test
  public void shouldReturnEmptyJsonObjectWhenNoClassIsFound() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonProcessingException {
    // given
    parserConfiguredFor(emptyStringList(), emptyStringList());
    // when
    String json = parser.parse();
    // then
    assertThat(json, is("{ }"));
  }

  /**
   * See method name.
 * @throws JsonProcessingException
 * @throws SecurityException
 * @throws NoSuchMethodException
 * @throws InvocationTargetException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 * @throws InstantiationException
   */
  @Test
  public void shouldReturnDefaultMessage() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonProcessingException {
    // given
    parserConfiguredFor(Lists.newArrayList(TestModelWithASingleAnnotatedMember.class.getPackage().getName()),
      emptyStringList());
    // when
    String json = parser.parse();
    // then
    String expected = "{" + LS +
      "  \"" + TestModelWithASingleAnnotatedMember.class.getSimpleName() + "\" : {" + LS +
      "    \"notNullString\" : {" + LS +
      "      \"required\" : {" + LS +
      "        \"message\" : \"{javax.validation.constraints.NotNull.message}\"" + LS +
      "      }" + LS +
      "    }" + LS +
      "  }" + LS +
      "}";
    assertThat(json, is(expected));
  }

  /**
   * See method name.
 * @throws JsonProcessingException
 * @throws SecurityException
 * @throws NoSuchMethodException
 * @throws InvocationTargetException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 * @throws InstantiationException
   */
  @Test
  public void shouldReturnCustomMessage() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonProcessingException {
    // given
    parserConfiguredFor(Lists.newArrayList(TestModelWithASingleAnnotatedMemberWithCustomMessageKey.class.getPackage()
      .getName()), emptyStringList());
    // when
    String json = parser.parse();
    // then
    String expected = "{" + LS +
      "  \"" + TestModelWithASingleAnnotatedMemberWithCustomMessageKey.class.getSimpleName() + "\" : {" + LS +
      "    \"notNullString\" : {" + LS +
      "      \"required\" : {" + LS +
      "        \"message\" : \"paul\"" + LS +
      "      }" + LS +
      "    }" + LS +
      "  }" + LS +
      "}";
    assertThat(json, is(expected));
  }

  /**
   * See method name.
 * @throws JsonProcessingException
 * @throws SecurityException
 * @throws NoSuchMethodException
 * @throws InvocationTargetException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 * @throws InstantiationException
   */
  @Test
  public void shouldIgnoreNotConfiguredCustomAnnotations() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonProcessingException {
    // given
    parserConfiguredFor(Lists.newArrayList(TestModelWithCustomValidator.class.getPackage().getName()),
      emptyStringList());
    // when
    String json = parser.parse();
    // then
    assertThat(json, is("{ }"));
  }

  /**
   * See method name.
 * @throws JsonProcessingException
 * @throws SecurityException
 * @throws NoSuchMethodException
 * @throws InvocationTargetException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 * @throws InstantiationException
   */
  @Test
  public void shouldIgnoreNotSupportedAnnotations() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonProcessingException {
    // given
    parserConfiguredFor(Lists.newArrayList(TestModelClassWithLotsOfIrrelevantAnnotations.class.getPackage().getName()
    ), emptyStringList());
    // when
    String json = parser.parse();
    // then
    assertThat(json, is("{ }"));
  }

  /**
   * See method name.
 * @throws JsonProcessingException
 * @throws SecurityException
 * @throws NoSuchMethodException
 * @throws InvocationTargetException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 * @throws InstantiationException
   */
  @Test
  public void shouldProcessConfiguredCustomAnnotation() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonProcessingException {
    // given
    parserConfiguredFor(Lists.newArrayList(TestModelWithCustomValidator.class.getPackage().getName()), Lists
      .newArrayList(CustomValidation.class.getName()));
    // when
    String json = parser.parse();
    // then
    String expected = "{" + LS +
      "  \"" + TestModelWithCustomValidator.class.getSimpleName() + "\" : {" + LS +
      "    \"customValidation\" : {" + LS +
      "      \"" + CustomValidation.class.getName() + "\" : { }" + LS +
      "    }" + LS +
      "  }" + LS +
      "}";
    assertThat(json, is(expected));
  }

  /**
   * See method name.
 * @throws JsonProcessingException
 * @throws SecurityException
 * @throws NoSuchMethodException
 * @throws InvocationTargetException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 * @throws InstantiationException
   */
  @Test
  public void shouldSupportHibernateEmailAnnotation() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonProcessingException {
    // given
    parserConfiguredFor(Lists.newArrayList(TestModelWithHibernateEmailAnnotation.class.getPackage().getName()),
      emptyStringList());
    // when
    String json = parser.parse();
    // then
    assertThat(json, containsString(TestModelWithHibernateEmailAnnotation.class.getSimpleName()));
    assertThat(json, containsString("hibernateEmail"));
    assertThat(json, containsString("{org.hibernate.validator.constraints.Email.message}"));
  }

  /**
   * See method name.
 * @throws JsonProcessingException
 * @throws SecurityException
 * @throws NoSuchMethodException
 * @throws InvocationTargetException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 * @throws InstantiationException
   */
  @Test
  public void shouldSupportHibernateUrlAnnotation() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonProcessingException {
    // given
    parserConfiguredFor(Lists.newArrayList(TestModelWithHibernateUrlAnnotation.class.getPackage().getName()),
      emptyStringList());
    // when
    String json = parser.parse();
    // then
    String expected = "{" + LS +
      "  \"" + TestModelWithHibernateUrlAnnotation.class.getSimpleName() + "\" : {" + LS +
      "    \"url\" : {" + LS +
      "      \"hibernateUrl\" : {" + LS;
    assertThat(json, startsWith(expected));
  }

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
  public void shouldConsiderSuperClassMembers() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    // given
    parserConfiguredFor(Lists.newArrayList(SubClassWithNoValidatedMembers.class.getPackage().getName()),
      emptyStringList());
    // when
    String json = parser.parse();
    JsonNode jsonNode = new ObjectMapper().readTree(json);
    // then
    assertThat(jsonNode.get(SuperClassWithValidatedMember.class.getSimpleName()).get("notNullString").get("required")
      .get("message").asText(), is("{javax.validation.constraints.NotNull.message}"));
    assertThat(jsonNode.get(SubClassWithNoValidatedMembers.class.getSimpleName()).get("notNullString").get
      ("required").get("message").asText(), is("{javax.validation.constraints.NotNull.message}"));
  }

  /**
   * See method name.
 * @throws JsonProcessingException
 * @throws SecurityException
 * @throws NoSuchMethodException
 * @throws InvocationTargetException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 * @throws InstantiationException
   */
  @Test
  public void shouldDecoratePatternConstraint() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonProcessingException {
    // given
    parserConfiguredFor(Lists.newArrayList(TestModelWithPatterns.class.getPackage().getName()), emptyStringList());
    // when
    String json = parser.parse();
    // then
    assertThat(json, containsString("/abc/"));
    assertThat(json, containsString("/\\\\abc\\\\./")); // JSON needs to escape \ -> double escape here
    assertThat(json, containsString("/\\\\\\\\abc\\\\./")); // JSON needs to escape \ -> double escape here
  }

  private void parserConfiguredFor(final List<String> modelPackages, final List<String> customAnnotationClasses) {
    Options options = new Options();
    options.setModelPackages(modelPackages);
    options.setCustomAnnotationClasses(customAnnotationClasses);
    parser = new ConstraintParser(options);
  }

  private List<String> emptyStringList() {
    return Collections.emptyList();
  }
}

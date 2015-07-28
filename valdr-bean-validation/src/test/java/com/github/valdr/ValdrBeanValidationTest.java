package com.github.valdr;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import com.github.valdr.cli.ValdrBeanValidation;
import com.github.valdr.util.SysOutSlurper;

/**
 * Tests ValdrBeanValidation.
 */
@Ignore
public class ValdrBeanValidationTest {




    public static void main(final String[] args) {
        String[] args2 = {"-cf", "my-config.json", "-outputFile", "c://versao//validation.json"};

        // when
        try {
          ValdrBeanValidation.main(args2);
        } catch (Exception e) {
          // expect
          System.out.println(e);
        }

    }












    /**
   * See method name.
   */
  @Test
  public void shouldThrowExceptionIfNoConfigFileExists() {
    // given
    String[] args = {""};

    // when
    try {
      ValdrBeanValidation.main(args);
      fail("Should throw IllegalArgumentException if no config file exists.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  /**
   * See method name.
   */
  @Test
  public void shouldLookForConfigFileInClasspathIfNoneConfigured() {
    // given
    SysOutSlurper sysOutSlurper = new SysOutSlurper();
    sysOutSlurper.activate();
    String[] args = {""};

    // when
    try {
      ValdrBeanValidation.main(args);
    } catch (IllegalArgumentException e) {
      // expected
    }

    // then
    String sysOutContent = sysOutSlurper.deactivate();
    assertThat(sysOutContent, containsString("classpath."));
  }

  /**
   * See method name.
 * @throws IOException
   */
  @Test
  @SneakyThrows(IOException.class)
  public void shouldComplainAboutInvalidConfigurations() throws IOException {
    // given
    String[] args = {"-cf", createTempFile("{}")};

    // when
    try {
      ValdrBeanValidation.main(args);
    } catch (Exception e) {
      // expect
      assertThat(e.getClass().getName().toString(), is(Options.InvalidConfigurationException.class.getName().toString
        ()));
    }
  }

  /**
   * See method name.
   */
  @Test
  public void shouldPrintToSysOutIfConfigFileValid() throws IOException {
    // given
    SysOutSlurper sysOutSlurper = new SysOutSlurper();
    sysOutSlurper.activate();

    String[] args = {"-cf", createTempFile("{\"modelPackages\":[\"bar.foo.inexistent\"]}")};

    // when
    ValdrBeanValidation.main(args);

    // then
    String sysOutContent = sysOutSlurper.deactivate();
    assertThat(sysOutContent, containsString("{ }"));
  }

  /**
   * See method name.
   */
  @Test
  public void shouldPrintToPrintToOutputFileIfConfigured() throws IOException {
    // given
    File outputTempFile = File.createTempFile("output", "txt");

    String[] args = {"-cf", createTempFile("{\"modelPackages\":[\"bar.foo.inexistent\"]," +
      "\"outputFile\":\""+outputTempFile.getAbsolutePath()+"\"}")};

    // when
    ValdrBeanValidation.main(args);

    // then
    assertThat(FileUtils.readFileToString(outputTempFile), is("{ }"));
  }

  private String createTempFile(final String string) throws IOException {
    File tempFile = File.createTempFile("valdr", "json");
    FileWriter writer = new FileWriter(tempFile);
    IOUtils.write(string, writer);
    IOUtils.closeQuietly(writer);
    return tempFile.getAbsolutePath();
  }

}

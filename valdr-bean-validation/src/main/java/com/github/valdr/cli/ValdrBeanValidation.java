package com.github.valdr.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.valdr.ConstraintParser;
import com.github.valdr.Options;

/**
 * Command line client to print the Bean Validation JSON model to system out or a defined output file. Usage is as
 * follows:
 * <pre>
 * java ValdrBeanValidation [-cf <arg>]
 *   -cf <arg>   path to JSON configuration file, if omitted valdr-bean-validation.json is expected at root of class
 *   path
 * </pre>
 *
 * @see Options
 */
public final class ValdrBeanValidation {

  private ValdrBeanValidation() {
    // utility class
  }

  /**
   * See class comment.
   *
   * @param args cli arguments
   */
  public static void main(final String[] args) {
    org.apache.commons.cli.Options cliOptions = createCliOptions();
    try {
      CommandLine cli = parseCli(args, cliOptions);
      Options options = loadOptions(cli);
      validate(options);
      ConstraintParser parser = new ConstraintParser(options);
      try {
        output(parser, options.getOutputFile());
      } catch (IOException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
          System.out.println(e);
          throw new RuntimeException(e);
      }
    } catch (IncompleteCliException e) {
      // If the command line is not complete just print usage and help
      printErrorWithUsageAndHelp(cliOptions);
    }
  }

  private static Options loadOptions(final CommandLine cli) {
    InputStream inputStream = null;
    String configFile = cli.getOptionValue("cf");
    String outputFile = cli.getOptionValue("outputFile");

    try {
      if (StringUtils.isEmpty(configFile)) {
        inputStream = ValdrBeanValidation.class.getResourceAsStream("/" + Options.CONFIG_FILE_NAME);
        System.out.println(
          "Building parser configuration from default file path. Looking for '/" + Options.CONFIG_FILE_NAME
            + "' in classpath.");
      } else {
        System.out.println("Building parser configuration from configured file path '" + configFile + "'.");
        inputStream = new FileInputStream(new File(configFile));
      }
      Options options = new ObjectMapper().readValue(inputStream, Options.class);
      if (StringUtils.isNotEmpty(outputFile)) {
        System.out.println("Setting 'outputFile' to '" + outputFile + "' as passed to CLI.");
        options.setOutputFile(outputFile);
      }
      return options;
    } catch (IOException e) {
      throw new IllegalArgumentException("Cannot read config file.", e);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  private static void validate(final Options options) {
    options.validate();
    System.out.println("Provided configuration validated: ok.");
  }

  private static void printErrorWithUsageAndHelp(final org.apache.commons.cli.Options cliOptions) {
    System.out.println("Error. Not all mandatory args provided.");
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("java " + ValdrBeanValidation.class.getSimpleName(), cliOptions, true);
  }

  private static CommandLine parseCli(final String[] args, final org.apache.commons.cli.Options options) throws
    IncompleteCliException {
    GracefulCliParser parser = new GracefulCliParser();
    try {
      CommandLine commandLine = parser.parse(options, args);
      if (parser.isIncomplete()) {
        throw new IncompleteCliException();
      } else {
        return commandLine;
      }
    } catch (ParseException e) {
      throw new RuntimeException("Failed to parse command line.", e);
    }
  }

  private static org.apache.commons.cli.Options createCliOptions() {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption(new Option("cf", true,
      "path to JSON config file, if omitted valdr-bean-validation.json is expected at root of class path"));
    options.addOption(new Option("outputFile", true,
      "path to output file, which will be used, if no outputFile is specified in the JSON config"));
    return options;
  }

  private static void output(final ConstraintParser parser, final String outputFile) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    String output = parser.parse();
    if (StringUtils.isEmpty(outputFile)) {
      System.out.println(output);
    } else {
      File file = new File(outputFile);
      file.getParentFile().mkdirs();
      try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), "utf-8")) {
        writer.write(output);
      }
    }
  }
}

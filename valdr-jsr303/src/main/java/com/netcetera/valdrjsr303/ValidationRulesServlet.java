package com.netcetera.valdrjsr303;


import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValidationRulesServlet extends HttpServlet {
  private static final String MODEL_PACKAGES_CONFIG_PARAM = "modelPackages";
  private static final String CUSTOM_VALIDATORS_CONFIG_PARAM = "customValidatorClassNames";
  private final String invalidConfigurationMessageIntro = "The Servlet is not configured correctly. ";
  private final String packageConfigurationMissingMessage = "The '" + MODEL_PACKAGES_CONFIG_PARAM + "' parameter in " +
    "web.xml is missing.";
  private final Logger logger = LoggerFactory.getLogger(ValidationRulesServlet.class);
  private boolean correctlyConfigured = false;
  private String invalidConfigurationMessage;
  private ParserConfiguration parserConfiguration;
  private ValidationConfigurationParser parser;

  @Override
  public void init() throws ServletException {
    super.init();
    assertCorrectConfiguration();
    if (StringUtils.isEmpty(invalidConfigurationMessage)) {
      correctlyConfigured = true;
      logger.info("The Servlet appears to be correctly configured.");
      parserConfiguration = buildParserConfiguration();
      logger.info("Built parser configuration '{}' based on configuration in web.xml.", parserConfiguration);
      parser = new ValidationConfigurationParser(parserConfiguration);
    }
  }

  private ParserConfiguration buildParserConfiguration() {
    List<String> modelPackageNames = getServletConfigAsList(MODEL_PACKAGES_CONFIG_PARAM);
    List<String> customValidatorClassNames = getServletConfigAsList(CUSTOM_VALIDATORS_CONFIG_PARAM);
    return new ParserConfiguration(modelPackageNames, customValidatorClassNames);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (correctlyConfigured) {
      String json = parser.parse();
      returnJson(response, json);
    } else {
      sendErrorInvalidConfiguration(response);
    }
  }

  private void assertCorrectConfiguration() {
    if (getServletConfig() == null) {
      logger.error("ServletConfig is null.");
      invalidConfigurationMessage = buildConfigurationErrorMessage(invalidConfigurationMessage);
    } else if (StringUtils.isEmpty(getServletConfig().getInitParameter(MODEL_PACKAGES_CONFIG_PARAM))) {
      logger.error("'{}' parameter missing in web.xml.", MODEL_PACKAGES_CONFIG_PARAM);
      invalidConfigurationMessage = buildConfigurationErrorMessage(invalidConfigurationMessageIntro,
        packageConfigurationMissingMessage);
    }
  }

  private String buildConfigurationErrorMessage(String... messages) {
    return Joiner.on("").join(messages);
  }

  private void sendErrorInvalidConfiguration(HttpServletResponse response) throws IOException {
    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, invalidConfigurationMessage);
  }

  private void returnJson(HttpServletResponse response, String json) throws IOException {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setContentType("application/json;charset=UTF-8");
    response.setContentLength(json.getBytes("utf-8").length);
    PrintWriter writer = response.getWriter();
    writer.write(json);
    writer.close();
  }

  private List<String> getServletConfigAsList(String paramName) {
    String initParameter = getServletConfig().getInitParameter(paramName);
    if (StringUtils.isEmpty(initParameter)) {
      return Collections.emptyList();
    } else {
      return Arrays.asList(StringUtils.split(initParameter, ','));
    }
  }
}

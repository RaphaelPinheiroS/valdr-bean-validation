package com.github.valdr;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.Lists;

/**
 * All supported options.
 */
@Getter
@Setter
public final class Options {

    /**
     * Name of the config file this class' values are mapped from.
     */
    public static final String CONFIG_FILE_NAME = "valdr-bean-validation.json";

    /**
     * Collection of fully qualified package names (e.g. com.company.abc.model) in which you keep Bean Validation annotated model classes. Mandatory: yes Use: CLI/Servlet
     */
    private List<String> modelPackages = Lists.newArrayList();

    /**
     * Collection of fully qualified class names (e.g. com.company.abc.model.Token) to exclude from parsing. Naturally this only makes sense for classes contained in packages
     * included in {@code modelPackages}. Mandatory: no Use: CLI/Servlet
     */
    private List<String> excludedClasses = Lists.newArrayList();

    /**
     * Collection of fully qualified field names (e.g. com.company.abc.model.Person#shoeSize). Fields contained in this list will not be included in the constraints JSON document.
     * Naturally this only makes sense for fields of classes contained in packages included in {@code modelPackages}. Mandatory: no Use: CLI/Servlet
     */
    private List<String> excludedFields = Lists.newArrayList();

    /**
     * Collection of custom validator annotation class names. By default only the ones defined in {@link BuiltInConstraint} are processed. Mandatory: no Use: CLI/Servlet
     */
    private List<String> customAnnotationClasses = Lists.newArrayList();

    /**
     * Whether the output type name should be the simple name of a type (default) or the full type name (i.e., including the package name). Mandatory: no Use: CLI/Servlet
     */
    private Boolean outputFullTypeName = Boolean.FALSE;

    /**
     * In case the Servlet is deployed under a different domain than the valdr client you can use <a href="http://en.wikipedia.org/wiki/Cross-origin_resource_sharing">CORS</a> to
     * access that resource. If omitted no CORS-related HTTP headers are set. Mandatory: no Use: Servlet
     */
    private String corsAllowOriginPattern = StringUtils.EMPTY;

    /**
     * File to which the validation meta-model (JSON) is written. Missing folders are created automatically. If omitted the output is sent to system out. Mandatory: no Use: CLI
     */
    private String outputFile = StringUtils.EMPTY;

    /**
     * Validates the consistency and integrity of the configured options.
     * @throws InvalidConfigurationException if problems are detected
     */
    public void validate() {
        if (this.getModelPackages().isEmpty()) {
            throw new InvalidConfigurationException("Model package names must not be empty.");
        }
    }

    /**
     * Obtém o valor de configFileName.
     * @return configFileName
     */
    public static String getConfigFileName() {
        return CONFIG_FILE_NAME;
    }

    /**
     * Obtém o valor de modelPackages.
     * @return modelPackages
     */
    public List<String> getModelPackages() {
        return modelPackages;
    }

    /**
     * Obtém o valor de excludedClasses.
     * @return excludedClasses
     */
    public List<String> getExcludedClasses() {
        return excludedClasses;
    }

    /**
     * Obtém o valor de excludedFields.
     * @return excludedFields
     */
    public List<String> getExcludedFields() {
        return excludedFields;
    }

    /**
     * Obtém o valor de customAnnotationClasses.
     * @return customAnnotationClasses
     */
    public List<String> getCustomAnnotationClasses() {
        return customAnnotationClasses;
    }

    /**
     * Obtém o valor de outputFullTypeName.
     * @return outputFullTypeName
     */
    public Boolean getOutputFullTypeName() {
        return outputFullTypeName;
    }

    /**
     * Obtém o valor de corsAllowOriginPattern.
     * @return corsAllowOriginPattern
     */
    public String getCorsAllowOriginPattern() {
        return corsAllowOriginPattern;
    }

    /**
     * Obtém o valor de outputFile.
     * @return outputFile
     */
    public String getOutputFile() {
        return outputFile;
    }

    /**
     * Informa o valor de modelPackages.
     * @param modelPackages valor de modelPackages
     */
    public void setModelPackages(final List<String> modelPackages) {
        this.modelPackages = modelPackages;
    }

    /**
     * Informa o valor de excludedClasses.
     * @param excludedClasses valor de excludedClasses
     */
    public void setExcludedClasses(final List<String> excludedClasses) {
        this.excludedClasses = excludedClasses;
    }

    /**
     * Informa o valor de excludedFields.
     * @param excludedFields valor de excludedFields
     */
    public void setExcludedFields(final List<String> excludedFields) {
        this.excludedFields = excludedFields;
    }

    /**
     * Informa o valor de customAnnotationClasses.
     * @param customAnnotationClasses valor de customAnnotationClasses
     */
    public void setCustomAnnotationClasses(final List<String> customAnnotationClasses) {
        this.customAnnotationClasses = customAnnotationClasses;
    }

    /**
     * Informa o valor de outputFullTypeName.
     * @param outputFullTypeName valor de outputFullTypeName
     */
    public void setOutputFullTypeName(final Boolean outputFullTypeName) {
        this.outputFullTypeName = outputFullTypeName;
    }

    /**
     * Informa o valor de corsAllowOriginPattern.
     * @param corsAllowOriginPattern valor de corsAllowOriginPattern
     */
    public void setCorsAllowOriginPattern(final String corsAllowOriginPattern) {
        this.corsAllowOriginPattern = corsAllowOriginPattern;
    }

    /**
     * Informa o valor de outputFile.
     * @param outputFile valor de outputFile
     */
    public void setOutputFile(final String outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * Thrown if configuration is invalid.
     */
    public static class InvalidConfigurationException extends RuntimeException {

        /**
         * C'tor.
         * @param msg message describing what the problem with the configuration is i.e. why it's invalid
         */
        public InvalidConfigurationException(final String msg) {
            super(msg);
        }
    }
}

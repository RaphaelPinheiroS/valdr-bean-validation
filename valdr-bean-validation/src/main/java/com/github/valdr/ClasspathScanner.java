package com.github.valdr;

import java.net.URL;
import java.util.Collection;
import java.util.Set;
import javax.persistence.Entity;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

/**
 * Provides means to scan the classpath for model classes that need to be parsed for constraint annotations.
 */
public class ClasspathScanner {
  private final Logger logger = LoggerFactory.getLogger(ClasspathScanner.class);
  private final Options options;

  /**
   * Constructor.
   *
   * @param options the only relevant input for the parser is this configuration
   */
  public ClasspathScanner(final Options options) {
    this.options = options;
  }

  /**
   * Scans the classpath to find all classes that are in the configured model packages. It ignores excluded classes.
   *
   * @return classes to parse
   * @see com.github.valdr.Options#getModelPackages()
   * @see com.github.valdr.Options#getExcludedClasses()
   */
  public Set<Class<?>> findClassesToParse() {
    ConfigurationBuilder cb = new ConfigurationBuilder().setUrls(buildClassLoaderUrls());
    //cb = cb.setScanners(new Annotation());
    cb = cb.filterInputsBy(buildPackagePredicates());
    Reflections reflections = new Reflections(cb);
    return reflections.getTypesAnnotatedWith(Entity.class);
  }

  private Collection<URL> buildClassLoaderUrls() {
    Collection<URL> urls = Sets.newHashSet();
    for (String packageName : options.getModelPackages()) {
      if (StringUtils.isNotEmpty(packageName)) {
        urls.addAll(ClasspathHelper.forPackage(packageName));
      }
    }
    return urls;
  }

  private Predicate<String> buildPackagePredicates() {
    FilterBuilder filterBuilder = new FilterBuilder();
    // Include package names
    for (String packageName : options.getModelPackages()) {
      filterBuilder.include(FilterBuilder.prefix(packageName));
    }
    // Exclude class names
    for (String excludedClassName : options.getExcludedClasses()) {
      filterBuilder.exclude("^" + StringUtils.replace(excludedClassName, ".", "\\.") + "\\.class$");
    }
    return filterBuilder;
  }
}

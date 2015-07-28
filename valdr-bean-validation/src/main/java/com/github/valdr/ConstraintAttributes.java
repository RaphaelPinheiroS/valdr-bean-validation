package com.github.valdr;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;
import com.github.valdr.thirdparty.spring.AnnotationUtils;

/**
 * All attributes of a constraint (i.e. Bean Validation annotation attributes).
 */
public class ConstraintAttributes implements MinimalObjectMap {

  private final Map<String, Object> map = new HashMap<>();

  /**
   * Constructor.
   *
   * @param annotation annotation which is queried for attributes
   */
  public ConstraintAttributes(final Annotation annotation, final Class fieldType) {
    Map<String, Object> annotationAttributes = AnnotationUtils.getAnnotationAttributes(annotation);
    //Reescrever com a localização das mensagens
    if (annotationAttributes.get("message") != null && annotationAttributes.get("message") instanceof String) {
        String msg = (String) annotationAttributes.get("message");
        if (msg.startsWith("{javax.validation.constraints.") || msg.startsWith("{org.hibernate.validator.constraints")) {
            if (annotation instanceof NotNull) {
                annotationAttributes.put("message", "Campo obrigatório");
            } else if (annotation instanceof Size) {
                Size s = (Size) annotation;
                if (String.class.isAssignableFrom(fieldType)) {
                    annotationAttributes.put("message", String.format("Campo deve possuir tamanho entre %d e %d caracteres", s.min(), s.max()));
                }
                if (Collection.class.isAssignableFrom(fieldType)) {
                    annotationAttributes.put("message", String.format("Coleção deve conter entre %d e %d elementos", s.min(), s.max()));
                }
            } else if (annotation instanceof Email) {
                annotationAttributes.put("message", "Endereço de email inválido");
            } else if (annotation instanceof Past) {
                annotationAttributes.put("message", "Data não pode ser no futuro");
            } else if (annotation instanceof Future) {
                annotationAttributes.put("message", "Data não pode ser no passado");
            } else if (annotation instanceof Pattern) {
                annotationAttributes.put("message", String.format("Campo deve ser preenchido no formato: %s", ((Pattern) annotation).regexp()));
            }


        }
    }

    removeUnusedAttributes(annotationAttributes);
    map.putAll(annotationAttributes);
  }

  @Override
  public Set<Map.Entry<String, Object>> entrySet() {
    return map.entrySet();
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public Object put(final String key, final Object value) {
    return map.put(key, value);
  }

  private void removeUnusedAttributes(final Map<String, Object> annotationAttributes) {
    Iterator<String> it = annotationAttributes.keySet().iterator();
    while (it.hasNext()) {
      String key = it.next();
      if ("groups".equals(key) || "payload".equals(key)) {
        it.remove();
      }
    }
  }
}

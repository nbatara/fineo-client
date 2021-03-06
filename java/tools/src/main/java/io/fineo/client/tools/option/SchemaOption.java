package io.fineo.client.tools.option;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.ParametersDelegate;
import io.fineo.client.tools.events.AnnotationAliases;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Options around creating/managing schemas
 */
public class SchemaOption {

  @ParametersDelegate
  public MetricClassOption type = new MetricClassOption();

  @ParametersDelegate
  public MetricNameOption metric = new MetricNameOption();

  @DynamicParameter(names = "-F",
                    description = "Field name and type specification. E.g. -Ffield1=VARCHAR")
  public Map<String, String> fieldAndType = new HashMap<>();

  public String getName() {
    return getMetricName(metric, type);
  }

  public static String getMetricName(MetricNameOption name, MetricClassOption type){
    if (name.get() == null) {
      return type.getTypeName();
    }
    return name.get();
  }

  public List<FieldInstance> getFields() throws ClassNotFoundException {
    if (type.getClazz() != null) {
      return loadFromClass();
    }

    // just convert the specified fields
    return fieldAndType.entrySet().stream().map(entry -> new FieldInstance(entry.getKey(), entry
      .getValue())).collect(Collectors.toList());
  }

  private List<FieldInstance> loadFromClass() throws ClassNotFoundException {
    Class clazz = getClazz();

    AnnotationAliases aliases = new AnnotationAliases(clazz);
    // get all the fields as simple get/set methods. this is rather crude, but its just a simple
    // tool with simple goals. If you want to get fancier, its recommended to write more java
    // code around a SchemaApi instance that handles the loading of data for you
    List<FieldInstance> fields = new ArrayList<>();
    for (Method method : clazz.getDeclaredMethods()) {
      String name = method.getName();
      if (name.startsWith("get")) {
        String suffix = name.substring(3);
        String fieldName = suffix.substring(0, 1).toLowerCase() + suffix.substring(1);
        if (fieldName.equals("metrictype")) {
          continue;
        }
        Class type = method.getReturnType();
        String typeName;
        switch (type.getTypeName()) {
          case "java.lang.String":
            typeName = "STRING";
            break;
          case "int":
            typeName = "INTEGER";
            break;
          case "long":
            typeName = "LONG";
            break;
          case "double":
            typeName = "DOUBLE";
            break;
          case "float":
            typeName = "FLOAT";
            break;
          case "boolean":
            typeName = "BOOLEAN";
            break;
          case "byte[]":
            typeName = "BINARY";
            break;
          default:
            throw new IllegalArgumentException(
              "Schema tool does not support type: " + type.getTypeName());
        }
        // add the field unless it a metric/timestamp alias
        if (!aliases.getMetricTypeAliases().contains(fieldName) &&
            !aliases.getTimestampAliases().contains(fieldName)) {
          fields.add(new FieldInstance(fieldName, typeName));
        }
      }
    }
    return fields;
  }

  public Class getClazz() throws ClassNotFoundException {
   return type.getClazz();
  }

  public static class FieldInstance {
    public String name;
    public String type;

    public FieldInstance(String name, String type) {
      this.name = name;
      this.type = type;
    }

    @Override
    public String toString() {
      return "FieldInstance{" +
             "name='" + name + '\'' +
             ", type='" + type + '\'' +
             '}';
    }
  }
}

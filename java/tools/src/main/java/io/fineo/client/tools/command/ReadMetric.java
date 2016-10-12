package io.fineo.client.tools.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.fineo.client.FineoClientBuilder;
import io.fineo.client.model.schema.SchemaApi;
import io.fineo.client.model.schema.metric.ReadMetricResponse;
import io.fineo.client.tools.option.SchemaOption;

@Parameters(commandNames = "read",
            commandDescription = "Read the schema for the metric and output in JSON format")
public class ReadMetric implements Command {

  @ParametersDelegate
  private final SchemaOption schema;

  @Parameter(names = {"--pp", "--pretty-print"})
  public boolean prettyPrint = false;

  public ReadMetric(SchemaOption schema) {
    this.schema = schema;
  }

  @Override
  public void run(FineoClientBuilder builder) throws Exception {
    try (SchemaApi.Metric metrics = builder.build(SchemaApi.Metric.class)) {
      ReadMetricResponse response = metrics.readMetric(schema.getName());
      String msg;
      if (prettyPrint) {
        msg = new ObjectMapper().writerWithDefaultPrettyPrinter()
                                .writeValueAsString(response);
      } else {
        msg = new ObjectMapper().writeValueAsString(response);
      }
      System.out.println(msg);
    }
  }
}

The java client is the easiest way to manipulate schema and write to the API. 

# Requirements

 * maven 3
 * java >= 8u101

# Usage

The basic java client is available through Maven here:

```
<project>
...
  <repositories>
    <repository>
      <id>Fineo</id>
      <url>http://maven.fineo.io/release</url>
    </repository>
  </repositories>
  ...
  <dependencies>
    <dependency>
      <groupId>io.fineo.client</groupId>
      <artifactId>common</artifactId>
      <version>1.1.2</version>
    </dependency>
    ...
  </dependencies>
</project>
```

From there, you can choose which module you want to use:

  * io.fineo.client.models:schema
  * io.fineo.client.models:write
  
Each module provides a basic interface of the API which you combine with a builder to create the 
instance you can call. For instance, to use the 'StreamWrite' API, you would do: 

```
  FineoClientBuilder builder = new FineoClientBuilder()
                                .withApiKey(apiKey)
                                .withCredentials(awsCredentials);
  StreamWrite stream = builder.build(StreamWrite.class)
```

All the Java APIs support a synchronous and an asynchronous version of each method.

Writes are not made through SQL interfaces (yet!), but instead made through RESTful APIs.

The swagger specifications are available [here]

However, you still need to authenticate with an API Key and
[sign the request](http://docs.aws.amazon.com/general/latest/gr/signing_aws_api_requests.html) 
with your IAM Credentials. You can try the API using [Postman](https://www.getpostman.com/). 
However, the easiest access is through our [Java client](client/java-client).

# Writes

There are two main interfaces for writing:

  * Stream
  * Batch
  
Stream provides the lowest latency to event 'readability' - generally on the order of 10s of 
seconds. However, it comes at a higher cost and with some more stringent
[limitations](#limitations).

Batch is a bit more scalable, but events are processed asynchronously and are available at some 
point in time later[[1](#1-batch)].

## Batch Uploads

Batch data uploads can take two forms:

 1. Sending a batch of events
 2. Sending a reference to an S3 file

The latter has some slight requirements around it. Unless your S3 bucket is publicly accessible 
(not recommended), you need to add the [fineo bucket policy](batch-upload-bucket-policy.json) to 
the bucket so we can read the data you send.
 
Currently, S3 file uploads support the following types:

  * csv
  * json
    * each record must be on a single line and not prefixed with `[` or followed by `]`. 
    * This is _not_ a well-formed JSON file.

With the following compression formats:

  * none
  * gzip (.gz)

# Limitations

There are some limitations to the size and shape of the data that can be written.
 
Every event is required to contain two fields:

  * timestamp
  * metrictype
  
That said, these fields are very flexible and can be changed to fix almost any scenario that you 
have. Please see the [Schema](Schema) documentation for how the schema flexibility works.

## Notes

### 1. Batch
During the BETA 'later' is loosely defined. In the future, we hope to provide a stricter SLA 
on when the batch data will be available.

[here]: http://api-doc.fineo.io

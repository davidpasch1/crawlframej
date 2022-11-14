# crawlframej
A simple framework for a web-crawler in Java.

**Original author**: David Pasch

## Scope
A framework that may serve as a foundation for a basic scraping tool or a focused web-crawler. As such it aims to be simple and comprehensive, but still powerful enough to support different application scenarios. It is not intended for a full-web search engine.

**Note**: Fetched data is handed out by the framework as-is. Consequently, the implementor of the framework must take care of any necessary cleaning of the data (eg invalid characters in HTML).

## Limitations

- *A crawl is limited to one website only.* Of course, you can invoke the program multiple times to crawl several sites. The reason for this limitation is that the framework is intended for a focused web-crawler, and not for a full-web crawler.
- *The crawling framework has not been tested with non UTF-8 pages.*
- *The disk writer stage always outputs page as UTF-8 regardless of the original encoding.*
- *The framework has not been tested with non-ASCII URLs.* Note, that according to the standard for URLs (RFC1738) non-ASCII URLs are invalid. However, the framework should work with URLs that were originally non-ASCII and have been encoded to ASCII according to the standard.
- *The crawl delay is fixed.* That's because it is a simple solution and there is not much gain in making it adaptive.

## Features

- Multithreaded design by running the fetching of webpages in a separate thread. As a consequence, the throughput is determined by the network communication.
- Flexible pipeline structure through assembly of pipeline with stages.
- Parameterization of stages wrt stage, job type, and job.
- Accumulation of stage results during the processing of a job.
- Custom error handling.
- On error abortion of individual job or of entire crawl.
- Complete error reporting through accumulation of exceptions throughout the pipeline.
- Ready-to-use implementations of various stages for basic needs.
- Sample code included for demo of framework.

## Contents

- The core of the framework.
- Collection of stages for the fetcher and the processing pipeline.
- Demo of a general crawler and a downloader.


## Requirements
For running the project the following requirements have to be met:
- Ant
- Java 8
- JUnit 4

Moreover, project files are included for Eclipse.

## Quick-start
Run `ant -p` to see the available targets in the individual projects.

Targets are provided for
- building the distributable (jar)
- running the unit tests
- building the API docs

## Documentation
Check-out the API documentation.

## Licenses
The following libraries are being referenced by the project:
- *jsoup*: MIT license
- *log4j*: Apache 2.0
- *commons-\**: Apache 2.0
- *javatuples*: Apache 2.0
- *httpclient*: Apache 2.0

The following libraries are only used for running the unit tests:
- *junit*: Eclipse Public License 1.0
- *hamcrest*: BSD License 3

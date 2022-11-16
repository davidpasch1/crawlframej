# crawlframej
A simple framework for a focused web-crawler in Java.

The framework has been successfully used to build a focused web-crawler for a major blogging site.

**Original author**: David Pasch

## Code Maintenance
The code is provided as-is to other developers for use in their projects.

*In general, there are no plans to further extend the framework.*
In particular, it is unlikely that the known limitations (sb) will be removed.

There might be shortcomings, however, which may be subject to future development (see Known Issues in the docs).

**Note**: Bug reports and fixes are always welcome.

## Scope
A framework that may serve as a foundation for a basic scraping tool or a focused web-crawler. As such it aims to be simple and comprehensive, but still powerful enough to support different application scenarios. *It is not intended for a full-web search engine.*

Because it is intended for simple web-crawlers, it's performance is not an important issue. In real application, the framework has been used so far to download up to 10,000 pages. *It is not intended to be used for 24/7 operation.*

**Note**: Fetched data is handed out by the framework as-is. Consequently, the implementor of the framework must take care of any necessary cleaning of the data (eg invalid characters in HTML).

## Limitations

- *A crawl is limited to one website only.* Of course, you can invoke the program multiple times to crawl several sites. The reason for this limitation is that the framework is intended for a focused web-crawler, and not for a full-web crawler.
- *The crawling framework has not been tested with non UTF-8 pages.*
- *The disk writer stage always outputs page as UTF-8 regardless of the original encoding.*
- *The framework has not been tested with non-ASCII URLs.* Note, that according to the standard for URLs (RFC1738) non-ASCII URLs are invalid. However, the framework should work with URLs that were originally non-ASCII and have been encoded to ASCII according to the standard.
- *The crawl delay is fixed.* That's because it is a simple solution and there is not much gain in making it adaptive.
- *The `robots.txt` file is not processed.* This is left to the implementor of the framework.

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
- Specification of the framework.

The framework is delivered in two modules:
- the core framework (`crawlframej-core`)
- the implementation of the framework (`crawlframej-stages`)

The core framework contains the interfaces and abstract classes of the framework.
It depends solely on the logging module, and can be thus used in scenarios where a minimal dependency on 3rd party libraries is desired.

The provided framework implementation contains default implementations for various stages:
- HTTP web fetcher
- disk fetcher
- disk writer
- job generator

The library `crawlframej-stages` also contains the demo programs.

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

You may want to try out the provided demo programs in the `crawlframej-stages` module.
Check-out the API documentation of the module for more information.

For a complete web-crawler, an implementor of the framework will usually have to provide the following:
- a parser stage for the downloaded web-pages
- a crawling strategy to direct the crawl and to filter URLs eg due to the `robots.txt`
- common utilites which are currently not included by the default implementation like a parser for sitemaps and for the `robots.txt`

## Documentation
Check-out the API documentation.

## Licenses
The following libraries are being referenced by the core framework `crawlframej-core`:
- *log4j*: Apache 2.0
- *commons-\**: Apache 2.0
- *javatuples*: Apache 2.0
- *toolkit*: David Pasch, Apache 2.0

The default implementation `crawlframej-stages` further needs the following libraries:
- *jsoup*: MIT license
- *httpclient*: Apache 2.0

The following libraries are only used for running the unit tests:
- *junit*: Eclipse Public License 1.0
- *hamcrest*: BSD License 3

**Note**: Take a look at the build scripts of the individual projects for dependencies on 3rd party libraries.

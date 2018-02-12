# AWS Lambda - Java HelloWorld calling a native C++ library

Serverless computing platforms are growing and, in many aspects, AWS Lambda leads the pack. At this time it supports code written in Node.js (JavaScript), Python, Java (Java 8 compatible), C# (.NET Core 1 and 2) and Go. Furthermore, Amazon declares that your code can include existing libraries and even native ones (AWS Lambda FAQs).

That is very important. Your company may have a big existing code base to capitalize on, and regardless, it's plenty of software out there that you may want to reuse.

Including a library written in the same language as your lambda function is pretty straightforward. It comes down to creating a valid deployment package (Creating a Deployment Package). Include a native c/c++ library can be a little trickier. It took me some time to figure out how to make it work, and that's why I've decided to post this article. Hopefully it will ease your path and help you to smoothly deploy the core of your new serverless application.

Please visit http://www.glfrc.com/2018/01/19/aws-lambda-with-java-and-native-code/ for the full article.

<h1>AWS Lambda with Java and native c++ code</h1>

Serverless computing platforms are growing and, in many aspects, AWS Lambda leads the pack. At this time it supports code written in Node.js (JavaScript), Python, Java (Java 8 compatible), C# (.NET Core 1 and 2) and Go. Furthermore, Amazon declares that your code can include existing libraries and even native ones (AWS Lambda FAQs).

That is very important. Your company may have a big existing code base to capitalize on, and regardless, it’s plenty of software out there that you may want to reuse.

Including a library written in the same language as your lambda function is pretty straightforward. It comes down to creating a valid deployment package (Creating a Deployment Package). Include a native c/c++ library can be a little trickier. It took me some time to figure out how to make it work, and that’s why I’ve decided to post this article. Hopefully it will ease your path and help you to smoothly deploy the core of your new serverless application.
<h2>Prerequisites</h2>

This post will cover a lot of different topics and I will not go into details. You are supposed to have at least basic knowledge of C++, Java, JNI, Maven, Docker, AWS IAM and Lambda.

You also need an AWS account and a pc with the Java JDK 1.8, Maven, Docker and AWS SAM Local installed. You find all the source code on github. Just clone it locally at any given location.

Of course there is a lot of collective wisdom in here. I can’t claim it all came from myself. There’s a lot of googling around and the whole Lambda part is based on the AWS example from AWS Labs.

The following procedures have been tested on a Windows machine. You might need to adapt a few back-slashes to slashes here and there.
<h2>Compile the C++ library</h2>

The compilation of the C++ library could be the most laborious part. It must be compiled  against its target environment, where your lambda function will be deployed and run. From Amazon’s documentation we know that it’s the current Amazon Linux AMI distribution. That leaves us with 2 options:

    compile it on a dedicated EC2 machine.
    cross-compile using the Amazon Linux Docker image.

We’ll go for the second approach. The Amazon Linux distribution is intended as a runtime environment. There are no development tools on it. We will have to install everything we need first.  It’s basically the C++ tool chain and the Java JDK. In the initial docker run command we will also map the repository folder, in order to have the compilation results available locally. Run this command starting from your repository root folder.

docker run -it -v $PWD\:/compile_lib amazonlinux bash

Once you get the bash prompt type

yum -y update;\
yum -y groupinstall "Development Tools"\
yum -y install open-jdk-1.8

That will install all the tools we need. Then cd in the compile_lib folder and run the makerun.sh script. You can open it and inspect it if you want more details. In short, it’s going to run a sequence of commands to compile the c++ library and run a very basic test from some Java code. That’s the output you can expect (if everything goes well):

bash-4.2# ./makerun.sh
Hello pippo
Hello pluto

Apart from the output, that should generate a libhello.so file in $PWD/src/main/java/resources. If it’s in there you can now exit the container and forget about it (docker rm to remove it).
Import the C++ library in Java

This is just standard Java JNI. The .so file must be available in the Java library path and then you simply load the library from your Java code. Here the related Java code from the JavaHello.java file.

public class JavaHello {
  static {
    System.loadLibrary("hello"); // loads libhello.so
  }
  public native String sayHello(String name);
}

<h2>Package the JAR artifact</h2>

This phase is the one that took me the longest, and eventually ended up being very easy. The  breakthrough was the discovery that the Jars archives get extracted before execution. It wasn’t obvious to me, I guess it must be to improve performance. In any case, that removes the obstacle of extracting the .so library to load it.

All I had to was add the resources folder to the jar from the pom.xml file. You can run mvn package. That should generate the Jar artifact in the target folder.
Test locally with SAM Local

SAM Local is a tool recently released from Amazon. It has 4 basic, and very useful functionalities.

    It can generate test events based on templates. There are templates for every main AWS service able to trigger Lambdas.
    It allows to run API Gateway and trig Lambda functions locally, inside a Docker based environment which simulates the real one.
    It produces Serverless AWS deployable packages.
    It can deploy a Cloud Formation specialised template defining a full Serverless application to AWS.

We will be using it all, starting with local testing. Let’s run the sam local invoke command to test our Lambda.

> sam local invoke -e event.json HelloNativeLambdaFunction
...
?[32mSTART RequestId: 5839a8e3-fd01-4ca0-b05c-851b5bc87c9d Version: $LATEST?[0m
name = pippo
?[32mEND RequestId: 5839a8e3-fd01-4ca0-b05c-851b5bc87c9d?[0m
?[32mREPORT RequestId: 5839a8e3-fd01-4ca0-b05c-851b5bc87c9d Duration: 58.21 ms Billed Duration: 100 ms Memory Size: 128 MB Max Memory Used: 5 MB ?[0m

{"message":"Hello pippo"}

<h2>Deploy on AWS</h2>

As said, SAM Local can also package and deploy to AWS. Locally, we already have everything. It’s now time to access our AWS account. I assume your environment is configured to access your AWS account from the AWS CLI. If not have a look at Configuring the AWS CLI.

We first need to create an S3 bucket. It will be used to upload our Lambda package. Here an example showing how to do it.

aws s3api create-bucket --bucket my-bucket --region us-east-1

A little tip here. As you may know, the bucket name (my-bucket) must be S3 universally unique. I normally go for my name of choice and append a GUID to it. So pick a name and select your region. Remember that for every region other than us-east-1 you need to specify the location constraint (create-bucket). If successful , you will get the http location of your bucket back. Now you can run:

sam package --template-file template.yaml --s3-bucket my-bucket --output-template-file aws-template.yaml

The output of the previous command will produce the command line you can run to deploy your Lambda in AWS. Beware that you can use the sam alias instead of aws cloudformation, and that the IAM capability is missing.

sam deploy --template-file aws-template.yaml --stack-name my-stack --capabilities CAPABILITY_IAM

Where my-stack is the name of your Cloudformation stack. If you get Successfully created/updated stack, then you have just deployed your Lambda function calling a C++ library.

Please leave a comment if you find any mistake, if you want to discuss in detail any topic, or even if you have just found it useful.

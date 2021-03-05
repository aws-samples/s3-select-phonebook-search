## Amazon S3 Select - Phonebook Search
  
Amazon S3 Select - Phonebook Search is a simple serverless Java application illustrating the usage of Amazon S3 Select to execute a SQL query on a comma separated value (CSV) file stored on Amazon Simple Storage Service (Amazon S3). S3 Select does not require any database servers and runs directly on S3.

Generally available in April, 2018, S3 Select and Amazon S3 Glacier Select allow customers to run SQL queries directly on data stored in S3 and Amazon S3 Glacier. Customers previously needed to deploy a database to query this data. With Amazon S3 Select, you simply store your data on S3 and query away using simple (SQL) statements to filter the contents of Amazon S3 objects and retrieve only the subset of data that you need. By retrieving only a subset of the data, customers reduce the amount of data that Amazon S3 transfers, which reduces the cost and latency to retrieve this data. 

Amazon S3 Select works on objects stored in CSV, JSON, or Apache Parquet format. Amazon S3 Select also supports compression on CSV and JSON objects with GZIP or BZIP2, and server-side encrypted objects.

You can perform SQL queries using AWS SDKs, the SELECT Object Content REST API, the AWS Command Line Interface (AWS CLI), or the Amazon S3 console.

In addition to using Amazon S3 for storage and running SQL queries, our simple phone book application will leverage Amazon API Gateway and AWS Lambda. In this sample, will use AWS Lambda to run the Amazon S3 Select SQL query. Amazon API Gateway will be used to interact with AWS Lambda.

## Architecture

The architecture for this workshop is the following:
<br><br>
![Architecture](/images/architecture.png)
 
## Description

The ‘Amazon S3 Select – Phonebook search’ demo showcases the power of S3 Select. S3 Select enables applications to retrieve only a subset of data from an object by using simple SQL expressions. By using S3 Select to retrieve only the data needed by your application, you can drastically improve performance and reduce cost.

This project contains a [sample_data.csv](/src/test/resources/sample_data.csv) file in CSV format that you can query to search for users based on name, occupation, or location. Requests are made through API Gateway via lambda to select a subset of data from the sample file. The lambda function uses Amazon S3 SDK for Java to issue the S3 Select query and returns the result back in JSON format.

## AWS Services and Features Used

* <b>[Amazon S3](https://aws.amazon.com/s3/)</b> is an object storage service that offers industry-leading scalability, data availability, security, and performance.
* <b>[Amazon S3 Select](https://docs.aws.amazon.com/AmazonS3/latest/API/API_SelectObjectContent.html)</b> enables applications to retrieve only a subset of data from an object by using simple SQL expressions.
* <b>[Amazon API Gateway](https://aws.amazon.com/api-gateway/)</b> is a fully managed service that makes it easy for developers to create, publish, maintain, monitor, and secure APIs at any scale.
* <b>[AWS Lambda](https://aws.amazon.com/lambda/)</b> lets you run code without provisioning or managing servers.
* <b>[AWS CloudFormation](https://aws.amazon.com/cloudformation/)</b> provides a common language for you to model and provision AWS and third party application resources in your cloud environment.

## Quick Start

The quick start guide is intended to deploy the sample application in your own AWS account using an AWS CloudFormation template.

### Quick Start Setup
1.	Sign-in to your existing AWS account or [Create a new AWS account](https://us-west-2.console.aws.amazon.com/)
2.	[Create an Amazon S3 bucket](https://docs.aws.amazon.com/AmazonS3/latest/gsg/CreatingABucket.html) and note the name of the bucket you created, as this will be used throughout this project.
3.	Upload the [sample_data.csv](/src/test/resources/sample_data.csv) file located in project [/src/test/resources](/src/test/resources) directory to your S3 bucket. Upload this file to the top level / root directory of you S3 bucket.
4.	Upload packaged code <b>[lambdaCode-1.0.0.jar](/lambdaCode-1.0.0.jar)</b> provided in /target directory to your S3 bucket.
5.	Using AWS Console, select <b>‘CloudFormation’</b> from the list of AWS Services.
6.	Choose <b> ‘Create Stack’</b> .
7.	Select <b>‘Template is ready’ </b>and <b>‘Upload a template file’</b>
8.	Choose [cloud_formation_template.yaml](cloud_formation_template.yaml) file located in project root directory and click "Next"

![Creating a Stack ](/images/createStack.png)

9.	On the next page, specify stack details<br>
a.	Choose a <b>stack name</b><br>, such as "s3-select-phonebook-demo"
b.	Specify your <b>bucket name</b> (this is the bucket you created previously)<br>
c.	Specify the uploaded <b>lambda code </b> (this is the code you uploaded)<br>
d.	Specify the SampleData <b>file name</b> (this is the sample_data.csv file you uploaded previously)<br>

![Stack Name ](/images/stackName.png)
 
10. On subsequent pages, leave all other fields to their <b>default </b> values.
11. On the final page, acknowledge all <b>‘Transform might require access capabilities’</b>
12. Choose <b>Create Stack</b>

At this point, your stack should have completed successfully. You will see a similar screen showing the status as <b>CREATE_COMPLETE</b>.

![CREATE_COMPLETE](/images/stack-created.png)

### Usage

S3-select-phonebook application allows you to query a subset of data from the sample fictitious data. Take a look at the uploaded sample file and perform the following to query a subset of the data.

1. Select the <b>‘Outputs’ </b>tab from the CloudFormation Stacks and copy the <b>value</b> of your API Gateway endpoint.
2. Using PostMan or Curl, you can issue a command to get a subset of data.

For example:

`curl -d '{"name":"Jane"}' -X POST {ENTER_API_GATEWAY_ENDPOINT};`

The above call should return Jane's information.

For example in my case: 

`curl -d '{"name":"Jane"}' -X POST https://3otm935he1.execute-api.us-west-2.amazonaws.com/Prod/s3-select-demo`

The output is:

`[{"Occupation":"Developer","PhoneNumber":"(949) 555-6704","City":"Chicago","Name":"Jane"}]`


User interface coming soon!!


## Building from Source 

This section is for developers who are looking to customize the application.

### Pre-Requisites
This section provides a list of prerequisites that are required to successfully build the ‘s3-select phonebook search’ application.

10.	Sign-in to AWS or [Create an Account](https://us-west-2.console.aws.amazon.com/)
11.	Install [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
12.	Install [AWS SAM](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
a.	Note that while the instructions specify Docker as a pre-requisite, Docker is only necessary for local development via SAM local. Feel free to skip installing Docker if you are not deploying locally.
13.	Install [Maven](https://maven.apache.org/install.html)

### Setup

Download the S3 Select demo application to your local machine and pick a region in the AWS console that matches your local configuration.

1.	[Create an Amazon S3 bucket](https://docs.aws.amazon.com/AmazonS3/latest/gsg/CreatingABucket.html)  
2.	Upload [sample_data.csv](/src/test/resources/sample_data.csv) file located in project /src/test/resources directory to your Amazon S3 bucket
3.	Before deploying the project to SAM for the first time, you'll need to update some variables with your bucket name. Please update the following in the <b>template.yaml </b>file located in the project root directory.<br>

<b>a.	Update Environment variables</b> <br>
        <b>Enter the name of your bucket.</b> <br>
          BUCKET_NAME: {ENTER_BUCKET_NAME}<br>
         <b> Enter the name/location of your sample file (e.g. sample_data.csv} </b><br>
         SAMPLE_DATA: {SAMPLE_DATA.csv}  <br>

<b>b.	Update Lambda Policy. </b>
       <b>Enter the name of your S3 sample data ARN </b> <br>
       (e.g. 'arn:aws:s3:::s3selectdemobucket/sample_data.csv')<br>
        Resource: 'arn:aws:s3:::{BUCKET_NAME/sample_data.csv'<br>

### Build and Deployment

Go to the root folder of ‘directory search’ and run the following SAM commands to build and deploy the application. 

```
sam build
sam package --output-template packaged.yaml --s3-bucket {name_of_your_bucket}
sam deploy --template-file packaged.yaml --stack-name s3-select-phonebook-stack --capabilities CAPABILITY_IAM
```
### Usage

S3-select-phonebook application allows you to query a subset of data from the sample fictitious data stored in comma separated value (CSV) format. Take a look at the uploaded sample file and perform the following steps to query a subset of the data.

1. Select the <b>‘Outputs’ </b>tab and copy the value of your API Gateway endpoint.
2. Using PostMan or Curl, you can issue a command to get a subset of data.

For example:

`curl -d '{"name":"Sam"}' -X POST {ENTER_API_GATEWAY_ENDPOINT};`

The above call should return Sam’s information.

## Cleanup

1.	Using AWS Console, select <b>‘CloudFormation’ </b>from the list of AWS Services.
2.	Select the <b>Stack </b>you created.
3.	Click <b>‘Delete’ </b>action button to delete the stack and all associated resources. 
4.  Please make sure to delete your S3 bucket and CloudWatch logs

## License

This library is licensed under the MIT-0 License. See the [LICENSE](https://github.com/aws-samples/s3-select-phonebook-search/blob/master/LICENSE) file.

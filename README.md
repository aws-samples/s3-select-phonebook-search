## Amazon S3 Select - Phonebook Search
  
Amazon S3 Select - Phonebook Search is a simple serverless Java application illustrating the usage of Amazon S3 Select to execute a SQL query on a comma separated value (CSV) file stored on Amazon Simple Storage Service (Amazon S3). S3 Select does not require any database servers and runs directly on S3.

## Architecture

The architecture for this workshop is the following:
<br><br>
![Architecture](/images/architecture.png)
 
## Description

‘Amazon S3 Select – Phonebook search’ demo showcases the power of S3 Select. S3 Select enables applications to retrieve only a subset of data from an object by using simple SQL expressions. By using S3 Select to retrieve only the data needed by your application, you can drastically improve performance and reduce cost.

This project contains [sample data](/src/test/resources/sample_data.csv) in CSV format that you can query to search for users based on name, occupation, or location. Requests are made through API Gateway via lambda to select a subset of data from the sample file. The lambda function uses Amazon S3 SDK for Java to issue the S3 Select query and returns the result back in JSON format.

## Quick Start

The quick start guide is intended to deploy the sample application in your own AWS account using an AWS CloudFormation template.

## AWS Services and Features Used

* [Amazon S3](https://aws.amazon.com/s3/)
* [Amazon S3 Select](https://docs.aws.amazon.com/AmazonS3/latest/API/API_SelectObjectContent.html)
* [Amazon API Gateway](https://aws.amazon.com/api-gateway/)
* [AWS Lambda](https://aws.amazon.com/lambda/)
* [AWS CloudFormation](https://aws.amazon.com/cloudformation/)

### Quick Start Setup
1.	Sign-in to AWS or [Create an Account](https://us-west-2.console.aws.amazon.com/)
2.	[Create an AWS S3 Bucket](https://docs.aws.amazon.com/AmazonS3/latest/gsg/CreatingABucket.html)
3.	Upload <b>‘sample_data.csv’</b> file located in project <b> /src/test/resources </b> directory to your Amazon S3 Bucket. 
4.	Upload packaged code <b> ‘lambdaCode-1.0.0.jar’ </b>provided in /target directory to your Amazon S3 Bucket.
5.	Using AWS Console, select <b>‘CloudFormation’</b> from the list of AWS Services.
6.	Choose <b> ‘Create Stack’. </b> 
7.	Select <b>‘Template is ready’ </b>and <b>‘Upload a template file’</b>

![Creating a Stack ](/images/createStack.png)

8.	Choose <b>‘cloud_formation_template.yaml’ </b>file located in project root directory.
9.	On the next page, specify stack details<br>
a.	Choose a <b>stack name</b><br>
b.	Specify your <b>bucket name</b> (this is the bucket you created earlier)<br>
c.	Specify the uploaded <b>lambda code </b> (this is the code you uploaded)<br>
d.	Specify the sample <b>file name</b> (this is the sample file you uploaded)<br>
e.	Specify the <b>file ARN </b>by replacing the values in brackets (including the brackets) with name of your bucket, slash(/) sample file name. 

![Stack Name ](/images/stackName.png)
 
10. On subsequent pages, leave all other fields to their <b>default </b> values.
11. On the final page, acknowledge all <b>‘Transform might require access capabilities’</b>
12. Choose <b>Create Stack</b>

### Usage

S3-select-phonebook application allows you to query a subset of data from the sample fictitious data. Take a look at the uploaded sample file and perform the following to query a subset of the data.

1. Select the <b>‘Outputs’ </b>tab and copy the <b>value</b> of your API Gateway endpoint.
2. Using PostMan or Curl, you can issue a command to get a subset of data.

For example:

<b>curl -d '{"name":"Sam"}' -X POST {ENTER_API_GATEWAY_ENDPOINT};</b>

The above call should return Sam’s information.

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

1.	[Create an Amazon S3 Bucket](https://docs.aws.amazon.com/AmazonS3/latest/gsg/CreatingABucket.html)  
2.	Upload [sample_data.csv](/src/test/resources/sample_data.csv) file located in project /src/test/resources directory to your Amazon S3 Bucket
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

## License

This library is licensed under the MIT-0 License. See the [LICENSE](https://github.com/aws-samples/s3-select-phonebook-search/blob/master/LICENSE) file.
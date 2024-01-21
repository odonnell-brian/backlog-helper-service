import * as cdk from 'aws-cdk-lib';
import { LambdaRestApi } from 'aws-cdk-lib/aws-apigateway';
import { Code, Function, Runtime } from 'aws-cdk-lib/aws-lambda';
import { Construct } from 'constructs';

export class InfraStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const testLambdaFunction = new Function(this, "TestLambda", {
      runtime: Runtime.JAVA_21,
      code: Code.fromAsset('../application/target/BacklogHelperService-1.0-SNAPSHOT-shaded.jar'), // TODO
      handler: 'com.brian.backloghelperservice.lambda.handlers.TestHandler',
    });

    const api = new LambdaRestApi(this, 'TestApi', {
      restApiName: 'TestApi',
      handler: testLambdaFunction,
      proxy: false,
    });

    const test = api.root.addResource('test');
    test.addMethod('POST');
    test.addMethod('GET');
  }
}

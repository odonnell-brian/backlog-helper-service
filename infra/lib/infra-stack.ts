import * as cdk from 'aws-cdk-lib';
import { HttpLambdaIntegration } from 'aws-cdk-lib/aws-apigatewayv2-integrations';
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

    const lambdaIntegration = new HttpLambdaIntegration('LambdaIntegration', testLambdaFunction, {

    });
  }
}

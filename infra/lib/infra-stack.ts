import { Cors, LambdaIntegration, RestApi } from 'aws-cdk-lib/aws-apigateway';
import { Code, Function, Runtime } from 'aws-cdk-lib/aws-lambda';
import { Duration, Stack, StackProps } from "aws-cdk-lib";
import { Construct } from 'constructs';
import { ApiLambdaHandler } from './api-lambda-handler';

export class InfraStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const testLambdaFunction = new Function(this, "TestLambda", {
      runtime: Runtime.JAVA_21,
      code: Code.fromAsset('../application/target/BacklogHelperService-1.0-SNAPSHOT.jar'), // TODO
      handler: 'com.brian.backloghelperservice.lambda.handlers.TestHandler',
      timeout: Duration.seconds(30),
    });

    const api = new RestApi(this, 'BacklogHelperApi', {
      restApiName: 'BacklogHelperApi',
      defaultCorsPreflightOptions: {
        allowOrigins: Cors.ALL_ORIGINS,
      },
      defaultIntegration: new LambdaIntegration(testLambdaFunction),
    });
    api.root.addMethod('GET');

    const itemsResource = api.root.addResource('items');
    new ApiLambdaHandler(this, 'GetItemsHandler', {
      apiResource: itemsResource,
      functionName: 'GetItems',
      handler: 'com.brian.backloghelperservice.lambda.handlers.item.GetItemsHandler::handleRequest',
      httpMethod: 'GET',
    });
    new ApiLambdaHandler(this, 'PutItemHandler', {
      apiResource: itemsResource,
      functionName: 'PutItem',
      handler: 'com.brian.backloghelperservice.lambda.handlers.item.PutItemHandler::handleRequest',
      httpMethod: 'PUT',
    });

    const itemResource = itemsResource.addResource('{itemId}')
    new ApiLambdaHandler(this, 'GetItemByIdHandler', {
      apiResource: itemResource,
      functionName: 'GetItemById',
      handler: 'com.brian.backloghelperservice.lambda.handlers.item.GetItemByIdHandler::handleRequest',
      httpMethod: 'GET',
    });
  }
}

import { Duration } from 'aws-cdk-lib';
import { LambdaIntegration, Resource } from 'aws-cdk-lib/aws-apigateway';
import { Code, Function, Runtime } from 'aws-cdk-lib/aws-lambda';
import { Construct } from 'constructs';

export interface ApiLambdaHandlerProps {
    apiResource: Resource;
    functionName: string;
    handler: string;
    httpMethod: string;
    customTimeout?: Duration;
}

export class ApiLambdaHandler extends Construct {

    constructor(scope: Construct, id: string, props: ApiLambdaHandlerProps) {
        super(scope, id);
    
        const lambdaFunction = new Function(this, props.functionName, {
            code: Code.fromAsset('../application/target/BacklogHelperService-1.0-SNAPSHOT.jar'),
            handler: props.handler,
            runtime: Runtime.JAVA_17,
            timeout: props.customTimeout || Duration.seconds(30),
        });

        props.apiResource.addMethod(props.httpMethod, new LambdaIntegration(lambdaFunction));
      }

}
# Development notes
### Need to install
* jdk 21
* maven
* node
* awscli
* aws cdk
* aws sam
* docker

### Infra
* `cdk synth` to create CFN template

### Application
* `mvn package` to create JAR

### Testing
* Invoke lambda locally using SAM:
  * First synthesize CDK and build application 
  * From `BacklogHelperService` directory: 
    * Run:
      ```bash
      sam local invoke TestLambda -t .\infra\cdk.out\InfraStack.template.json -e .\events\apigateway_event.json
      ```
* Invoke API locally using SAM:
  * TODO
      
### Credentials
* To get SSO creds:
  ```bash
  aws sso login --sso-session=brian
  ```
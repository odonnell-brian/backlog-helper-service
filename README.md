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
* First synthesize CDK and build application
* Start docker
* Invoke lambda locally using SAM:
  * From `BacklogHelperService` directory: 
    * Run:
      ```bash
      sam local invoke TestLambda -t .\infra\cdk.out\InfraStack.template.json -e .\events\apigateway_event.json
      ```
* Invoke API locally using SAM:
  * From `BacklogHelperService` directory:
    * Run:
      ```bash
      sam local start-api --template .\infra\cdk.out\InfraStack.template.json
      ```
  * Now you can hit your API at the endpoint seen in the command's output (default is `http://127.0.0.1:3000`)
    * Example: `curl http://127.0.0.1:3000/test`
      
### Credentials
* To get SSO creds:
  ```bash
  aws sso login --sso-session=brian
  ```
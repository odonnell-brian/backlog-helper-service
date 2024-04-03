# Development notes
### Need to install
* jdk 21
* maven
* node
* awscli
* aws cdk
* aws sam
* [NoSql Workbench for DynamoDB](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/workbench.html)

### Infra
* `cdk synth` to create CFN template

### Application
* `mvn package` to create JAR

### Setup
* DynamoDB
  1. In NoSql workbench, start the local DDB instance on port `8111` (bottom left toggle).
  2. Navigate to the `Data Modeler` and import [backlog-items.json](./application/resources/development/backlog-items.json)
  3. Navigate to the `Visualizer` and commit the data model to the local DDB instance.
  4. (Optional) Add some items to your table via the `Operation Builder`.

### Testing
* First create the jar using Maven and then synthesize CDK.
* In NoSql workbench start the local DDB instance on port 8111.
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
install:
    1. docker
    2. localstack
    3. stepfunctions-local
    
build and run:
    1. mvn package
    2. docker run --name localstack -p 4574:4574 --rm localstack/localstack
    3. DEBUG=stepfunctions-local:* stepfunctions-local start --lambda-endpoint http://localhost:4574 --lambda-region us-east-1
    4. run bdd tests
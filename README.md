install:
    1. docker
    2. localstack
    3. stepfunctions-local
    
build and run:
    1. mvn package
    2. docker run  -e "DEBUG=1" --name localstack -p 4574:4574 -p 4569:4569 -p 4572:4572 -p 4567:4567 -p 4582:4582 -p 32768:8080 -p 5005:5005 --rm localstack/localstack:0.8.7
    3. DEBUG=stepfunctions-local:* stepfunctions-local start
    4. run bdd tests
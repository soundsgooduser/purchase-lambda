install:
    1. docker
    2. localstack
    
build and run:
    1. mvn package
    2. docker run --name localstack -p 4574:4574 --rm localstack/localstack
    3. run bdd tests
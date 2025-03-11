package com.mycompany

class CommonDependencies {
    static def awsLambda = [
            'com.amazonaws:aws-lambda-java-core:1.2.2',
            'com.amazonaws:aws-lambda-java-events:3.11.1',
            'software.amazon.awssdk:sqs:2.20.56',
            'software.amazon.awssdk:kinesis:2.20.56'
    ]

    static def jacksonDependencies = [
            'com.fasterxml.jackson.core:jackson-databind:2.15.0',
            'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.0'
    ]

    static def testDependencies = [
            'org.junit.jupiter:junit-jupiter-api:5.9.3',
            'org.junit.jupiter:junit-jupiter-engine:5.9.3',
            'org.mockito:mockito-junit-jupiter:5.3.1'
    ]
}
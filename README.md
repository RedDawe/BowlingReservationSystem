# BowlingReservationSystem

Reservation system for a bowling alley to manage their users' reservations.

Functionality is described by the following Use Case Diagram and further by Analysis present in the repository.

![Use Case Diagram](https://drive.google.com/uc?export=view&id=1I9JOl8UCkrnuSKwodPG17Z9lF7K2ld6I)

You can also refer to the following ERD Diagram.

![Entity Relationship Diagram](https://drive.google.com/uc?export=view&id=1QyL_qiP26h3APjhSeEyRlHpTwHblvBV3)

# Tech stack

- The server is written in Java with Spring Boot and it is configured to use a PostgreSQL database
- The frontend is written in React that is automatically compiled by maven during compilation
- All of that is tested by GitHub Actions and bundled in a docker image that is then pushed and published on docker hub
- This allows for a pretty convenient way to deploy using Kubernetes, for example with the yaml files in <a href="https://github.com/RedDawe/BowlingReservationSystem/tree/main/kubernetes-specs">kubernetes-specs</a>

# Deployment

## Pre-requisites:
- Kubernetes
- kubectl
- <a href="https://github.com/RedDawe/BowlingReservationSystem/tree/main/kubernetes-specs">kubernetes-specs</a> files 

## Configuration:
The basic configuration that should be done is changing the password manager in: kubernetes-specs/bowling-reservation-system-spring.yml.
The value that you want to change is: brs-secret > data > manager_password

You are of couse free to modify any other value if you wish/need to do so.

## Installation:
- kubectl apply -f kubernetes-specs/postgres.yml
- kubectl create configmap hostname-config --from-literal=postgres_host=$(kubectl get svc postgres -o jsonpath="{.spec.clusterIP}")
- kubectl apply -f kubernetes-specs/bowling-reservation-system-spring.yml

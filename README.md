# BowlingReservationSystem

This system can be used by a bowling alley to manage their users reservations.

Functionality is described by the following Use Case Diagram and further by Analysis present in the repository.

![Use Case Diagram](https://drive.google.com/uc?export=view&id=1hj5hHYzzj_LsvGsLT8bA0t_EdzgbdIJz)

You can also refer to the following ERD Diagram.

![Use Case Diagram](https://drive.google.com/uc?export=view&id=1QyL_qiP26h3APjhSeEyRlHpTwHblvBV3)

# Tech stack

- The server is written in Java with Spring Boot and it is configured to use a PostgreSQL database, which is connected by hibernate
- The frontend is written in React that is automatically compiled by maven during compilation
- All of that is tested by GitHub Actions and bundled in a docker image that is then pushed and published on docker hub
- This allows for a pretty convenient way to deploy using Kubernetes, for example with the yaml files in <a href="https://github.com/RedDawe/BowlingReservationSystem/tree/main/kubernetes-specs">kubernetes-specs</a> folder


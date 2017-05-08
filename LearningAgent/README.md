# Learning Agent
## Running
Run command `mvn spring-boot:run`. Add `-Dgrpc.port=<port_number>` to specify port number (default is `6565`).

(Uncomment maven-resources-plugin in `pom.xml`)

## Docker
Run `docker build -f LearningDockerfile -t <image_name> .` 

and `docker run -p 6565:6565 -v <path_to_your_local_maven_repo>:/root/.m2 <image_name>`

If you want to specify port number add `-e port=<port>` and change `-p <port>:<port>`.

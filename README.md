# Server side

This is the server for the message handler app

## New version

release new version on docker-hub

1) Run docker-hub desktop
2) Increase the version number in build.gradle.kts file
3) Run this command to push the version on docker-hub

```gradle
./gradlew jib
```


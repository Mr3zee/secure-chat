# Secure Chat

## Steps to run:
Docker:
```bash
docker-compose -f deployments/docker-compose.yaml up -d
```

Application:
```bash
./gradlew server:application:run 
```

## What's under the hood
Secure chat is indeed secure. We use end-to-end encryption not for just your account, 
but for each chat you participate in.

Client-Server connection holds a state: 
you log in to your account, and a websocket session is being created 
inside which all the communications are being held. Server stores all the data encrypted, 
no one except you can decrypt it.

The app is fully reactive thanks to JetBrains Compose and Websockets.

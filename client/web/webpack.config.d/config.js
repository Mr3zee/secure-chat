if (config.devServer) {
    config.devServer.proxy['/websocket'] = {
        target: 'ws://localhost:8080',
        ws: true
    }
}

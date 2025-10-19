const fs = require('fs');
const http = require('http');
const {argv} = require('node:process');

const debug = argv.indexOf('-d') > -1
const basePath = debug ? './debug' : '.';

const port = 22228;

const requestListener = function (req, res) {

    try {
        const url = new URL(req.url, 'http://localhost:' + port);
        let file = basePath + url.pathname;
        console.log("Serving " + file)
        let content = fs.readFileSync(file);
        res.writeHead(200);
        res.end(content);
    } catch (e) {
        res.writeHead(500);
        res.end(JSON.stringify(e));
    }
}

const server = http.createServer(requestListener);
console.log('Listening on port ' + port + "...")
server.listen(port);
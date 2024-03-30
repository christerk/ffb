const fs = require('fs');
const http = require('http');

const port = 22228;

const requestListener = function (req, res) {

    try {
        const url = new URL(req.url, 'http://localhost:' + port);
        let content = fs.readFileSync('.' + url.pathname);
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
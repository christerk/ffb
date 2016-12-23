import * as express from "express";
import * as path from "path";

import { testPathFinder } from "./test/pathfinder";

let app = express();
app.use(express.static("client/htdocs"));

app.get("/test/pathfinder", testPathFinder);

app.get("/", (req, res) => {
    res.sendFile(path.join(__dirname + "client/htdocs/index.html"));
});

app.listen(3000);

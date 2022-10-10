var express = require("express");
var mysql = require("mysql");
var bodyParser = require("body-parser");
var app = express();

app.use(bodyParser.json());

var connection = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "",
  database: "delivery_app_project",
});
connection.connect();

//GET Services

app.get("/getCommands", function (req, res) {
  var query = "select * from commandes";
  connection.query(query, function (error, results) {
    if (error) {
      throw error;
    }
    res.send(JSON.stringify(results));
  });
});

app.get("/getCommand/:NumCmd", function (req, res) {
  var query = "select * from commandes where NumCmd=?";
  connection.query(query, [req.params.NumCmd], function (error, results) {
    if (error) {
      throw error;
    }
    res.send(JSON.stringify(results[0]));
  });
});

app.get("/getProducts", function (req, res) {
  var query = "select * from products";
  connection.query(query, function (error, results) {
    if (error) {
      throw error;
    }
    res.send(JSON.stringify(results));
  });
});

app.get("/getCommandsFromUserByID/:ID", function (req, res) {
  var query =
    "select C.NumCmd,C.NomClient,C.AdresseClient,C.NumTelClient,C.EmailClient,C.PrixCmd,UC.Delivered,UC.DateRecup from users_commands as UC inner join commandes as C on UC.CommandID = C.NumCmd where UC.UserID = ?";
  connection.query(query, [req.params.ID], function (error, results) {		
    if (error) {
      throw error;
    }
    res.send(JSON.stringify(results));
  });
});

app.get("/getProductsFromCommandByNum/:NumCmd", function (req, res) {
  var query =
    "select P.ID,P.Nom,P.Description,P.StockQte,P.PrixProduct,PC.ProductQte from commandes as C inner join products_commands as PC on C.NumCmd = PC.CommandID inner join products as P on PC.ProductID = P.ID where C.NumCmd = ?";
  connection.query(query, [req.params.NumCmd], function (error, results) {
    if (error) {
      throw error;
    }
    res.send(JSON.stringify(results));
  });
});

app.get(
  "/getProductsFromCommandByNumTelClient/:NumTelClient",
  function (req, res) {
    var query =
      "select P.ID,P.Nom,P.Description,P.StockQte,P.PrixProduct,PC.ProductQte from commandes as C inner join products_commands as PC on C.NumCmd = PC.CommandID inner join products as P on PC.ProductID = P.ID where C.NumTelClient = ?";
    connection.query(
      query,
      [req.params.NumTelClient],
      function (error, results) {
        if (error) {
          throw error;
        }
        res.send(JSON.stringify(results));
      }
    );
  }
);

//POST Services

app.post("/getUser", function (req, res) {
  var user = req.body;
  var query = "select * from users where Username = ? and Password = ?";
  connection.query(
    query,
    [user.Username, user.Password],
    function (error, results) {
      if (error) {
        throw error;
      }
      res.send(JSON.stringify(results));
    }
  );
});

app.post("/getUsername", function (req, res) {
  var user = req.body;
  var query = "select * from users where Username = ?";
  connection.query(
    query,
    [user.Username],
    function (error, results) {
      if (error) {
        throw error;
      }
      res.send(JSON.stringify(results));
    }
  );
});

app.post("/updtCmd", function (req, res) {
  var user_command = req.body;
  var query = "update users_commands set UserID = ?,Delivered = ?,DateRecup = ? where CommandID = ?";
  connection.query(
    query,
    [user_command.UserID,user_command.Delivered,user_command.DateRecup,user_command.CommandID],
    function (error, results) {
      if (error) {
        throw error;
      }
      res.send("success");
    }
  );
});

app.post("/updtCmdDelivered", function (req, res) {
  var user_command = req.body;
  var query = "update users_commands set Delivered = ? where CommandID = ?";
  connection.query(
    query,
    [user_command.Delivered,user_command.CommandID],
    function (error, results) {
      if (error) {
        throw error;
      }
      res.send("success");
    }
  );
});

var server = app.listen(8082, function () {
  var host = server.address().address;
  var port = server.address().port;
});

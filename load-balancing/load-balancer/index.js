var http      = require('http');
var httpProxy = require('http-proxy');
var url       = require('url')
var proxy     = httpProxy.createProxyServer({});
var port      = process.env.PORT;

var appServers = [
  {
    host: 'still-badlands-66687.herokuapp.com',
    target: 'https://still-badlands-66687.herokuapp.com'
  },
  {
    host: 'tranquil-earth-29722.herokuapp.com',
    target: 'https://tranquil-earth-29722.herokuapp.com'
  },
  // {
  //   host: 'localhost',
  //   target: 'http://localhost:5001'
  // },
  // {
  //   host: 'localhost',
  //   target: 'http://localhost:5002'
  // },
];

// https://sleepy-tor-37576.herokuapp.com/fib/40
var i = 0;
http.createServer(function(req, res) {
  req.headers.host = appServers[i].host;
  proxy.web(req, res, {target: appServers[i].target});
  i = (i+1) % appServers.length;
}).listen(port, function() {
  console.log('Load balancer listening on port ' + port.toString());
});

/*
 * loadICEF.js v1.0
 *
 * This file contains source code developed by the European
 * FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 *
 */

var jsonfile = require('jsonfile');
var http = require("http");
var fs = require("fs");
var path = require("path");

if(process.argv.length < 5) {
    console.log("Error: Invalid number of arguments");
    console.log("Usage: nodejs load.js SPEC HOST PORT");
    console.log("\tSPEC\tpath to file containing the ICEF specification to load");
    console.log("\tHOST\thostname or IP of CoreASIM manager");
    console.log("\tPORT\tport of CoreASIM manager");
    return -1;
}

var specification = process.argv[2];
var trgHost = process.argv[3];
var trgPort = process.argv[4];
console.log("ICEF Specification to load: "+specification);

function reloadJSON(icef, baseDir) {
    var toload = [];
    
    for(var prop in icef) {
<<<<<<< HEAD
	switch(prop) {
	case "asims" :
	case "schedulers" :
	    var asims = icef[prop];
	    if(!(asims instanceof Array)) {
		console.log("Invalid ICEF specification. Expected array of ASIM specifications in '"+prop+"'.");
	    } else {
		for(var i in asims) {
		    var asim = asims[i];
		    if(asim != undefined && asim.file != undefined) {
		    	console.log("file: ", asim.file);
			toload.push({ file : asim.file, index : i, prop : prop, start : asim.start });
		    }
		}
	    }
	    break;
	}
    }
    
    for(var i in toload) {
    	console.log("toload: ", toload);
	var file = toload[i].file;
	var index = toload[i].index;
	var prop = toload[i].prop;
	var start = toload[i].start;

	console.log("file: "+file);

	console.log(((String)("File"))[0]);

	if(file[0] != "/") {
	    file = baseDir + "/" + file;
	}
	
	data = new String(fs.readFileSync(file));
=======
	      switch(prop) {
	      case "asims" :
	      case "schedulers" :
	          var asims = icef[prop];
	          if(!(asims instanceof Array)) {
		            console.log("Invalid ICEF specification. Expected array of ASIM specifications in '"+prop+"'.");
	          } else {
		            for(var i in asims) {
		                var asim = asims[i];
		                if(asim != undefined && asim.file != undefined) {
			                  toload.push({ file : asim.file, index : i, prop : prop, start : asim.start });
		                }
		            }
	          }
	          break;
	      }
    }
    
    for(var i in toload) {
	      var file = toload[i].file;
	      var index = toload[i].index;
	      var prop = toload[i].prop;
	      var start = toload[i].start;

	      if(file[0] != "/") {
	          file = baseDir + "/" + file;
	      }
	      
	      data = new String(fs.readFileSync(file));
>>>>>>> 9c925a181cb29fa47e67117fa48d931fb5fec735

	      if(data == null) {
	          return null;
	      } else {
	          var newASIM = {};
	          
	          data = data.replace(/\/\/.+/g, "");
	          data = data.replace(/[ \n]*use .+/g, "");
	          
	          newASIM.name = /[ \n]*CoreASIM (.+)\n/g.exec(data)[1];
	          if(newASIM.name == null)
		            newASIM.name = "undefined";
	          newASIM.init = /[ \n]*init (.+)\n/g.exec(data)[1];
	          if(newASIM.init == null)
		            newASIM.init = "skip";
	          newASIM.policy = /[ \n]*scheduling (.+)\n/g.exec(data)[1];
	          if(newASIM.policy == null)
		            newASIM.policy = "skip";
	          
	          // guess the init rule and extract program(self)
	          var initRuleExp = new RegExp("rule[ \t]+"+newASIM.init+".*=(.|[\n\r])+?(rule|derived|controlled|universe)", "m");
	          var initRule = initRuleExp.exec(data)[0];
	          newASIM.program = /program\(self\) *:= *(.+?)\n/g.exec(initRule)[1];
	          if(newASIM.program == null)
		            newASIM.program = "skip";
	          
	          data = data.replace(/[ \n]*CoreASIM (.+)\n/g, "");
	          data = data.replace(/[ \n]*init (.+)\n/g, "");
	          data = data.replace(/[ \n]*scheduling (.+)\n/g, "");
	          data = data.replace(/^[ \n]*/m, "");
	          data = data.replace(/[ \n]*$/m, "");
	          
	          newASIM.signature = data;
	          
	          if(start != undefined)
		            newASIM.start = start;
	          
	          icef[prop][index] = newASIM;
	      }
    }

    return icef;
}

jsonfile.readFile(specification, function(err, icef) {
    if(err != null) {
	      console.log("Error: "+err);
    } else {
	      var baseDir = path.dirname(specification);
	      
	      var newICEF = reloadJSON(icef, baseDir);

	      console.log("Load the following specification:");
	      console.log(newICEF);

	      var data = JSON.stringify(icef);
	      
        var options = {
            host: trgHost,
            port: trgPort,
            path: '/simulations',
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Content-Length': Buffer.byteLength(data)
            }
        };

        var self = this;
        var req = http.request(options, function(res) {
            var resData = "";
	          
            res.setEncoding('utf8');
	          
            res.on('data', function(chunk) {
                if(chunk)
                    resData += chunk;
            });
	          
            // TODO: create error in brapper which shows that something went wrong
            res.on('end', function(chunk) {
                if(chunk)
                    resData += chunk;
		            
                if(res.statusCode != 201) {
                    console.log("Specification not loaded!");
		                console.log("Problem: ", resData);
                } else {
		                var result = null;
                    try {
                        result = JSON.parse(resData);
                        console.log("Specification loaded successfully.");
                    } catch(e) {
                        console.log("Unexpected response from manager: "+resData);
                    }
                }
            });
        });
	      
        req.on('error', function(e) {
            console.log("Unable to load ICEF specifiation: "+e);
        });
	      
        req.write(data);
        req.end();
    }
});

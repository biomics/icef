ICEF - The Interaction Computing Execution Framework
====================================================

This framework extends the original CoreASM modelling and execution framework to enable the specification and execution of distributed and concurrent ASMs.

More to come soon ...

Downloading, Building and Setup
-------------------------------

### CoreASIM Wrapper - Brapper

To build CoreASIM you are required to install [Maven](https://maven.apache.org/). This will also require a recent version of Java (> 1.7)

In order to build CoreASIM, please execute the following commands (ICEFDIR denotes a directory you choose):

```bash
git clone https://github.com/biomics/icef.git ICEFDIR
cd ICEFDIR/coreASIM/org.coreasim.parent
mvn package
```
After the build is complete you will find the standalone ``brapper.jar`` in ``ICEFDIR/coreASIM/org.coreasim.biomics.wrapper/target/``.

### CASIMA

Building and running it requires a current version of [node.js](https://nodejs.org/en/download/) and a current version of [npm](https://www.npmjs.com/), the node packet manager (usually packaged in node.js Downloads). To download the CoreASIM Manager, you need to clone the git repository locally as indicated above. To install all required packages you need to execute the following steps:

```bash
cd ICEFDIR/manager
npm install
```
After successful download and installation the manager is ready to run.

### loadICEF

As the project does not implement a user frontend which can be used to easily specify and load CoreASIM and ICEF specifications, we also provide a loader in the tools folder. It allows the referencing of CoreASIM specifications inside of ICEF specifications and uploads the ICEF specification directly to CASIMA. Cumbersome transformations of the JSON into REST requests are not necessary.

To install the loader, execute the following steps

```bash
cd ICEFDIR/tools
npm install
```

How To Run
----------

### CASIMA

In order to run ICEF simulations, it is first required to start the CoreASIM manager which will manage several CoreASIM instances during their execution, including the creation and destruction of engines, forwarding of messages, control ASIM instances, etc.

Before you run the manager, you may want to configure it using the configuration `ICEFDIR/manager/config.js`. By default it contains the following data:

```JavaScript
var Config = {
  socket : {
    port : 3000
  },
  httpServer : {
    port : 9090,
    host : 'localhost',
    path : '/'
  },
  scheduler : {
    port : 9091,
    host : 'localhost',
    jar : '../coreASIM/org.coreasim.biomics.wrapper/target/brapper.jar'
  }
};
```

The manager offers a `socket` which clients can connect to. For example, user interfaces or visualisation frameworks may be clients connecting to this port. At the moment this is only used for demo purposes. Thus we recommend to leave this port configuration as is.

Port and hostname (or IP) of the server to which all wrapped CoreASIM engines need to talk to, is configured in the `httpServer` section. 

Finally, each manager will run a scheduler ASIM whose connection data is specified in the `scheduler` section.

So, in case you want the manager and its scheduler to listen at a different network interface than localhost (and potentiall at different ports), you need to change the configuration file above.

To finally run the manager change into the manager directory and run it, as indicated below:

```
cd ICEFDIR/manager
npm start
```

As soon as the command succeeds, the CoreASIM manager will provide a REST interface which is documented in more details [here](http://docs.icef.apiary.io/#reference/manager). 

### CoreASIM Wrapper - Brapper

As indicated above, you will find the standalone ``brapper.jar`` in ``ICEFDIR/coreASIM/org.coreasim.biomics.wrapper/target/`` after building it. To get more information about the different command line options, simply run in the root of your installation, i.e. in ICEFDIR

```bash
java -jar coreASIM/org.coreasim.biomics.wrapper/target/brapper.jar
```

This will print the following usage information:

```Bash
 --help     : Shows this usage information (default: true)
 -c         : Allows the execution of channel ASIMs. (default: false)
 -h <host>  : Provide the IP or the hostname to which the wrapper should be
              bound. (default: localhost)
 -m <host>  : Sets the IP or the hostname which the wrapper should use as a
              wrapper manager.
 -mp <port> : Sets the port on which the wrapper manager listens. 
              This option is only valid in combination with -m. (default: 8080)
 -p <port>  : Sets the port to which the wrapper should listen to. (default:
              8080)
 -r <host>  : Wrapper only mode operates without manager, i.e. all messages are
              sent to the remote wrapper specified in this option.
 -rp <port> : Specifies the port of the remote brapper. (default: 8080)
 -s         : Allows the execution of several scheduling ASIMs. (default: false)
 -u         : Enables update accumulation inside the brapper. (default: false)
```

In order to run the modulo two counter cascade example sepcified in `ICEF/specifications/icef/mod2cc.icef` you will be required to run at least one brapper. We show how to run two brappers using the manager which was started as described above (executed in a linux shell):

```Bash
java -jar coreASIM/org.coreasim.biomics.wrapper/target/brapper.jar -m localhost -mp 9090 &
java -jar coreASIM/org.coreasim.biomics.wrapper/target/brapper.jar -m localhost -mp 9090 -p 8090 &
```

The first command line will start a brapper which connects to the manager running at `localhost` at port `9090`. As no host or port is specified the brapper will use the default values, i.e. the brapper will bind to port `8080` at `localhost`. The second command will also start a brapper which is managed by the same CAIMA instance but the brapper will listen to port `8090` at `localhost` instead. Note, that both brappers will only connect to the manager successfully, if the manager is already running. If the manager is running, the brappers will automatically register with it and can be used by it.

The brapper provides a REST interface which is also documented [here](http://docs.icef.apiary.io/#reference/brapper).

### loadICEF

You can run the loader either by executing ```npm start``` or by executing node.js ```nodejs loadICEF.js```. This will generate a usage message, describing the arguments required for a valid execution.

The loader requires a
* path to an ICEF specification
* the hostname/IP of the manager
* the port which the manager is listening to

The ICEF specification can have the following format:

```JavaScript
{
  "id":"mod2cc",
  "schedulers":[
    {
      "file":"../casim/inputProvider.casim",
      "start":true
    }
  ],
  "asims":[
    {
      "file":"../casim/lowestBit.casim",
      "start":true
    }
  ],
  "updates":[
    {
      "target":"@UI@",
      "registrations":[
        {
          "location":"state"
        },
        {
          "location":"successor"
        }
      ]
    }
  ]
}
```

The loader resolves the references to the CoreASIM specifications above (relative to the current location of the specification) and transforms it into a valid ICEF specification which can be sent to CASIMA.

The Latest Version
------------------

Latest released versions of ICEF can be found on [github](https://github.com/biomics/).

Licensing
---------
 
The ICEF project is licensed under the Academic Free License version 3.0 which is available from <http://www.opensource.org/licenses/afl-3.0.php>

Thanks for using ICEF

The BIOMICS Consortium
2015-2016


# AVAILABILITY TACTICS

This assignment is on availability tactics in Software Architecture. Two popular tactics, ping-echo and heartbeat is
implemented in this assignment.
I have used docker to create multiple servers (for slaves) and have used my host machine as listener (master) to listen
to ping echoes and heartbeats.

## PING - ECHO
A listener (master) server pings slave servers and expects reply (PONG) in a specified time limit, otherwise marks it
dead. Slave servers are deployed using docker containers and listener is on the host machine. Listener can also be
in a docker container, in that case, please change following run command (for master) accordingly.

### Running Instructions
    * To deploy slaves
    ```sh
    docker run -ti -v /path/to/ping-echo:/ping-echo ubuntu python3 /ping-echo/slave.py
    ```
    Accordingly edit config.py with correct IP addresses of the slaves.
    * To run master
    ```sh
    python3 /path/to/ping-echo/master.py
    ```

## HEARTBEAT
A listener (master) server has a list of servers which keep sending heartbeats at regular intervals to the master.
For implementation, I have used twice the sleeptime to be the time interval before which I mark the slave as dead.
Deployment of slave and master is exactly similar as above PING-ECHO.

### Running Instructions
    * To deploy slaves
    ```sh
    docker run -ti -v /path/to/heartbeat:/heartbeat ubuntu python3 /heartbeat/slave.py
    ```
    * To run master
    ```sh
    python3 /path/to/heartbeat/master.py
    ```

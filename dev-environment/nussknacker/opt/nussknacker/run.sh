#!/bin/sh

/opt/nussknacker/runServer.sh &
cd /opt/nussknacker/ui/client && npm ci && npm start

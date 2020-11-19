#!/bin/sh

/opt/nussknacker/ui/runServer.sh &
cd /opt/nussknacker/ui/client && npm ci && npm start

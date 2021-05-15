#!/bin/bash

cd .test

docker compose up -d

cd ..

(
for i in `seq 1 10`;
            do
              if [[ "$OSTYPE" == "msys" ]]; then
                # make sure you have installed ncat via nmap on your windows installation
                ncat -z localhost 5432 && echo Success && exit 0
              else
                nc -z localhost 5432 && echo Success && exit 0
              fi
              echo -n .
              sleep 1
            done
            echo Failed waiting for Postgres && exit 1
)

sbt ciTests

cd .test

docker compose down

cd ..
@echo off

echo Start docker container with postgres db
cd .test
docker compose up -d
cd ..

echo Wait until container is started
set /A counter=0
:loop
IF %counter% gtr 9 (
  goto failed
) ELSE (
  ncat -z localhost 5432 && echo Success: connection established && goto connection_etablished
  timeout /t 1 /nobreak
  set /A counter=%counter% + 1
  goto loop
)

:failed
echo Failed establishing connection && exit 1
goto:eof

:connection_etablished
echo Running tests
@sbt ciTests
echo Shutdown docker container
@cd .test
@docker compose down
@cd ..
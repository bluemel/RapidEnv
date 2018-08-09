@echo off
echo Test Argument passing:
echo argument 1: @%1@
echo argument 2: @%2@
echo argument 3: @%3@
set arg2stripped=@@@%2@@@
set arg2stripped=%arg2stripped:@@@"=%
set arg2stripped=%arg2stripped:"@@@=%
echo argument 2': @%arg2stripped%@
set arg3stripped=@@@%3@@@
set arg3stripped=%arg3stripped:@@@"=%
set arg3stripped=%arg3stripped:"@@@=%
set arg3stripped=%arg3stripped:\"="%
echo argument 3': @%arg3stripped%@

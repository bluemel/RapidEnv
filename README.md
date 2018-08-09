RapidEnv (renv)
===============

RapidEnv is a small tool to set up and maintain complex development environments
(environment variables and tools) for large teams.
The RapidEnv approach is to describe all necessary environment set up tasks:
- set up of environment variables
- installation and configuration of tools and frameworks
in order to set up a standard development environment in one single XML file.
The renv command line tool then performs all set up tasks by interpreting this XML description.

Some notes concerning the environment set up of RapidEnv:

- the final goal is to use RapidEnv (renv) itself for the environment set up checked into source control
  under folder "environment"
  Status August 2018: RapidEnv (renv) environment set up has to be reworked

- if you can't or do not want to use RapidEnv

  - install:
    - a latest and greatest JDK version - current version - August 2018 - 10.0.2
    - (optional) a JDK 8 in order to verify downward compatibility
    - a latest and greatest Apache Maven version - current version - August 2018 - 3.5.4
    - The Java IDE of your choice Eclipse, IntelliJ, ... or whatever

    - Build project rapidenv-tool in order to build the renv command line tool.

    - Use setupenv.cmd.tmpl and setupenv18.cmd.tmpl to configure your environment
       by copying it to folder rapidenv-tool and filling in correct values

    - Please note: the set up of environment Variable RAPID_ENV_HOME is crucial for the
      tests to work out because this variable is used by the renv tool production code.

     - Please note also: take care that the environment variable set up is done before
       starting the Java IDE of your choice by "sourcing" the environment setup script.
       E. g. Windows shortcut example:
	   C:\Windows\System32\cmd.exe /C call D:\Projects\sources\RapidEnv\rapidenv-tool\setupenv.cmd & start D:\Projects\tools\eclipse\4.8.0\eclipse.exe

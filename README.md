# CamundaSpring
Simple Camunda workflow with spring beans

This project is to help me learn/re-learn a few things.
* Camunda BPMN 2.0 workflow
* RESTful controller using Spring MVC
* Spring annotations
* Spring Profiles
* Mockito
* Maven based builds
* configuring SLF4J as default for all logging

<code>maven clean install</code> will run a couple of unit tests:
* exercise the rest controller using mockito mocks
* run the workflow engine through a service interface to run the workflow to completion

Workflow is defined using BPMN 2.0 and run using the Camunda engine. An image (PNG) of the workflow can
be found for those that don't have a BPMN editor.

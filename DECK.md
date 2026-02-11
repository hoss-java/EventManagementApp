---
Title: EventManagement
Description: plans and project management sheets
Date: 
Robots: noindex,nofollow
Template: index
---

# EventManagement

## Analyzing all parts

|#|Part|Details|Total Duration|Status|
|:-|:-|:-|:-|:-|
|1|-|-|-|-|-|
|:-|:-|:-|::||


## Timeplan

```mermaid
gantt
    section %BOARD%
```

# 1 - EventManagement

## 001-0001
> **Add deck and github workflow to the repo.** ![status](https://img.shields.io/badge/status-DONE-brightgreen)
> <details >
>     <summary>Details</summary>
> The goal of this card is to add git-deck and github actions files to the repo.
> 
> # DOD (definition of done):
> Git-deck is initialized.
> GitHub workflow files are added.
> 
> # TODO:
> - [x] 1. Install pm and git-deck
> - [x] 2. Add GitHub Workflow
> - [x] 3. Add actiom secret-TAP to the repo
> - [x] 3. Add step-1-cards
> 
> # Reports:
> * git-deck and GitHub workflow are initialized.
> * A board named EventManagment was added
> * A workflow to update DECK was added
> * Plan to continue working
> > 1. Create skeleton (both code and test parts) 
> > 2. Start to document (creating README and other document files)
> > 3. Start using a separated branch instead main to develop
> >> * How to automate it
> >> * Create a action and way to merge the develop branch to the main branch both automatically when all tests are passed and manually via running an action
> > 4. Build/create/develop local test flows (DI)
> > 5. Integrate local and remote workflows
> > 6. plan next steps
> </details>

## 001-0002
> **Create skeleton - both code and test parts** ![status](https://img.shields.io/badge/status-DONE-brightgreen)
> <details >
>     <summary>Details</summary>
> The goal of this card is to create a skeleton for the project
> 
> # DOD (definition of done):
> Skeleton for main codes are added.
> Skeleton for tests are added.
> Skeleton for documents are added.
> 
> # TODO:
> - [x] 1. Create main code skeleton
> - [x] 2. Test skeletons
> - [x] 3, README and other document skeletons
> 
> # Reports:
> * An empty class with two test skeletons were created
> </details>

## 001-0003
> **Initialize documents** ![status](https://img.shields.io/badge/status-DONE-brightgreen)
> <details >
>     <summary>Details</summary>
> The goal of this card is to create document templates, and a summery of the project an its aims.
> 
> # DOD (definition of done):
> Templates are created and initialized.
> 
> # TODO:
> - [x] 1. Update README with a summery of all that have been done until now
> - [x] 2. Aim of the project.
> - [x] 3. An overview of the plan
> 
> # Reports:
> *
> </details>

## 001-0004
> **Spike how to structure working with dev and main branch.** ![status](https://img.shields.io/badge/status-DONE-brightgreen)
> <details >
>     <summary>Details</summary>
> The goal of this card is to spike how to work with two branches, dev and main in a structured way,
> 
> # DOD (definition of done):
> All findings are documented
> 
> # TODO:
> - [x] 1. Start to push on a dev branch
> 
> # Reports:
> * The repository will now include three branches.
> > |Branch Type|Common Names|
> > |:-|:-|
> > |Development Branch|`develop`|
> > |Main Branch|`main`|
> > |Release Branch|`release`|
> 
> * Until now all codes were stored/pushed to a main branch, the idea is to use `develop` as a developing branch,
> > * All codes on local repo(s) will be committed/pushed to `develop` branches only
> > * When a commit is created on the `develop` branch a GitHub action recommit some files such as README and kanban boards files automatically to main.
> > * If a commit to `develop` has a specific command in its message. and passes all tests, an other action merges `develop` to `main`
> > * One or several action checks the `main` branch commits, if they pass integration tests they will be merged to a `release` branch (updated periodically, such as on a weekly basis.)
> > * They can also be merged manually
> > * A `release` branch can also implement an auto-downgrade action, allowing for rollbacks of releases based on analytical insights from logs, not now maybe later!
> 
> * Some useful commands to work with branches
> > ```
> > # Confirm A Branch
> > git branch
> > 
> > #Check Out the Main Branch
> > git checkout main
> > # Pull Latest Changes
> > #git pull origin main
> > 
> > # Create the develop Branch, all current branch files are copied to the new branch , `develop`
> > git checkout -b develop
> > # It also can create an empty branch and then copy or fetch files manually
> > git checkout develop
> > # To copy files from a branch to another for example from main to develop
> > git checkout main
> > git checkout develop -- path/to/file1 path/to/file2
> > 
> > 
> > # Commit on the develop branch (If Needed)
> > #git add .
> > #git commit -m "Create develop branch and add initial files from main"
> > #git push origin develop
> > 
> > ```
> 
> * Summary of Commands
> |Action|Command|
> |:-|:-|
> |Check out Main Branch|`git checkout main`|
> |Create Empty Develop Branch|`git branch develop`|
> |Switch to Develop Branch|`git checkout develop`|
> |Checkout Specific Files from Main|	`git checkout main -- path/to/file`|
> |Stage Added Files|`git add path/to/file`|
> |Commit Changes|`git commit -m "..."`|
> 
> * Apply the idea above to the current repo
> > * The `main` is created as an empty branch, GithHub workflow files (`.github` )are only file that can committed manually. It will manage by GitHUb actions from the `develop` branch.
> > * A local repo is a clone of the `develop` branch.
> > * A `develop` does not need to update DECK files, or run other similar actions, auto generate document and pm files are only generated on the `main` branch.
> > * When a commit is pushed to `develop`, some files and folders such as `.pm` and `README.md` will be automatically fetched an re committed to main (from `develop`).
> > * The same as today when `.pm` files on the main are changed, an action re-build DECk and other auto generated documents ( on the main),regardless `develop` passes tests or not.
> > * When a commit are pushed to the develop, at least to actions are runs, one to test codes (test codes can updated or create repot filed sd README on the develop branch), and an action to auto merge selected files from `develop` to `main`
> 
> * What it needs to do on this card to start working on a multi-branches repo
> > 1. Create a new `develop`  branch contained all `main`branch files
> >>```
> >># switch to main, if the current branch is not the main
> >>git checkout main
> >># Create `develop` with a copy of all files in the current branch (`main`)
> >>git checkout -b develop
> >>```
> > 2. Delete all files from the root of the branch `main`, except for the `.github` directory.
> >>```
> >># Back to the main branch
> >>git checkout main
> >>
> >># Delet all files and folder except `.github` folder
> >>git add -A
> >>git commit -m "message"
> >>git push origin main
> >>
> >>git checkout develop
> >># Continue working with project con the develop branch
> >>```
> </details>

## 001-0005
> **Create a pre-action to merge the dev branch to the main branch.** ![status](https://img.shields.io/badge/status-DONE-brightgreen)
> <details >
>     <summary>Details</summary>
> The goal of this card is to push some files (from a list) push to the `develop`branch to the main automatically.
> 
> # DOD (definition of done):
> A GitHub action is developed and implemented.
> 
> # TODO:
> - [x] 1. Create an GitHub action for the `develop` to auto-push files to the `main`
> 
> # Reports:
> * The current action, `generate-deck.yml` was updated to work only if the branch is `main`. So even the action file is added to other branches such as develop, it does nothing.
> > * However I thing removing a file ( in this case an YAML action file) that is not used or ignored makes a repo more clear.
> > * For now it will be kept for a while but it will be removed from `develop` when designing actions are completely done.
> * A new action name `auto-merge-changes.yml` was added to the `develop`  branch to read a list of files and folders from a file named `.gitmainauto` from the root of the `develop` branch and push them to main automatically after pushing to the `develop`
> 
> ## Updating rge design for git branches and their connections
>  
> * During working on workflow automation' action it was realized there is no need copy/merge git-deck (`.pm/`) files and its related files such as `.gitdefault` (and potentially `.gitignore`) from the `develop` branch to the `main` branch
>  
> > * git-dek and kanban boards a part of develop flows, not a part of the code.
> > * The `main` branch serves as a repository for the ready to use code (beta version of the final code), which is used for intermediate testing of integrations between various components of the project. Once the code passes all tests, it will be merged into the 'release' branch.
> > * The `release` branch serves as a repository for the final code (ready to implement).
> 
> * So according to this new design, the only files that needs to be merged to the `main` automatically is `README.md`.
> * There is also no needs to generate `DECK.md` on the `main` branch. The main is not contained git-deck `.pm/` files anymore. So the action to generate DECK on the main can removed.
> * The action to generate DECK is run on the `develop` and push the generated `DECK.md`to the `main`
> 
> ```mermaid
> graph TD
>     A[**`develop branch`**] -->|Push auto-generated files| B[**`main branch`**]
>     A -->|Push default files| B
>     A -->|Run Tests| D{Tests Passed?}
>     D -->|Yes| B
>     D -->|No| A
>     B -->|Run Tests| E{Tests Passed?}
>     E -->|Yes| C[**`release branch`**]
>     E -->|No| B
>     C -.->|Implement| F((production))
> ```
> 
> ## Findings
> 
> ### multi-jobs' action and jobs orders
> There are several method that can be used to run multi-actions in a repo
> 
> 1. If the actions can run concurrently without conflicts, creating actions YAML files and adding them to GitHub Workflows work fine.
> 2. If the actions must run in a specific order or cannot run in parallel, it needs to trigger them sequentially by each other.
> 
> ### GitHub Job Ordering
> * Define job ordering in a workflow
> 
> ```name: CI Workflow
> 
> on:
>   push:
>     branches:
>       - develop
> 
> jobs:
>   job1:
>     runs-on: ubuntu-latest
>     steps:
>       - name: Step 1
>         run: echo "This is Job 1"
> 
>   job2:
>     runs-on: ubuntu-latest
>     needs: job1  # This job will wait for job1 to finish
>     steps:
>       - name: Step 2
>         run: echo "This is Job 2"
> 
>   job3:
>     runs-on: ubuntu-latest
>     needs: [job1, job2]  # This job will wait for both job1 and job2 to finish
>     steps:
>       - name: Step 3
>         run: echo "This is Job 3"
> ```
> 
> * Define Workflows in Separate Files
> > * Workflow A (workflow-a.yml):
> >```
> >name: Workflow A
> >
> >on:
> >  push:
> >    branches:
> >      - develop
> >
> >jobs:
> >  job_a:
> >    runs-on: ubuntu-latest
> >    steps:
> >      - name: Step A
> >        run: echo "Running Workflow A"
> >```
> > * Workflow A (workflow-a.yml):
> >```
> >name: Workflow B
> >
> >on: 
> >  workflow_run:
> >    workflows: ["Workflow A"]  # Depends on Workflow A
> >    types:
> >      - completed
> >
> >jobs:
> >  job_b:
> >    runs-on: ubuntu-latest
> >    steps:
> >      - name: Step B
> >        run: echo "Running Workflow B after Workflow A"
> >```
> 
> * **OBS!** In multi-trigger actions, the initial trigger always activates the actions. For example if in the `on` section `push` and `workflow_run` both are defined as triggers (example below `Auto-Merge changes`) and also the second action (`Generate DECK.md`) is defined to be activates by `push`, the first one (`Auto-Merge changes`) will not wait for the second (`Generate DECK.md`). Both start in the same time by `push`.
> ```
> name: Auto-Merge changes
> 
> on:
>   push:
>     branches:
>       - develop  # Commit changes here will trigger the action
>   workflow_run:
>     workflows: ["Generate DECK.md"]  # Depends on 
>     types:
>       - completed  # Ensure this is set to completed
> ```
> 
> * **OBS!** Regardless how actions are added, all in a file or in several files, if no order or dependency are defined, all action will be run in their isolated workflow in a parallel way.
> 
> ### Define action orders and trigger actions by each other
> ## Three methods to chain GitHub Actions
> 
> 1. **Separate workflows (cross-workflow pipeline)**  
>    - Create distinct workflows and chain them using `workflow_run` or `repository_dispatch` so one workflow triggers the next.  
>    - Example: workflow A finishes → workflow B starts via `workflow_run` with `types: [completed]`.  
>    - **Limitations:** `workflow_run` only triggers when the upstream workflow runs on the default branch (commonly main); passing complex artifacts requires explicit upload/download; cross-repo triggers need extra auth (PAT or repository_dispatch).
> 
> 2. **Single workflow — ordered jobs/steps**  
>    - Define multiple jobs in one workflow and enforce order with `needs` (or use ordered steps within a job).  
>    - Use `concurrency`, `if` conditions, or matrix strategy to control parallelism and conditional execution.  
>    - **Limitations:** Very large workflows become harder to maintain; long-running workflows may delay other jobs; limited cross-repo orchestration.
> 
> 3. **Orchestrator / reusable pipeline action**  
>    - Use a reusable workflow (`workflow_call`) or a custom orchestrator action that triggers workflows via the GitHub API/gh to run and coordinate other workflows (including cross-repo).  
>    - Offers dynamic branching, retries, and centralized control; requires handling auth if using the API.  
>    - **Limitations:** Custom orchestrator actions need a PAT for API triggers (manage secrets/permissions); more complex to implement; API rate limits and auth scopes apply. Reusable workflows cannot be called across forks without extra configuration.
> 
> Summary table
> 
> | Method | When to use | Key idea | Example trigger | Key limitations |
> |---|---:|---|---|---|
> | 1) Separate workflows | Clear stage separation, independent retries | Chain workflows using `workflow_run` or `repository_dispatch` | workflow A → workflow B via `workflow_run` | `workflow_run` only for default branch; artifact passing harder; cross-repo auth needed |
> | 2) Single workflow | Simple pipeline, easy artifact sharing | Define jobs/steps in one workflow; use `needs` | jobA → jobB (needs: [jobA]) | Harder to maintain when large; less flexible cross-repo |
> | 3) Orchestrator / reusable pipeline | Complex runtime orchestration or cross-repo control | Use `workflow_call` or custom action to orchestrate runs via API | Orchestrator triggers workflow_dispatch or calls reusable workflow | Requires PAT/auth for API; possible rate limits; added complexity |
> 
> Minimal examples
> 
> 1) Single workflow (ordered jobs)
> ```yaml
> name: CI
> on: [push]
> jobs:
>   build:
>     runs-on: ubuntu-latest
>     steps: [...]
>   test:
>     runs-on: ubuntu-latest
>     needs: [build]
>     steps: [...]
>   deploy:
>     runs-on: ubuntu-latest
>     needs: [test]
>     steps: [...]
> ```
> 
> 2) Separate workflows (workflow_run)
> workflow A (build):
> ```yaml
> on: [push]
> # produces artifacts
> ```
> workflow B (test/deploy):
> ```yaml
> on:
>   workflow_run:
>     workflows: ["workflow A name"]
>     types: [completed]
> # Note: triggers only when workflow A ran on the default branch
> ```
> 
> 3) Orchestrator / reusable pipeline
> - Reusable pipeline (pipeline.yml):
> ```yaml
> on:
>   workflow_call:
>     inputs:
>       run-tests: { type: boolean, required: false, default: true }
> jobs:
>   build: {...}
>   test:
>     needs: [build]
>     if: ${{ inputs.run-tests }}
>   deploy:
>     needs: [test]
> ```
> - Caller workflow (calls pipeline):
> ```yaml
> on: [workflow_dispatch]
> jobs:
>   call-pipeline:
>     uses: ./.github/workflows/pipeline.yml
>     with:
>       run-tests: true
> ```
> Or: use a custom orchestrator action/step that calls the GitHub API (`workflow_dispatch`) or `gh` to trigger workflows across repos; remember to store PAT in secrets.
> 
> Recommendation
> - Prefer single workflow for small/simple pipelines and easy artifact sharing.  
> - Use separate workflows with `workflow_run` when you want stage separation and independent retries, but watch default-branch limitations.  
> - Use reusable workflows (`workflow_call`) or a custom orchestrator action for reusable pipelines or complex cross-repo orchestration; manage auth and rate limits carefully.
> 
> 
> * **OBS!** **GitHub only fires workflow_run for workflows whose workflow file exists on the repository default branch (usually main)**
> 
> 
> * References
> > * https://docs.github.com/en/actions/reference/workflows-and-actions/events-that-trigger-workflows
> </details>

## 001-0006
> **Plan and create stories for next steps.** ![status](https://img.shields.io/badge/status-DONE-brightgreen)
> <details >
>     <summary>Details</summary>
> The goal of this card is to plan for next steps.
> 
> # DOD (definition of done):
> Related cards are added to the board.
> 
> # TODO:
> - [x] 1. List what needs to do on this step
> - [x] 2. Create cards
> 
> # Reports:
> ## What need to do
> 1. Start with main class and its test
> 2. Automate local and remote repos to run test automatically
> 3. Implement an UI class (+ it tests)
> 4. Implement a simple block test
> 5. Implement an event class
> 6. Implement a participants class
> 7. Implement a report manager class
> 8. Implement a DB layer
> </details>

## 001-0007
> **Develop a basic main class with its tests.** ![status](https://img.shields.io/badge/status-DONE-brightgreen)
> <details >
>     <summary>Details</summary>
> The goal of this card is to develop a basic main class with its tests.
> 
> # DOD (definition of done):
> A main class is created.
> Tests are added.
> Boths local and remote DI run tests as a part of the workflow
> All findings are documented.
> 
> # TODO:
> - [x] 1. Create the main class
> - [x] 2. Recap how work with maven projects
> 
> # Reports:
> * A basic java app and its basic tests based the calapp was create (https://github.com/hoss-java/calapp-workshop/)
> * A simple script was added to repo to make it faster to run maven commands
> >```
> >#!/bin/sh
> >docker exec -it maven mvn -f EventManagementApp/EventManApp "$@"
> >```
> * Now instead a long commands a shorter command can be used:
> >```
> ># To clean temporary files and caches
> >docker exec -it maven mvn -f EventManagementApp/EventManApp clean
> ># or
> >./emrun clean
> >
> ># To compile and creating jar file
> >docker exec -it maven mvn -f EventManagementApp/EventManApp compile
> >#docker exec -it maven mvn -f EventManagementApp/EventManApp clean compile
> ># or
> >./emrun compile
> >./emrun clean compile
> >
> ># To run tests
> >docker exec -it maven mvn -f EventManagementApp/EventManApp test
> ># or
> >./emrun test
> >
> ># To Create package
> >docker exec -it maven mvn -f EventManagementApp/EventManApp package
> ># or
> >./emrun package
> >
> ># To run main
> >docker exec -it maven mvn -f EventManagementApp/EventManApp exec:java
> ># or
> >./emrun exec:java
> >```
> </details>

## 001-0008
> **Automate local and remote repos to run test automatically.** ![status](https://img.shields.io/badge/status-DONE-brightgreen)
> <details >
>     <summary>Details</summary>
> The goal of this card is to add needed actions to GIHub workflow to run tests.
> 
> # DOD (definition of done):
> GitHub workflows uses tests results to verify commits.
> 
> # TODO:
> - [x] 1. Develop a simple local CI
> - [x] 2. Find out how to run tests on GitHub side
> - [x] 3. Find out how to check commit messages via workflow actions
> - [x] 4. Implement test check to git hooks
> - [x] 5. Implement test check to GitHUb Workflow
> - [x] 6. Develop an automation to run test and push codes to main branch if test passed
> 
> # Reports:
> * What it means with local CI here :
> > * A simple Continuous Integration (CI) system integrates one or more tools to run tests on code, ensuring that all components function correctly.
> > * Considering the above point, a tool can run tests either manually or automatically before pushing code to the repository.
> > * The tool(s) report the results of running tests as logical outputs, such as 0 (true, passed) and 1 (false, failed). This format allows scripts, particularly local ones using Git and its hooks, to make informed decisions based on the test outcomes.
> * So what it needs to do for the local part
> > * Develop a simple tools to run test which is already done (`mavenrun`  script)
> >> * `mavenrun` can be used both directly and via sourcing
> >>> * Use as a script on cli
> >>>> ```
> >>>>#./mavenrun [--quiet] <maven-directives>
> >>>># for example
> >>>>./mavenrun test
> >>>>./mavenrun --quiet test
> >>>>./mavenrun clean test
> >>>>./mavenrun compile
> >>>>./mavenrun clean compile
> >>>>./mavenrun package
> >>>>./mavenrun exec:java
> >>>>
> >>>># exit code can be use of the result
> >>>># 0 - Success
> >>>># 1 - Failed
> >>>> ```
> >>> * Sourcing
> >>>> ```
> >>>> source mavenrun
> >>>> 
> >>>>#mrun [--quiet] <maven-directives>
> >>>>#mstatus
> >>>>
> >>>>mstatus
> >>>>
> >>>>mrun test
> >>>>mrun --quiet test
> >>>>mrun clean test
> >>>>mrun compile
> >>>>mrun clean compile
> >>>>mrun package
> >>>>mrun exec:java
> >>>>
> >>>># exit code can be use of the result
> >>>># 0 - Success
> >>>># 1 - Failed
> >>>> ```
> > * Implement the script to git hooks
> >> * It has also been implemented in Git hooks. The current `.git/hooks/pre-commit` script checks for a file named .commit-check in the root of the Git repository. If it finds the file, it will execute it and return an exit code to git. [pre-commit](https://github.com/hoss-java/git-hooks/blob/main/hooks/pre-commit)
> >> * It means it needs only to call `mrun` in the `.commit-check` and return the exit code of `mrun` to git, a simple `.commit-check`
> >>>```
> >>>#: Pre-commit checker to use within git hook `pre-commit`
> >>>#:
> >>>#: File : .commit-check
> >>>#: Description : The code here run to check committed files
> >>>#:      before committing.
> >>>#:      The code set exitCode=1 if the check result is false
> >>>#:      Otherwise exitCode=0 (or doing nothing).
> >>>#: OBS! : This file should be located in the root of the repo.
> >>>#
> >>>#
> >>># Local variables:
> >>>#  exitcode
> >>>#  GIT_ROOT
> >>>#---------------------------------------
> >>>    bash -c "source $GIT_ROOT/mavenrun && mrun --quiet test" \
> >>>    && exitcode=0
> >>>else
> >>>    exitcode=1
> >>>fi
> >>>```
> >> * It also needs to make it possible to skip checks when it needs.
> >>> * To achieve this, the `git-hooks` have been updated to include improved scripts that are more generic. Most of the scripts added/developed to/for the `git-hooks` are designed to work specifically with `commit-msg`. However, these helper scripts are now more versatile, allowing them to be used with other hooks such as `pre-commit` [git-hooks](https://github.com/hoss-java/git-hooks/tree/main).
> >>> * A new setting (`commitCheckFlag=true`) was added to `.gitdefault` to defain default status to run tests.
> >>> * It's also possible now skip running test by using a directive (`--notest`) via git message
> >>>>```
> >>>># for example 
> >>>>git commit -m "[B001-C0009] Improve git hooks." -m "--notest"
> >>>>```
> >>> * By default if no file named `.commit-check` is found in the root of the repository, running tests is skipped regardless what the default setting is or which directive is used.
> 
> ## Find out how to run tests on GitHub side
> * It seems packages such as JDK and maven can be not installed in the way that is used to Ubuntu packages.
> * It can be install through reuse the packages that already available and installed (I don't how!).
> * However here the way that worked
> >```
> >    steps:
> >      # Step 1: Checkout the repository
> >      - name: Checkout repo (fetch all)
> >        uses: actions/checkout@v5
> >        with:
> >          fetch-depth: 0
> >
> >      # Step 2: Set up Java
> >      - name: Set up JDK 17
> >        uses: actions/setup-java@v4
> >        with:
> >          java-version: '17'
> >          distribution: 'temurin'
> >          cache: maven
> >```
> 
> * Running tests and merge codes from the `develop`  branch to the `main` has been not set up to run after each commit.
> > * A new section has been added to the pipeline to check for directives included in the commit message. If the message contains `--merge` (not on the first line, but added on the second line or later), then the tests and merge action will be triggered.
> >>```
> >># for example 
> >>git commit -m "[B001-C0008] Automate running tests - github action" -m "--merge"
> >>```
> 
> * Now both local and remote provide running tests to check code integration. In the local case , tests is run before commit changes via `git commit -m ...`  or `git commit --amend`
> * Running tests in local cases can be skipped by passing the directive `--notest` via git commit message, or by changing default settings via file `.gitdefault` stored on the repository root
> * On the remote case, the tests are run if a commit message provides the directiv `--merge`. In the remote case it all tests are passed, files listed on the file `.gitmain` stored on the the repository root are merged from `develop` to `main` also.
> </details>

## 001-0009
> **Implement an UI class and it tests** ![status](https://img.shields.io/badge/status-DONE-brightgreen)
> <details >
>     <summary>Details</summary>
> The goal of this card is to implement an UI class.
> 
> # DOD (definition of done):
> A UI class with basic requiments are implemented.
> 
> # TODO:
> - [x] 1. Create an empty class as a library
> 
> # Reports:
> * The idea is develop the app in a way that supports multi-interfaces, as As the primary and default user interface a cl-interface is designed
> * The interface receives a list of supported commands in JSON format along with a callback function. It can call this callback to execute commands defined within the JSON data. For instance, a CLI interface can create an interactive menu. When a user selects an item from this menu, the corresponding command is executed by invoking the callback function.
> > * Similarly, a REST interface can start a REST service and listen for incoming requests. When a supported command is received, the REST interface calls the callback function to execute that command.
> > * It is also possible to send a list of commands and their parameters (in JSON format) to clients, such as JavaScript-based applications like React, Angular, or Node.js. The REST interface facilitates this communication, allowing clients to interact with users in the same way as the CLI interface does. Clients can send commands back to the REST interface, which then forwards them to the callback function.
> > * The plan for implementing a second interface includes developing the REST interface first, followed by a CLI-based interface, such as cURL, to connect to the REST interface.
> * I also found how to work with Threads on Java, and added a simple log manager to test it. It can be used later to manage multi-iterfaces. For now cl-interface is an interactive interface and can not be run on background but interfaces such rest-interface can.
> * To make it simple , a callback function method was used to run commands, it means an interface gets the list of commands in a json format and a callback function, then interact between user/service and the callback function.
> * Call back function runs commands and report the result, and also log requests.
> 
> 
> ## Not related to this card
> * **OBS!** The action to merge the `develop` branch to the `main` had an issue to use env flags between jobs, it was solved by the guide here https://cicube.io/blog/github-actions-if-condition/
> </details>

## 001-0011
> **Implement an event class.** ![status](https://img.shields.io/badge/status-DONE-brightgreen)
> <details >
>     <summary>Details</summary>
> The goal of this card is to implement a class for manage events.
> 
> # DOD (definition of done):
> An event class according the scenario is implemented.
> 
> # TODO:
> - [x] 1. Add a class skeleton for event
> - [x] 2. Impalement a command parser to the class
> 
> # Reports:
> The goal is to create a class that stores items in a database. Initially, before establishing a database connection layer, the class will retain data in memory as event objects. 
> 
> To facilitate migration from memory objects to database records, an `EventObject` class has been defined, which manages the attributes of event objects. The `EventObjectMan` class, built on the `ObjectHandler`, operates in conjunction with `EventObject`. 
> 
> The `EventObjectMan` class includes two crucial methods for managing connections between interfaces and event objects: **`parseCommands`** and **`isValidCommand`**.
> 
> 
> ## How It Works
> 
> When the interface generates a JSON command, it follows this structure:
> 
> ```json
> {
>     "id": "addevent",
>     "description": "Add an event",
>     "args": {
>         "title": "<title>",
>         "location": "<location>",
>         "capacity": "<capacity>",
>         "date": "<date>",
>         "time": "<time>",
>         "duration": "<duration>"
>     }
> }
> ```
> 
> 1. The interface calls a callback function with the aforementioned JSON.
> 2. This callback function verifies the command's validity with all registered classes (e.g., `EventObjectMan`) by invoking the **`isValidCommand`** method.
> 3. If a registered class accepts the command, the command and its arguments (in JSON format) are forwarded to **`parseCommands`**.
> 4. **`parseCommands`** acts as a multi-command parser, although in this instance, only one command is processed.
> 5. **`parseCommands`** executes the command; for our example, it adds a new event based on the specified arguments.
> 6. It then generates a response and returns it to the caller, which is the callback function in this case.
> 7. The callback function sends the response to a logging thread and also to the original caller, such as `ConsoleInterface`, which has its own unique identifier.
> 
> The above design is generic and will also be utilized for the participant class.
> </details>

## 001-0012
> **Implement a participants class.** ![status](https://img.shields.io/badge/status-DONE-brightgreen)
> <details >
>     <summary>Details</summary>
> The goal of this card is to implement a class for participants.
> 
> # DOD (definition of done):
> 
> # TODO:
> - [x] 1. Clean Up an restructure current code to be more reusable
> - [ ] 2. Update the main to support multi interfaces.
> - [ ] 3. Add Participant object handler
> 
> # Reports:
> * The code now resembles an application, but many sections remain unoptimized. Before proceeding with the participants, it’s essential to refine the code. This involves focusing on specific components, such as event classes, to ensure consistency in their structure. The code needs some cleanup to enhance clarity and performance.
> > * Moving JSON methods an functions to a helper class
> > * Adding a responsHelper
> > * Move interfaces to lib/interfaces
> > * Many improvement were applied
> >> * `ConsoleInterface` was moved to `lib/interfaces`
> >> * A new addons folder was aded to `lib/validator` to add validators, for now there validators have been coded , `Duration`, `Date` and `Time`
> >> * A new helper named `StringParserHelper` was added to parse tags with in fields. It is applied to input strings before adding to EMObjects
> >> * `EventObject` class was removed and a new generic class named `EMObject` was coded instead. The new `EMObject` can be used to manage all kind of needed objects such as `event` , `participant` and `organizer`
> >> * The structure the JSON used on `command.json` was updated with new attributes.
> >> improve code structures
> >> A participant object and object handle based on the even object was added
> >> * UML was updated
> </details>

## 001-0010
> **Implement a simple block test.** ![status](https://img.shields.io/badge/status-ONGOING-yellow)
> <details open>
>     <summary>Details</summary>
> The goal of this card is to implement a simple block test system to project.
> 
> # DOD (definition of done):
> 
> # TODO:
> - [ ] 1. 
> 
> # Reports:
> * 
> </details>

## 001-0013
> **Implement an organize class.** ![status](https://img.shields.io/badge/status-ONGOING-yellow)
> <details open>
>     <summary>Details</summary>
> The goal of this card is to implement an organizer to connect events to participant.
> 
> # DOD (definition of done):
> 
> # TODO:
> - [x] 1. Add Organize object
> 
> # Reports:
> * To make it possible the tables were restructured.
> * Now all group items are seen as KVObject, each object has its own identifier
> * KVObjects are separated when they saved via a storage class.
> * There is no table(object) manager specified for a group anymore
> * A KVObject manager handles all kind of objects.
> * A subject manager was implemented. A subject define contents of an object type, A subject can be seen as a schema.
> * For now three pre-define schema have been added,(`event`, 'participant', and `organize`)
> * In the first run the subject handlers reads pre-define data for subjects from a xml file named `subjects.xml` from the resources folder. And save them through Storage manager. After the first time when the sorage manager created a copy of the schema , the subject handler looks for subjects om the storage. It means it can be change if it needed.
> ```
> >```
> ><subjects>
> >    <subject identifier="event">
> >        <field name="id" field="id" type="int" mandatory="true" modifier="auto" >defaultValue="1"/>
> >        <field name="title" field="title" description="Title" type="str" mandatory>="true" modifier="user" defaultValue="workshop"/>
> >        <field name="location" field="location" description="Location" type="str" >mandatory="false" modifier="user" defaultValue="here"/>
> >        <field name="capacity" field="capacity" description="Capacity" type=">unsigned" mandatory="false" modifier="user" defaultValue="1"/>
> >        <field name="date" field="date" description="Date" type="date" mandatory=">false" modifier="user" defaultValue="2023-10-01"/> <!-- Example Date -->
> >        <field name="starttime" field="starttime" description="Time" type="time" >mandatory="false" modifier="user" defaultValue="00:00:00"/>
> >        <field name="duration" field="duration" description="Duration" type=">duration" mandatory="false" modifier="user" defaultValue="PT0H"/>
> >    </subject>
> >    <subject identifier="participant">
> >        <field name="id" field="id" type="int" mandatory="true" modifier="auto" >defaultValue="1"/>
> >        <field name="name" field="name" description="Name" type="str" mandatory=">true" modifier="user" defaultValue=""/>
> >        <field name="email" field="email" description="Enaik" type="str" mandatory>="false" modifier="user" defaultValue=""/>
> >    </subject>    
> >    <subject identifier="organize">
> >        <field name="id" field="id" type="int" mandatory="true" modifier="auto" >defaultValue="1"/>
> >        <field name="eventid" field="eventid@id:event.title" description="Event" >type="int@str" mandatory="true" modifier="user" defaultValue=""/>
> >        <field name="participantid" field="participantid@id:participant.name" >description="Participant" type="int@str" mandatory="false" modifier="user"> defaultValue=""/>
> >    </subject>
> ></subjects>
> >```
> * There was some issue to auto id with the KVOBjects and the old system did not work and could not be adapted to new objects. To fix issues an improve other parts a new class name ConfigManager was added. ConfigManager handles and responsible to save and reading settings (a singleton class).
> * For now KVObjects support there functions, `add` , `remove` and `gets` or `get` they are flexible and can be used and configured for almost all needs.
> * All KVOBjects functions `add` , `remove` and `gets` support chain commands, in other words they can be configured to find an object in a group of objects and then use result to apply next steps. For example a filed define as "participantid@id:participant.name", means `participantid` is `id` of `participant.name`. or in an action means we send/input a name, it will find id of name in the object group of participant and use it as participantid on organize table. A chain has no limit on the number of parts.
> * The structure of `commands.json` was also improved, now a commands.json looks >like below
> >```
> >{
> >    "commands": [
> >        {
> >            "id" : "event",
> >            "description": "Event",
> >            "commands": [
> >                {
> >                "id" :"event.add",
> >                "description": "Add an event",
> >                "action" : "event.add",
> >                "args": {
> >                    "id": {
> >                        "field" : "id",
> >                        "type": "int",
> >                        "mandatory" : false,
> >                        "modifier": "auto",
> >                        "defaultValue": "1",
> >                        },
> >                    "title": {
> >                        "field" : "title",
> >                        "description": "Title",
> >                        "type": "str",
> >                        "mandatory" : true,
> >                        },
> >                    "location": {
> >                        "field" : "location",
> >                        "description": "Location",
> >                        "type": "str",
> >                        "mandatory" : false,
> >                        "defaultValue": "here",
> >                        },
> >                    "capacity": {
> >                        "field" : "capacity",
> >                        "description": "Capacity",
> >                        "type": "unsigned",
> >                        "mandatory" : false,
> >                        "defaultValue": "12",
> >                        },
> >                    "date" : {
> >                        "field" : "date",
> >                        "description": "Date",
> >                        "type": "date",
> >                        "mandatory" : false,
> >                        "defaultValue": "%DATE%",
> >                        },
> >                    "time" : {
> >                        "field" : "time",
> >                        "description": "Time",
> >                        "type": "time",
> >                        "mandatory" : false,
> >                        "defaultValue": "%TIME%",
> >                        },
> >                    "duration" : {
> >                        "field" : "duration",
> >                        "description": "Duration",
> >                        "type": "duration",
> >                        "mandatory" : false,
> >                        "defaultValue": "PT15M",
> >                        },
> >                    },
> >                },
> >                {
> >                "id" : "event.getsall",
> >                "description": "List all events.",
> >                "action" : "event.gets",
> >                "args": {
> >                    }
> >                },
> >                {
> >                "id" : "event.gets",
> >                "description": "Search/List events.",
> >                "action" : "event.get",
> >                "args": {
> >                    "id": {
> >                        "field" : "id",
> >                        "type": "int",
> >                        "mandatory" : false,
> >                        "compareMode": "=",
> >                        },
> >                    "title": {
> >                        "field" : "title",
> >                        "description": "Title",
> >                        "type": "str",
> >                        "mandatory" : false,
> >                        "compareMode": "contains"
> >                        },
> >                    "location": {
> >                        "field" : "location",
> >                        "description": "Location",
> >                        "type": "str",
> >                        "mandatory" : false,
> >                        "compareMode": "contains",
> >                        },
> >                    "date" : {
> >                        "field" : "date",
> >                        "description": "Date",
> >                        "type": "date",
> >                        "mandatory" : false,
> >                        "defaultValue": "%DATE%",
> >                        "compareMode": "<="
> >                        },
> >                    }
> >                },
> >                {
> >                "id" : "event.removebyid",
> >                "description": "Remove event by id.",
> >                "action" : "event.remove",
> >                "args": {
> >                    "id": {
> >                        "field" : "id",
> >                        "type": "int",
> >                        "mandatory" : true,
> >                        "compareMode": "="
> >                        },
> >                    }
> >                },
> >                {
> >                "id" : "event.removebytitle",
> >                "description": "Remove event by title.",
> >                "action" : "event.remove",
> >                "args": {
> >                    "title": {
> >                        "field" : "title",
> >                        "description": "Title",
> >                        "type": "str",
> >                        "mandatory" : true,
> >                        "compareMode": "="
> >                        },
> >                    }
> >                },
> >            ]
> >        },
> >        {
> >            "id" : "participant",
> >            "description": "Participant",
> >            "commands": [
> >                {
> >                "id" :"participant.add",
> >                "description": "Add a participant",
> >                "action" : "participant.add",
> >                "args": {
> >                    "id": {
> >                        "field" : "id",
> >                        "type": "int",
> >                        "mandatory" : false,
> >                        "modifier": "auto",
> >                        "defaultValue": "1",
> >                        },
> >                    "name": {
> >                        "field" : "name",
> >                        "description": "Name",
> >                        "type": "str",
> >                        "mandatory" : true,
> >                        },
> >                    "email": {
> >                        "field" : "email",
> >                        "description": "Email",
> >                        "type": "str",
> >                        "mandatory" : false,
> >                        "defaultValue": "",
> >                        },
> >                    },
> >                },
> >                {
> >                "id" : "participant.getsall",
> >                "description": "List all participants.",
> >                "action" : "participant.gets",
> >                "args": {
> >                    }
> >                },
> >                {
> >                "id": "participant.gets",
> >                "description": "Search/List participants.",
> >                "action" : "participant.gets",
> >                "args": {
> >                    "id": {
> >                        "field" : "id",
> >                        "type": "int",
> >                        "mandatory" : false,
> >                        "compareMode": "=",
> >                        },
> >                    "name" : {
> >                        "field" : "name",
> >                        "description": "Name",
> >                        "type": "str",
> >                        "mandatory" : false,
> >                        "compareMode": "contains"
> >                        },
> >                    }
> >                },
> >                {
> >                "id" : "participant.removebyname",
> >                "description": "Remove participant by id/name.",
> >                "action" : "participant.remove",
> >                "args": {
> >                    "id": {
> >                        "field" : "id",
> >                        "type": "int",
> >                        "mandatory" : true,
> >                        "compareMode": "="
> >                        },
> >                    "name": {
> >                        "field" : "name",
> >                        "description": "Name",
> >                        "type": "str",
> >                        "mandatory" : true,
> >                        "compareMode": "="
> >                        },
> >                    },
> >                },
> >                {
> >                "id" : "participant.removebyid",
> >                "description": "Remove participant by id.",
> >                "action" : "participant.remove",
> >                "args": {
> >                    "id": {
> >                        "field" : "id",
> >                        "type": "int",
> >                        "mandatory" : true,
> >                        "compareMode": "="
> >                        },
> >                    }
> >                },
> >            ]
> >        },
> >        {
> >            "id" : "organize",
> >            "description": "Organize",
> >            "commands": [
> >                {
> >                "id" :"organize.add",
> >                "description": "Register participant to an event",
> >                "action" : "organize.add",
> >                "args": {
> >                    "id": {
> >                        "field" : "id",
> >                        "type": "int",
> >                        "mandatory" : false,
> >                        "modifier": "auto",
> >                        "defaultValue": "1",
> >                        },
> >                    "eventid": {
> >                        "description": "Event",
> >                        "field" : "eventid@id:event.title",
> >                        "type": "int@str",
> >                        "mandatory" : true,
> >                        },
> >                    "participantid": {
> >                        "description": "Participan",
> >                        "field" : "participantid@id:participant.name",
> >                        "type": "int@str",
> >                        "mandatory" : true,
> >                        },
> >                    },
> >                },
> >                {
> >                "id" : "organize.getsall",
> >                "description": "List all organize.",
> >                "action" : "organize.gets",
> >                "args": {
> >                    }
> >                },
> >                {
> >                "id": "organize.gets",
> >                "description": "Search/list registered participant to events.",
> >                "action" : "organize.gets",
> >                "args": {
> >                    "participantid" : {
> >                        "field" : "participantid@id:participant.name",
> >                        "description": "Participant",
> >                        "type": "int@str",
> >                        "mandatory" : false,
> >                        "compareMode": "=",
> >                        "defaultValue": "-1",
> >                        },
> >                    },
> >                },
> >                {
> >                "id" : "organize.remove",
> >                "description": "Remove participant from organize by name.",
> >                "action" : "organize.remove",
> >                "args": {
> >                    "participantid": {
> >                        "field" : "participantid@id:participant.name",
> >                        "description": "Participant",
> >                        "type": "int@str",
> >                        "mandatory" : true,
> >                        "compareMode": "=",
> >                        "defaultValue": "-1",
> >                        },
> >                    }
> >                },
> >            ]
> >        },
> >    ]
> >}
> >
> >```
> </details>

## 001-0014
> **Implement a DB layer.** ![status](https://img.shields.io/badge/status-ONGOING-yellow)
> <details open>
>     <summary>Details</summary>
> The goal of this card is to implement a DB layer
> 
> # DOD (definition of done):
> 
> # TODO:
> - [ ] 1. Spike to find ways to implement a data manager layer
> 
> # Reports:
> * I realize it needs to apply many changes on the current designed to apply an efficient data layer.
> * The current object handlers use an object named EMObject, the object has already an identifier and handlers set it when they create an object. It means objects have already an identifier but it is not use.
> * In the current design each handler creates its own EMObject list. So there is nothing unrelated to the handler in the list, they don't need to check it!
> * Howw it looks like now
> > * The core is based on a multi  key-value object named BaseObject, base object it self use a internal sub object to manage filed (key-value : String-<value-type>). This object dose not care about teh number of fields and fields name/type or owner. Its only check names to avoid key duplication.
> > * EMObject extends BaseObject and adds a fields map and an id to specify objects. 
> >> * It uses a mao based on EMObjectField which is the class to define field attributes such as type, modifier, default value and so on.
> >> * An EMObject stores a key-value base data based an JSON source according to a field map. In other words it get a field map and an JSON source, then creates a BaseObject from THE JSON source according to the fields map. It means values stored on the JSON as String is validated and converted to types defined via the fields map,
> >> * Additional key-vlues(s) on the JSON that are not covered by the fields maps are ignored.
> >> * So EMObject contains , an **id**, **fields** map and also **fields key-value(s) data**
> >> * It has also a validator method that can be override if needed. For now validator supports **str**, **int**, **unsigned**, **date**, **time** and **duration** only.
> >>> TOFIX ->>**isValidForAddition**
> 
> * All variables and files with name EM were change to KV to make it more generic name.
> * Add and remove objects can be moved from all type handlers to KVObjectHandler, It just needs to know id for both add and remove, and also which object(s) to remove
> * 
> 
> ```sql
> -- Define user, password, and database
> -- Define user, password, and database
> SET @username := 'eventman';
> SET @password := 'eventman-pass';
> SET @database := 'eventman';
> SET @ip_range := '172.32.0.%';
> 
> -- Drop the user if it exists
> SET @drop_user_stmt = CONCAT('DROP USER IF EXISTS ''', @username, '''@''', @ip_range, '''');
> PREPARE stmt FROM @drop_user_stmt;
> EXECUTE stmt;
> DEALLOCATE PREPARE stmt;
> 
> -- Create the user
> SET @create_user_stmt = CONCAT('CREATE USER ''', @username, '''@''', @ip_range, ''' IDENTIFIED BY ''', @password, '''');
> PREPARE stmt FROM @create_user_stmt;
> EXECUTE stmt;
> DEALLOCATE PREPARE stmt;
> 
> -- Create the database
> SET @create_database_stmt = CONCAT('CREATE DATABASE IF NOT EXISTS ', @database);
> PREPARE stmt FROM @create_database_stmt;
> EXECUTE stmt;
> DEALLOCATE PREPARE stmt;
> 
> -- Grant privileges to the user
> SET @grant_stmt = CONCAT('GRANT ALL PRIVILEGES ON ', @database, '.* TO ''', @username, '''@''', @ip_range, '''');
> PREPARE stmt FROM @grant_stmt;
> EXECUTE stmt;
> DEALLOCATE PREPARE stmt;
> 
> -- Optional: Flush privileges to ensure changes take effect
> FLUSH PRIVILEGES;
> 
> -- Verify the created user and display grants
> SET @verify_stmt = CONCAT('SHOW GRANTS FOR ''', @username, '''@''', @ip_range, '''');
> PREPARE stmt FROM @verify_stmt;
> EXECUTE stmt;
> DEALLOCATE PREPARE stmt;
> 
> -- Instead, you can directly execute SHOW GRANTS
> -- SHOW GRANTS FOR 'eventman'@'172.32.0.%';
> 
> -- End of script
> 
> ```
> </details>

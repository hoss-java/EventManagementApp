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
> * A new action name `auto-merge-changes.yml` was added to the `develop`  branch to read a list of files and folders from a file named `.gitautomarge` from the root of the `develop` branch and push them to main automatically after pushing to the `develop`
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

## 001-0009
> **Implement an UI class and it tests** ![status](https://img.shields.io/badge/status-NOT--STARTED-lightgrey)
> <details >
>     <summary>Details</summary>
> The goal of this card is to implement an UI class.
> 
> # DOD (definition of done):
> A UI class with basic requiments are implemented.
> 
> # TODO:
> - [] 1.
> 
> # Reports:
> *
> </details>

## 001-0010
> **Implement a simple block test.** ![status](https://img.shields.io/badge/status-NOT--STARTED-lightgrey)
> <details >
>     <summary>Details</summary>
> The goal of this card is to implement a simple block test system to project.
> 
> # DOD (definition of done):
> 
> # TODO:
> - [] 1.
> 
> # Reports:
> *
> </details>

## 001-0011
> **Implement an event class.** ![status](https://img.shields.io/badge/status-NOT--STARTED-lightgrey)
> <details >
>     <summary>Details</summary>
> The goal of this card is to implement a class for manage events.
> 
> # DOD (definition of done):
> 
> # TODO:
> - [] 1.
> 
> # Reports:
> *
> </details>

## 001-0012
> **Implement a participants class.** ![status](https://img.shields.io/badge/status-NOT--STARTED-lightgrey)
> <details >
>     <summary>Details</summary>
> The goal of this card is to implement a class for participants.
> 
> # DOD (definition of done):
> 
> # TODO:
> - [] 1.
> 
> # Reports:
> *
> </details>

## 001-0013
> **Implement a report manager class.** ![status](https://img.shields.io/badge/status-NOT--STARTED-lightgrey)
> <details >
>     <summary>Details</summary>
> The goal of this card is to implement a report  manager.
> 
> # DOD (definition of done):
> 
> # TODO:
> - [] 1.
> 
> # Reports:
> *
> </details>

## 001-0014
> **Implement a DB layer.** ![status](https://img.shields.io/badge/status-NOT--STARTED-lightgrey)
> <details >
>     <summary>Details</summary>
> The goal of this card is to implement a DB layer
> 
> # DOD (definition of done):
> 
> # TODO:
> - [] 1.
> 
> # Reports:
> *
> </details>

## 001-0008
> **Automate local and remote repos to run test automatically.** ![status](https://img.shields.io/badge/status-ONGOING-yellow)
> <details open>
>     <summary>Details</summary>
> The goal of this card is to add needed actions to GIHub workflow to run tests.
> 
> # DOD (definition of done):
> GitHub workflows uses tests results to verify commits.
> 
> # TODO:
> - [] 1.
> 
> # Reports:
> *
> </details>

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

## 001-0006
> **Code scripts needed to run test locally.** ![status](https://img.shields.io/badge/status-NOT--STARTED-lightgrey)
> <details >
>     <summary>Details</summary>
> The goal of this card is to code some scripts to run tests.
> 
> # DOD (definition of done):
> The DI scripts are coded and tested
> 
> # TODO:
> - [] 1.
> 
> # Reports:
> *
> </details>

## 001-0007
> **Integrate local and remode workflows.** ![status](https://img.shields.io/badge/status-NOT--STARTED-lightgrey)
> <details >
>     <summary>Details</summary>
> The goal of this card is to integrate local workflow scripts with GitHub workflow actions.
> 
> # DOD (definition of done):
> All findings are documented.
> Both local and remote workflows follow the same patterns.
> 
> # TODO:
> - [] 1.
> 
> # Reports:
> *
> </details>

## 001-0008
> **Plan and create stories for next steps** ![status](https://img.shields.io/badge/status-NOT--STARTED-lightgrey)
> <details >
>     <summary>Details</summary>
> The goal of this card is to plan for next steps.
> 
> # DOD (definition of done):
> Related cards are added to the board.
> 
> # TODO:
> - [] 1.
> 
> # Reports:
> *
> </details>

## 001-0005
> **Create a pre-action to merge the dev branch to the main branch.** ![status](https://img.shields.io/badge/status-ONGOING-yellow)
> <details open>
>     <summary>Details</summary>
> The goal of this card is to push some files (from a list) push to the `develop`branch to the main automatically.
> 
> # DOD (definition of done):
> A GitHub action is developed and implemented.
> 
> # TODO:
> - [ ] 1. Create an GitHub action for the `develop` to auto-push files to the `main`
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
>     A(Develop) -->|Push Code| B(Main)
>     A -->|GitHub Action| C(README.md updated in Main)
>     A -->|Commit " —mergetomain"| B
>     B -->|Run Tests| D{Tests Passed?}
>     D -->|Yes| E(Integration Merge into Release)
>     D -->|No| F(Stop Process)
>     
>     style A fill:#f9f,stroke:#333,stroke-width:4px;
>     style B fill:#bbf,stroke:#333,stroke-width:4px;
>     style C fill:#bbf,stroke:#333,stroke-width:2px;
>     style D fill:#ff0,stroke:#333,stroke-width:4px,stroke-dasharray: 5, 5;
>     style E fill:#afa,stroke:#333,stroke-width:4px;
>     style F fill:#fbb,stroke:#333,stroke-width:4px;
> ```
> </details>

ioaSDASIO 
asdas
sadfasf

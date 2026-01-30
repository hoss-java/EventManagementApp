# Event Management App
[**Deck Board**](https://github.com/hoss-java/EventManagementApp/blob/main/DECK.md)

## An introduction and overview

### A summery all have been done until now

* Java
>> * Things that have been done
>> * Investigation about some Java frameworks to have a better image about the area that is working on [Java frameworks](https://github.com/hoss-java/lessons/blob/main/w48-java1/java_frameworks_spike.md)
>> * Code and develop java standalone classes, and run them via cli [Raw java](https://github.com/hoss-java/calapp-workshop/tree/main/rawjava)
>> * Create maven projects and coding classes as a part of a maven project [Maven project](https://github.com/hoss-java/calapp-workshop/tree/main/CalApp)
>> * Create libraries and helper classes [Librery and helper class](https://github.com/hoss-java/calapp-workshop/blob/main/CalApp/src/main/java/com/CalApp/lib/ExpressionParser.java)
>> * Using libraries and helper classes [Using library](https://github.com/hoss-java/calapp-workshop/blob/main/CalApp/src/main/java/com/CalApp/CalApp.java)
>> * Develop unit tests for methods using classic Junit [Using classic Junit](https://github.com/hoss-java/calapp-workshop/blob/main/CalApp/src/test/java/com/CalApp/ExpressionParserTestJ3.java)
>> * Develop unit tests for methods using Jupiter [using Jupiter](https://github.com/hoss-java/calapp-workshop/blob/main/CalApp/src/test/java/com/CalApp/ExpressionParserTestJ5.java)
>> * Mock and stubbing on both Junit and Jupiter by invoking [Stubbing](https://github.com/hoss-java/calapp-workshop/blob/main/CalApp/src/test/java/com/CalApp/CalAppTest.java)
>> * Touch coding block tests (only Junit) [Block test](https://github.com/hoss-java/calapp-workshop/blob/main/CalApp/src/test/java/com/CalApp/CalAppTest.java)
>> * Command and instruction to work with Maven projects [README](https://github.com/hoss-java/calapp-workshop/blob/main/README.md)
> * Findings
>> * Simple Maven project can be created via cli or pre-defined project on IDEs but an advanced project needs to work/edit (with)  with pom files directly. [Here](https://github.com/hoss-java/calapp-workshop/blob/main/CalApp/pom.xml) an example that uses both Junit and Jupiter on a maven project, and it was not possible to add both of them through cli or IDEs.
>> * Java has some powerful frameworks to develop tests but not good as other languages to mock and stubbing.
>> * It is very time-consuming to develop block-tests and advanced unit-tests by using Jupiter. Jupiter focues mostly on unit-tests that no needs to advanced mock and stub. However classic Junit works better in the case of block-tests.
>> * Maven cli has many pre-define instructions and option that can make easy to automate running tests and code on an CI/DI environment, specially through GitHub workflows' actions. Some maven commands are documented [here](https://github.com/hoss-java/calapp-workshop/blob/main/README.md)

* Working environment and tools
> * Basic requirements such as java-jdk and maven
>> * Almost all basic requirements can be used via containers to have an isolated working environment which is ready to move to clouds or setting up under k8.
>> * To prepare the requirements and isolate (as well as optimize) the environment, a separate task was undertaken to develop the necessary containers. [containers' repo](https://github.com/hoss-java/containers?tab=readme-ov-file)
>>> * Some containers, such as `phpMyAdmin`, were a bit complicated. Unfortunately, the methods used for their development and configuration are not well documented. There are only some notes [here](https://github.com/hoss-java/EventManagementApp/blob/main/DECK.md) added to the cards created during working on them.
>> * Develop n Lan status monitor to report container statues, [`lan-monitor`](https://github.com/hoss-java/lan-monitor)
> * Job and story management
>> * Investigation GitHub and its tools to manage a projects, documented [here](https://github.com/hoss-java/git-hooks/blob/main/github_api_spike.md)
>> * Develop a git base Kanban board management named [`git-deck`](https://github.com/hoss-java/git-hooks)
>> * Integrate `git-deck` with GitHub by using [GitHub workflow actions](https://github.com/hoss-java/git-hooks/tree/main/.github)
> * Editor and IDE
>> * Develop several sublime plugins to improve and optimize coding and documenting environments. [Sublime-text plugins](https://github.com/hoss-java/sublime-plugins)
>> * Improved cli editor, `nano` to optimized and make it faster to edit and have access to files (Not documented :().
>> * Integrate `git-deck` (kanban board) with both cli and Sublime-text ,[cli integration](https://github.com/hoss-java/git-hooks/blob/main/extra-tools/git-deck-completion.sh), [sublime-text integration](https://github.com/hoss-java/sublime-plugins/tree/main/MarkdownXtra)

* Using AI tools
> * Tools
>> * Develop a python base tools to filter sensitive contents (such as name, password, email ...) from clipboard when codes/logs are copied/pasted to AI chat tools. [clipboard-privacy](https://github.com/hoss-java/clipboard-privacy/blob/main/clipboard-privacy.py)
>> * As the way the is usually used to work with AI chats during spiking and coding (using browser base AI interfaces), A browser base solutions to manage sensitive contents instead a system base was started and a Firefox extension developed. It works but still needs more improvements. [clipboard-privacy] Firefox extension](https://github.com/hoss-java/clipboard-privacy/tree/main/Firefox-extensions/clipboard-privacy)
>> * Several regEx patterns were created to filtering sensitive contents, [RegEx patterns](https://github.com/hoss-java/clipboard-privacy/blob/main/clipboard-privacy.json)
> * Findings
>> * Utilizing AI as an assistant integrated into WOW for the first time has significantly improved overall speed. However, there are insights on how and where to effectively utilize AI tools to enhance efficiency and prevent getting stuck.
>>> * Areas that helps a lot and enhances efficiency
>>>> * Correcting and improving short texts and notes works perfectly.
>>>> * Analyzing logs and outputs is highly efficient. However, copying and pasting logs containing personal or specific information, such as usernames, paths, or passwords—regardless of privacy concerns—makes management difficult. AI tools often concentrate on these not-related-issue parts of the logs rather than the overall content. In many AI tools, using generic placeholders like `USERNAME` or `path/to/file` instead of real data helps refocus the AI's attention on the main issues rather than on irrelevant text. That was the main reason to develop [clipboard-privacy](https://github.com/hoss-java/clipboard-privacy/blob/main/clipboard-privacy.py).
>>>> * Using AI to remember code that one already knows enhances efficiency.
>>> * Areas that are time-consuming and can actually hinder optimization and reduce efficiency.
>>>> * Structuring Doxygen and summarizing functions/method inputs and returns works well, but the additional descriptions often add complexity and, in many cases, create confusion.
>>>> * Creating the README and user guide results in well-formatted text, but unfortunately, the content is completely unusable and confuses the reader. Writing prompts to produce concise and efficient text that helps users quickly understand the subject is truly time-consuming.
>>>> * Prompts that mix logic and algorithms with coding can lead to confusion on AI tools and getting stuck to get a correct result.
>>>> * Asking AI to code functions or methods when there’s uncertainty about their correctness is truly time-consuming.


### The goal and aims

* * The main goal of this project is to use it as a platform to leverage all previously developed methods and tools to enhance WOW efficiently. Additionally, it aims to integrate these components to work together and develop or add any missing required elements.
* Aims
> * Using git branches as intermediate repositories instead of merging code directly into the main branch.
> * Automate the merging of the intermediate branch into the main branch using GitHub Actions workflows.
> * Automate tests (DI)
> * Automate the product integration (CI)
> * Enhance productivity when working with helper classes and libraries in Java.
> * Enhance unit testing techniques learned previously to develop robust and trustworthy tests.
> * Touch/Implement real block-test coding.
> * Enhance productivity when working with databases in Java.
> * Implement Java code in an isolated, networked setup using a container-based solution.
> * Touch/Implement java codes/apps as services.

### An overview of the plan

* Step 1: Implementing a git branch base workflow
* Step 2: Develop a basic code according the project scenario
* Step 3: Implement tests for both unit and block levels.
* Step 4: Automate testing and integrating it to GitHub workflows
* Step 5: Add advanced/required futures according the project scenario
* Step 6: Documenting and summarizing findings, developments, and integrations.
* Step 7: Touch a service implementation of the project


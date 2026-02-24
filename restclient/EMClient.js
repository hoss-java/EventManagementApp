// Constants
const BASE_URL = "http://172.32.0.11:32768/api";
const GET_COMMANDS_URL = `${BASE_URL}/get`;
const CMD_COMMANDS_URL = `${BASE_URL}/cmd`;
const COMMANDS_PER_PAGE = 5;

const readline = require("readline");

// Create a single readline interface that persists throughout the application
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

// Utility function to prompt user
function prompt(question) {
  return new Promise((resolve) => {
    rl.question(question, (answer) => {
      resolve(answer);
    });
  });
}

// Utility Functions
function clearScreen() {
  /**
   * Clears the console
   */
  console.clear();
}

async function fetchCommands() {
  /**
   * Fetches the list of commands from the API.
   * @returns {Promise<Array|null>} A list of command objects or null on error
   */
  try {
    const response = await fetch(GET_COMMANDS_URL);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data.commands || [];
  } catch (error) {
    console.error(`Error fetching commands: ${error.message}`);
    return null;
  }
}

function displayMenu(commands, level = 0, page = 1) {
  /**
   * Displays the command menu with pagination.
   * @param {Array} commands - List of command objects
   * @param {number} level - Current menu level
   * @param {number} page - Current page number
   * @returns {Object} Object containing display info and paginated commands
   */
  const startIndex = (page - 1) * COMMANDS_PER_PAGE;
  const endIndex = startIndex + COMMANDS_PER_PAGE;
  const paginatedCommands = commands.slice(startIndex, endIndex);

  clearScreen();
  console.log("\n" + "=".repeat(level + 1) + " Menu " + "=".repeat(level + 1));

  if (paginatedCommands.length === 0) {
    console.log("No more commands to display.");
    return { displayed: false, paginatedCommands: [], totalCommands: commands.length };
  }

  paginatedCommands.forEach((command, idx) => {
    const description = command.description || "No description available.";
    const commandId = command.id || "No ID";
    console.log(`${startIndex + idx + 1}: ${description} (${commandId})`);
  });

  // Calculate menu options based on level
  let nextOptionNumber = paginatedCommands.length + 1;

  // Show "Back" for level > 0
  if (level > 0) {
    console.log(`${nextOptionNumber}: Back`);
    nextOptionNumber++;
  }

  // Show "Exit" for level 0
  if (level === 0) {
    console.log(`${nextOptionNumber}: Exit`);
  }

  return { 
    displayed: true, 
    paginatedCommands, 
    totalCommands: commands.length,
    currentPage: page,
    backOption: level > 0 ? paginatedCommands.length + 1 : null,
    exitOption: level === 0 ? nextOptionNumber : null
  };
}

function isValid(inputValue, argType) {
  /**
   * Validates user input based on field type.
   * @param {string} inputValue - The value to validate
   * @param {string} argType - The type of the argument
   * @returns {boolean} True if input is valid
   */
  let type = argType;
  if (argType.includes("@")) {
    type = argType.split("@").pop();
  }

  if (type === "str") {
    if (inputValue.trim() === "") {
      console.log("Hint: Input should be a non-empty string.");
      return false;
    }
    return true;
  }

  if (type === "int" && /^-?\d+$/.test(inputValue)) {
    return true;
  } else if (type === "unsigned" && /^\d+$/.test(inputValue)) {
    return true;
  } else if (type === "date") {
    const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
    if (!dateRegex.test(inputValue)) {
      console.log("Hint: Input should be a valid date in the format YYYY-MM-DD.");
      return false;
    }
    const date = new Date(inputValue);
    return !isNaN(date.getTime());
  } else if (type === "time") {
    const timeRegex = /^([0-1][0-9]|2[0-3]):[0-5][0-9]$/;
    if (!timeRegex.test(inputValue)) {
      console.log("Hint: Input should be a valid time in HH:mm format.");
      return false;
    }
    return true;
  } else if (type === "duration" && /^\d+h \d+m$/.test(inputValue)) {
    return true;
  }

  console.log(`Hint: Unknown argument type: ${type}`);
  return false;
}

async function getUserInput(fieldInfo) {
  /**
   * Prompts user for input based on field information.
   * @param {Object} fieldInfo - Field information including name, type, etc.
   * @returns {Promise<string|number>} User input value or default value
   */
  const fieldName = fieldInfo.field;
  const description = fieldInfo.description || fieldName;
  const fieldType = fieldInfo.type;
  const mandatory = fieldInfo.mandatory || false;
  const modifier = fieldInfo.modifier;

  if (modifier === "auto") {
    return fieldInfo.defaultValue || "";
  }

  while (true) {
    const userInput = await prompt(`Enter ${description} (${fieldType}): `);

    if (mandatory && userInput.trim() === "") {
      console.log(`${description} is mandatory. Please provide a value.`);
      continue;
    }

    if (!mandatory && userInput.trim() === "") {
      return fieldInfo.defaultValue || "";
    }

    if (isValid(userInput, fieldType)) {
      return fieldType === "int" ? parseInt(userInput) : userInput;
    }

    console.log("Invalid input. Please try again.");
  }
}

async function createPayload(argsInfo) {
  /**
   * Creates a JSON payload from user inputs.
   * @param {Object} argsInfo - Dictionary of field information
   * @returns {Promise<Object>} Dictionary representing the payload
   */
  const payload = {};

  for (const [fieldKey, fieldInfo] of Object.entries(argsInfo)) {
    const userInput = await getUserInput(fieldInfo);
    payload[fieldKey] = userInput;
  }

  return payload;
}

async function runCommand(selectedCommand, rootIdentifier) {
  /**
   * Executes the command with the provided identifier.
   * @param {Object} selectedCommand - The selected command object
   * @param {string} rootIdentifier - Identifier from the root
   */
  const payload = {
    identifier: rootIdentifier,
    commands: [
      {
        args: {},
        data: {},
        id: selectedCommand.action,
      },
    ],
  };

  if (selectedCommand.args) {
    for (const [fieldKey, fieldInfo] of Object.entries(selectedCommand.args)) {
      const userInput = await getUserInput(fieldInfo);
      payload.commands[0].args[fieldKey] = fieldInfo;
      if (userInput !== null) {
        payload.commands[0].data[fieldKey] = userInput;
      }
    }
  }

  try {
    const response = await fetch(CMD_COMMANDS_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const responseData = await response.json();
    console.log("Response from the server:");
    console.log(JSON.stringify(responseData, null, 2));
  } catch (error) {
    console.error(`Error sending command: ${error.message}`);
  }
}

async function handleSelection(commands, level = 0) {
  /**
   * Handles user selection from the menu.
   * @param {Array} commands - List of command objects
   * @param {number} level - Current menu level
   */
  let page = 1;

  while (true) {
    const menuInfo = displayMenu(commands, level, page);

    if (!menuInfo.displayed) {
      break;
    }

    const selection = await prompt("Select a command by number: ");
    let selectedNum;

    try {
      selectedNum = parseInt(selection);
    } catch (error) {
      console.log("Invalid selection, please enter a number.");
      continue;
    }

    const paginatedCommands = menuInfo.paginatedCommands;
    const backOption = menuInfo.backOption;
    const exitOption = menuInfo.exitOption;

    // Handle "Back" option (level > 0)
    if (backOption !== null && selectedNum === backOption) {
      return;
    }

    // Handle "Exit" option (level 0)
    if (exitOption !== null && selectedNum === exitOption) {
      console.log("Exiting the program.");
      rl.close();
      process.exit(0);
    }

    // Handle command selection
    if (selectedNum >= 1 && selectedNum <= paginatedCommands.length) {
      const selectedCommand = paginatedCommands[selectedNum - 1];

      if (selectedCommand.commands) {
        // Nested menu
        await handleSelection(selectedCommand.commands, level + 1);
      } else {
        // Execute command
        const rootIdentifier = selectedCommand.id || "root";
        await runCommand(selectedCommand, rootIdentifier);
        await prompt("Press Enter to continue...");
      }
    } else {
      console.log("Invalid selection, please try again.");
    }
  }
}

async function main() {
  /**
   * Main function to execute the command-line interface.
   */
  const commands = await fetchCommands();
  if (commands === null) {
    return;
  }

  await handleSelection(commands);
}

async function checkServiceAvailability() {
  /**
   * Checks if the REST service is available.
   * @returns {Promise<boolean>} True if service is available
   */
  try {
    const response = await fetch(GET_COMMANDS_URL, { timeout: 5000 });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    console.log("Service is available.");
    return true;
  } catch (error) {
    console.log("Service is not available");
    return false;
  }
}

// Main execution
(async () => {
  if (await checkServiceAvailability()) {
    await main();
  } else {
    console.log("Exiting due to service unavailability.");
    rl.close();
    process.exit(1);
  }
})();

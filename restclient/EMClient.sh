#!/bin/bash

# Constants
BASE_URL="http://172.32.0.11:32768/api"
GET_COMMANDS_URL="$BASE_URL/get"
CMD_COMMANDS_URL="$BASE_URL/cmd"
COMMANDS_PER_PAGE=5

# Clear the terminal screen
clear_screen() {
    clear
}

echostd() {
    echo "$@" 1>&2
}    

debug() {
    echostd "$@"
}

debug_wait() {
    echostd "$@"
    read -p "Press Enter to continue..."  # Wait for user input before returning to the menu
}

# Fetch commands from the API
fetch_commands() {
    response=$(curl -s "$GET_COMMANDS_URL")  # Fetch commands
    echo "$response" | jq -r '.commands[] | @base64'  # Ensure correct parsing of JSON
}

# Decode commands from base64
decode_command() {
    # Use -d for decoding with BusyBox compatibility
    echo "$1" | base64 -d 2>/dev/null | jq -r '.'
}

# Validate user input
is_valid() {
    local input_value="$1"
    local arg_type="$2"

    case $arg_type in
        "str")
            [[ -n "$input_value" ]]
            ;;
        "int")
            [[ "$input_value" =~ ^-?[0-9]+$ ]]
            ;;
        "unsigned")
            [[ "$input_value" =~ ^[1-9][0-9]*$ ]]
            ;;
        "date")
            date -d "$input_value" "+%Y-%m-%d" &> /dev/null
            ;;
        "time")
            date -d "$input_value" "+%H:%M" &> /dev/null
            ;;
        "duration")
            # Check format of ISO 8601 duration, e.g., PT15M, P1Y2M, PT1H30M
            if [[ "$input_value" =~ ^P(T([0-9]+H)?([0-9]+M)?([0-9]+S)?)?([0-9]+Y)?([0-9]+M)?([0-9]+D)?$ ]]; then
                return 0
            else
                return 1
            fi
            ;;
        *)
            echostd "Unknown argument type: $arg_type"
            return 1
            ;;
    esac
    return 0
}

# Get user input
get_user_input() {
    local field_info="$1"
    local field_name=$(echo "$field_info" | jq -r '.field')
    local description=$(echo "$field_info" | jq -r --arg field_name "$field_name" '.description // $field_name')
    local field_type=$(echo "$field_info" | jq -r '.type')
    local mandatory=$(echo "$field_info" | jq -r '.mandatory // false')
    local defaultValue=$(echo "$field_info" | jq -r '.defaultValue // ""')
    local modifier=$(echo "$field_info" | jq -r '.modifier // "user"')

    while true; do
        if [[ "$modifier" == "auto" ]]; then
            echo "$defaultValue"
            return
        fi
        read -p "Enter $description ($field_type): " user_input

        if [[ "$mandatory" == "true" && -z "$user_input" ]]; then
            echostd "$description is mandatory. Please provide a value."
            continue
        fi

        if [[ -z "$user_input" ]]; then
            echo "$defaultValue"
            return
        fi

        if is_valid "$user_input" "$field_type"; then
            echo "$user_input"
            return
        fi

        echostd "Invalid input. Please try again."
    done
}

# Create payload for the command
create_payload() {
    local args_info=$(echo "$1" | jq -c '.')
    local payload="{}"

    if [[ "$args_info" == "{}" ]]; then
        payload=$(echo "$payload" | jq '. + {args: {}}')
    else
        # Add args_info to payload as args
        payload=$(echo "$payload" | jq --argjson args_info "$args_info" '. + {args: $args_info}')
        # Initialize the data object
        payload=$(echo "$payload" | jq '. + {data: {}}')
        
        for field_key in $(echo "$args_info" | jq -r 'keys[]'); do
            local field_info=$(echo "$args_info" | jq -r --arg key "$field_key" '.[$key]')
            local field_name=$(echo "$field_info" | jq -r '.field')
            local description=$(echo "$field_info" | jq -r --arg field_name "$field_name" '.description // $field_name')
            local field_type=$(echo "$field_info" | jq -r '.type')
            local mandatory=$(echo "$field_info" | jq -r '.mandatory // false')
            local defaultValue=$(echo "$field_info" | jq -r '.defaultValue // ""')

            # Get user input for each field
            local user_input=$(get_user_input "$field_info")
            # Add user input to `data`
            payload=$(echo "$payload" | jq --arg key "$field_key" --arg value "$user_input" '.data += {($key): $value}')
        done
    fi

    echo "$payload"
}

# Run the command with the provided identifier
run_command() {
    local selected_command="$1"
    local root_identifier="$2"

    local command_id=$(echo "$selected_command" | jq -r '.action')
    local args_info=$(echo "$selected_command" | jq -c '.args // {}')

    local payload="{\"identifier\": \"$root_identifier\", \"commands\": [{\"args\": {}, \"data\": {}, \"id\": \"$command_id\"}]}"
    
    if [[ "$args_info" != "null" ]]; then
        local args_payload=$(create_payload "$args_info")
        payload=$(echo "$payload" | jq --argjson args "$args_payload" '.commands[0].args = $args.args')
        payload=$(echo "$payload" | jq --argjson args "$args_payload" '.commands[0].data = $args.data')
    fi

    echo "$payload"
    response=$(curl -s -X POST -H "Content-Type: application/json" -d "$payload" "$CMD_COMMANDS_URL")
    echo "Response from the server:"
    echo "$response" | jq .
}

# Display the command menu with pagination
display_menu() {
    local identifier="$1"  # First parameter is the identifier
    shift  # Shift the parameters to access the command array

    local commands=("$@")
    local total=${#commands[@]}
    local start=$(( (page - 1) * COMMANDS_PER_PAGE ))
    local end=$(( start + COMMANDS_PER_PAGE ))
    
    clear_screen
    echo "Menu"
    echo "===="
    
    for (( i=start; i<end && i<total; i++ )); do
        local command=$(decode_command "${commands[$i]}")
        local description=$(echo "$command" | jq -r '.description // "No description available."')
        local command_id=$(echo "$command" | jq -r '.id // "No ID"')
        echo "$((i + 1)): $description ($command_id)"
    done

    if [ "$identifier" != "root" ]; then
        echo "$((total + 1)): Back"
        echo "$((total + 2)): Exit"
    else
        echo "$((total + 1)): Exit"
    fi
}

# Handle user selection from the menu
handle_selection() {
    local identifier="$1"  # First parameter is the identifier
    shift  # Shift the parameters to access the command array
    local commands=("$@")
    local total=${#commands[@]}
    local page=1
    local is_first_level=false

    # Determine if this is the first level
    if [[ "$identifier" == "root" ]]; then
        is_first_level=true
    fi

    while true; do
        display_menu "$identifier" "${commands[@]}"  # Display the command menu

        read -p "Select a command by number: " selection

        if [[ $is_first_level == true && "$selection" -eq $((total + 1)) ]]; then
            echo "Exiting the program."
            exit 0
        elif [[ $is_first_level == false && "$selection" -eq $((total + 2)) ]]; then
            echo "Exiting the program."
            exit 0
        elif [[ $is_first_level == false && "$selection" -eq $((total + 1)) ]]; then
            return  # Go back to the previous menu
        elif [[ "$selection" -eq $((total + 1)) ]]; then
            page=$((page + 1))
            continue
        elif [[ "$selection" -lt 1 || "$selection" -gt total ]]; then
            echo "Invalid selection, please try again."
            continue
        fi

        local command=$(decode_command "${commands[$((selection - 1))]}")  # Decode the selected command
        if [[ -z "$command" ]]; then  # Check if decoding was successful
            echo "Failed to decode command."
            continue
        fi
        
        # Check if the command has subcommands
        if echo "$command" | jq -e '.commands' &> /dev/null; then
            local command_id=$(echo "$command" | jq -r '.id // "subcommand"')  # Extract the command's ID
            # If it has subcommands, call handle_selection recursively
            handle_selection "$command_id" $(echo "$command" | jq -r '.commands[] | @base64')
        else
            #local root_identifier=$(echo "$command" | jq -r '.id // "root"')  # Set root identifier
            local root_identifier="$identifier"
            run_command "$command" "$root_identifier"  # Run the command
            read -p "Press Enter to continue..."  # Wait for user input before returning to the menu
        fi

    done
}

# Function to check service availability
check_service_availability() {
    if curl --output /dev/null --silent --head --fail "$GET_COMMANDS_URL"; then
        echo "Service is available."
        return 0
    else
        echo "Service is not available."
        return 1
    fi
}

# Main function
main() {
    commands=($(fetch_commands))

    if [[ ${#commands[@]} -eq 0 ]]; then
        echo "No commands available."
        exit 1
    fi

    handle_selection "root" "${commands[@]}"  # Start at root level
}

# Start the script
if check_service_availability; then
    main
else
    echo "Exiting due to service unavailability."
    exit 1
fi


#!/bin/bash


# Function to run a command and display its exit code, with error handling
run_command() {

    echo "Testing command:${@}"
    # Run the command
    "$@"

    # Get the exit code
    exit_code=$?

    # Display the exit code
    echo "Exit code: $exit_code"

    # Check if the exit code is non-zero
    if [ $exit_code -ne 0 ]; then
        echo "Error: The command '$@' exited with a non-zero status."
        exit $exit_code
    fi
}

# Delete any registered users called tester

echo "CLEANUP - Deleting any registered users called tester or tester-updated"

ids=$(./gradlew run --args "get-users" | sed -n 's/User\[ID=\([0-9]*\), username=\(tester\|tester-updated\)\]/\1/p')

# using read -u 9 to specify not to use stdin/out/err i.e. 0-2
while read -u 9 line ; do
   echo "Deleting duplicate tester user with ID ${line}"
   ./gradlew run --args "delete-user ${line}"
done 9<<< $(echo "$ids") 




# USER TEST SECTION

echo "Testing add-user command"

./gradlew run --args "add-user tester"

echo "Testing get-users command"

userId=$(./gradlew run --args "get-users" | grep "username=tester" | grep -Eo -m1 "ID=[0-9]*" | cut -d "=" -f2)
echo "Found tester users ID - userId=${userId}"

echo "Testing get-user command"
./gradlew run --args "get-user ${userId}"
# TODO - Add check to make sure user was actually added

echo "Testing update-user command"
./gradlew run --args "update-user -u='tester-updated' ${userId}"

echo "Checking that update-user command changed username"
# test checks for non zero exit code
test $(./gradlew run --args "get-user ${userId}" | grep "username=tester-updated")

echo "Testing add-video command"
./gradlew run --args "add-video 'test-video' ${userId} ['testTag1','testTag2']"

echo "Testing get-videos command"



echo "Testing delete-user command"
./gradlew run --args "delete-user ${userId}"

echo "Test passed!"





echo "Test Passed!"

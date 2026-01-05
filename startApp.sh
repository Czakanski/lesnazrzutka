#!/bin/bash

# Script to start the lesnazrzutka application
# Default accounts:
#   - Admin: username=admin, password=admin (ADMIN, USER roles)
#   - User: username=user, password=user (USER role)

echo "========================================"
echo "Starting lesnazrzutka application..."
echo "========================================"
echo ""
echo "Default accounts:"
echo "  Admin account: admin / admin"
echo "  User account: user / user"
echo ""

# Check if gradle wrapper exists
if [ ! -f "gradlew" ]; then
    echo "Error: gradlew not found. Please run this script from the project root directory."
    exit 1
fi

# Build the project
echo "Building the project..."
./gradlew clean build

if [ $? -ne 0 ]; then
    echo "Build failed. Please check the errors above."
    exit 1
fi

echo ""
echo "========================================"
echo "Build completed successfully!"
echo "========================================"
echo ""
echo "Starting the application..."
echo ""

# Run the application
./gradlew bootRun



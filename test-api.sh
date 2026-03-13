#!/bin/bash

# Track Warrant API Test Script
# This script tests all REST endpoints of the Track Warrant Management System

BASE_URL="http://localhost:8080/api/warrants"

echo "=========================================="
echo "Track Warrant API Testing Script"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test 1: Get all warrants
echo -e "${YELLOW}Test 1: GET all warrants${NC}"
curl -s -X GET "$BASE_URL" | jq '.'
echo ""
echo ""

# Test 2: Get active warrants only
echo -e "${YELLOW}Test 2: GET active warrants${NC}"
curl -s -X GET "$BASE_URL/active" | jq '.'
echo ""
echo ""

# Test 3: Create a new warrant
echo -e "${YELLOW}Test 3: POST create new warrant${NC}"
NEW_WARRANT=$(cat <<EOF
{
  "warrantId": "TW-API-TEST-001",
  "trainId": "TEST-TRAIN-999",
  "startingLocation": "API Test Station A",
  "endingLocation": "API Test Station B",
  "trackName": "API Test Line",
  "issuedDateTime": "$(date -u +%Y-%m-%dT%H:%M:%S)",
  "expirationDateTime": "$(date -u -d '+8 hours' +%Y-%m-%dT%H:%M:%S)",
  "maxSpeed": 75,
  "issuedBy": "API Test Dispatcher",
  "instructions": "This is a test warrant created via API",
  "direction": "NORTH"
}
EOF
)

curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d "$NEW_WARRANT" | jq '.'
echo ""
echo ""

# Test 4: Get the newly created warrant
echo -e "${YELLOW}Test 4: GET single warrant (TW-API-TEST-001)${NC}"
curl -s -X GET "$BASE_URL/TW-API-TEST-001" | jq '.'
echo ""
echo ""

# Test 5: Get warrants by train ID
echo -e "${YELLOW}Test 5: GET warrants by train ID${NC}"
curl -s -X GET "$BASE_URL/train/TEST-TRAIN-999" | jq '.'
echo ""
echo ""

# Test 6: Complete the warrant
echo -e "${YELLOW}Test 6: PUT complete warrant${NC}"
curl -s -X PUT "$BASE_URL/TW-API-TEST-001/complete" | jq '.'
echo ""
echo ""

# Test 7: Verify warrant is completed
echo -e "${YELLOW}Test 7: Verify warrant status is COMPLETED${NC}"
curl -s -X GET "$BASE_URL/TW-API-TEST-001" | jq '.status'
echo ""
echo ""

# Test 8: Create another warrant for cancellation test
echo -e "${YELLOW}Test 8: Create warrant for cancellation test${NC}"
CANCEL_WARRANT=$(cat <<EOF
{
  "warrantId": "TW-API-TEST-002",
  "trainId": "TEST-TRAIN-888",
  "startingLocation": "Test Start",
  "endingLocation": "Test End",
  "maxSpeed": 60
}
EOF
)

curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d "$CANCEL_WARRANT" | jq '.'
echo ""
echo ""

# Test 9: Cancel the warrant
echo -e "${YELLOW}Test 9: PUT cancel warrant${NC}"
curl -s -X PUT "$BASE_URL/TW-API-TEST-002/cancel" | jq '.'
echo ""
echo ""

# Test 10: Get all warrants (should show 2 test warrants + 3 sample warrants)
echo -e "${YELLOW}Test 10: GET all warrants (final check)${NC}"
curl -s -X GET "$BASE_URL" | jq 'length'
echo " warrants in system"
echo ""
echo ""

# Test 11: Test error handling - try to get non-existent warrant
echo -e "${YELLOW}Test 11: GET non-existent warrant (should return 404)${NC}"
curl -s -w "\nHTTP Status: %{http_code}\n" -X GET "$BASE_URL/NON-EXISTENT"
echo ""
echo ""

# Test 12: Delete a warrant
echo -e "${YELLOW}Test 12: DELETE warrant${NC}"
curl -s -w "\nHTTP Status: %{http_code}\n" -X DELETE "$BASE_URL/TW-API-TEST-002"
echo ""
echo ""

echo -e "${GREEN}=========================================="
echo "All API tests completed!"
echo -e "==========================================${NC}"


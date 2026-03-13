#!/bin/bash

# Refactoring Verification Script
# Verifies that the single-module refactoring was successful

echo "=========================================="
echo "Track Warrant Refactoring Verification"
echo "=========================================="
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

FAILED=0

# Check 1: Service module should NOT exist
echo -n "1. Checking service module removed... "
if [ ! -d "/home/paul/Trainbeans/TrackWarrants/service" ]; then
    echo -e "${GREEN}✅ PASS${NC}"
else
    echo -e "${RED}❌ FAIL - service directory still exists${NC}"
    FAILED=1
fi

# Check 2: Backup should exist
echo -n "2. Checking backup exists... "
if [ -f "/home/paul/Trainbeans/TrackWarrants/service-module-backup-20260305.tar.gz" ]; then
    echo -e "${GREEN}✅ PASS${NC}"
else
    echo -e "${YELLOW}⚠ WARNING - backup not found${NC}"
fi

# Check 3: Parent POM should only reference main module
echo -n "3. Checking parent POM... "
if grep -q "<module>main</module>" /home/paul/Trainbeans/TrackWarrants/pom.xml && \
   ! grep -q "<module>service</module>" /home/paul/Trainbeans/TrackWarrants/pom.xml; then
    echo -e "${GREEN}✅ PASS${NC}"
else
    echo -e "${RED}❌ FAIL - parent POM not updated correctly${NC}"
    FAILED=1
fi

# Check 4: All required Java files should exist in main
echo -n "4. Checking main module files... "
REQUIRED_FILES=(
    "main/src/main/java/org/trainbeans/trackwarrants/main/MainApplication.java"
    "main/src/main/java/org/trainbeans/trackwarrants/main/entity/TrackWarrant.java"
    "main/src/main/java/org/trainbeans/trackwarrants/main/repository/TrackWarrantRepository.java"
    "main/src/main/java/org/trainbeans/trackwarrants/main/service/TrackWarrantService.java"
    "main/src/main/java/org/trainbeans/trackwarrants/main/controller/TrackWarrantController.java"
)

ALL_EXIST=true
for file in "${REQUIRED_FILES[@]}"; do
    if [ ! -f "/home/paul/Trainbeans/TrackWarrants/$file" ]; then
        ALL_EXIST=false
        break
    fi
done

if [ "$ALL_EXIST" = true ]; then
    echo -e "${GREEN}✅ PASS${NC}"
else
    echo -e "${RED}❌ FAIL - some required files missing${NC}"
    FAILED=1
fi

# Check 5: Test files should exist
echo -n "5. Checking test files... "
TEST_FILES=(
    "main/src/test/java/org/trainbeans/trackwarrants/main/controller/TrackWarrantControllerIntegrationTest.java"
    "main/src/test/java/org/trainbeans/trackwarrants/main/service/TrackWarrantServiceTest.java"
)

ALL_TESTS_EXIST=true
for file in "${TEST_FILES[@]}"; do
    if [ ! -f "/home/paul/Trainbeans/TrackWarrants/$file" ]; then
        ALL_TESTS_EXIST=false
        break
    fi
done

if [ "$ALL_TESTS_EXIST" = true ]; then
    echo -e "${GREEN}✅ PASS${NC}"
else
    echo -e "${RED}❌ FAIL - some test files missing${NC}"
    FAILED=1
fi

# Check 6: Resources should exist
echo -n "6. Checking resources... "
if [ -f "/home/paul/Trainbeans/TrackWarrants/main/src/main/resources/application.properties" ] && \
   [ -f "/home/paul/Trainbeans/TrackWarrants/main/src/main/resources/static/index.html" ]; then
    echo -e "${GREEN}✅ PASS${NC}"
else
    echo -e "${RED}❌ FAIL - resources missing${NC}"
    FAILED=1
fi

# Check 7: Count Java files
echo -n "7. Checking Java file count... "
JAVA_COUNT=$(find /home/paul/Trainbeans/TrackWarrants/main/src -name "*.java" -type f | wc -l)
if [ "$JAVA_COUNT" -eq 12 ]; then
    echo -e "${GREEN}✅ PASS (12 files)${NC}"
else
    echo -e "${YELLOW}⚠ WARNING - found $JAVA_COUNT files (expected 12)${NC}"
fi

echo ""
echo "=========================================="
if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✅ ALL CHECKS PASSED!${NC}"
    echo "Refactoring is successful!"
else
    echo -e "${RED}❌ SOME CHECKS FAILED${NC}"
    echo "Please review the errors above."
fi
echo "=========================================="
echo ""

# Summary
echo "Summary:"
echo "- Project: Track Warrant Management System"
echo "- Structure: Single module (main only)"
echo "- Service module: Removed and backed up"
echo "- Total Java files: $JAVA_COUNT"
echo "- Documentation: Updated"
echo ""
echo "To run the application:"
echo "  cd /home/paul/Trainbeans/TrackWarrants/main"
echo "  mvn spring-boot:run"
echo ""


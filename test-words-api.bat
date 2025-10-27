@echo off
REM Test script for Words API endpoints

echo ========================================
echo Testing Words API Endpoints
echo ========================================
echo.

echo [1] GET all words
curl http://localhost:8080/api/words
echo.
echo.

echo [2] GET daily word (deterministic - same word on same day)
curl http://localhost:8080/api/words/daily
echo.
echo.

echo [3] GET random word (different each time)
curl http://localhost:8080/api/words/random
echo.
echo.

echo [4] GET word by term: apple
curl http://localhost:8080/api/words/apple
echo.
echo.

echo [5] POST new word: dragon
curl -X POST http://localhost:8080/api/words -H "Content-Type: application/json" -d "{\"term\":\"dragon\",\"definition\":\"A mythical creature\"}"
echo.
echo.

echo [6] POST duplicate word (should fail with 409)
curl -X POST http://localhost:8080/api/words -H "Content-Type: application/json" -d "{\"term\":\"apple\",\"definition\":\"Fruit\"}"
echo.
echo.

echo [7] POST word with empty term (should fail with 400)
curl -X POST http://localhost:8080/api/words -H "Content-Type: application/json" -d "{\"term\":\"\",\"definition\":\"Some definition\"}"
echo.
echo.

echo [8] GET all words again (should now include dragon)
curl http://localhost:8080/api/words
echo.
echo.

echo ========================================
echo All tests complete!
echo ========================================

pause


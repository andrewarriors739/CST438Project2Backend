#!/bin/bash
# Test script for Words API endpoints - Production Heroku Database

echo "========================================"
echo "Testing Words API Endpoints (PRODUCTION)"
echo "Heroku: https://group6-backend-717076585089.herokuapp.com"
echo "========================================"
echo ""

echo "[1] GET daily word (deterministic - same word for everyone today)"
curl https://group6-backend-717076585089.herokuapp.com/api/words/daily
echo ""
echo ""

echo "[2] GET random word (different each time)"
curl https://group6-backend-717076585089.herokuapp.com/api/words/random
echo ""
echo ""

echo "[3] GET another random word (to show it changes)"
curl https://group6-backend-717076585089.herokuapp.com/api/words/random
echo ""
echo ""

echo "[4] POST new word 'AutoTestWord' to production database"
curl -X POST https://group6-backend-717076585089.herokuapp.com/api/words \
  -H "Content-Type: application/json" \
  -d "{\"term\":\"AutoTestWord\",\"definition\":\"This is a test word created by automated script\"}" \
  > temp_word.json
echo ""
echo ""

echo "[5] GET the word we just added by term 'AutoTestWord'"
curl https://group6-backend-717076585089.herokuapp.com/api/words/AutoTestWord
echo ""
echo ""

echo "[6] Extract word ID and DELETE the word we just added"
WORD_ID=$(cat temp_word.json | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
curl -X DELETE https://group6-backend-717076585089.herokuapp.com/api/words/$WORD_ID
echo ""
echo ""

echo "[7] GET the deleted word again (should return 404)"
curl https://group6-backend-717076585089.herokuapp.com/api/words/AutoTestWord
echo ""
echo ""

echo "[8] Clean up temp file"
rm -f temp_word.json
echo ""
echo ""

echo "[9] POST duplicate word (should fail with 409)"
curl -X POST https://group6-backend-717076585089.herokuapp.com/api/words \
  -H "Content-Type: application/json" \
  -d "{\"term\":\"Testing\",\"definition\":\"Duplicate test\"}"
echo ""
echo ""

echo "[10] POST word with empty term (should fail with 400)"
curl -X POST https://group6-backend-717076585089.herokuapp.com/api/words \
  -H "Content-Type: application/json" \
  -d "{\"term\":\"\",\"definition\":\"Some definition\"}"
echo ""
echo ""

echo "[11] Test getting a word that doesn't exist (should return 404)"
curl https://group6-backend-717076585089.herokuapp.com/api/words/nonexistentword123xyz
echo ""
echo ""

echo "========================================"
echo "Production API tests complete!"
echo "The word was automatically added, verified, and deleted."
echo "========================================"


#!/bin/zsh
# Dubultklikšķis uz šī faila Finderī -> atver mācību aplikāciju pārlūkā.
# Apturēt: aizver šo termināļa logu (vai Ctrl+C).
cd "$(dirname "$0")/frontend"
[ -d node_modules ] || npm install
npm run dev -- --open
